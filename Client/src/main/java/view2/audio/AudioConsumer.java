package view2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.utils.Disposable;
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

/**
 * Eine Klasse, durch welche das Mischen und Abspielen empfangener Audiodaten realisiert wird.
 */
public class AudioConsumer implements Disposable {

    private static final int MIN_PACKETS = 6;
    private static final int MAX_PACKETS = 30;

    private final AudioDevice player;
    private final Map<IUserView, ProducerQueue> voiceDataBuffer;
    private final ProducerQueue musicStream;

    private float musicVolume;
    private float voiceVolume;
    private boolean isRunning;

    /**
     * Erzeugt eine neue Instanz des AudioConsumer.
     */
    public AudioConsumer() {
        this.player = Gdx.audio.newAudioDevice(AudioManager.SAMPLE_RATE, AudioManager.MONO);
        this.voiceDataBuffer = new ConcurrentHashMap<>();
        this.musicStream = new ProducerQueue();
    }

    /**
     * Startet einen Thread zum Mischen und Abspielen empfangener Audiodaten, sofern nicht bereits einer läuft.
     */
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
                    while (voiceDataBuffer.isEmpty() && !musicStream.isReady()) {
                        try {
                            if (!isRunning) {
                                return;
                            }
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                setVoiceVolume();
                setMusicVolume();

                int[] temp = new int[AudioManager.BLOCK_SIZE];
                // Mische das oberste Element aller Warteschlangen pro Sender und des Musikstreams zusammen.
                final Set<ProducerQueue.AudioDataBlock> blocks = voiceDataBuffer.values().stream()
                        .filter(ProducerQueue::isReady)
                        .map(ProducerQueue::getBlock)
                        .collect(Collectors.toSet());
                for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                    int j = i;
                    blocks.forEach(block -> temp[j] += block.getAudioData()[j]);
                    temp[j] *= voiceVolume;
                }
                if (musicStream.isReady()) {
                    ProducerQueue.AudioDataBlock musicBlock = musicStream.getBlock();
                    for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                        musicBlock.getAudioData()[i] *= musicVolume;
                        temp[i] += musicBlock.getAudioData()[i];
                    }
                }
                for (int i = 0; i < AudioManager.BLOCK_SIZE; i++) {
                    mixedData[i] = (short) (temp[i] > Short.MAX_VALUE ? Short.MAX_VALUE :
                            (temp[i] < Short.MIN_VALUE ? Short.MIN_VALUE : temp[i]));
                }
                player.writeSamples(mixedData, 0, mixedData.length);
                voiceDataBuffer.values().removeIf(Predicate.not(ProducerQueue::hasData));
            }
            this.voiceDataBuffer.clear();
            this.musicStream.audioDataQueue.clear();
        });
        mixAndPlaybackThread.setDaemon(true);
        mixAndPlaybackThread.start();
    }

    /**
     * Stoppt den gerade laufenden Thread zum Mischen und Abspielen von Audiodaten.
     */
    public synchronized void stop() {
        isRunning = false;
        notifyAll();
    }

    /**
     * Gibt zurück, ob gerade ein Thread zum Mischen und Abspielen von Audiodaten aktiv ist.
     * @return true, wenn ein Thread aktiv ist, sonst false.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gibt zurück, ob gerade Daten eines Musikstreams abgespielt werden.
     * @return true, wenn Musikdaten abgespielt werden, sonst false.
     */
    public boolean isPlayingMusic() {
        return musicStream.hasData();
    }

    /**
     * Reiht empfangene Sprachdaten in die Warteschlange der abzuspielenden Daten ein.
     * @param senderId ID des sendenden Benutzers.
     * @param timestamp Zeitstempel der Sprachdaten.
     * @param voiceData Abzuspielende Sprachdaten.
     * @throws UserNotFoundException falls kein Benutzer mit der ID gefunden wurde.
     */
    public void receiveVoiceData(UUID senderId, LocalDateTime timestamp, byte[] voiceData) throws UserNotFoundException {
        if (!isRunning) {
            return;
        }
        IUserView user = Chati.CHATI.getUserManager().getExternUserView(senderId);
        short[] receivedData = AudioManager.toShort(voiceData, true);

        synchronized (this) {
            if (!voiceDataBuffer.containsKey(user)) {
                voiceDataBuffer.put(user, new ProducerQueue(senderId, timestamp, receivedData));
            } else {
                voiceDataBuffer.get(user).addData(timestamp, receivedData);
            }
            notifyAll();
        }
    }

    /**
     * Reiht empfangene Daten eines Musikstreams in die Warteschlange der abzuspielenden Daten ein.
     * @param timestamp Zeitstempel der Musikstreamdaten.
     * @param musicData Abzuspielende Musikdaten.
     */
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

    @Override
    public void dispose() {
        player.dispose();
    }

    /**
     * Setzt die Lautstärke der abzuspielenden Sprachdaten anhand der Einstellungen von dieser und der Gesamtlautstärke.
     */
    private void setVoiceVolume() {
        voiceVolume = Chati.CHATI.getPreferences().isSoundOn() ?
                (float) Math.sqrt(Chati.CHATI.getPreferences().getTotalVolume() *
                Chati.CHATI.getPreferences().getVoiceVolume()) : 0;
    }

    /**
     * Setzt die Lautstärke der abzuspielenden Musikdaten anhand der Einstellungen von dieser und der Gesamtlautstärke.
     */
    private void setMusicVolume() {
        musicVolume = Chati.CHATI.getPreferences().isSoundOn() ?
                (float) (0.25 * Math.sqrt(Chati.CHATI.getPreferences().getTotalVolume() *
                Chati.CHATI.getPreferences().getMusicVolume())) : 0;
    }

    /**
     * Eine Klasse, welche eine Warteschlange repräsentiert, in der die Daten eines Senders eingereiht werden.
     */
    private static class ProducerQueue {

        private final UUID producerId;
        private final Queue<AudioDataBlock> audioDataQueue;
        private LocalDateTime lastTimeReceived;
        private boolean ready;

        /**
         * Erzeugt eine neue Instanz eines ProducerQueue. Wird für das Einreihen von Daten eines Musikstreams verwendet.
         */
        public ProducerQueue() {
            this.producerId = null;
            this.audioDataQueue = new LinkedBlockingQueue<>();
        }

        /**
         * Erzeugt eine neue Instanz eines ProducerQueue. Wird für das Einreihen von Sprachdaten verwendet.
         * @param producerId ID des sendenden Benutzers.
         * @param timestamp Zeitstempel der Sprachdaten.
         * @param receivedData Erhaltene Sprachdaten.
         */
        public ProducerQueue(UUID producerId, LocalDateTime timestamp, short[] receivedData) {
            this.producerId = producerId;
            this.audioDataQueue = new LinkedBlockingQueue<>();
            addData(timestamp, receivedData);
        }

        /**
         * Reiht neue Daten in die Warteschlange ein.
         * @param timestamp Zeitstempel der empfangenen Daten.
         * @param audioData Abzuspielende Daten.
         */
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

        /**
         * Gibt zurück, ob die Warteschlange Daten enthält.
         * @return true, wenn sie Daten enthält, sonst false.
         */
        private boolean hasData() {
            return !audioDataQueue.isEmpty();
        }

        /**
         * Gibt zurück, ob die Warteschlange bereit zum Abspielen von Daten ist.
         * @return true, wenn sie bereit ist, sonst false.
         */
        private boolean isReady() {
            return hasData() && ready;
        }

        /**
         * Gibt die ID des zu dieser Warteschlange gehörigen Benutzers zurück.
         * @return ID des Benutzers.
         */
        public UUID getProducerId() {
            return producerId;
        }

        /**
         * Entfernt den aktuell abzuspielenden Block mit Audiodaten aus der Warteschlange und gibt diesen zurück.
         * @return Block mit aktuell abzuspielenden Audiodaten.
         */
        private AudioDataBlock getBlock() {
            return audioDataQueue.poll();
        }

        /**
         * Gibt den Zeitstempel zurück, an dem in dieser Warteschlange das letzte mal Daten empfangen wurde.
         * @return Zeitstempel der letzten empfangenen Daten.
         */
        private LocalDateTime getLastTimeReceived() {
            return lastTimeReceived;
        }

        /**
         * Eine Klasse, welche einen Block mit Audiodaten repräsentiert.
         */
        private static class AudioDataBlock {

            private final short[] audioData;

            /**
             * Erzeugt eine neue Instanz des AudioDataBlock.
             * @param audioData Enthaltene Audiodaten.
             */
            private AudioDataBlock(short[] audioData) {
                this.audioData = audioData;
            }

            /**
             * Gibt die enthaltenen Audiodaten zurück.
             * @return Enthaltene Audiodaten.
             */
            private short[] getAudioData() {
                return audioData;
            }
        }
    }
}
