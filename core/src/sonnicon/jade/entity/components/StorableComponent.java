package sonnicon.jade.entity.components;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.Arrays;
import java.util.Objects;

public class StorableComponent extends Component {
    public String displayName, displayDescription;
    public Drawable[] icons;

    public StorableComponent() {

    }

    public StorableComponent(String displayName, Drawable... icons) {
        setup(displayName, icons);
    }

    protected StorableComponent setup(String displayName, Drawable... icons) {
        this.displayName = displayName;
        this.icons = icons;
        return this;
    }

    //todo funcs

    @Override
    public StorableComponent copy() {
        return ((StorableComponent) super.copy()).setup(displayName, icons);
    }

    @Override
    public boolean compare(Component other) {
        if (other instanceof StorableComponent) {
            return Arrays.equals(icons, ((StorableComponent) other).icons) &&
                    Objects.equals(displayName, ((StorableComponent) other).displayName);
        }
        return false;
    }
}
