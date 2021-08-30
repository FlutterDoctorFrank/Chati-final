package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class VoiceChatV2 {
    private static final int SAMPLE_RATE = 32000;
    private static final int SEND_RATE = 30;
    private static final int PACKET_SIZE = SAMPLE_RATE / SEND_RATE;
    private static final int GATE = 1024;
    private static final long STOP_RECORD_DELAY = 250;

    private static VoiceChatV2 voiceChat;

    private final Map<IUserView, VoiceDataQueue> mixDataBuffer;
    private final AudioRecorder recorder;
    private final AudioDevice player;

    private final Semaphore recordSemaphore;
    private final Semaphore playSemaphore;

    private boolean isRunning;
    private boolean recordAndSend;

    private VoiceChatV2() {
        this.recorder = Gdx.audio.newAudioRecorder(SAMPLE_RATE, true);
        this.player = Gdx.audio.newAudioDevice(SAMPLE_RATE, true);
        this.mixDataBuffer = new ConcurrentHashMap<>();
        this.recordSemaphore = new Semaphore(1);
        this.playSemaphore = new Semaphore(1);
    }

    public void start() {
        isRunning = true;
        recordAndSend();
        mixAndPlaybackAudio();
    }

    public void stop() {
        isRunning = false;
    }

    public void startSending() {
        recordAndSend = true;
        recordSemaphore.release();
    }

    public void stopSending() {
        recordAndSend = false;
    }

    public void receiveData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
        if (!mixDataBuffer.containsKey(user)) {
            mixDataBuffer.put(user, new VoiceDataQueue(user));
        }
        mixDataBuffer.get(user).addData(timestamp, toShort(voiceData, false));

        playSemaphore.release();
    }

    /*
        Erzeuge und sende Audiopakete in einem Thread.
     */
    private void recordAndSend() {
        Thread recordAndSendThread = new Thread(() -> {
            long timestamp = System.currentTimeMillis();
            short[] recordData = new short[SAMPLE_RATE / SEND_RATE];
            while (isRunning) {
                while (!recordAndSend) {
                    try {
                        recordSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*
                    Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet und setze Zeitstempel.
                    Nehme ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter auf, bevor die Aufnahme gestoppt
                    wird. Dadurch wird ein Dauersenden verhindert ohne am Ende "abgehackt" zu werden.
                */
                recorder.read(recordData, 0, recordData.length);
                for (short sample : recordData) {
                    if (Math.abs(sample) >= GATE) {
                        timestamp = System.currentTimeMillis();
                        break;
                    }
                }
                if (System.currentTimeMillis() - timestamp < STOP_RECORD_DELAY) {
                    Chati.CHATI.getServerSender()
                            .send(ServerSender.SendAction.VOICE, toByte(recordData, false));
                }
            }
        });
        recordAndSendThread.setDaemon(true);
        recordAndSendThread.start();
    }

    /*
        Mixe erhaltene Audiopakete und spiele Ergebnisse in einem Thread ab.
     */
    private void mixAndPlaybackAudio() {
        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[PACKET_SIZE];
            while (isRunning) {
                while (mixDataBuffer.isEmpty()) {
                    try {
                        playSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Mixe alle Audiodaten zusammen
                int[] temp = new int[PACKET_SIZE];
                int numProducers = 0;
                Iterator<VoiceDataQueue> iterator = mixDataBuffer.values().iterator();
                while (iterator.hasNext()) {
                    VoiceDataQueue queue = iterator.next();
                    if (queue.hasData()) {
                        VoiceDataQueue.VoiceDataBlock dataBlock = queue.getData();
                        for (int i = 0; i < PACKET_SIZE; i++) {
                            temp[i] += dataBlock.getVoiceData()[i];
                        }
                        numProducers++;
                    } else {
                        iterator.remove();
                    }
                }
                if (numProducers == 0) {
                    continue;
                }
                for (int i = 0; i < PACKET_SIZE; i++) {
                    mixedData[i] = (short) (temp[i] / numProducers);
                }
                System.out.println(mixedData);
                player.writeSamples(mixedData, 0, mixedData.length);
            }
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    private byte[] toByte(short[] shorts, boolean littleEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (littleEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        }
        return bytes;
    }

    private short[] toShort(byte[] bytes, boolean littleEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (littleEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        }
        return shorts;
    }

    public static VoiceChatV2 getInstance() {
        if (voiceChat == null) {
            voiceChat = new VoiceChatV2();
        }
        return voiceChat;
    }

    private static class VoiceDataQueue {

        private final IUserView sender;
        private final Queue<VoiceDataBlock> voiceDataQueue;

        private VoiceDataQueue(IUserView sender) {
            this.sender = sender;
            this.voiceDataQueue = new LinkedBlockingQueue<>();
        }

        private void addData(LocalDateTime timestamp, short[] voiceData) {
            this.voiceDataQueue.add(new VoiceDataBlock(timestamp, voiceData));
        }

        private boolean hasData() {
            return !voiceDataQueue.isEmpty();
        }

        private IUserView getSender() {
            return sender;
        }

        private VoiceDataBlock getData() {
            return voiceDataQueue.poll();
        }

        private static class VoiceDataBlock {

            private final LocalDateTime timestamp;
            private final short[] voiceData;

            private VoiceDataBlock(LocalDateTime timestamp, short[] voiceData) {
                this.timestamp = timestamp;
                this.voiceData = voiceData;
            }

            private LocalDateTime getTimestamp() {
                return timestamp;
            }

            private short[] getVoiceData() {
                return voiceData;
            }
        }
    }
}
