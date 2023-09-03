package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public enum Shaders {
    normal("normal"),
    fow("fow"),
    viewdist("viewdist"),
    gridoverlay("gridoverlay");

    private final String name;
    private ShaderProgram program;

    private static final FileHandle SHADER_FILE = Gdx.files.internal("shaders/");

    Shaders(String name) {
        this.name = name;
    }

    public ShaderProgram getProgram() {
        if (program == null) {
            program = new ShaderProgram(SHADER_FILE.child(name + ".vert"), SHADER_FILE.child(name + ".frag"));
            if (!program.isCompiled()) {
                throw new RuntimeException(program.getLog());
            }
        }
        return program;
    }
}
