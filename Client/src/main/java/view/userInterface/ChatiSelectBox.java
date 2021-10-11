package view.userInterface;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import org.jetbrains.annotations.NotNull;
import view.Chati;
import java.util.function.Function;

/**
 * Eine Klasse, welche die in der Anwendung verwendeten SelectBoxen repräsentiert.
 */
public class ChatiSelectBox<T> extends SelectBox<T> {

    private final Function<T, String> mapper;

    /**
     * Erzeugt eine neue Instanz einer ChatiSelectBox.
     * @param mapper Die Funktion, die ein Objekt der Klasse T auf einen String mapped.
     */
    public ChatiSelectBox(@NotNull final Function<T, String> mapper) {
        super(Chati.CHATI.getSkin());

        this.mapper = mapper;
    }

    @Override
    protected String toString(@NotNull final T item) {
        return this.mapper.apply(item);
    }
}
