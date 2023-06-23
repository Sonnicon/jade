package sonnicon.jade.entity.components;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.Arrays;
import java.util.Objects;

public class StorableComponent extends Component {
    public String displayName, displayDescription;
    public Drawable[] icons;

    public StorableComponent(String displayName, Drawable... icons) {
        this.displayName = displayName;
        this.icons = icons;
    }

    //todo funcs

    @Override
    public Component copy() {
        return new StorableComponent(displayName, icons);
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
