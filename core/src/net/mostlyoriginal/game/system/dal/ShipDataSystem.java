package net.mostlyoriginal.game.system.dal;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.game.component.ShipData;

/**
 * @author Daan van Yperen
 */
@Wire
public class ShipDataSystem extends BaseSystem {

    private ShipLibrary shipLibrary;

    public ShipDataSystem() {
        super();
        loadShips();
    }

    @Override
    protected void processSystem() {
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    private void loadShips() {
        final Json json = new Json();
        shipLibrary = json.fromJson(ShipLibrary.class, Gdx.files.internal("ships.json"));
    }

    public ShipData get( String id ) {
        return shipLibrary.getById(id);
    }
}
