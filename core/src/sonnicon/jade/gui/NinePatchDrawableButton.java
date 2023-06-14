package sonnicon.jade.gui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import sonnicon.jade.graphics.Textures;

public class NinePatchDrawableButton extends NinePatchDrawable {
    public NinePatchDrawableButton() {
        super(new NinePatch(Textures.atlasFindRegion("button1"), 1,  1, 0, 1));
        getPatch().setLeftWidth(4);
        getPatch().setRightWidth(4);
        getPatch().setBottomHeight(6);
    }
}
