package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.Color;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.GraphicsBatch;

public class BoxParticle extends Particle {
    protected float width;
    protected float height;
    protected Color color;
    protected float rotation;


    public BoxParticle() {

    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        layer.shapeDrawer.setColor(color.r, color.g, color.b, color.a * (1f - getProgress()));
        layer.shapeDrawer.filledRectangle(x - width / 2f, y - height / 2f, width, height, (float) Math.toRadians(-rotation));
    }

    @Override
    public void onObtained() {
        super.onObtained();
        setSize(8f, 8f, 0f);
        //todo
        color = Color.WHITE.cpy();
    }

    public void setSize(float width, float height, float rotation) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
