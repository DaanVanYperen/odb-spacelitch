
package net.mostlyoriginal.game.desktop;

import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.evo.NEAT.Environment;
import com.evo.NEAT.Genome;
import com.evo.NEAT.Pool;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.G;

import java.util.ArrayList;

public class DesktopLauncher {

    public static void main(String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = G.SCREEN_WIDTH;
        config.height = G.SCREEN_HEIGHT;
        config.title = "LD40";
        config.vSyncEnabled=false;
        config.backgroundFPS=0;
        config.foregroundFPS=0;
        config.disableAudio=true;
        LwjglApplication app = new LwjglApplication(new GdxArtemisGame() {
            public void slowFramerate() {
                config.foregroundFPS = 60;
            }
            public void fastFramerate() {
                config.foregroundFPS = 0;
            }
        }, config);
    }
}
