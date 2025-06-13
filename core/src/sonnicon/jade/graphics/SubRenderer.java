package sonnicon.jade.graphics;

import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubRenderer implements IDebuggable {
    protected final int[] renderLayers = new int[RenderLayer.size()];
    protected final List<IRenderable> renderList = new ArrayList<>();

    public void addRenderable(IRenderable renderable, RenderLayer layer) {
        int incr = -1;
        for (RenderLayer listLayer : RenderLayer.all) {
            if (incr == -1) {
                if (listLayer == layer) {
                    renderList.add(renderLayers[listLayer.index], renderable);
                    incr = listLayer.index;
                }
            } else if (listLayer.index > incr) {
                renderLayers[incr = listLayer.index]++;
            }
        }
    }

    public boolean removeRenderable(IRenderable renderable) {
        int index;
        boolean result = false;
        while ((index = renderList.indexOf(renderable)) != -1) {
            result = renderList.remove(index) != null || result;
            for (int i = 0; i < renderLayers.length; i++) {
                if (renderLayers[i] > index) {
                    renderLayers[i]--;
                }
            }
        }
        return result;
    }

    public void renderRenderables(GraphicsBatch batch, float delta, RenderLayer layer) {
        if (layer != null) {
            int end;
            if (layer.index < RenderLayer.size() - 1) {
                end = renderLayers[layer.index + 1];
            } else {
                end = renderList.size();
            }
            for (int i = renderLayers[layer.index]; i < end; i++) {
                renderList.get(i).render(batch, delta, layer);
            }
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("indexes", renderLayers, "renderables", renderList);
    }
}
