package sonnicon.jade.graphics.draw;

public abstract class CachedIndexedDrawBatch extends CachedDrawBatch {
    protected int iIndex = 0;
    protected short[] iArray;

    public CachedIndexedDrawBatch(int vSize, int iSize) {
        super(vSize);
        iArray = new short[iSize];
    }

    @Override
    public void begin() {
        super.begin();
        iIndex = 0;
    }

    @Override
    public void end() {
        mesh.setIndices(iArray, 0, iIndex);
        super.end();
    }

    public void flush() {
        if (iIndex == 0) {
            return;
        }
        super.flush();
    }
}
