package sonnicon.jade.game.actions;

import sonnicon.jade.util.Utils;

import java.util.Map;

public class RunnableAction extends Actions.Action {
    private Runnable func;

    public RunnableAction set(Runnable func) {
        this.func = func;
        return this;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {
        func.run();
    }

    @Override
    protected void onInterrupt() {

    }

    @Override
    protected void onFrame() {

    }

    @Override
    protected void onAlign() {

    }

    @Override
    protected void onTick() {

    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "func", func);
    }
}