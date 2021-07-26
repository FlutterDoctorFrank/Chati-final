package controller.network;

import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle, welche als Beobachter für die View dient.
 */
public interface ServerSender {

    /**
     * Teilt dem Controller mit, welche Aktionen und Informationen zum Server gesendet werden sollen.
     * Sendet das zur Aktion zugehörige Paket an den Server.
     * @param action Die Aktion die gesendet werden soll.
     * @param objects Die Informationen zu der sendenden Aktion.
     */
    void send(@NotNull final SendAction action, @NotNull final Object... objects);

    /**
     * Eine Enumeration über die verschiedenen Informationen, die von der View aus an den Server gesendet werden können.
     */
    enum SendAction {

        /**
         * Information, dass der Benutzer eingeloggt werden soll.
         */
        PROFILE_LOGIN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des ProfileAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass das Profil des Benutzers geändert werden soll.
         */
        PROFILE_CHANGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des ProfileAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass der Benutzer ausgeloggt werden soll.
         */
        PROFILE_LOGOUT {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des ProfileAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine Welt betreten werden soll.
         */
        WORLD_JOIN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des WorldAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass die aktuelle Welt verlassen wurde.
         */
        WORLD_LEAVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des WorldAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine neue Welt erstellt werden soll.
         */
        WORLD_CREATE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des WorldAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine vorhandene Welt gelöscht werden soll.
         */
        WORLD_DELETE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des WorldAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass sich die Position des Avatars geändert hat.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link Integer}, Die X-Koordinate<br>
         *     - {@code 1}: {@link Integer}, Die Y-Koordinate
         * </p>
         */
        AVATAR_MOVE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length == 2) {
                    try {
                        final int posX = (int) objects[0];
                        final int posY = (int) objects[1];

                        return new PacketAvatarMove(posX, posY);
                    } catch (ClassCastException ex) {
                        throw new IllegalArgumentException("Expected Integer and Integer, got "
                                + objects[0].getClass() + " and " + objects[1].getClass(), ex);
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass mit einem Kontext interagiert werden soll.
         */
        CONTEXT_INTERACT {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des ContextInteract-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass ein geöffnetes Menü geschlossen wurde.
         */
        MENU_ACTION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des MenuAction-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass im geöffneten Menü eine Option ausgewählt wurde.
         */
        MENU_OPTION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des MenuOption-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine Verwaltungsmöglichkeit auf einen anderen Benutzer ausgeführt werden soll.
         */
        USER_MANAGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des UserManage-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine Benachrichtigung gelöscht wurde.
         */
        NOTIFICATION_DELETE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des NotificationReply-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass auf eine Anfrage reagiert wurde.
         */
        NOTIFICATION_RESPONSE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des NotificationReply-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        },

        /**
         * Information, dass eine Chat-Nachricht gesendet werden soll.
         */
        MESSAGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                //TODO Verpackung des ChatMessage-Pakets implementieren.
                throw new UnsupportedOperationException("Not implemented yet");
            }
        };

        /**
         * Verarbeitet die übergebenen Objekte und verpackt die zu sendenden Informationen in ein Paket.
         * @param objects Die Informationsobjekte, welche die benötigten Informationen beinhalten.
         * @return Das erzeugte Paket.
         */
        protected abstract @NotNull Packet<?> getPacket(@NotNull final Object... objects);
    }
}
