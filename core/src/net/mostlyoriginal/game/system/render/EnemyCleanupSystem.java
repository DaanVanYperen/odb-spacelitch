package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.All;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.Team;
import com.artemis.FluidIteratingSystem;

/**
 * Unfreeze near camera.
 *
 * @author Daan van Yperen
 */
@All({Pos.class, Team.class})
public class EnemyCleanupSystem extends FluidIteratingSystem {

    CameraSystem cameraSystem;
    private MyAnimRenderSystem myAnimRenderSystem;
    private boolean lockCamera;
    private CameraFollowSystem cameraFollowSystem;
    private float minY;

    @Override
    protected void begin() {
        super.begin();
        final E camera = E.withTag("camera");
        minY = cameraFollowSystem.minCameraY() - 60;
    }

    @Override
    protected void process(E e) {
        if ( e.teamTeam() == G.TEAM_ENEMIES && e.posY() <= minY ) {
            e.deleteFromWorld();
        }
    }
}

