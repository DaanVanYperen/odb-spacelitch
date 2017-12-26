package net.mostlyoriginal.game.system.detection;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.managers.GroupManager;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.physics.Frozen;
import net.mostlyoriginal.game.component.*;
import com.artemis.FluidIteratingSystem;
import net.mostlyoriginal.game.system.map.EntitySpawnerSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.game.api.EUtil.overlaps;

/**
 * @author Daan van Yperen
 */
@All({Pos.class, Pickup.class})
@Exclude(Frozen.class)
public class PickupSystem extends FluidIteratingSystem {

    private E player;
    private EntitySpawnerSystem entitySpawnerSystem;
    private GroupManager groupManager;
    private GameScreenAssetSystem gameScreenAssetSystem;

    @Override
    protected void begin() {
        super.begin();
        player = E.withTag("player");
    }

    @Override
    protected void process(E e) {
        if ( overlaps(e, player) ) {
            upgradeGuns(player);
            e.deleteFromWorld();
        }
    }

    public void upgradeGuns(E playerShip) {
        Player player = playerShip.getPlayer();
        player.upgradeLevel++;
        String mainGun = null;
        String bounceGun = null;
        switch (player.upgradeLevel) {
            case 1:
                mainGun = "minigun";
                bounceGun = null;
                break;
            case 2:
                mainGun = "minigun";
                bounceGun = "bouncegun";
                break;
            case 3:
                mainGun = "minigun";
                bounceGun = "bouncegun_r2";
                break;
            case 4:
                mainGun = "minigun_r2";
                bounceGun = "bouncegun_r3";
                break;
            case 5:
                mainGun = "minigun_r2";
                bounceGun = "bouncegun_r4";
                break;
            case 6:
                mainGun = "minigun_r3";
                bounceGun = "bouncegun_r5";
                break;
            case 7:
                mainGun = "minigun_r3";
                bounceGun = "bouncegun_r6";
                break;
            case 8:
                mainGun = "minigun_r3";
                bounceGun = "bouncegun_r7";
                break;
            case 9:
                mainGun = "minigun_r4";
                bounceGun = "bouncegun_r7";
                break;
            case 10:
                mainGun = "minigun_r5";
                bounceGun = "bouncegun_r7";
                break;
            case 11:
                mainGun = "minigun_r5";
                bounceGun = "bouncegun_r7";
                break;
            default: return;
        }

        gameScreenAssetSystem.playSfx("Misc_2");
        killOldGuns();
        entitySpawnerSystem.addArsenal(playerShip, "player-guns", G.TEAM_PLAYERS, 0, mainGun, false);
        if ( bounceGun != null  ) entitySpawnerSystem.addArsenal(playerShip, "player-guns", G.TEAM_PLAYERS, 0, bounceGun, false);
    }

    private void killOldGuns() {
        for (Entity entity : groupManager.getEntities("player-guns")) {
            entity.deleteFromWorld();
        }
    }
}
