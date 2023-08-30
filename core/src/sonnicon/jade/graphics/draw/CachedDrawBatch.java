package sonnicon.jade.graphics.draw;

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

        shader.bind();
        setupExtra();

        internalFlush();
    }

    public void invalidate() {
        this.invalidated = true;
    }
}
