package view2;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.*;

/**
 * Eine Klasse, durch welche das Verwenden von Emojis in der Anwendung ermöglicht wird.
 * Der Code in dieser Klasse basiert auf: https://github.com/DavidPDev/gdx-EmojiSupport
 */
public class ChatiEmojiManager {

    private static final char START_CHAR = 0xB000; // Selten genutzter Character-Bereich

    private final Map<Integer, Emoji> emojis;

    /**
     * Erzeugt eine neue Instanz des ChatiEmojiManager.
     */
    public ChatiEmojiManager() {
        this.emojis = new HashMap<>();

        // Ermittle alle Emoji-Texturen und füge Emojis in einer Datenstruktur ein.
        // Emojis sind im TexturAtlas nach dem Schema emoji_codePoint benannt, wobei codePoint dem Unicode-Codepoint
        // entspricht.
        Array<TextureAtlas.AtlasRegion> atlasRegions = Chati.CHATI.getAtlas().getRegions();
        for (int i = 0; i < atlasRegions.size; i++) {
            String regionName = atlasRegions.get(i).name;
            try {
                if (regionName.matches("^emoji_.*")) {
                    int codePoint = Integer.parseInt(regionName.split("_")[1], 16);
                    emojis.put(codePoint, new Emoji(codePoint, (char) (START_CHAR + i), atlasRegions.get(i)));
                }
            }
            catch (NumberFormatException e) {
                throw new GdxRuntimeException("Invalid emoji (Not valid Hex code): " + atlasRegions.get(i).name, e);
            }
        }

        // Füge Glyphen der Emojis zu den Fonts hinzu.
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-title"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-label"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-button"));
    }

    /**
     * Ersetzt Unicode-Codepoints durch Character von Emojis in einem übergebenen Text.
     * @param text Text, in dem Codepoints durch Emojis ersetzt werden sollen.
     * @return Text, in dem Codepoints durch Emojis ersetzt wurden.
     */
    public String insertEmojis(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            boolean isSurrogate = (c >= '\uD800' && c <= '\uDFFF');
            boolean isIgnorable = (c >= '\uFE00' && c <= '\uFE0F');
            Emoji emoji = emojis.get(text.codePointAt(i));
            if (emoji != null) {
                stringBuilder.append(emoji.getChar());
            } else if (!isSurrogate && !isIgnorable) {
                stringBuilder.append(c);
            }
            i += isSurrogate ? 2 : 1;
        }
        return stringBuilder.toString();
    }

    /**
     * Gibt den, beziehungsweise im Falle eines Surrogates die beiden Character zurück, die den Emoji des übergebenen
     * Unicode-Codepoints repräsentieren.
     * @param codePoint Unicode-Codepoint des zurückzugebenden Emojis.
     * @return Der String mit den enthaltenen Charactern des Emojis.
     */
    public String getEmojiChar(int codePoint) {
        return new StringBuilder().append(emojis.get(codePoint).getChar()).toString();
    }

    /**
     * Gibt alle in der Anwendung bekannten Emojis zurück.
     * @return Alle Emojis
     */
    public Collection<Emoji> getEmojis() {
        return Collections.unmodifiableCollection(emojis.values());
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
    public static class Emoji {

        private final int codePoint;
        private final char character;
        private final TextureRegion region;

        /**
         * Erzeugt eine neue Instanz eines Emoji.
         * @param codePoint Unicode-Codepoint des Emoji.
         * @param character Character, der diesen Emoji repräsentiert.
         * @param region Textur-Region des Emoji.
         */
        public Emoji(int codePoint, char character, TextureRegion region) {
            this.codePoint = codePoint;
            this.character = character;
            this.region = region;
        }

        /**
         * Gibt den Unicode-Codepoint des Emoji zurück.
         * @return Unicode-Codepoint
         */
        public int getCodePoint() {
            return codePoint;
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
