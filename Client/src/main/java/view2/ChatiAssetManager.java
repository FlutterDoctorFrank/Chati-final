package view2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class ChatiAssetManager {

    private static final boolean CREATE_ATLAS = false;

    public ChatiAssetManager() {
        if (CREATE_ATLAS) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.edgePadding = true;
            settings.paddingX = 2;
            settings.paddingY = 2;
            TexturePacker.process(settings, Gdx.files.internal("Client/src/main/resources/images").path(),
                    Gdx.files.internal("Client/src/main/resources/atlas").path(), "image_atlas");
        }


    }
}
