package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FowBatch extends CachedIndexedDrawBatch {

    public FowBatch() {
        this(4000);
    }

    public FowBatch(int size) {
        super(size * 3, size * 3);

        int numVertices = size * 3;
        if (numVertices > (1 << 16)) {
            throw new IndexOutOfBoundsException();
        }

        mesh = new Mesh(VERTEX_DATA_TYPE, true, numVertices, numVertices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));

        shader = Shaders.fow.getProgram();
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

        iArray[iIndex] = vertdiv; // 0
        iArray[iIndex + 1] = (short) (vertdiv + 1);
        iArray[iIndex + 2] = (short) (vertdiv + 2);
        iArray[iIndex + 3] = (short) (vertdiv + 1); // 1
        iArray[iIndex + 4] = (short) (vertdiv + 2);
        iArray[iIndex + 5] = (short) (vertdiv + 3);

        vIndex += 8;
        iIndex += 6;
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

        iArray[iIndex] = vertdiv; // 0
        iArray[iIndex + 1] = (short) (vertdiv + 1);
        iArray[iIndex + 2] = (short) (vertdiv + 5);
        iArray[iIndex + 3] = (short) (vertdiv + 1); // 1
        iArray[iIndex + 4] = (short) (vertdiv + 2);
        iArray[iIndex + 5] = (short) (vertdiv + 3);
        iArray[iIndex + 6] = (short) (vertdiv + 2); // 2
        iArray[iIndex + 7] = (short) (vertdiv + 3);
        iArray[iIndex + 8] = (short) (vertdiv + 4);

        vIndex += 12;
        iIndex += 9;
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

        iArray[iIndex] = vertdiv; // 0
        iArray[iIndex + 1] = (short) (vertdiv + 1);
        iArray[iIndex + 2] = (short) (vertdiv + 6);
        iArray[iIndex + 3] = (short) (vertdiv + 1); // 1
        iArray[iIndex + 4] = (short) (vertdiv + 2);
        iArray[iIndex + 5] = (short) (vertdiv + 4);
        iArray[iIndex + 6] = (short) (vertdiv + 2); // 2
        iArray[iIndex + 7] = (short) (vertdiv + 4);
        iArray[iIndex + 8] = (short) (vertdiv + 5);
        iArray[iIndex + 9] = (short) (vertdiv + 2); // 3
        iArray[iIndex + 10] = (short) (vertdiv + 3);
        iArray[iIndex + 11] = (short) (vertdiv + 7);

        vIndex += 16;
        iIndex += 12;
    }

    private int diagVIndex;
    private boolean diagSwap;

    public void drawDiag(float x0, float x2, float x5, float x6,
                         float y0, float y2, float y5, float y6,
                         boolean swap) {
        int s = swap ? 1 : 0;
        short vertdiv = (short) (vIndex / 2);

        diagVIndex = vIndex;
        diagSwap = swap;

        vArray[vIndex + s] = x2; // 0
        vArray[vIndex - s + 1] = y0;
        vArray[vIndex + s + 2] = x5; // 1
        vArray[vIndex - s + 3] = y5;
        vArray[vIndex + s + 4] = x2; // 2
        vArray[vIndex - s + 5] = y2;
        vArray[vIndex + s + 6] = x6; // 3
        vArray[vIndex - s + 7] = y6;
        vArray[vIndex + s + 8] = x0; // 4
        vArray[vIndex - s + 9] = y2;

        iArray[iIndex] = vertdiv;
        iArray[iIndex + 1] = (short) (vertdiv + 1);
        iArray[iIndex + 2] = (short) (vertdiv + 2);

        iArray[iIndex + 3] = (short) (vertdiv + 1);
        iArray[iIndex + 4] = (short) (vertdiv + 2);
        iArray[iIndex + 5] = (short) (vertdiv + 3);

        iArray[iIndex + 6] = (short) (vertdiv + 2);
        iArray[iIndex + 7] = (short) (vertdiv + 3);
        iArray[iIndex + 8] = (short) (vertdiv + 4);

        vIndex += 10;
        iIndex += 9;
    }

    public void drawDiagLeftShallow(float x3, float y1) {
        int s = diagSwap ? 1 : 0;

        vArray[vIndex + s] = x3;
        vArray[vIndex - s + 1] = y1;

        vArray[diagVIndex - s + 1] = y1;

        iArray[iIndex] = (short) (diagVIndex / 2);
        iArray[iIndex + 1] = (short) (diagVIndex / 2 + 1);
        iArray[iIndex + 2] = (short) (vIndex / 2);

        vIndex += 2;
        iIndex += 3;
    }

    public void drawDiagLeftDeep(float x4) {
        int s = diagSwap ? 1 : 0;

        vArray[diagVIndex + s] = x4; // 5
        vArray[diagVIndex - s + 1] = vArray[diagVIndex - s + 5];
    }

    public void drawDiagRightShallow(float x1, float y3) {
        int s = diagSwap ? 1 : 0;

        vArray[vIndex + s] = x1;
        vArray[vIndex - s + 1] = y3;

        vArray[diagVIndex + s + 8] = x1;

        iArray[iIndex] = (short) (diagVIndex / 2 + 3);
        iArray[iIndex + 1] = (short) (diagVIndex / 2 + 4);
        iArray[iIndex + 2] = (short) (vIndex / 2);

        vIndex += 2;
        iIndex += 3;
    }

    public void drawDiagRightDeep(float y4) {
        int s = diagSwap ? 1 : 0;

        vArray[diagVIndex + s + 8] = vArray[diagVIndex + s + 4];
        vArray[diagVIndex - s + 9] = y4;
    }

    @Override
    public void internalFlush() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        mesh.render(shader, GL20.GL_TRIANGLES, 0, iIndex);
    }
}
