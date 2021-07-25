package model;

import org.jetbrains.annotations.NotNull;

/**
 * Eine Schnittstelle, welche Methoden für Ressourcen der Anwendung bereitstellt.
 */
public interface Resource {

    /**
     * Gibt den Namen der Ressource zurück.
     * @return Der Name der Ressource.
     */
    @NotNull String getName();

    /**
     * Gibt den absoluten Pfad zu der Ressource zurück.
     * @return Der Pfad der Ressource.
     */
    @NotNull String getPath();

}
