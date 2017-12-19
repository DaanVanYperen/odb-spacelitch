package net.mostlyoriginal.game.system.map;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.GenomeSensorSystem;
import net.mostlyoriginal.game.system.ShipControlSystem;
import net.mostlyoriginal.game.system.detection.DialogSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
public class GenomeSensorUISystem extends BaseSystem {

    public static final int NODE_SIZE = 24;
    private CameraSystem cameraSystem;
    private SpriteBatch batch;
    private TagManager tagManager;
    private E player;
    private E boss;
    private GameScreenAssetSystem assetSystem;
    private TextureRegion tick;
    private DialogSystem dialogSystem;
    private GenomeSensorSystem genomeSensorSystem;
    private ShipControlSystem shipControlSystem;

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(512);
    }

    @Override
    protected void begin() {
        tick = ((Animation<TextureRegion>) assetSystem.get("tick-node")).getKeyFrame(0);
        batch.begin();
    }

    @Override
    protected void end() {

        batch.end();
        super.end();
    }

    @Override
    protected void processSystem() {
        for (int y = 0; y < GenomeSensorSystem.HEIGHT; y++) {
            for (int x = 0; x < GenomeSensorSystem.WIDTH; x++) {
                float v = genomeSensorSystem.getValue(x, y);
                setColor(v);
                batch.draw(tick, 16 + x * NODE_SIZE, 16 + y * NODE_SIZE, NODE_SIZE - 2, NODE_SIZE - 2);
            }
        }
        for (int x = 0; x < ShipControlSystem.CONTROL_COUNT; x++) {
            float v = shipControlSystem.evaluate[x];
            setColor(v);
            batch.draw(tick, 16 + x * NODE_SIZE, 16 + GenomeSensorSystem.HEIGHT * NODE_SIZE + NODE_SIZE, NODE_SIZE - 2, NODE_SIZE - 2);
        }
    }

    private void setColor(float v) {
        batch.setColor(v < 0 ? 1f : 0, 0, v > 0 ? 1f : 0, 0.1f + Math.abs(v) * 0.9f);
    }
}