package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.ObjectMap;

public class ChatiAssetManager {

    private static final int TITLE_FONT_SIZE = 22;
    private static final int LABEL_FONT_SIZE = 23;
    private static final int BUTTON_FONT_SIZE = 21;

    private final AssetManager assetManager;

    public ChatiAssetManager() {
        // Definiere Parameter für TexturePacker.
        String texturePackerInput = Gdx.files.internal("Client/src/main/resources/images").path();
        String texturePackerOutput = Gdx.files.internal("Client/src/main/resources/atlas").path();
        String texturePackerFileName = "image_atlas";
        TexturePacker.Settings texturePackerSettings = new TexturePacker.Settings();
        texturePackerSettings.edgePadding = true;
        texturePackerSettings.paddingX = 2;
        texturePackerSettings.paddingY = 2;

        // Erzeuge neuen TextureAtlas, falls Ressourcen modifiziert wurden.
        if (TexturePacker.isModified(texturePackerInput, texturePackerOutput, texturePackerFileName, texturePackerSettings)) {
            TexturePacker.process(texturePackerSettings, texturePackerInput, texturePackerOutput, texturePackerFileName);
        }

        this.assetManager = new AssetManager();

        // TextureAtlas in die Warteschlange des AssetManager.
        assetManager.load(Gdx.files.internal("Client/src/main/resources/atlas/image_atlas.atlas").path(), TextureAtlas.class);

        // Generiere Fonts.
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        BitmapFont fontTitle = fontGenerator.generateFont(getFontParameter(TITLE_FONT_SIZE));
        fontTitle.setUseIntegerPositions(false);
        BitmapFont fontLabel = fontGenerator.generateFont(getFontParameter(LABEL_FONT_SIZE));
        fontLabel.setUseIntegerPositions(false);
        BitmapFont fontButton = fontGenerator.generateFont(getFontParameter(BUTTON_FONT_SIZE));
        fontButton.setUseIntegerPositions(false);

        ObjectMap<String, Object> fontMap = new ObjectMap<>();
        fontMap.put("font-title", fontTitle);
        fontMap.put("font-label", fontLabel);
        fontMap.put("font-button", fontButton);

        // Skin in die Warteschlange des AssetManager mit generierten Fonts als Parameter.
        assetManager.load(Gdx.files.internal("Client/src/main/resources/shadeui/uiskin.json").path(), Skin.class,
                new SkinLoader.SkinParameter(fontMap));

        // Lade Ressourcen.
        assetManager.finishLoading();
    }

    private FreeTypeFontGenerator.FreeTypeFontParameter getFontParameter(int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.shadowColor = Color.DARK_GRAY;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        return parameter;
    }

    public TextureAtlas getImageAtlas() {
        return assetManager.get(Gdx.files.internal("Client/src/main/resources/atlas/image_atlas.atlas").path(),
                TextureAtlas.class);
    }

    public Skin getSkin() {
        return assetManager.get(Gdx.files.internal("Client/src/main/resources/shadeui/uiskin.json").path(), Skin.class);
    }
}
