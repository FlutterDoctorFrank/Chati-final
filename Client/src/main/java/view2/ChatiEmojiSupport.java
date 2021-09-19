package view2;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.*;

public class ChatiEmojiSupport {

    private static final char START_CHAR = 0xB000; // Startbereich zum Einsetzen der Character (Selten genutzter Bereich)

    private final Map<Integer, Emoji> emojis;

    public ChatiEmojiSupport() {
        this.emojis = new HashMap<>();

        Array<TextureAtlas.AtlasRegion> atlasRegions = Chati.CHATI.getAtlas().getRegions();
        for (int i = 0; i < atlasRegions.size; i++) {
            String regionName = atlasRegions.get(i).name;
            try {
                if (regionName.matches("^emoji_.*")) {
                    int codepoint = Integer.parseInt(regionName.split("_")[1], 16);
                    emojis.put(codepoint, new Emoji(codepoint, i, atlasRegions.get(i)));
                }
            }
            catch (NumberFormatException e) {
                throw new GdxRuntimeException("Invalid emoji (Not valid Hex code): " + atlasRegions.get(i).name, e);
            }
        }

        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-title"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-label"));
        addEmojisToFont(Chati.CHATI.getSkin().getFont("font-button"));
    }

    public String filterEmojis(String str) { // Translates str replacing emojis with its 0xB000+i index
        if (str == null || str.length() == 0) {
            return str;
        }
        int length = str.length();
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (i < length) {
            char c = str.charAt(i);
            boolean isCharSurrogate = (c >= '\uD800' && c <= '\uDFFF'); // Special 2-chars surrogates (uses two chars)
            boolean isCharVariations = (c >= '\uFE00' && c <= '\uFE0F'); // Special char for skin-variations (omit)
            int codePoint = str.codePointAt(i);
            Emoji emoji = emojis.get(codePoint);
            if (emoji != null) {
                stringBuilder.append((char) (START_CHAR + emoji.index)); // Füge gefundenen Emoji hinzu.
            }
            else if (!isCharSurrogate && !isCharVariations) {
                stringBuilder.append(c); // Exclude special chars
            }
            i += isCharSurrogate ? 2 : 1; // Surrogate chars use 2 characters
        }
        return stringBuilder.toString();
    }

    public Collection<Emoji> getEmojis() {
        return Collections.unmodifiableCollection(emojis.values());
    }

    public String getEmojiString(int codePoint) {
        return new StringBuilder().append((char) (START_CHAR + emojis.get(codePoint).index)).toString();
    }

    private void addEmojisToFont(BitmapFont font) {
        Array<TextureRegion> fontRegions = font.getRegions();
        int page = fontRegions.size;
        Texture texture = Chati.CHATI.getAtlas().getRegions().get(0).getTexture();
        fontRegions.add(new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight()));

        int size = (int) (font.getData().lineHeight/font.getData().scaleY);
        for (Emoji emoji : emojis.values()) {
            char emojiChar = (char) (START_CHAR + emoji.index);
            BitmapFont.Glyph emojiGlyph = font.getData().getGlyph(emojiChar);
            if (emojiGlyph == null) { // Füge neuen Glyph für Emoji hinzu, sofern er noch nicht existiert.
                emojiGlyph = new BitmapFont.Glyph();
                emojiGlyph.id = emojiChar;
                emojiGlyph.srcX = 0;
                emojiGlyph.srcY = 0;
                emojiGlyph.width = size;
                emojiGlyph.height = size;
                emojiGlyph.u = emoji.atlasRegion.getU();
                emojiGlyph.v = emoji.atlasRegion.getV2();
                emojiGlyph.u2 = emoji.atlasRegion.getU2();
                emojiGlyph.v2 = emoji.atlasRegion.getV();
                emojiGlyph.xoffset = 0;
                emojiGlyph.yoffset = -size;
                emojiGlyph.xadvance = size;
                emojiGlyph.kerning = null;
                emojiGlyph.fixedWidth = true;
                emojiGlyph.page = page;
                font.getData().setGlyph(emojiChar, emojiGlyph);
            }
        }
    }

    public static class Emoji {

        public final int codepoint;
        private final int index;
        public final TextureAtlas.AtlasRegion atlasRegion;

        public Emoji(int codepoint, int index, TextureAtlas.AtlasRegion atlasRegion) {
            this.codepoint = codepoint;
            this.index = index;
            this.atlasRegion = atlasRegion;
        }
    }
}
