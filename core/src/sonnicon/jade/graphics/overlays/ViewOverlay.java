package sonnicon.jade.graphics.overlays;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import sonnicon.jade.graphics.draw.Shaders;
import sonnicon.jade.graphics.draw.SpriteBatch;

public class ViewOverlay {
    private FrameBuffer framebuffer;
    private final Mesh mesh;

    private int radius;
    public float x = 0, y = 0;
    private boolean invalidated = false;

    public ViewOverlay() {
        mesh = new Mesh(true, 4, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
        mesh.setVertices(new float[]{-1f, -1f, -1f, 1f, 1f, 1f, 1f, -1f});
    }

    public void setRadius(int radius) {
        if (this.radius == radius) {
            return;
        }

        this.radius = radius;
        this.invalidated = true;
    }

    private void create() {
        if (framebuffer != null) {
            framebuffer.dispose();
        }
        framebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, radius * 2, radius * 2, true);

        framebuffer.begin();

        ShaderProgram shader = Shaders.viewdist.getProgram();
        shader.bind();
        shader.setUniformf("u_radius", radius);
        mesh.render(Shaders.viewdist.getProgram(), GL20.GL_TRIANGLE_FAN);

        framebuffer.end();
        invalidated = false;
    }

    public void render(SpriteBatch batch) {
        if (radius <= 0) {
            return;
        }

        if (invalidated) {
            create();
        }

        batch.draw(framebuffer.getColorBufferTexture(), x - radius, y - radius, radius * 2, radius * 2);
    }
}
