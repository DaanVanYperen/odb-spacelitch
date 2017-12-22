package net.mostlyoriginal.api.system.physics;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.game.component.Carries;
import com.artemis.FluidIteratingSystem;

import static com.artemis.E.E;

/**
 * Applies gravity on Y axis to Entity.
 *
 * @author Daan van Yperen
 * @see Gravity
 */
@Wire
public class CarriedSystem extends FluidIteratingSystem {

    public CarriedSystem() {
        super(Aspect.all(Carries.class, Pos.class));
    }

    @Override
    protected void process(E e) {
        E pickup = E(e.getCarries().entityId);
        pickup.pos(e.posX() + e.carriesAnchorX() + pickup.boundsMinx() + pickup.boundsMaxx() * 0.5f, e.posY() + e.carriesAnchorY());
    }
}
