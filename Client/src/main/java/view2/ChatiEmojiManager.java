package view2;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Eine Klasse, durch welche das Verwenden von Emojis in der Anwendung ermöglicht wird.
 */
public class ChatiEmojiManager {

    private static final char START_CHAR = 0xB000; // Selten genutzter Character-Bereich

    private final Map<Character, Emoji> emojis;

    /**
     * Erzeugt eine neue Instanz des ChatiEmojiManager.
     */
    public ChatiEmojiManager() {
        this.emojis = new TreeMap<>();

        // Ermittle alle Emoji-Texturen und füge Emojis in einer Datenstruktur ein.
        Array<TextureAtlas.AtlasRegion> atlasRegions = Chati.CHATI.getAtlas().getRegions();
        for (int i = 0; i < atlasRegions.size; i++) {
            String regionName = atlasRegions.get(i).name;
            if (regionName.matches("^emoji_.*")) {
                char character = (char) (START_CHAR + i);
                int unicode = Integer.parseInt(regionName.split("_")[1], 16);
                emojis.put(character, new Emoji(character, unicode, atlasRegions.get(i)));
            }
        }

        // Füge Glyphen der Emojis zu den Fonts hinzu.
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-title"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-label"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-button"));
    }

    public boolean isEmoji(char character) {
        return emojis.containsKey(character);
    }

    /**
     * Gibt alle in der Anwendung bekannten Emojis zurück.
     * @return Alle Emojis
     */
    public Map<Character, Emoji> getEmojis() {
        return Collections.unmodifiableMap(emojis);
    }

    /**
     * Fügt die Emojis zu einer Schrift hinzu.
     * @param font Schrift, in die die Emoji-Glyphen eingefügt werden sollen.
     */
    private void addEmojisToFont(BitmapFont font) {
        Array<TextureRegion> fontRegions = font.getRegions();
        int page = fontRegions.size;
        Texture texture = Chati.CHATI.getAtlas().getRegions().get(0).getTexture();
        fontRegions.add(new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight()));

        int size = (int) (font.getData().lineHeight / font.getData().scaleY);
        for (Emoji emoji : emojis.values()) {
            if (font.getData().getGlyph(emoji.getChar()) == null) {
                BitmapFont.Glyph emojiGlyph = new BitmapFont.Glyph();
                emojiGlyph.id = emoji.getChar();
                emojiGlyph.width = size;
                emojiGlyph.height = size;
                emojiGlyph.u = emoji.getRegion().getU();
                emojiGlyph.v = emoji.getRegion().getV2();
                emojiGlyph.u2 = emoji.getRegion().getU2();
                emojiGlyph.v2 = emoji.getRegion().getV();
                emojiGlyph.yoffset = -size;
                emojiGlyph.xadvance = size;
                emojiGlyph.page = page;
                font.getData().setGlyph(emoji.getChar(), emojiGlyph);
            }
        }
    }

    /**
     * Eine Klasse, welche Emojis repräsentiert.
     */
    public static class Emoji implements Comparable<Emoji> {

        private final char character;
        private final int unicode;
        private final TextureRegion region;

        /**
         * Erzeugt eine neue Instanz eines Emoji.
         * @param character Character, der diesen Emoji repräsentiert.
         * @param region Textur-Region des Emoji.
         */
        public Emoji(char character, int unicode, TextureRegion region) {
            this.character = character;
            this.unicode = unicode;
            this.region = region;
        }

        /**
         * Gibt den Character zurück, der diesen Emoji repräsentiert.
         * @return Character
         */
        public char getChar() {
            return character;
        }

        /**
         * Gibt die Textur-Region des Emoji zurück.
         * @return Textur-Region
         */
        public TextureRegion getRegion() {
            return region;
        }

        @Override
        public int compareTo(@NotNull ChatiEmojiManager.Emoji other) {
            return Integer.compare(this.unicode, other.unicode);
        }
    }
}
