package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;

public abstract class CachedDrawBatch extends DrawBatch {
    public boolean invalidated = true;

    public CachedDrawBatch(int vSize) {
        vArray = new float[vSize];
    }

    @Override
    public void begin() {
        super.begin();
        vIndex = 0;
    }

    @Override
    public void end() {
        mesh.setVertices(vArray, 0, vIndex);
        super.end();

        invalidated = false;
    }

    public void flush() {
        if (vIndex == 0) {
            return;
        }

        Gdx.gl.glDepthMask(false);
        shader.bind();
        setupExtra();

        internalFlush();

        Gdx.gl.glDepthMask(true);
    }

    public void invalidate() {
        this.invalidated = true;
    }
}
