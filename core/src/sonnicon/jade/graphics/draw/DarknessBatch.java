package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

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

    public void drawCShadowZero(float x1, float x2, float x5, float x6,
                                float y1, float y2,
                                boolean swap) {
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);

        vArray[vIndex + s] = x1; // 0
        vArray[vIndex - s + 1] = y1;
        vArray[vIndex + s + 2] = x6; // 1
        vArray[vIndex - s + 3] = y1;
        vArray[vIndex + s + 4] = x2; // 2
        vArray[vIndex - s + 5] = y2;
        vArray[vIndex + s + 6] = x5; // 3
        vArray[vIndex - s + 7] = y2;

        triangleArray[indicesIndex] = vertdiv; // 0
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 1); // 1
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 3);

        vIndex += 8;
        indicesIndex += 6;
    }

    // x1, xf: shadowed side
    // x6: unshadowed side
    public void drawCShadowOne(float x1, float xf, float x5, float x6,
                               float y1, float y2, float y3,
                               boolean swap) {
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);

        vArray[vIndex + s] = x1; // 0
        vArray[vIndex - s + 1] = y1;
        vArray[vIndex + s + 2] = xf; // 1
        vArray[vIndex - s + 3] = y1;
        vArray[vIndex + s + 4] = x6; // 2
        vArray[vIndex - s + 5] = y1;
        vArray[vIndex + s + 6] = xf; // 3
        vArray[vIndex - s + 7] = y2;
        vArray[vIndex + s + 8] = x5; // 4
        vArray[vIndex - s + 9] = y2;
        vArray[vIndex + s + 10] = xf; // 5
        vArray[vIndex - s + 11] = y3;

        triangleArray[indicesIndex] = vertdiv; // 0
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 1); // 1
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 3);
        triangleArray[indicesIndex + 6] = (short) (vertdiv + 2); // 2
        triangleArray[indicesIndex + 7] = (short) (vertdiv + 3);
        triangleArray[indicesIndex + 8] = (short) (vertdiv + 4);

        vIndex += 12;
        indicesIndex += 9;
    }

    public void drawCShadowTwo(float x1, float x3, float x4, float x6,
                               float y1, float y2, float y3,
                               boolean swap) {
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);

        vArray[vIndex + s] = x1; // 0
        vArray[vIndex - s + 1] = y1;
        vArray[vIndex + s + 2] = x3; // 1
        vArray[vIndex - s + 3] = y1;
        vArray[vIndex + s + 4] = x4; // 2
        vArray[vIndex - s + 5] = y1;
        vArray[vIndex + s + 6] = x6; // 3
        vArray[vIndex - s + 7] = y1;
        vArray[vIndex + s + 8] = x3; // 4
        vArray[vIndex - s + 9] = y2;
        vArray[vIndex + s + 10] = x4; // 5
        vArray[vIndex - s + 11] = y2;
        vArray[vIndex + s + 12] = x3; // 6
        vArray[vIndex - s + 13] = y3;
        vArray[vIndex + s + 14] = x4; // 7
        vArray[vIndex - s + 15] = y3;

        triangleArray[indicesIndex] = vertdiv; // 0
        triangleArray[indicesIndex + 1] = (short) (vertdiv + 1);
        triangleArray[indicesIndex + 2] = (short) (vertdiv + 6);
        triangleArray[indicesIndex + 3] = (short) (vertdiv + 1); // 1
        triangleArray[indicesIndex + 4] = (short) (vertdiv + 2);
        triangleArray[indicesIndex + 5] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 6] = (short) (vertdiv + 2); // 2
        triangleArray[indicesIndex + 7] = (short) (vertdiv + 4);
        triangleArray[indicesIndex + 8] = (short) (vertdiv + 5);
        triangleArray[indicesIndex + 9] = (short) (vertdiv + 2); // 3
        triangleArray[indicesIndex + 10] = (short) (vertdiv + 3);
        triangleArray[indicesIndex + 11] = (short) (vertdiv + 7);

        vIndex += 16;
        indicesIndex += 12;
    }

    public void drawDShadowZero(float x2, float x3, float x4, float x5,
                                float y2, float y3, float y4, float y5,
                                boolean swap) {
        // warning: changing things here will break the other drawDShadow functions
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);

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
        int s = swap ? 1 : 0;
        int ovi = vIndex;

        if (left) {
            drawDShadowZero(x2, x2, x4, x5, y2, y3, y4, y5, swap);
            vArray[ovi - s + 3] = f;
        } else {
            drawDShadowZero(x2, x3, x4, x5, y2, y2, y4, y5, swap);
            vArray[ovi + s + 8] = f;
        }
    }

    public void drawDShadowTwo(float x1, float x2, float x4, float x5,
                               float y1, float y2, float y4, float y5,
                               boolean swap) {
        int s = swap ? 1 : 0;
        int ovi = vIndex;

        drawDShadowZero(x2, x2, x4, x5, y2, y2, y4, y5, swap);
        vArray[ovi - s + 3] = y1;
        vArray[ovi + s + 8] = x1;
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
