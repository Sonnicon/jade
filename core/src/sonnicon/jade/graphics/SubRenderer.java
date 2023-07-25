package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SubRenderer {
    protected final int[] renderLayers = new int[Renderer.RenderLayer.all().length];
    protected final List<IRenderable> renderList = new ArrayList<>();

    public void addRenderable(IRenderable renderable, Renderer.RenderLayer layer) {
        int listIndex = 0;
        boolean inserted = false;
        for (Renderer.RenderLayer listLayer : Renderer.RenderLayer.all()) {
            if (!inserted) {
                if (listLayer == layer) {
                    renderList.add(listIndex, renderable);
                    inserted = true;
                }
                listIndex += renderLayers[listLayer.ordinal()];
            } else {
                renderLayers[listLayer.ordinal()]++;
            }
        }
    }

    public boolean removeRenderable(IRenderable renderable) {
        int index = renderList.indexOf(renderable);
        boolean result = renderList.remove(index) != null;
        for (int i = 0; i < renderLayers.length; i++) {
            if (renderLayers[i] > index) {
                renderLayers[i]--;
            }
        }
        return result;
    }

    public void renderRenderables(SpriteBatch batch, float delta, Renderer.RenderLayer layer) {
        if (layer != null) {
            int end;
            if (layer.ordinal() < renderLayers.length - 1) {
                end = renderLayers[layer.ordinal() + 1];
            } else {
                end = renderList.size();
            }
            for (int i = renderLayers[layer.ordinal()]; i < end; i++) {
                renderList.get(i).render(batch, delta, layer);
            }
        }
    }
}
