package net.mostlyoriginal.game.api;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.EBag;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;

/**
 * @author Daan van Yperen
 */
public class EUtil {

    public static E firstOverlapping(E subject, EBag scope) {
        for (E e : scope) {
            if (overlaps(e, subject))
                return e;
        }
        return null;
    }

    public static boolean overlaps(final E a, final E b) {
        final Bounds b1 = a.getBounds();
        final Pos p1 = a.getPos();
        final Bounds b2 = b.getBounds();
        final Pos p2 = b.getPos();

        if (b1 == null || p1 == null || b2 == null || p2 == null)
            return false;

        final float minx = p1.xy.x + b1.minx;
        final float miny = p1.xy.y + b1.miny;
        final float maxx = p1.xy.x + b1.maxx;
        final float maxy = p1.xy.y + b1.maxy;

        final float bminx = p2.xy.x + b2.minx;
        final float bminy = p2.xy.y + b2.miny;
        final float bmaxx = p2.xy.x + b2.maxx;
        final float bmaxy = p2.xy.y + b2.maxy;

        return
                !(minx > bmaxx || maxx < bminx ||
                        miny > bmaxy || maxy < bminy);
    }

}
