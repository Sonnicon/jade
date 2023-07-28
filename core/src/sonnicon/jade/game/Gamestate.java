package sonnicon.jade.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import sonnicon.jade.EventGenerator;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.Gui;
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
        state = newState;
        Gui.setActiveStage(newState.getStage());
        Gdx.input.setInputProcessor(newState.inputProcessor);
        EventTypes.StateSetEvent.handle(events, state);
    }

    public enum State {
        loading,
        menu(StageMenuMain.class),
        ingame(StageIngame.class);

        private GuiStage stage;
        private final Class<? extends GuiStage> stageClass;
        public InputProcessor inputProcessor;

        State() {
            this(null);
        }

        State(Class<? extends GuiStage> stageClass) {
            this.stageClass = stageClass;
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

        public boolean isActive() {
            return state == this;
        }
    }
}
