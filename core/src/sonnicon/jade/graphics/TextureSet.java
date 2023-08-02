package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sonnicon.jade.game.Clock;

public class TextureSet implements Drawable {
    protected TextureRegionDrawable[] regions;
    public boolean animateTick = false;
    public float animateSpeed = 1f;

    public TextureSet(String name) {
        regions = new TextureRegionDrawable[]{Textures.atlasFindDrawable(name)};
    }

    public TextureSet(String name, int count) {
        regions = new TextureRegionDrawable[count];
        for (int i = 0; i < count; i++) {
            regions[i] = Textures.atlasFindDrawable(name + i);
        }
    }

    public TextureSet(TextureRegionDrawable... regions) {
        this.regions = regions;
    }

    public TextureRegionDrawable getDrawable() {
        return getDrawable(getRegionIndex());
    }

    public TextureRegionDrawable getDrawable(int index) {
        return regions[index];
    }

    public int size() {
        return regions.length;
    }

    public TextureSet setAnimateTick(boolean animateTick) {
        this.animateTick = animateTick;
        return this;
    }

    public TextureSet setAnimateSpeed(float speed) {
        this.animateSpeed = speed;
        return this;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        getDrawable().draw(batch, x, y, width, height);
    }

    protected int getRegionIndex() {
        return Math.floorMod((int) ((animateTick ? Clock.getTickNum() : Clock.getUpdateNum()) * animateSpeed), regions.length);
    }

    @Override
    public float getLeftWidth() {
        return getDrawable().getLeftWidth();
    }

    @Override
    public void setLeftWidth(float leftWidth) {
        for (Drawable d : regions) {
            d.setLeftWidth(leftWidth);
        }
    }

    @Override
    public float getRightWidth() {
        return getDrawable().getRightWidth();
    }

    @Override
    public void setRightWidth(float rightWidth) {
        for (Drawable d : regions) {
            d.setRightWidth(rightWidth);
        }
    }

    @Override
    public float getTopHeight() {
        return getDrawable().getTopHeight();
    }

    @Override
    public void setTopHeight(float topHeight) {
        for (Drawable d : regions) {
            d.setTopHeight(topHeight);
        }
    }

    @Override
    public float getBottomHeight() {
        return getDrawable().getBottomHeight();
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
        for (Drawable d : regions) {
            d.setBottomHeight(bottomHeight);
        }
    }

    @Override
    public float getMinWidth() {
        return getDrawable().getMinWidth();
    }

    @Override
    public void setMinWidth(float minWidth) {
        for (Drawable d : regions) {
            d.setMinWidth(minWidth);
        }
    }

    @Override
    public float getMinHeight() {
        return getDrawable().getMinHeight();
    }

    @Override
    public void setMinHeight(float minHeight) {
        for (Drawable d : regions) {
            d.setMinHeight(minHeight);
        }
    }
}
