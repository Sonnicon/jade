package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.Color;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.GraphicsBatch;

public class LineParticle extends Particle {

    public float thickness;
    public Color color;
    public float destX, destY;

    public LineParticle() {

    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        color.a = 1f - getProgress();
        layer.shapeDrawer.line(x, y, destX, destY, color, thickness);
    }

    @Override
    public void onObtained() {
        super.onObtained();
        //todo dont
        color = Color.WHITE.cpy();
        thickness = 1f;
        setDest(0f, 0f);
    }

    public void setDest(float destX, float destY) {
        this.destX = destX;
        this.destY = destY;
    }
}
