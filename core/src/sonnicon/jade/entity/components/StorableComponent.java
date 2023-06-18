package sonnicon.jade.entity.components;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.Arrays;

public class StorableComponent extends Component {
    public Drawable[] icons;

    public StorableComponent(Drawable... icons) {
        this.icons = icons;
    }

    //todo funcs

    @Override
    public Component copy() {
        return new StorableComponent(icons);
    }

    @Override
    public boolean compare(Component other) {
        if (other instanceof StorableComponent) {
            return Arrays.equals(icons, ((StorableComponent) other).icons);
        }
        return false;
    }
}
