package sonnicon.jade.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import sonnicon.jade.EventGenerator;
import sonnicon.jade.Jade;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.gui.GuiStage;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.gui.StageMenuMain;
import sonnicon.jade.util.Events;

@EventGenerator(id = "StateSet", param = {Gamestate.State.class}, label = {"state"})
public class Gamestate {
    protected static State state = State.loading;
    public final static Events events = new Events();

    public static State getState() {
        return state;
    }

    public static GuiStage getStage() {
        return state.getStage();
    }

    public static void setState(State newState) {
        // Cleanup previous
        if (state.stage != null) {
            Jade.renderer.removeRenderable(state.stage);
        }
        // Switch
        state = newState;

        //todo move elsewhere?
        // Setup new stage
        GuiStage stage = state.getStage();
        if (stage != null) {
            stage.create();
            stage.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            stage.getViewport().apply();
            Jade.renderer.addRenderable(stage, RenderLayer.gui);
        }
        // Setup new state
        Gdx.input.setInputProcessor(newState.inputProcessor);
        EventTypes.StateSetEvent.handle(events, state);
    }

    public enum State {
        loading,
        menu(StageMenuMain.class, new RenderLayer[]{RenderLayer.gui}),
        ingame(StageIngame.class, RenderLayer.all);

        private GuiStage stage;
        private final Class<? extends GuiStage> stageClass;
        private final RenderLayer[] layersToRender;
        public InputProcessor inputProcessor;

        State() {
            this(null, new RenderLayer[]{});
        }

        State(Class<? extends GuiStage> stageClass, RenderLayer[] layersToRender) {
            this.stageClass = stageClass;
            this.layersToRender = layersToRender;
        }

        public GuiStage getStage() {
            if (stage == null && stageClass != null) {
                try {
                    return stage = stageClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new InstantiationError();
                }
            } else {
                return stage;
            }
        }

        public RenderLayer[] getLayersToRender() {
            return layersToRender;
        }

        public boolean isActive() {
            return state == this;
        }
    }
}
