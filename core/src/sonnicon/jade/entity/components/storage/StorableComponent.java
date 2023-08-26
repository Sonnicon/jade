package sonnicon.jade.entity.components.storage;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Structs;

import java.util.Arrays;
import java.util.Map;
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
    public Map<Object, Object> debugProperties() {
        return Structs.mapExtendFrom(super.debugProperties(), "name", displayName, "description", displayDescription, "icons", icons);
    }

    @Override
    public boolean compare(IComparable other) {
        if (other instanceof StorableComponent) {
            return Arrays.equals(icons, ((StorableComponent) other).icons) &&
                    Objects.equals(displayName, ((StorableComponent) other).displayName);
        }
        return false;
    }
}
