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
    private static final int MAX_PACKETS = 30;

    private final Map<IUserView, ProducerQueue> voiceDataBuffer;
    private final ProducerQueue musicStream;
    private final AudioDevice player;

    private boolean isRunning;

    public AudioConsumer() {
        this.voiceDataBuffer = new ConcurrentHashMap<>();
        this.musicStream = new ProducerQueue();
        this.player = Gdx.audio.newAudioDevice(AudioManager.SAMPLE_RATE, AudioManager.MONO);
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;

        Thread mixAndPlaybackThread = new Thread(() -> {
            short[] mixedData = new short[AudioManager.BLOCK_SIZE];
            while (isRunning) {
                synchronized (this) {
                    // Warte, solange keine Daten vorhanden sind.
                    while (voiceDataBuffer.isEmpty() && !musicStream.hasData()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                int[] temp = new int[AudioManager.BLOCK_SIZE];
                // Mische das oberste Element aller Warteschlangen pro Producer zusammen, die bereit zum Abspielen sind.
                final Set<ProducerQueue.AudioDataBlock> blocks = voiceDataBuffer.values().stream()
                        .filter(ProducerQueue::isReady)
                        .map(ProducerQueue::getBlock)
                        .collect(Collectors.toSet());
                if (musicStream.isReady()) {
                    ProducerQueue.AudioDataBlock musicBlock = musicStream.getBlock();
                    for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                        musicBlock.getAudioData()[i] = (short) (0.05 * musicBlock.getAudioData()[i]);
                    }
                    blocks.add(musicBlock);
                }
                if (blocks.size() == 0) {
                    continue;
                }
                for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                    int j = i;
                    blocks.forEach(block -> temp[j] += block.getAudioData()[j]);
                    mixedData[i] = (short) temp[i];
                }
                player.writeSamples(mixedData, 0, mixedData.length);
                voiceDataBuffer.values().removeIf(Predicate.not(ProducerQueue::hasData));
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

    public boolean isPlayingMusic() {
        return musicStream.hasData();
    }

    public void receiveVoiceData(UUID userId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(userId);
        short[] receivedData = AudioManager.toShort(voiceData, true);

        synchronized (this) {
            if (!voiceDataBuffer.containsKey(user)) {
                voiceDataBuffer.put(user, new ProducerQueue(userId, timestamp, receivedData));
            } else {
                voiceDataBuffer.get(user).addData(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    public void receiveMusicStream(LocalDateTime timestamp, byte[] musicData) {
        if (!isRunning) {
            return;
        }
        short[] receivedData = AudioManager.toShort(musicData, false);

        synchronized (this) {
            musicStream.addData(timestamp, receivedData);
            notifyAll();
        }
    }

    private static class ProducerQueue {

        private final UUID producerId;
        private final Queue<AudioDataBlock> audioDataQueue;
        private LocalDateTime lastTimeReceived;
        private boolean ready;

        public ProducerQueue() {
            this.producerId = null;
            this.audioDataQueue = new LinkedBlockingQueue<>();
        }

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
            // Hashmap entfernt wird, wenn in einer Iteration keine Daten vorhanden sind.
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
