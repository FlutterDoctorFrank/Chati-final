package controller.network;

import controller.network.protocol.Packet;
import controller.network.protocol.PacketAvatarMove;
import controller.network.protocol.PacketInContextInteract;
import controller.network.protocol.PacketInUserManage;
import controller.network.protocol.PacketProfileAction;
import controller.network.protocol.PacketWorldAction;
import model.User.AdministrativeAction;
import model.context.ContextID;
import model.context.spatial.SpatialMap;
import model.user.Avatar;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link String}, der Benutzername des anzumeldenden Benutzers<br>
         *     - {@code 1}: {@link String}, das Passwort des anzumeldenden Benutzers<br>
         *     - {@code 2}: {@link Boolean}, true, wenn der Benutzer registriert werden soll, ansonsten false
         * </p>
         */
        PROFILE_LOGIN {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 3) {
                    if (objects[0] instanceof String && objects[1] instanceof String && objects[2] instanceof Boolean) {
                        // Sende Paket zum anmelden oder registrieren.
                        return new PacketProfileAction((String) objects[0], (String) objects[1], (boolean) objects[2]);
                    }

                    throw new IllegalArgumentException("Expected String, String and Boolean, got "
                            + objects[0].getClass() + ", " + objects[1].getClass() + " and " + objects[2].getClass());
                } else {
                    throw new IllegalArgumentException("Expected Array size of 3, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass das Profil des Benutzers geändert werden soll.
         * <p>
         *     Zum Ändern des Passworts wird folgendes Objekt Array erwartet:<br>
         *     - {@code 0}: {@link String}, das aktuelle Password des Benutzers<br>
         *     - {@code 1}: {@link String}, das neue Password für den Benutzer
         * </p>
         * <p>
         *     Zum Ändern des Avatars wird folgendes Objekt Array erwartet:<br>
         *     - {@code 0}: {@link Avatar}, der neue Avatar für den Benutzer
         * </p>
         */
        PROFILE_CHANGE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length == 2) {
                    if (objects[0] instanceof String && objects[1] instanceof String) {
                        // Sende Paket zum Ändern des Passworts.
                        return new PacketProfileAction((String) objects[0], (String) objects[1]);
                    }

                    throw new IllegalArgumentException("Expected String and String, got "
                            + objects[0].getClass() + ", " + objects[1].getClass());
                } else if (objects.length == 1) {
                    if (objects[0] instanceof Avatar) {
                        // Sende Paket zum Ändern des Avatars.
                        return new PacketProfileAction((Avatar) objects[0]);
                    }

                    throw new IllegalArgumentException("Expected Avatar, got " + objects[0].getClass());
                } else {
                    throw new IllegalArgumentException("Expected Array size of 1 to 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass der Benutzer ausgeloggt oder gelöscht werden soll.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link String}, das aktuelle Password wenn der Benutzer gelöscht werden soll, ansonsten ein leerer String<br>
         *     - {@code 1}: {@link Boolean}, true, wenn der Benutzer gelöscht werden soll, ansonsten false
         * </p>
         */
        PROFILE_LOGOUT {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 2) {
                    if (objects[0] instanceof String && objects[1] instanceof Boolean) {
                        return new PacketProfileAction((String) objects[0], (boolean) objects[1]);
                    } else {
                        throw new IllegalArgumentException("Expected String and Boolean, got "
                                + objects[0].getClass() + " and " + objects[1].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass eine Welt betreten oder verlassen werden soll.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link ContextID}, die Kontext-ID der Welt die betreten oder verlassen werden soll<br>
         *     - {@code 1}: {@link Boolean}, true, wenn die Welt betreten und false wenn die Welt verlassen werden soll
         * </p>
         */
        WORLD_ACTION {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 2) {
                    if (objects[0] instanceof ContextID && objects[1] instanceof Boolean) {
                        final ContextID contextId = (ContextID) objects[0];
                        final boolean join = (boolean) objects[1];

                        return new PacketWorldAction(join ? PacketWorldAction.Action.JOIN : PacketWorldAction.Action.LEAVE, contextId);
                    } else {
                        throw new IllegalArgumentException("Expected ContextID and Boolean, got "
                                + objects[0].getClass() + " and " + objects[1].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass eine neue Welt erstellt werden soll.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link SpatialMap}, die Karte, die die zu erstellende Welt besitzen soll.<br>
         *     - {@code 1}: {@link String}, der Name der zu erstellenden Welt.
         * </p>
         */
        WORLD_CREATE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 2) {
                    if (objects[0] instanceof SpatialMap && objects[1] instanceof String) {
                        return new PacketWorldAction((SpatialMap) objects[0], (String) objects[1]);
                    } else {
                        throw new IllegalArgumentException("Expected SpatialMap and String, got "
                                + objects[0].getClass() + " and " + objects[1].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass eine vorhandene Welt gelöscht werden soll.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link ContextID}, die Kontext-ID der Welt, die gelöscht werden soll.
         * </p>
         */
        WORLD_DELETE {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 1) {
                    if (objects[0] instanceof ContextID) {
                        return new PacketWorldAction(PacketWorldAction.Action.DELETE, (ContextID) objects[0]);
                    } else {
                        throw new IllegalArgumentException("Expected ContextID, got " + objects[0].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 1, got " + objects.length);
                }
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
                    if (objects[0] instanceof Integer && objects[1] instanceof Integer) {
                        final int posX = (int) objects[0];
                        final int posY = (int) objects[1];

                        return new PacketAvatarMove(posX, posY);
                    } else {
                        throw new IllegalArgumentException("Expected Integer and Integer, got "
                                + objects[0].getClass() + " and " + objects[1].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 2, got " + objects.length);
                }
            }
        },

        /**
         * Information, dass mit einem Kontext interagiert werden soll.
         * <p>
         *     Erwartet als Objekt Array die Klassen:<br>
         *     - {@code 0}: {@link ContextID}, Die ID des Kontexts, mit dem interagiert wird.
         * </p>
         */
        CONTEXT_INTERACT {
            @Override
            protected @NotNull Packet<?> getPacket(@NotNull final Object... objects) {
                if (objects.length != 1) {
                    if (objects[0] instanceof ContextID) {
                        return new PacketInContextInteract((ContextID) objects[0]);
                    } else {
                        throw new IllegalArgumentException("Expected ContextID, got " + objects[0].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 1, got " + objects.length);
                }
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
                if (objects.length != 3) {
                    if (objects[0] instanceof UUID && objects[1] instanceof AdministrativeAction) {
                        try {
                            PacketInUserManage.Action action = PacketInUserManage.Action.valueOf(((AdministrativeAction) objects[1]).name());

                            return new PacketInUserManage((UUID) objects[0], action, (String) objects[2]);
                        } catch (IllegalArgumentException ex) {
                            throw new IllegalStateException("Failed to provide corresponding administrative-action", ex);
                        }
                    } else {
                        throw new IllegalArgumentException("Expected UUID, AdministrativeAction and String, got "
                                + objects[0].getClass() + "," + objects[1].getClass() + " and " + objects[2].getClass());
                    }
                } else {
                    throw new IllegalArgumentException("Expected Array size of 3, got " + objects.length);
                }
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
