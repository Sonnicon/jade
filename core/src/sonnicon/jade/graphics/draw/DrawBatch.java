package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public abstract class DrawBatch implements GraphicsBatch {
    protected static final float PIXEL_FIXER = 0.001f;
    protected static final float PIXEL_FIXER_XL = PIXEL_FIXER * 2;

    protected Mesh mesh;
    protected ShaderProgram shader;

    protected float[] vArray;
    protected int vIndex = 0;

    protected final Matrix4 projectionMatrix = new Matrix4();
    protected final Matrix4 transformMatrix = new Matrix4();
    public Matrix4 combinedMatrix = new Matrix4();

    protected static final Mesh.VertexDataType vertexDataType;

    static {
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        } else {
            vertexDataType = Mesh.VertexDataType.VertexArray;
        }
    }

    public DrawBatch() {
        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader = Shaders.normal.getProgram();
    }

    public void flush() {
        if (vIndex == 0) {
            return;
        }

        Gdx.gl.glDepthMask(false);

        shader.bind();
        setupExtra();
        mesh.setVertices(vArray, 0, vIndex);

        internalFlush();

        Gdx.gl.glDepthMask(true);
        vIndex = 0;
    }

    protected abstract void internalFlush();

    protected void setupExtra() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        shader.setUniformMatrix("u_projTrans", combinedMatrix);
    }

    @Override
    public void setProjectionMatrix(Matrix4 projection) {
        projectionMatrix.set(projection);
    }

    @Override
    public void setTransformMatrix(Matrix4 transform) {
        transformMatrix.set(transform);
    }
}
