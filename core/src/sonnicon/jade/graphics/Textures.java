package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Textures {
    public static TextureAtlas atlas;
    private static final HashMap<String, TextureRegion> storedRegions = new HashMap<>();

    public static void init() {
        atlas = new TextureAtlas(Gdx.files.internal("sprites/sprites.atlas"));
    }

    public static TextureRegion atlasFindRegion(String name) {
        TextureRegion result = storedRegions.getOrDefault(name, null);
        if (result == null) {
            result = atlas.findRegion(name);
            storedRegions.put(name, result);
        }
        return result;
    }
}
