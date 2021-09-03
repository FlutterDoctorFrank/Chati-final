package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import model.exception.UserNotFoundException;
import model.user.IUserView;
import view2.Chati;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AudioConsumer {

    private static final int MIN_PACKETS = 6;

    private final Map<UUID, ProducerQueue> receivedDataBuffer;
    private final AudioDevice player;

    private boolean isRunning;

    public AudioConsumer() {
        this.receivedDataBuffer = new ConcurrentHashMap<>();
        this.player = Gdx.audio.newAudioDevice(AudioManager.SAMPLE_RATE, AudioManager.MONO);
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[AudioManager.PACKET_SIZE];

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

                int[] temp = new int[AudioManager.PACKET_SIZE];

                // Mische das oberste Element aller Warteschlangen pro Producer zusammen, die bereit zum Abspielen sind.
                final Set<ProducerQueue.AudioDataBlock> blocks = receivedDataBuffer.values().stream()
                        .filter(ProducerQueue::isReady)
                        .map(ProducerQueue::getBlock)
                        .collect(Collectors.toSet());
                if (blocks.size() == 0) {
                    continue;
                }
                for (int i = 0; i < AudioManager.PACKET_SIZE; i++) {
                    int j = i;
                    blocks.forEach(block -> temp[j] += block.getAudioData()[j]);
                    mixedData[j] = (short) (temp[j] / blocks.size());
                }
                player.writeSamples(mixedData, 0, mixedData.length);

                receivedDataBuffer.values().removeIf(Predicate.not(ProducerQueue::hasData));
            }
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setVolume(float volume) {
        if (player != null) {
            player.setVolume(volume);
        }
    }

    public void receiveVoiceData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
        short[] receivedData = AudioManager.toShort(voiceData, true);

        synchronized (this) {
            if (!receivedDataBuffer.containsKey(user.getUserId())) {
                receivedDataBuffer.put(userId, new ProducerQueue(userId, timestamp, receivedData));
            } else {
                receivedDataBuffer.get(userId).addData(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    public void receiveMusicStream(UUID streamId, LocalDateTime timestamp, byte[] musicData) {
        if (!isRunning) {
            return;
        }
        short[] receivedData = AudioManager.toShort(musicData, false);

        synchronized (this) {
            if (!receivedDataBuffer.containsKey(streamId)) {
                receivedDataBuffer.put(streamId, new ProducerQueue(streamId, timestamp, receivedData));
            } else {
                receivedDataBuffer.get(streamId).addData(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    private static class ProducerQueue {

        private final UUID producerId;
        private final Queue<AudioDataBlock> audioDataQueue;
        private LocalDateTime lastTimeReceived;
        private boolean ready;

        public ProducerQueue(UUID producerId, LocalDateTime timestamp, short[] receivedData) {
            this.producerId = producerId;
            this.audioDataQueue = new LinkedBlockingQueue<>();
            addData(timestamp, receivedData);
        }

        private void addData(LocalDateTime timestamp, short[] audioData) {
            this.audioDataQueue.add(new AudioDataBlock(audioData));
            this.lastTimeReceived = timestamp;

            // Warteschlange ist erst bereit, wenn eine minimale Anzahl an Paketen vorhanden ist. Dies führt zu einer
            // kleinen Verzögerung von MIN_PACKETS / SEND_RATE, sorgt aber dafür, dass man nicht abgehackt wird wenn
            // ein Paket verspätet ankommt, da sich sonst evtl. die Warteschlange bis zum Eintreffen des verspäteten
            // Pakets entleert. Ein Zurücksetzen des Ready-Parameters ist nicht nötig, da die Warteschlange aus der
            // Hashmap entfernt wird,wenn für eine bestimmte Zeit kein Paket von einem Sender eintrifft.
            if (!ready && audioDataQueue.size() >= MIN_PACKETS) {
                ready = true;
            }
        }

        private boolean hasData() {
            return !audioDataQueue.isEmpty();
        }

        private boolean isReady() {
            return hasData() && ready;
        }

        public UUID getProducerId() {
            return producerId;
        }

        private AudioDataBlock getBlock() {
            return audioDataQueue.poll();
        }

        private LocalDateTime getLastTimeReceived() {
            return lastTimeReceived;
        }

        private static class AudioDataBlock {

            private final short[] audioData;

            private AudioDataBlock(short[] audioData) {
                this.audioData = audioData;
            }

            private short[] getAudioData() {
                return audioData;
            }
        }
    }
}
