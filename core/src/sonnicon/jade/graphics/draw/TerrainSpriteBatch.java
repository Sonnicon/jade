package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

import java.nio.Buffer;

public class TerrainSpriteBatch extends CachedDrawBatch implements IRegularDraw {
    public TerrainSpriteBatch() {
        this(10000);
    }

    public TerrainSpriteBatch(int size) {
        super(size * 16);
        if (size * 4 > (1 << 16)) {
            throw new IndexOutOfBoundsException();
        }

        int maxVertices = size * 4, maxIndices = size * 6;

        mesh = new Mesh(VERTEX_DATA_TYPE, false, maxVertices, maxIndices,
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
    }

    @Override
    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;

        float tx1 = region.getU();
        float tx2 = region.getU2();
        float ty1 = region.getV();
        float ty2 = region.getV2();

        vArray[vIndex] = x;
        vArray[vIndex + 1] = y;
        vArray[vIndex + 2] = tx1;
        vArray[vIndex + 3] = ty2;

        vArray[vIndex + 4] = x;
        vArray[vIndex + 5] = y2;
        vArray[vIndex + 6] = tx1;
        vArray[vIndex + 7] = ty1;

        vArray[vIndex + 8] = x2;
        vArray[vIndex + 9] = y2;
        vArray[vIndex + 10] = tx2;
        vArray[vIndex + 11] = ty1;

        vArray[vIndex + 12] = x2;
        vArray[vIndex + 13] = y;
        vArray[vIndex + 14] = tx2;
        vArray[vIndex + 15] = ty2;
        vIndex += 16;
    }

    public void draw(TextureRegion region, float x, float y, float width, float height, byte rotation) {
        if ((rotation & 1) > 0) {
            float intermediate = width;
            //noinspection SuspiciousNameCombination
            width = height;
            height = intermediate;
        }

        float x2 = x + width;
        float y2 = y + height;

        float tx1 = region.getU();
        float tx2 = region.getU2();
        float ty1 = region.getV();
        float ty2 = region.getV2();

        vArray[vIndex] = x;
        vArray[vIndex + 1] = y;
        vArray[vIndex + 4] = x;
        vArray[vIndex + 5] = y2;
        vArray[vIndex + 8] = x2;
        vArray[vIndex + 9] = y2;
        vArray[vIndex + 12] = x2;
        vArray[vIndex + 13] = y;

        short offset = (short) (rotation * 4);

        vArray[vIndex + (2 + offset) % 16] = tx1;
        vArray[vIndex + (3 + offset) % 16] = ty2;


        vArray[vIndex + (6 + offset) % 16] = tx1;
        vArray[vIndex + (7 + offset) % 16] = ty1;


        vArray[vIndex + (10 + offset) % 16] = tx2;
        vArray[vIndex + (11 + offset) % 16] = ty1;


        vArray[vIndex + (14 + offset) % 16] = tx2;
        vArray[vIndex + (15 + offset) % 16] = ty2;

        vIndex += 16;
    }

    public void draw(TextureRegion region, int x, int y, byte rotation) {
        draw(region, x * Tile.TILE_SIZE - PIXEL_FIXER,
                y * Tile.TILE_SIZE - PIXEL_FIXER,
                Tile.TILE_SIZE + PIXEL_FIXER_XL,
                Tile.TILE_SIZE + PIXEL_FIXER_XL, rotation);
    }

    @Override
    protected void internalFlush() {
        Buffer indicesBuffer = mesh.getIndicesBuffer(true);
        indicesBuffer.position(0);
        indicesBuffer.limit(vIndex - 1);
        Textures.getSpriteSheet().bind();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        mesh.render(shader, GL20.GL_TRIANGLES, 0, vIndex / 16 * 6);
    }

    @Override
    protected void setupExtra() {
        super.setupExtra();
        shader.setUniformi("u_texture", 0);
    }
}
