package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import org.jetbrains.annotations.NotNull;

/**
 * Eine Klasse, in welcher sämtliche verwendete Ressourcen geladen werden und die der Anwendung den Zugriff auf diese
 * Ressourcen bereitstellt.
 */
public class ChatiAssetManager implements Disposable {

    private static final int TITLE_FONT_SIZE = 22;
    private static final int LABEL_FONT_SIZE = 23;
    private static final int BUTTON_FONT_SIZE = 21;

    private final AssetManager assetManager;

    /**
     * Erzeugt eine neue Instanz des ChatiAssetManager. Lädt alle in der Anwendung benötigten Ressourcen.
     */
    public ChatiAssetManager() {
        // Definiere Parameter für den TexturePacker.
        String texturePackerInput = Gdx.files.internal("Client/src/main/resources/textures").path();
        String texturePackerOutput = Gdx.files.internal("Client/src/main/resources/atlas").path();
        String texturePackerFileName = "image_atlas";
        TexturePacker.Settings texturePackerSettings = new TexturePacker.Settings();
        texturePackerSettings.maxWidth = 2048;
        texturePackerSettings.filterMin = Texture.TextureFilter.Linear;
        texturePackerSettings.filterMag = Texture.TextureFilter.Linear;
        texturePackerSettings.combineSubdirectories = true;
        texturePackerSettings.flattenPaths = true;

        // Erzeuge neuen TextureAtlas, falls Ressourcen nicht vorhanden sind oder modifiziert wurden.
        if (TexturePacker.isModified(texturePackerInput, texturePackerOutput, texturePackerFileName, texturePackerSettings)) {
            TexturePacker.process(texturePackerSettings, texturePackerInput, texturePackerOutput, texturePackerFileName);
        }

        // Erzeuge neuen AssetManager zum Laden von allen benötigten Ressourcen.
        this.assetManager = new AssetManager();

        // TextureAtlas in die Warteschlange des AssetManager.
        assetManager.load(Gdx.files.internal("atlas/image_atlas.atlas").path(), TextureAtlas.class);

        // Generiere Fonts mit Hilfe eines FreeTypeFontGenerators.
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.shadowColor = Color.DARK_GRAY;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.size = TITLE_FONT_SIZE;
        BitmapFont fontTitle = fontGenerator.generateFont(parameter);
        fontTitle.setUseIntegerPositions(false);
        parameter.size = LABEL_FONT_SIZE;
        BitmapFont fontLabel = fontGenerator.generateFont(parameter);
        fontLabel.setUseIntegerPositions(false);
        parameter.size = BUTTON_FONT_SIZE;
        BitmapFont fontButton = fontGenerator.generateFont(parameter);
        fontButton.setUseIntegerPositions(false);

        ObjectMap<String, Object> fontMap = new ObjectMap<>();
        fontMap.put("font-title", fontTitle);
        fontMap.put("font-label", fontLabel);
        fontMap.put("font-button", fontButton);

        // Skin in die Warteschlange des AssetManager mit ObjectMap der generierten Fonts als Parameter.
        assetManager.load(Gdx.files.internal("shadeui/uiskin.json").path(), Skin.class, new SkinLoader.SkinParameter(fontMap));

        // Lade Ressourcen.
        assetManager.finishLoading();
    }

    /**
     * Gibt den TextureAtlas zurück.
     * @return TextureAtlas
     */
    public @NotNull TextureAtlas getAtlas() {
        return assetManager.get(Gdx.files.internal("atlas/image_atlas.atlas").path(), TextureAtlas.class);
    }

    /**
     * Gibt eine Textur zurück.
     * @param name Name der Textur.
     * @return Die angeforderte Textur.
     */
    public @NotNull TextureRegionDrawable getDrawable(@NotNull final String name) {
        return new TextureRegionDrawable(getAtlas().findRegion(name));
    }

    /**
     * Gibt eine Menge von zusammengehöriger Texturen zurück.
     * @param name Name der Menge der zusammengehörigen Texturen.
     * @return Die angeforderte Menge von Texturen.
     */
    public @NotNull Array<TextureAtlas.AtlasRegion> getRegions(@NotNull final String name) {
        return getAtlas().findRegions(name);
    }

    /**
     * Gibt den in der View verwendeten Skin zurück.
     * @return Der verwendete Skin.
     */
    public @NotNull Skin getSkin() {
        return assetManager.get(Gdx.files.internal("shadeui/uiskin.json").path(), Skin.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
