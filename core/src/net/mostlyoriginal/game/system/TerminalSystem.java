package net.mostlyoriginal.game.system;

import com.artemis.C;
import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import net.mostlyoriginal.game.component.Terminal;

/**
 * @author Daan van Yperen
 */
@All(Terminal.class)
public class TerminalSystem extends FluidIteratingSystem {
    @Override
    protected void process(E e) {
        e.deleteFromWorld();
    }
}
