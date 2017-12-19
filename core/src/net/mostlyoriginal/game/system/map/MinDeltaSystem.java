package net.mostlyoriginal.game.system.map;

import com.artemis.BaseSystem;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.api.utils.MapMask;

import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
public class MinDeltaSystem extends BaseSystem {
    private final int fps;

    public MinDeltaSystem(int fps) {

        this.fps = fps;
    }

    @Override
    protected void processSystem() {
        world.delta=1f/fps;
    }
}

