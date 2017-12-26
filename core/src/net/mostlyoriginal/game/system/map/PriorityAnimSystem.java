package net.mostlyoriginal.game.system.map;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.All;
import net.mostlyoriginal.game.component.PriorityAnim;
import com.artemis.FluidIteratingSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
@All(PriorityAnim.class)
public class PriorityAnimSystem extends FluidIteratingSystem {
    @Override
    protected void process(E e) {
        e.priorityAnimCooldown(e.priorityAnimCooldown() - world.delta);
        if (e.priorityAnimCooldown() <= 0) {
            e.removePriorityAnim();
        } else {
            e.animId(e.priorityAnimAnimId());
            e.animAge(e.priorityAnimAge());
            e.priorityAnimAge(e.priorityAnimAge() + world.delta);
        }
    }
}
