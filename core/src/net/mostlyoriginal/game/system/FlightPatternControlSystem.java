package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.C;
import com.artemis.E;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.interact.Aim;
import net.mostlyoriginal.api.component.physics.Frozen;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.game.component.*;
import com.artemis.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
@All({FlightPattern.class, Physics.class})
@Exclude(Frozen.class)
public class FlightPatternControlSystem extends FluidIteratingSystem {

    Vector2 v2 = new Vector2();

    @Override
    protected void process(E e) {
        final FlightPattern pattern = e.getFlightPattern();
        if (pattern.data == null) return;

        pattern.age += world.delta;

        if (pattern.age >= pattern.data.steps[pattern.activeStep].seconds) {
            pattern.age -= pattern.data.steps[pattern.activeStep].seconds;
            pattern.activeStep++;
            if (pattern.activeStep >= pattern.data.steps.length) {
                pattern.activeStep = 0;
            }
        }

        FlightPatternStep step = pattern.data.steps[pattern.activeStep];
        switch (step.step) {
            case HIDE_LEFT:
                hide(e, step, -G.SCREEN_WIDTH * 0.5f);
                break;
            case HIDE_RIGHT:
                hide(e, step, G.SCREEN_WIDTH * 0.5f);
                break;
            case FLY:
                fly(e, step);
                break;
            case FLY_SINUS:
                flySinus(e, step);
                break;
            case EXPLODE:
                explode(e, step);
                break;
            case FACE_PLAYER:
                facePlayer(e, step);
                break;
            case SPIN:
                spin(e, step);
                break;
            case PAUSE:
                break;
        }
    }

    private void spin(E e, FlightPatternStep step) {
        e.physicsVr(e.physicsVr() + world.delta * 100f);
        e.physicsVx(0);
        e.physicsVy(0);
    }

    private void facePlayer(E e, FlightPatternStep step) {

        Pos turretPos = e.getPos();
        E player = E.withTag("player");
        if (player != null) {
            Pos playerPos = player.getPos();
            float angle = v2.set(playerPos.getX(), playerPos.getY()).sub(turretPos.getX(), turretPos.getY()).angle();
            e.angleRotation(90 + angle);
            e.physicsVr(0);
            e.physicsVx(0);
            e.physicsVy(0);
        }
    }

    private void hide(E e, FlightPatternStep step, float offsetX) {
        e.posX(e.posX() + offsetX);
        e.flightPatternActiveStep(e.flightPatternActiveStep() + 1); // step instantly dobne.
    }

    private void explode(E e, FlightPatternStep step) {
        e.deleteFromWorld();
    }

    private void fly(E e, FlightPatternStep step) {
        v2.set(0, 50).rotate(step.angle);
        //e.angleRotate(step.facing);
        e.physicsVx(-v2.x);
        e.physicsVy(v2.y);
    }

    private void flySinus(E e, FlightPatternStep step) {
        v2.set(MathUtils.sin(e.flightPatternAge() * 4f) * 100f, 50).rotate(step.angle);
        //e.angleRotate(step.facing);
        e.physicsVx(v2.x);
        e.physicsVy(v2.y);
    }
}
