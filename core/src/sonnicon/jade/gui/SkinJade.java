package sonnicon.jade.gui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import sonnicon.jade.graphics.Textures;

public class SkinJade extends Skin {

    public SkinJade(FileHandle internal, TextureAtlas atlas) {
        super(internal, atlas);
    }

    @Override
    protected Json getJsonLoader(FileHandle skinFile) {
        Json json = super.getJsonLoader(skinFile);

        json.setSerializer(NinePatch.class, new Json.ReadOnlySerializer<NinePatch>() {
            @Override
            public NinePatch read(Json json, JsonValue jsonData, Class type) {
                String name = json.readValue("name", String.class, jsonData);

                int leftEdge = json.readValue("leftEdge", Integer.class, 0, jsonData);
                int rightEdge = json.readValue("rightEdge", Integer.class, 0, jsonData);
                int topEdge = json.readValue("topEdge", Integer.class, 0, jsonData);
                int bottomEdge = json.readValue("bottomEdge", Integer.class, 0, jsonData);

                int leftWidth = json.readValue("leftWidth", Integer.class, -1, jsonData);
                int rightWidth = json.readValue("rightWidth", Integer.class, -1, jsonData);
                int topHeight = json.readValue("topHeight", Integer.class, -1, jsonData);
                int bottomHeight = json.readValue("bottomHeight", Integer.class, -1, jsonData);

                NinePatch result = new NinePatch(Textures.atlasFindRegion(name), leftEdge, rightEdge, topEdge, bottomEdge);

                if (leftWidth >= 0) result.setLeftWidth(leftWidth);
                if (rightWidth >= 0) result.setRightWidth(rightWidth);
                if (topHeight >= 0) result.setTopHeight(topHeight);
                if (bottomHeight >= 0) result.setBottomHeight(bottomHeight);

                return result;
            }
        });

        return json;
    }
}
