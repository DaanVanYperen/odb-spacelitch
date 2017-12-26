package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.All;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.physics.Frozen;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.G;
import com.artemis.FluidIteratingSystem;

/**
 * Unfreeze near camera.
 *
 * @author Daan van Yperen
 */
@All({Pos.class, Frozen.class})
public class CameraUnfreezeSystem extends FluidIteratingSystem {

    CameraSystem cameraSystem;
    private MyAnimRenderSystem myAnimRenderSystem;
    private boolean lockCamera;
    private CameraFollowSystem cameraFollowSystem;
    private float maxY;

    @Override
    protected void begin() {
        super.begin();
        final E camera = entityWithTag("camera");
        maxY = cameraFollowSystem.minCameraY() + (G.SCREEN_HEIGHT / G.CAMERA_ZOOM);
    }

    @Override
    protected void process(E e) {
        if ( e.posY() <= maxY ) {
            e.removeFrozen();
        }
    }
}

