package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import sonnicon.jade.graphics.Textures;

import java.nio.Buffer;

public class SideTerrainSpriteBatch extends CachedDrawBatch {
    public SideTerrainSpriteBatch() {
        this(10000);
    }

    public SideTerrainSpriteBatch(int size) {
        super(size * 56);

        int maxVertices = size * 4, maxIndices = size * 6;

        mesh = new Mesh(VERTEX_DATA_TYPE, false, maxVertices, maxIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_positionA"),
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_positionB"),
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_positionC"),
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_positionD"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texTransX"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texTransY"));

        short[] indices = new short[maxIndices];
        for (int i = 0, j = 0; i < indices.length; i += 6, j += 4) {
            indices[i] = (short) j;
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j);
            indices[i + 4] = (short) (j + 2);
            indices[i + 5] = (short) (j + 3);
        }
        mesh.setIndices(indices);

        shader = Shaders.terrainSide.getProgram();
    }

    public void draw(TextureRegion region,
                     float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {

        float tx1 = region.getU();
        float tx2 = region.getU2();
        float ty1 = region.getV();
        float ty2 = region.getV2();

        int vin0 = vIndex;
        // First vertex pt 1
        vArray[vIndex++] = x1;
        vArray[vIndex++] = y1;

        // These are stored for first vertex
        int startCopy = vIndex;
        // Full vertex data
        vArray[vIndex++] = x1;
        vArray[vIndex++] = y1;
        vArray[vIndex++] = x2;
        vArray[vIndex++] = y2;
        vArray[vIndex++] = x3;
        vArray[vIndex++] = y3;
        vArray[vIndex++] = x4;
        vArray[vIndex++] = y4;
        // Texture transformation
        vArray[vIndex++] = tx2 - tx1;
        vArray[vIndex++] = tx1;
        vArray[vIndex++] = ty2 - ty1;
        vArray[vIndex++] = ty1;
        int copyLength = vIndex - startCopy;

        // Second vertex
        vArray[vIndex++] = x2;
        vArray[vIndex++] = y2;
        System.arraycopy(vArray, startCopy, vArray, vIndex, copyLength);
        vIndex += copyLength;

        // Third vertex
        vArray[vIndex++] = x3;
        vArray[vIndex++] = y3;
        System.arraycopy(vArray, startCopy, vArray, vIndex, copyLength);
        vIndex += copyLength;

        // Fourth vertex
        vArray[vIndex++] = x4;
        vArray[vIndex++] = y4;
        System.arraycopy(vArray, startCopy, vArray, vIndex, copyLength);
        vIndex += copyLength;
    }

    @Override
    protected void internalFlush() {
        Buffer indicesBuffer = mesh.getIndicesBuffer(true);
        indicesBuffer.position(0);
        indicesBuffer.limit(vIndex - 1);
        Textures.getSpriteSheet().bind();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, vIndex / 56 * 6);
    }

    @Override
    protected void setupExtra() {
        super.setupExtra();

        // Texture
        shader.setUniformi("u_texture", 0);

        // Resolution
        shader.setUniform2fv("u_resolution", new float[]{Gdx.graphics.getWidth(), Gdx.graphics.getHeight()}, 0, 2);
    }
}
