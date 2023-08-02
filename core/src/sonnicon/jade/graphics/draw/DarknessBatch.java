package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;

//todo clean up all these spritebatches
public class DarknessBatch implements Batch {
    private final Mesh mesh;
    private final float[] vertexArray;
    private final short[] triangleArray;
    private boolean drawing = false;
    private int vertIndex = 0, indicesIndex = 0;

    private ShaderProgram shader;
    private final Matrix4 projectionMatrix;
    private final Matrix4 transformMatrix;
    private final Matrix4 combinedMatrix;

    public DarknessBatch() {
        this(1000);
    }

    public DarknessBatch(int size) {
        int numVertices = size * 3;
        if (numVertices > (1 << 16)) {
            throw new IndexOutOfBoundsException();
        }

        Mesh.VertexDataType vertexDataType;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        } else {
            vertexDataType = Mesh.VertexDataType.VertexArray;
        }

        mesh = new Mesh(vertexDataType, false, numVertices, numVertices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));

        vertexArray = new float[numVertices * 2];
        triangleArray = new short[numVertices * 3];

        shader = Shaders.darkness.getProgram();
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
        throw new UnsupportedOperationException();
    }

    public void draw(TextureRegion region, float x, float y, float width, float height, byte rotation) {
        throw new UnsupportedOperationException();
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

    public void draw(float x1, float y1, float x2, float y2, float x3, float y3) {
        vertexArray[vertIndex] = x1;
        vertexArray[vertIndex + 1] = y1;
        vertexArray[vertIndex + 2] = x2;
        vertexArray[vertIndex + 3] = y2;
        vertexArray[vertIndex + 4] = x3;
        vertexArray[vertIndex + 5] = y3;

        short pureVertIndex = (short) (vertIndex / 2);

        triangleArray[indicesIndex] = pureVertIndex;
        triangleArray[indicesIndex + 1] = (short) (pureVertIndex + 1);
        triangleArray[indicesIndex + 2] = (short) (pureVertIndex + 2);

        vertIndex += 6;
        indicesIndex += 3;
    }

    public void draw(float x1, float y1, float x2, float y2) {
        vertexArray[vertIndex] = x1;
        vertexArray[vertIndex + 1] = y1;
        vertexArray[vertIndex + 2] = x1;
        vertexArray[vertIndex + 3] = y2;
        vertexArray[vertIndex + 4] = x2;
        vertexArray[vertIndex + 5] = y2;
        vertexArray[vertIndex + 6] = x2;
        vertexArray[vertIndex + 7] = y1;

        short pureIndex = (short) (vertIndex / 2);

        triangleArray[indicesIndex] = pureIndex;
        triangleArray[indicesIndex + 1] = (short) (pureIndex + 1);
        triangleArray[indicesIndex + 2] = (short) (pureIndex + 2);
        triangleArray[indicesIndex + 3] = pureIndex;
        triangleArray[indicesIndex + 4] = (short) (pureIndex + 2);
        triangleArray[indicesIndex + 5] = (short) (pureIndex + 3);

        vertIndex += 8;
        indicesIndex += 6;
    }

    public void drawCShadowZero(float x1, float x2, float x3, float x4, float y1, float y2, boolean swap) {
        // warning: changing things here will break the other drawCShadow functions
        // Top row
        int s = swap ? 1 : 0;

        vertexArray[vertIndex + s] = x1; // 0
        vertexArray[vertIndex + 1 - s] = y1;
        vertexArray[vertIndex + 2 + s] = x2; // 1
        vertexArray[vertIndex + 3 - s] = y1;
        vertexArray[vertIndex + 4 + s] = x3; // 2
        vertexArray[vertIndex + 5 - s] = y1;
        vertexArray[vertIndex + 6 + s] = x4; // 3
        vertexArray[vertIndex + 7 - s] = y1;
        // Middle Row
        vertexArray[vertIndex + 8 + s] = x2; // 4
        vertexArray[vertIndex + 9 - s] = y2;
        vertexArray[vertIndex + 10 + s] = x3; // 5
        vertexArray[vertIndex + 11 - s] = y2;

        short vertdiv = (short) (vertIndex / 2);
        // Left triangle
        triangleArray[indicesIndex] = vertdiv;
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 4);
        // Left rectangle triangle
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 4);
        // Right rectangle triangle
        triangleArray[indicesIndex + 6] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 7] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 8] = (short) (vertdiv + 5);
        // Right triangle
        triangleArray[indicesIndex + 9] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 10] = (short) (vertdiv + 3);
        triangleArray[indicesIndex + 11] = (short) (vertdiv + 5);

        vertIndex += 12;
        indicesIndex += 12;
    }

    public void drawCShadowOne(float x1, float x2, float x3, float x4, float x5, float y1, float y2, float y3, boolean left, boolean swap) {
        //todo triangle merging
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vertIndex / 2);
        drawCShadowZero(x1, x2, x4, x5, y1, y2, swap);

        vertexArray[vertIndex + s] = x3; // 6
        vertexArray[vertIndex + 1 - s] = y2;
        vertexArray[vertIndex + 2 + s] = x3; // 7
        vertexArray[vertIndex + 3 - s] = y3;

        triangleArray[indicesIndex] = (short) (vertdiv + (left ? 4 : 5));
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 6);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 7);

        vertIndex += 4;
        indicesIndex += 3;
    }

    public void drawCShadowTwo(float x1, float x2, float x3, float x4, float x5, float x6, float y1, float y2, float y3, boolean swap) {
        //todo triangle merging
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vertIndex / 2);
        drawCShadowZero(x1, x2, x5, x6, y1, y2, swap);

        vertexArray[vertIndex + s] = x3; // 6
        vertexArray[vertIndex + 1 - s] = y2;
        vertexArray[vertIndex + 2 + s] = x3; // 7
        vertexArray[vertIndex + 3 - s] = y3;
        vertexArray[vertIndex + 4 + s] = x4; // 8
        vertexArray[vertIndex + 5 - s] = y2;
        vertexArray[vertIndex + 6 + s] = x4; // 9
        vertexArray[vertIndex + 7 - s] = y3;


        triangleArray[indicesIndex] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 6);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 7);
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 8);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 9);

        vertIndex += 8;
        indicesIndex += 6;
    }

    public void drawDShadowZero(float x2, float x3, float x4, float x5,
                                float y2, float y3, float y4, float y5, boolean swap) {
        // warning: changing things here will break the other drawDShadow functions
        int s = swap ? 1 : 0;

        vertexArray[vertIndex + s] = x2; // 0
        vertexArray[vertIndex - s + 1] = y2;
        vertexArray[vertIndex + s + 2] = x3; // 1
        vertexArray[vertIndex - s + 3] = y2;
        vertexArray[vertIndex + s + 4] = x4; // 2
        vertexArray[vertIndex - s + 5] = y4;
        vertexArray[vertIndex + s + 6] = x5; // 3
        vertexArray[vertIndex - s + 7] = y5;
        vertexArray[vertIndex + s + 8] = x2; // 4
        vertexArray[vertIndex - s + 9] = y3;

        short vertdiv = (short) (vertIndex / 2);
        // Left triangle
        triangleArray[indicesIndex] = vertdiv;
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 2);
        // Middle triangle
        triangleArray[indicesIndex + 3] = vertdiv;
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 3);
        // Right triangle
        triangleArray[indicesIndex + 6] = vertdiv;
        triangleArray[indicesIndex + 7] = (short) (vertdiv + 3);
        triangleArray[indicesIndex + 8] = (short) (vertdiv + 4);


        vertIndex += 10;
        indicesIndex += 9;
    }

    public void drawDShadowOne(float x2, float x3, float x4, float x5,
                               float y2, float y3, float y4, float y5,
                               float f,
                               boolean swap, boolean left) {
        //todo triangle merging 025 034
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vertIndex / 2);
        drawDShadowZero(x2, x3, x4, x5, y2, y3, y4, y5, swap);

        vertexArray[vertIndex + s] = left ? x2 : f; // 5
        vertexArray[vertIndex - s + 1] = left ? f : y2;

        triangleArray[indicesIndex] = (short) (vertdiv + (left ? 1 : 4));
        triangleArray[indicesIndex + 1] = vertdiv;
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 5);

        vertIndex += 2;
        indicesIndex += 3;
    }

    public void drawDShadowTwo(float x1, float x2, float x3, float x4, float x5,
                               float y1, float y2, float y3, float y4, float y5,
                               boolean swap) {
        //todo triangle merging 025 034
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vertIndex / 2);
        drawDShadowZero(x2, x3, x4, x5, y2, y3, y4, y5, swap);

        vertexArray[vertIndex + s] = x2; // 5
        vertexArray[vertIndex - s + 1] = y1;
        vertexArray[vertIndex + s + 2] = x1; // 6
        vertexArray[vertIndex - s + 3] = y2;

        triangleArray[indicesIndex] = vertdiv;
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 3] = vertdiv;
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 6);

        vertIndex += 4;
        indicesIndex += 6;
    }

    @Override
    public void flush() {
        if (vertIndex + indicesIndex == 0) {
            return;
        }

        mesh.setVertices(vertexArray, 0, vertIndex);
        mesh.setIndices(triangleArray, 0, indicesIndex);

        Gdx.gl.glDisable(GL20.GL_BLEND);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, indicesIndex);
        vertIndex = 0;
        indicesIndex = 0;
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
