package sonnicon.jade;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);//60);
		config.setWindowedMode(640, 480);
		//config.useVsync(true);
		config.setTitle("JADE");
		new Lwjgl3Application(new Jade(), config);
	}
}
