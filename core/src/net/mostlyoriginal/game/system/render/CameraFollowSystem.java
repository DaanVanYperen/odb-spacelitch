package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.render.CameraFocus;
import com.artemis.FluidIteratingSystem;
import com.badlogic.gdx.math.Interpolation;

/**
 * @author Daan van Yperen
 */
public class CameraFollowSystem extends FluidIteratingSystem {

    CameraSystem cameraSystem;
    private MyAnimRenderSystem myAnimRenderSystem;
    private boolean lockCamera;

    public CameraFollowSystem() {
        super(Aspect.all(Pos.class, CameraFocus.class));
    }

    private int targetY = 0;
    private int sourceY = 0;
    private float cooldown = 0f;

    public float minCameraY() {
        return cameraSystem.camera.position.y - G.SCREEN_HEIGHT / 2;
    }

    @Override
    protected void process(E e) {
        if ( Gdx.input.isKeyJustPressed(Input.Keys.F9) ) lockCamera = !lockCamera;

        if ( lockCamera) return;
        if (e.wallSensorOnFloor() || e.wallSensorOnPlatform() || true) {
            float newTargetY = (int) myAnimRenderSystem.roundToPixels(e.posY() + e.boundsCy()) + (G.SCREEN_HEIGHT / G.CAMERA_ZOOM);
            if (targetY != newTargetY) {
                sourceY = (int) cameraSystem.camera.position.y;
                targetY = (int) (newTargetY);
                cooldown = 0f;
            }
        }
        if (cooldown <= 1F) {
            cooldown += world.delta*2f;
            if (cooldown > 1f) cooldown = 1f;
            cameraSystem.camera.position.y = myAnimRenderSystem.roundToPixels(Interpolation.pow2Out.apply(sourceY,targetY, cooldown));        }
        cameraSystem.camera.position.x = myAnimRenderSystem.roundToPixels(e.posX()) + e.boundsCx();
        cameraSystem.camera.update();

        float maxDistance = (Gdx.graphics.getHeight() / G.CAMERA_ZOOM) * 0.5F * 0.7f;
        if (  e.posY() < cameraSystem.camera.position.y - maxDistance) {
            cameraSystem.camera.position.y = myAnimRenderSystem.roundToPixels(e.posY() + maxDistance);
            cameraSystem.camera.update();
        }
    }
}

