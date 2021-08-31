package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import controller.network.ServerSender;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import view2.Chati;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VoiceChat {

    private static final int SAMPLE_RATE = 32000;
    private static final int SEND_RATE = 30;
    private static final int PACKET_SIZE = SAMPLE_RATE / SEND_RATE;
    private static final int GATE = 1024;
    private static final long STOP_SENDING_DELAY = 250;

    private final Map<IUserView, SenderQueue> receivedDataBuffer;
    private final AudioRecorder recorder;
    private final AudioDevice player;

    private final Semaphore sendSemaphore;
    private final Semaphore playSemaphore;

    private boolean isRunning;
    private boolean isSending;

    public VoiceChat() {
        this.recorder = Gdx.audio.newAudioRecorder(SAMPLE_RATE, true);
        this.player = Gdx.audio.newAudioDevice(SAMPLE_RATE, true);
        this.receivedDataBuffer = new ConcurrentHashMap<>();
        this.sendSemaphore = new Semaphore(1);
        this.playSemaphore = new Semaphore(1);
    }

    public void start() {
        isRunning = true;
        recordAndSend();
        mixAndPlayback();
    }

    public void stop() {
        isRunning = false;
    }

    public void startSending() {
        isSending = true;
        sendSemaphore.release();
    }

    public void stopSending() {
        isSending = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setVolume(float volume) {
        if (player != null) {
            player.setVolume(volume);
        }
    }

    public void receiveData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
        short[] receivedData = toShort(voiceData);

        if (!receivedDataBuffer.containsKey(user)) {
            receivedDataBuffer.put(user, new SenderQueue(user, timestamp, receivedData));
        } else {
            receivedDataBuffer.get(user).addData(timestamp, receivedData);
        }

        playSemaphore.release();
    }

    private void recordAndSend() {
        Thread recordAndSendThread = new Thread(() -> {
            long timestamp = System.currentTimeMillis();
            short[] recordedData = new short[PACKET_SIZE];
            while (isRunning) {
                while (!isSending) {
                    try {
                        sendSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet, fange dann an zu senden und setze
                // Zeitstempel. Sende ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter, bevor das Senden
                // gestoppt wird. Dadurch wird ein Dauersenden verhindert ohne am Ende "abgehackt" zu werden.
                recorder.read(recordedData, 0, recordedData.length);
                for (short sample : recordedData) {
                    if (Math.abs(sample) >= GATE) {
                        timestamp = System.currentTimeMillis();
                        break;
                    }
                }
                if (System.currentTimeMillis() - timestamp < STOP_SENDING_DELAY) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.VOICE, toByte(recordedData));
                }
            }
        });
        recordAndSendThread.setDaemon(true);
        recordAndSendThread.start();
    }

    private void mixAndPlayback() {
        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[PACKET_SIZE];
            while (isRunning) {
                while (receivedDataBuffer.isEmpty()) {
                    try {
                        playSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                int[] temp = new int[PACKET_SIZE];
                Set<SenderQueue.VoiceDataBlock> blocks = receivedDataBuffer.values().stream()
                        .filter(SenderQueue::hasData)
                        .map(SenderQueue::getBlock)
                        .collect(Collectors.toSet());
                for (int i = 0; i < PACKET_SIZE; i++) {
                    int j = i;
                    blocks.forEach(block -> temp[j] += block.getVoiceData()[j]);
                    mixedData[j] = (short) (temp[j] / blocks.size());
                }
                receivedDataBuffer.values().removeIf(Predicate.not(SenderQueue::hasData).and(queue -> queue
                        .getLastTimeReceived().isBefore(LocalDateTime.now().minus(STOP_SENDING_DELAY, ChronoUnit.MILLIS))));
                player.writeSamples(mixedData, 0, mixedData.length);
            }
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    private byte[] toByte(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            bytes[2 * i] = (byte) (shorts[i] >> 8);
            bytes[2 * i + 1] = (byte) shorts[i];
        }
        return bytes;
    }

    private short[] toShort(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
        }
        return shorts;
    }

    private static class SenderQueue {

        private final IUserView sender;
        private final Queue<VoiceDataBlock> voiceDataQueue;
        private LocalDateTime lastTimeReceived;

        public SenderQueue(IUserView sender, LocalDateTime timestamp, short[] receivedData) {
            this.sender = sender;
            this.voiceDataQueue = new LinkedBlockingQueue<>();
            addData(timestamp, receivedData);
        }

        private void addData(LocalDateTime timestamp, short[] voiceData) {
            this.voiceDataQueue.add(new VoiceDataBlock(voiceData));
            this.lastTimeReceived = timestamp;
        }

        private boolean hasData() {
            return !voiceDataQueue.isEmpty();
        }

        private IUserView getSender() {
            return sender;
        }

        private VoiceDataBlock getBlock() {
            return voiceDataQueue.poll();
        }

        private LocalDateTime getLastTimeReceived() {
            return lastTimeReceived;
        }

        private static class VoiceDataBlock {

            private final short[] voiceData;

            private VoiceDataBlock(short[] voiceData) {
                this.voiceData = voiceData;
            }

            private short[] getVoiceData() {
                return voiceData;
            }
        }
    }
}
