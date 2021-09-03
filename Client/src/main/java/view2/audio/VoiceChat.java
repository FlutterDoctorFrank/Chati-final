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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VoiceChat {

    private static final int SAMPLE_RATE = 44100;
    private static final int SEND_RATE = 30;
    private static final int PACKET_SIZE = SAMPLE_RATE / SEND_RATE;
    private static final long PACKET_PLAY_TIME = 1000 / SEND_RATE;
    private static final int MIN_PACKETS = 6;
    private static final short SEND_GATE = 1024;
    private static final long STOP_SENDING_DELAY = 250;

    private final Map<IUserView, SenderQueue> receivedDataBuffer;
    private final AudioRecorder recorder;
    private final AudioDevice player;

    private boolean isRunning;
    private boolean isSending;

    public VoiceChat() {
        this.recorder = Gdx.audio.newAudioRecorder(SAMPLE_RATE, true);
        this.player = Gdx.audio.newAudioDevice(SAMPLE_RATE, true);
        this.receivedDataBuffer = new ConcurrentHashMap<>();
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        recordAndSend();
        mixAndPlayback();
    }

    public void stop() {
        isRunning = false;
    }

    public synchronized void startSending() {
        isSending = true;
        notifyAll();
    }

    public void stopSending() {
        isSending = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isSending() {
        return isSending;
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
        short[] receivedData = toShort(voiceData, true);

        synchronized (this) {
            if (!receivedDataBuffer.containsKey(user)) {
                receivedDataBuffer.put(user, new SenderQueue(user, timestamp, receivedData));
            } else {
                receivedDataBuffer.get(user).addData(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    private void recordAndSend() {
        Thread recordAndSendThread = new Thread(() -> {
            long timestamp = System.currentTimeMillis();
            short[] recordedData = new short[PACKET_SIZE];

            while (isRunning) {
                synchronized (this) {
                    // Warte solange nicht gesendet werden soll.
                    while (!isSending) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Überprüfe ob Lautstärke eines Samples gewissen Wert überschreitet, fange dann an zu senden und setze
                // Zeitstempel. Sende ab diesem Zeitpunkt mindestens für eine gewisse Dauer weiter, bevor das Senden
                // gestoppt wird. Dadurch wird ein Dauersenden verhindert, ohne am Ende "abgehackt" zu werden.
                recorder.read(recordedData, 0, recordedData.length);
                for (short sample : recordedData) {
                    if (Math.abs(sample) >= SEND_GATE) {
                        timestamp = System.currentTimeMillis();
                        break;
                    }
                }
                if (System.currentTimeMillis() - timestamp < STOP_SENDING_DELAY) {
                    Chati.CHATI.getServerSender().send(ServerSender.SendAction.VOICE, toByte(recordedData, true));
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
                synchronized (this) {
                    // Warte, solange keine Daten vorhanden sind.
                    while (receivedDataBuffer.isEmpty()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Entferne alle Warteschlangen, wenn in dieser für die Abspielzeit eines Pakets kein Paket eingetroffen
                // ist.
                int[] temp = new int[PACKET_SIZE];
                receivedDataBuffer.values().removeIf(Predicate.not(SenderQueue::hasData).and(queue -> queue
                        .getLastTimeReceived().isBefore(LocalDateTime.now().minus(PACKET_PLAY_TIME, ChronoUnit.MILLIS))));

                // Mische das oberste Element aller Warteschlangen pro Benutzer zusammen, die bereit zum Abspielen sind.
                final Set<SenderQueue.VoiceDataBlock> blocks = receivedDataBuffer.values().stream()
                        .filter(SenderQueue::isReady)
                        .map(SenderQueue::getBlock)
                        .collect(Collectors.toSet());
                if (blocks.size() == 0) {
                    continue;
                }
                for (int i = 0; i < PACKET_SIZE; i++) {
                    int j = i;
                    blocks.forEach(block -> temp[j] += block.getVoiceData()[j]);
                    mixedData[j] = (short) (temp[j] / blocks.size());
                }
                player.writeSamples(mixedData, 0, mixedData.length);
            }
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    private byte[] toByte(short[] shorts, boolean bigEndian) {
        byte[] bytes = new byte[shorts.length * 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) (shorts[i] >> 8);
                bytes[2 * i + 1] = (byte) shorts[i];
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                bytes[2 * i] = (byte) shorts[i];
                bytes[2 * i + 1] = (byte) (shorts[i] >> 8);
            }
        }
        return bytes;
    }

    private short[] toShort(byte[] bytes, boolean bigEndian) {
        short[] shorts = new short[bytes.length / 2];
        if (bigEndian) {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
            }
        } else {
            for (int i = 0; i < shorts.length; i++) {
                shorts[i] = (short) ((bytes[2 * i] & 0xff) | (bytes[2 * i + 1] << 8));
            }
        }
        return shorts;
    }

    private static class SenderQueue {

        private final IUserView sender;
        private final Queue<VoiceDataBlock> voiceDataQueue;
        private LocalDateTime lastTimeReceived;
        private boolean ready;

        public SenderQueue(IUserView sender, LocalDateTime timestamp, short[] receivedData) {
            this.sender = sender;
            this.voiceDataQueue = new LinkedBlockingQueue<>();
            addData(timestamp, receivedData);
        }

        private void addData(LocalDateTime timestamp, short[] voiceData) {
            this.voiceDataQueue.add(new VoiceDataBlock(voiceData));
            this.lastTimeReceived = timestamp;

            // Warteschlange ist erst bereit, wenn eine minimale Anzahl an Paketen vorhanden ist. Dies führt zu einer
            // kleinen Verzögerung von MIN_PACKETS / SEND_RATE, sorgt aber dafür, dass man nicht abgehackt wird wenn
            // ein Paket verspätet ankommt, da sich sonst evtl. die Warteschlange bis zum Eintreffen des verspäteten
            // Pakets entleert. Ein Zurücksetzen des Ready-Parameters ist nicht nötig, da die Warteschlange aus der
            // Hashmap entfernt wird,wenn für eine bestimmte Zeit kein Paket von einem Sender eintrifft.
            if (!ready && voiceDataQueue.size() >= MIN_PACKETS) {
                ready = true;
            }
        }

        private boolean hasData() {
            return !voiceDataQueue.isEmpty();
        }

        private boolean isReady() {
            return hasData() && ready;
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
