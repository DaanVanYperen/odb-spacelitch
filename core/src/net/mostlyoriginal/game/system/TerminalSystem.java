package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.E;
import net.mostlyoriginal.game.component.Terminal;
import com.artemis.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
public class TerminalSystem extends FluidIteratingSystem {
    public TerminalSystem() {
        super(Aspect.all(Terminal.class));
    }

    @Override
    protected void process(E e) {
        e.deleteFromWorld();
    }
}
