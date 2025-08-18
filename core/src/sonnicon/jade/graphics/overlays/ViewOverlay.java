package sonnicon.jade.graphics.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.Shaders;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.World;

import java.util.Map;

public class ViewOverlay implements IRenderable, IDebuggable, IPositionMoving {
    private FrameBuffer framebuffer;
    private final Mesh mesh;
    private int radius = 0;
    protected float x = 0, y = 0;
    private boolean invalidated = false;

    public ViewOverlay(int radius) {
        mesh = new Mesh(true, 4, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
        mesh.setVertices(new float[]{-1f, -1f, -1f, 1f, 1f, 1f, 1f, -1f});

        Jade.renderer.addRenderable(this, RenderLayer.overfow);
        setRadius(radius);
    }

    public void setRadius(int radius) {
        if (this.radius == radius) {
            return;
        }

        this.radius = radius;
        this.invalidated = true;
    }

    @Override
    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean rotateTo(float degrees) {
        return false;
    }

    private void create() {
        end();
        if (framebuffer != null) {
            framebuffer.dispose();
        }
        framebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, radius * 2, radius * 2, false);
        framebuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        ShaderProgram shader = Shaders.viewdist.getProgram();
        shader.bind();
        shader.setUniformf("u_radius", radius);
        mesh.render(Shaders.viewdist.getProgram(), GL20.GL_TRIANGLE_FAN);

        framebuffer.end();
        invalidated = false;
        start();
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        //todo
        if (!isEnabled()) {
            return;
        }

        if (invalidated) {
            create();
        }

        ((SpriteBatch) batch).draw(
                framebuffer.getColorBufferTexture(),
                x - radius, y - radius,
                radius * 2, radius * 2);
    }

    public boolean isEnabled() {
        return radius > 0;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "radius", radius,
                "x", x,
                "y", y,
                "screen center", TEMP_VEC1,
                "overlay SW corner", TEMP_VEC2
        );
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public World getWorld() {
        //todo
        return Content.world;
    }

    private final Vector2 TEMP_VEC1 = new Vector2();
    private final Vector2 TEMP_VEC2 = new Vector2();

    public void start() {
        if (!isEnabled()) return;

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Center
        Entity player = PlayerControlComponent.getControlled().entity;
        TEMP_VEC1.set(player.getX(), player.getY());
        Jade.renderer.worldToScreen(TEMP_VEC1, TEMP_VEC1);

        // Edge
        TEMP_VEC2.set(player.getX() - radius, player.getY() - radius);
        Jade.renderer.worldToScreen(TEMP_VEC2, TEMP_VEC2);

        Gdx.gl.glScissor(
                (int) TEMP_VEC2.x + 2, (int) TEMP_VEC2.y + 2,
                (int) (TEMP_VEC1.x - TEMP_VEC2.x) * 2 - 4,
                (int) (TEMP_VEC1.y - TEMP_VEC2.y) * 2 - 4);
    }

    public void end() {
        if (!isEnabled()) return;
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    @Override
    public boolean culled(RenderLayer layer) {
        return false;
    }
}
