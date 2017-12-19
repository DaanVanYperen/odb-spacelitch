package net.mostlyoriginal.game.system;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.api.EBag;
import net.mostlyoriginal.game.component.Deadly;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.Pickup;
import net.mostlyoriginal.game.system.map.MapCollisionSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class GenomeSensorSystem extends BaseSystem {

    public static final float SCALAR = 1f / 32f;
    private float sensor[] = new float[WIDTH * HEIGHT];
    public static final int WIDTH = 9;
    public static final int HEIGHT = 9;
    public MapCollisionSystem mapCollisionSystem;
    Vector2 v2 = new Vector2();

    GenomeSystem genomeSystem;

    @Override
    protected void processSystem() {
        world.delta = world.delta * 3f;
        clearSensor();
        senseMap();
        sense(Aspect.all(Pickup.class, Pos.class), 0.5f);
        sense(Aspect.all(Deadly.class, Pos.class), -1f);
    }

    private void senseMap() {
        E player = entityWithTag("player");
        if ( player == null ) return;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                v2.set(-WIDTH / 2 + x, -HEIGHT / 2 + y).scl(1f / SCALAR).add(player.posX(), player.posY()).add(player.boundsCx(), player.boundsCy());
                if (mapCollisionSystem.collides(v2.x, v2.y)) {
                    setValue(x, y, 1f);
                }
            }
        }
    }

    private void sense(Aspect.Builder scope, float value) {
        E player = entityWithTag("player");
        if ( player == null ) return;
        genomeSystem.setFitness(entityWithTag("camera").posY());
        for (E e : allEntitiesMatching(scope)) {

            if (e.hasTeam() && e.teamTeam() != G.TEAM_ENEMIES)
                continue;

            v2.set(e.posX() + e.boundsCx(), e.posY() + e.boundsCy()).sub(player.posX() + player.boundsCx(), player.posY() + player.boundsCy());

            v2.scl(SCALAR);

            setValue((int) ((WIDTH / 2 * SCALAR) + v2.x), (int) ((HEIGHT / 2 * SCALAR) + v2.y), value);
        }
    }

    private void setValue(int x, int y, float value) {
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return;
        sensor[x + y * WIDTH] = value;
    }

    protected E entityWithTag(String tag) {
        final Entity entity = world.getSystem(TagManager.class).getEntity(tag);
        return entity != null ? E(entity) : null;

    }

    protected EBag allEntitiesMatching(Aspect.Builder scope) {
        return new EBag(world.getAspectSubscriptionManager().get(scope).getEntities());
    }

    protected EBag allEntitiesWith(Class<? extends Component> scope) {
        return new EBag(world.getAspectSubscriptionManager().get(Aspect.all(scope)).getEntities());
    }

    private void clearSensor() {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            sensor[i] = 0;
        }
    }

    public float[] getInput() {
        return sensor;
    }

    public float getValue(int x, int y) {
        return sensor[x + y * WIDTH];
    }
}
