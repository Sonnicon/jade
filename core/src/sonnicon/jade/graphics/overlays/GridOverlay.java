package sonnicon.jade.graphics.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.Shaders;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.world.Tile;

public class GridOverlay implements IRenderable {

    private static final Vector3 TEMP_VEC = new Vector3();

    public GridOverlay() {
        Jade.renderer.addRenderable(this, Renderer.RenderLayer.overfow);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        TEMP_VEC.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        Jade.renderer.camera.unproject(TEMP_VEC);
        TEMP_VEC.x = Math.floorDiv((int) TEMP_VEC.x, Tile.TILE_SIZE) * Tile.TILE_SIZE;
        TEMP_VEC.y = Math.floorDiv((int) TEMP_VEC.y, Tile.TILE_SIZE) * Tile.TILE_SIZE;

        SpriteBatch b = (SpriteBatch) batch;

        ShaderProgram shader = Shaders.gridoverlay.getProgram();
        b.setShader(shader);
        shader.setUniform2fv("u_resolution", Jade.renderer.resolution, 0, 2);
        shader.setUniform2fv("u_cursor", new float[]{
                        Gdx.input.getX(),
                        Gdx.graphics.getHeight() - Gdx.input.getY()},
                0, 2);

        b.draw(Textures.atlasFindRegion("dark-10"),
                TEMP_VEC.x - Tile.TILE_SIZE,
                TEMP_VEC.y - Tile.TILE_SIZE,
                Tile.TILE_SIZE * 3, Tile.TILE_SIZE * 3);
        b.setShader(null);
    }
}
