package view2;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.*;

/**
 * Eine Klasse, durch welche das Verwenden von Emojis in der Anwendung ermöglicht wird.
 */
public class ChatiEmojiManager {

    private static final char START_CHAR = 0xB000; // Selten genutzter Character-Bereich

    private final Map<Integer, Emoji> emojis;

    /**
     * Erzeugt eine neue Instanz des ChatiEmojiManager.
     */
    public ChatiEmojiManager() {
        this.emojis = new TreeMap<>();

        // Ermittle alle Emoji-Texturen und füge Emojis in einer Datenstruktur ein.
        Array<TextureAtlas.AtlasRegion> atlasRegions = Chati.CHATI.getRegions("emoji");
        for (int i = 0; i < atlasRegions.size; i++) {
            emojis.put(i, new Emoji((char) (START_CHAR + i), atlasRegions.get(i)));
        }

        // Füge Glyphen der Emojis zu den Fonts hinzu.
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-title"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-label"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-button"));
    }

    public boolean isEmoji(char character) {
        return emojis.values().stream().anyMatch(emoji -> emoji.getChar() == character);
    }

    /**
     * Gibt alle in der Anwendung bekannten Emojis zurück.
     * @return Alle Emojis
     */
    public Map<Integer, Emoji> getEmojis() {
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
                emojiGlyph.srcX = 0;
                emojiGlyph.srcY = 0;
                emojiGlyph.width = size;
                emojiGlyph.height = size;
                emojiGlyph.u = emoji.getRegion().getU();
                emojiGlyph.v = emoji.getRegion().getV2();
                emojiGlyph.u2 = emoji.getRegion().getU2();
                emojiGlyph.v2 = emoji.getRegion().getV();
                emojiGlyph.xoffset = 0;
                emojiGlyph.yoffset = -size;
                emojiGlyph.xadvance = size;
                emojiGlyph.kerning = null;
                emojiGlyph.fixedWidth = true;
                emojiGlyph.page = page;
                font.getData().setGlyph(emoji.getChar(), emojiGlyph);
            }
        }
    }

    /**
     * Eine Klasse, welche Emojis repräsentiert.
     */
    public static class Emoji {

        private final char character;
        private final TextureRegion region;

        /**
         * Erzeugt eine neue Instanz eines Emoji.
         * @param character Character, der diesen Emoji repräsentiert.
         * @param region Textur-Region des Emoji.
         */
        public Emoji(char character, TextureRegion region) {
            this.character = character;
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
    }
}
