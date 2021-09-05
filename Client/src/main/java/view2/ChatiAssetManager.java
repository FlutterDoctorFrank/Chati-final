package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

        // Skin in die Warteschlange des AssetManager mit generierten Fonts als Parameter.
        assetManager.load(Gdx.files.internal("Client/src/main/resources/shadeui/uiskin.json").path(), Skin.class,
                new SkinLoader.SkinParameter(fontMap));

        // Lade Ressourcen.
        assetManager.finishLoading();
    }

    public TextureRegion getTextureRegion(String name) {
        return assetManager.get(Gdx.files.internal("Client/src/main/resources/atlas/image_atlas.atlas").path(),
                TextureAtlas.class).findRegion(name);
    }

    public Skin getSkin() {
        return assetManager.get(Gdx.files.internal("Client/src/main/resources/shadeui/uiskin.json").path(), Skin.class);
    }
}
