package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

import java.nio.Buffer;

public class TerrainSpriteBatch implements Batch {
    private static final float FLOATING_POINT_DEMOLISHER = 0.001f;

    private final Mesh mesh;
    private final float[] vertexArray;
    private boolean drawing = false;
    private int vertIndex = 0;

    private ShaderProgram shader;
    private final Matrix4 projectionMatrix;
    private final Matrix4 transformMatrix;
    private final Matrix4 combinedMatrix;

    public TerrainSpriteBatch() {
        this(10000);
    }

    public TerrainSpriteBatch(int size) {
        if (size * 4 > (1 << 16)) {
            throw new IndexOutOfBoundsException();
        }

        int maxVertices = size * 4, maxIndices = size * 6;

        Mesh.VertexDataType vertexDataType;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        } else {
            vertexDataType = Mesh.VertexDataType.VertexArray;
        }

        mesh = new Mesh(vertexDataType, false, maxVertices, maxIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));

        short[] indices = new short[maxIndices];
        for (int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
            indices[i] = (short) j;
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = (short) j;
        }
        mesh.setIndices(indices);

        vertexArray = new float[size * 16];

        shader = Shaders.normal.getProgram();
        projectionMatrix = new Matrix4();
        transformMatrix = new Matrix4();
        combinedMatrix = new Matrix4();
        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void begin() {
        Gdx.gl.glDepthMask(false);
        shader.bind();
        setupMatrices();
        drawing = true;
    }

    @Override
    public void end() {
        flush();
        Gdx.gl.glDepthMask(true);
        drawing = false;
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void setColor(Color tint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Color getColor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPackedColor(float packedColor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getPackedColor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(TextureRegion region, float x, float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;

        float tx1 = region.getU();
        float tx2 = region.getU2();
        float ty1 = region.getV();
        float ty2 = region.getV2();

        vertexArray[vertIndex] = x;
        vertexArray[vertIndex + 1] = y;
        vertexArray[vertIndex + 2] = tx1;
        vertexArray[vertIndex + 3] = ty2;

        vertexArray[vertIndex + 4] = x;
        vertexArray[vertIndex + 5] = y2;
        vertexArray[vertIndex + 6] = tx1;
        vertexArray[vertIndex + 7] = ty1;

        vertexArray[vertIndex + 8] = x2;
        vertexArray[vertIndex + 9] = y2;
        vertexArray[vertIndex + 10] = tx2;
        vertexArray[vertIndex + 11] = ty1;

        vertexArray[vertIndex + 12] = x2;
        vertexArray[vertIndex + 13] = y;
        vertexArray[vertIndex + 14] = tx2;
        vertexArray[vertIndex + 15] = ty2;
        vertIndex += 16;
    }

    public void draw(TextureRegion region, float x, float y, float width, float height, byte rotation) {
        if ((rotation & 1) > 0) {
            float intermediate = width;
            width = height;
            height = intermediate;
        }

        float x2 = x + width;
        float y2 = y + height;

        float tx1 = region.getU();
        float tx2 = region.getU2();
        float ty1 = region.getV();
        float ty2 = region.getV2();

        vertexArray[vertIndex] = x;
        vertexArray[vertIndex + 1] = y;
        vertexArray[vertIndex + 4] = x;
        vertexArray[vertIndex + 5] = y2;
        vertexArray[vertIndex + 8] = x2;
        vertexArray[vertIndex + 9] = y2;
        vertexArray[vertIndex + 12] = x2;
        vertexArray[vertIndex + 13] = y;

        short offset = (short) (rotation * 4);

        vertexArray[vertIndex + (2 + offset) % 16] = tx1;
        vertexArray[vertIndex + (3 + offset) % 16] = ty2;


        vertexArray[vertIndex + (6 + offset) % 16] = tx1;
        vertexArray[vertIndex + (7 + offset) % 16] = ty1;


        vertexArray[vertIndex + (10 + offset) % 16] = tx2;
        vertexArray[vertIndex + (11 + offset) % 16] = ty1;


        vertexArray[vertIndex + (14 + offset) % 16] = tx2;
        vertexArray[vertIndex + (15 + offset) % 16] = ty2;

        vertIndex += 16;
    }

    public void draw(TextureRegion region, int x, int y, byte rotation) {
        // We don't need to do +2x becasue inaccuracies will push it over
        draw(region, x * Tile.TILE_SIZE - FLOATING_POINT_DEMOLISHER,
                y * Tile.TILE_SIZE - FLOATING_POINT_DEMOLISHER,
                Tile.TILE_SIZE + FLOATING_POINT_DEMOLISHER,
                Tile.TILE_SIZE + FLOATING_POINT_DEMOLISHER, rotation);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        if (vertIndex == 0) {
            return;
        }

        Textures.getSpriteSheet().bind();
        mesh.setVertices(vertexArray, 0, vertIndex);

        Buffer indicesBuffer = mesh.getIndicesBuffer(true);
        indicesBuffer.position(0);
        indicesBuffer.limit(vertIndex - 1);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertIndex / 16 * 6);
        vertIndex = 0;
    }

    @Override
    public void disableBlending() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enableBlending() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlendFunction(int srcFunc, int dstFunc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlendSrcFunc() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlendDstFunc() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlendSrcFuncAlpha() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBlendDstFuncAlpha() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Matrix4 getProjectionMatrix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Matrix4 getTransformMatrix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProjectionMatrix(Matrix4 projection) {
        if (drawing) flush();
        projectionMatrix.set(projection);
        if (drawing) setupMatrices();
    }

    @Override
    public void setTransformMatrix(Matrix4 transform) {
        if (drawing) flush();
        transformMatrix.set(transform);
        if (drawing) setupMatrices();
    }

    private void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        shader.setUniformMatrix("u_projTrans", combinedMatrix);
        shader.setUniformi("u_texture", 0);
    }

    @Override
    public void setShader(ShaderProgram shader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShaderProgram getShader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBlendingEnabled() {
        return true;
    }

    @Override
    public boolean isDrawing() {
        return drawing;
    }

    @Override
    public void dispose() {

    }
}
