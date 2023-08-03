package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

//todo clean up all these spritebatches
public class DarknessBatch extends DrawBatch {
    private int indicesIndex = 0;
    protected short[] triangleArray;

    public DarknessBatch() {
        this(1000);
    }

    public DarknessBatch(int size) {
        int numVertices = size * 3;
        if (numVertices > (1 << 16)) {
            throw new IndexOutOfBoundsException();
        }

        mesh = new Mesh(vertexDataType, false, numVertices, numVertices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));

        vArray = new float[numVertices * 2];
        triangleArray = new short[numVertices * 3];

        shader = Shaders.darkness.getProgram();
    }

    public void draw(float x1, float y1, float x2, float y2, float x3, float y3) {
        vArray[vIndex] = x1;
        vArray[vIndex + 1] = y1;
        vArray[vIndex + 2] = x2;
        vArray[vIndex + 3] = y2;
        vArray[vIndex + 4] = x3;
        vArray[vIndex + 5] = y3;

        short pureVertIndex = (short) (vIndex / 2);

        triangleArray[indicesIndex] = pureVertIndex;
        triangleArray[indicesIndex + 1] = (short) (pureVertIndex + 1);
        triangleArray[indicesIndex + 2] = (short) (pureVertIndex + 2);

        vIndex += 6;
        indicesIndex += 3;
    }

    public void draw(float x1, float y1, float x2, float y2) {
        vArray[vIndex] = x1;
        vArray[vIndex + 1] = y1;
        vArray[vIndex + 2] = x1;
        vArray[vIndex + 3] = y2;
        vArray[vIndex + 4] = x2;
        vArray[vIndex + 5] = y2;
        vArray[vIndex + 6] = x2;
        vArray[vIndex + 7] = y1;

        short pureIndex = (short) (vIndex / 2);

        triangleArray[indicesIndex] = pureIndex;
        triangleArray[indicesIndex + 1] = (short) (pureIndex + 1);
        triangleArray[indicesIndex + 2] = (short) (pureIndex + 2);
        triangleArray[indicesIndex + 3] = pureIndex;
        triangleArray[indicesIndex + 4] = (short) (pureIndex + 2);
        triangleArray[indicesIndex + 5] = (short) (pureIndex + 3);

        vIndex += 8;
        indicesIndex += 6;
    }

    public void drawCShadowZero(float x1, float x2, float x3, float x4, float y1, float y2, boolean swap) {
        // warning: changing things here will break the other drawCShadow functions
        // Top row
        int s = swap ? 1 : 0;

        vArray[vIndex + s] = x1; // 0
        vArray[vIndex + 1 - s] = y1;
        vArray[vIndex + 2 + s] = x2; // 1
        vArray[vIndex + 3 - s] = y1;
        vArray[vIndex + 4 + s] = x3; // 2
        vArray[vIndex + 5 - s] = y1;
        vArray[vIndex + 6 + s] = x4; // 3
        vArray[vIndex + 7 - s] = y1;
        // Middle Row
        vArray[vIndex + 8 + s] = x2; // 4
        vArray[vIndex + 9 - s] = y2;
        vArray[vIndex + 10 + s] = x3; // 5
        vArray[vIndex + 11 - s] = y2;

        short vertdiv = (short) (vIndex / 2);
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

        vIndex += 12;
        indicesIndex += 12;
    }

    public void drawCShadowOne(float x1, float x2, float x3, float x4, float x5, float y1, float y2, float y3, boolean left, boolean swap) {
        //todo triangle merging
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);
        drawCShadowZero(x1, x2, x4, x5, y1, y2, swap);

        vArray[vIndex + s] = x3; // 6
        vArray[vIndex + 1 - s] = y2;
        vArray[vIndex + 2 + s] = x3; // 7
        vArray[vIndex + 3 - s] = y3;

        triangleArray[indicesIndex] = (short) (vertdiv + (left ? 4 : 5));
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 6);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 7);

        vIndex += 4;
        indicesIndex += 3;
    }

    public void drawCShadowTwo(float x1, float x2, float x3, float x4, float x5, float x6, float y1, float y2, float y3, boolean swap) {
        //todo triangle merging
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);
        drawCShadowZero(x1, x2, x5, x6, y1, y2, swap);

        vArray[vIndex + s] = x3; // 6
        vArray[vIndex + 1 - s] = y2;
        vArray[vIndex + 2 + s] = x3; // 7
        vArray[vIndex + 3 - s] = y3;
        vArray[vIndex + 4 + s] = x4; // 8
        vArray[vIndex + 5 - s] = y2;
        vArray[vIndex + 6 + s] = x4; // 9
        vArray[vIndex + 7 - s] = y3;


        triangleArray[indicesIndex] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 6);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 7);
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 8);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 9);

        vIndex += 8;
        indicesIndex += 6;
    }

    public void drawDShadowZero(float x2, float x3, float x4, float x5,
                                float y2, float y3, float y4, float y5, boolean swap) {
        // warning: changing things here will break the other drawDShadow functions
        int s = swap ? 1 : 0;

        vArray[vIndex + s] = x2; // 0
        vArray[vIndex - s + 1] = y2;
        vArray[vIndex + s + 2] = x3; // 1
        vArray[vIndex - s + 3] = y2;
        vArray[vIndex + s + 4] = x4; // 2
        vArray[vIndex - s + 5] = y4;
        vArray[vIndex + s + 6] = x5; // 3
        vArray[vIndex - s + 7] = y5;
        vArray[vIndex + s + 8] = x2; // 4
        vArray[vIndex - s + 9] = y3;

        short vertdiv = (short) (vIndex / 2);
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


        vIndex += 10;
        indicesIndex += 9;
    }

    public void drawDShadowOne(float x2, float x3, float x4, float x5,
                               float y2, float y3, float y4, float y5,
                               float f,
                               boolean swap, boolean left) {
        //todo triangle merging 025 034
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);
        drawDShadowZero(x2, x3, x4, x5, y2, y3, y4, y5, swap);

        vArray[vIndex + s] = left ? x2 : f; // 5
        vArray[vIndex - s + 1] = left ? f : y2;

        triangleArray[indicesIndex] = (short) (vertdiv + (left ? 1 : 4));
        triangleArray[indicesIndex + 1] = vertdiv;
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 5);

        vIndex += 2;
        indicesIndex += 3;
    }

    public void drawDShadowTwo(float x1, float x2, float x3, float x4, float x5,
                               float y1, float y2, float y3, float y4, float y5,
                               boolean swap) {
        //todo triangle merging 025 034
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);
        drawDShadowZero(x2, x3, x4, x5, y2, y3, y4, y5, swap);

        vArray[vIndex + s] = x2; // 5
        vArray[vIndex - s + 1] = y1;
        vArray[vIndex + s + 2] = x1; // 6
        vArray[vIndex - s + 3] = y2;

        triangleArray[indicesIndex] = vertdiv;
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 3] = vertdiv;
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 6);

        vIndex += 4;
        indicesIndex += 6;
    }

    @Override
    public void flush() {
        if (indicesIndex == 0) {
            return;
        }
        super.flush();
    }

    @Override
    public void internalFlush() {
        mesh.setIndices(triangleArray, 0, indicesIndex);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, indicesIndex);

        indicesIndex = 0;
    }
}
