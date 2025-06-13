package sonnicon.jade.graphics.draw;

import sonnicon.jade.Jade;

public class TopTerrainSpriteBatch extends StaticTerrainSpriteBatch {
    public TopTerrainSpriteBatch() {
        this(10000);
    }

    public TopTerrainSpriteBatch(int size) {
        super(size);
        shader = Shaders.terrainTop.getProgram();
    }

    @Override
    protected void setupExtra() {
        super.setupExtra();

        // Camera Position
        shader.setUniformf("u_camCoord", Jade.renderer.camera.position);
    }


}
