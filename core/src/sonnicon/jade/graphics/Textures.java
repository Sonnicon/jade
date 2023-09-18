package sonnicon.jade.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

public class Textures {
    public static TextureAtlas atlas;
    private static final HashMap<String, TextureRegion> storedRegions = new HashMap<>();
    private static final HashMap<String, TextureRegionDrawable> storedDrawables = new HashMap<>();
    private static Texture spriteSheet;

    public static void init() {
        atlas = new TextureAtlas(Gdx.files.internal("sprites/pack.atlas"));
        spriteSheet = atlas.getTextures().first();
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        spriteSheet.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    public static TextureRegion atlasFindRegion(String name) {
        TextureRegion result = storedRegions.getOrDefault(name, null);
        if (result == null) {
            result = atlas.findRegion(name);
            storedRegions.put(name, result);
        }
        return result;
    }

    public static TextureRegionDrawable atlasFindDrawable(String name) {
        TextureRegionDrawable result = storedDrawables.getOrDefault(name, null);
        if (result == null) {
            result = new TextureRegionDrawable(atlasFindRegion(name));
            storedDrawables.put(name, result);
        }
        return result;
    }

    public static Texture getSpriteSheet() {
        return spriteSheet;
    }
}
