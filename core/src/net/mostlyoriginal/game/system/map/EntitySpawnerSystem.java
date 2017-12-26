package net.mostlyoriginal.game.system.map;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.system.physics.SocketSystem;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.system.dal.ArsenalDataSystem;
import net.mostlyoriginal.game.system.dal.DialogDataSystem;
import net.mostlyoriginal.game.system.dal.FlightPatternDataSystem;
import net.mostlyoriginal.game.system.dal.ShipDataSystem;
import net.mostlyoriginal.game.system.detection.PickupSystem;
import net.mostlyoriginal.game.system.detection.SpoutSystem;
import net.mostlyoriginal.game.system.view.*;

import static com.artemis.E.E;
import static net.mostlyoriginal.game.component.G.*;

/**
 * @author Daan van Yperen
 */
public class EntitySpawnerSystem extends BaseSystem {

    private SocketSystem socketSystem;
    private PowerSystem powerSystem;
    private SpoutSystem spoutSystem;
    private GameScreenAssetSystem gameScreenAssetSystem;
    private ShipDataSystem shipDataSystem;
    private ArsenalDataSystem arsenalDataSystem;
    private FlightPatternDataSystem flightPatternDataSystem;
    private PickupSystem pickupSystem;
    private DialogDataSystem dialogDataSystem;

    @Override
    protected void processSystem() {
    }


    public boolean spawnEntity(float x, float y, MapProperties properties) {

        final String entity = (String) properties.get("entity");

        switch (entity) {
            case "player":
                assemblePlayer(x, y, shipDataSystem.get("player"));
                break;
            case "cable":
                if (properties.containsKey("glow")) {
                    spawnGlow(x, y, (String) properties.get("glow"));
                }
                return false;
            case "exit":
                assembleExit(x, y);
                break;
            case "trigger":
                assembleTrigger(x, y, (String) properties.get("trigger"), (String) properties.get("parameter"));
                break;
            case "robot":
                E robot = assembleRobot(x, y);
                robot.chargeCharge(3).flying(true);
                break;
            case "battery":
                assembleBattery(x, y, "battery");
                break;
            case "battery2":
                assembleBattery(x, y, "battery2");
                break;
//            case "gremlin":
//                spoutSystem.spawnGremlin(0,x,y);
//                return true;
            case "birds":
                for (int i = 0, s = MathUtils.random(1, 3); i <= s; i++) {
                    assembleBird(x + MathUtils.random(G.CELL_SIZE), y + MathUtils.random(G.CELL_SIZE));
                }
                return true;
            case "powerup":
                assemblePowerup(x,y);
                return true;
            case "dialog":
                assembleDialog(x,y, (String) properties.get("dialog"));
                return true;
            case "spout":
                assembleSpout(x, y, (Integer) properties.get("angle"), "ACID");
                return false;
            case "spawner":
                assembleSpout(x, y, (Integer) properties.get("angle"), (String) properties.get("spawns"))
                        .spoutSprayInterval(0.5f).spoutSprayDuration(1);
                return false;
            case "socket":
                assembleBatterySlot(x, y, (Boolean) properties.get("powered"), (String) properties.get("accept"));
                break;
            case "sandsprinkler":
                assembleSandSprinkler(x, y - 1);
                return false;
            default:
                ShipData shipData = shipDataSystem.get(entity);
                if (shipData != null) {
                    assembleEnemy(x, y, shipData);
                    return true;
                }
                return false;
            //throw new RuntimeException("No idea how to spawn entity of type " + entity);
        }
        return true;
    }

    private void assemblePowerup(float x, float y) {
        E().pos(x+8-14, y+8-16)
                .bounds(0,0,28,16)
                .anim("pickup")
                .pickup()
                .frozen()
                .physicsFriction(0)
                .angle()
                .ethereal()
                .physicsVr(-100f)
                .physicsVy(-75f)
                .renderLayer(990);
    }
    private void assembleDialog(float x, float y, String dialog) {
        DialogData data = dialogDataSystem.get(dialog);
        if ( data == null) data =dialogDataSystem.get("missing");
        E().pos(x, y)
                .bounds(0,0,64,64)
                .dialogData(data);
        //.anim("player-idle")
        //.renderLayer(9999);
    }

    private void spawnGlow(float x, float y, String glow) {
        E()
                .pos(x, y)
                .bounds(0, 0, 16, 16)
                .glowAnim(glow)
                .glowExtendPixels(0)
                .glowMinIntensity(0)
                .glowMaxIntensity(0.5f)
                .glowPulseSpeed(3f);
    }

    private void assembleSandSprinkler(float x, float y) {
        E.E().pos(x, y).bounds(0, 0, 16, 1).sandSprinkler();
    }

    private int birdIndex = 0;

    private void assembleBird(float x, float y) {
        String birdType = "bird-" + MathUtils.random(1, 3);
        E().pos(x, y)
                .bounds(0, 0, 2, 2)
                .anim()
                .renderLayer(G.LAYER_BIRDS + birdIndex++)
                .animFlippedX(MathUtils.randomBoolean())
                .birdBrain()
                .birdBrainAnimIdle(birdType + "-idle")
                .birdBrainAnimFlying(birdType + "-flying")
                .gravityY(-0.2f)
                .physics()
                .teamTeam(2)
                .wallSensor();

    }

    private E assembleSpout(float x, float y, Integer angle, String spawns) {
        return E().pos(x, y).bounds(0, 0, 16, 16).spoutAngle(angle).spoutType(Spout.Type.valueOf(spawns));
    }

    private void assembleTrigger(float x, float y, String trigger, String parameter) {
        boolean tallTrigger = !trigger.equals("music");
        E().pos(x, y - (tallTrigger ? 5000 : 0)).bounds(0, 0, 16, (tallTrigger ? 10000 : 16)).trigger(trigger).triggerParameter(parameter);
    }

    private void assembleBatterySlot(float x, float y, boolean b, String batteryType) {
        E socket = E();
        socket.anim()
                .pos(x, y)
                .socketAnimSocketed("socket_on_" + batteryType)
                .socketAnimEmpty("socket_off_" + batteryType)
                .shieldHp(6)
                .mortal(true)
                .type(batteryType)
                .render(G.LAYER_PLAYER - 1)
                .bounds(-4, -4, G.CELL_SIZE+4, G.CELL_SIZE+4);

        if (b) {
            spawnBatteryInSocket(batteryType, socket);
        } else {
            powerSystem.powerMapCoordsAround((int) (x / G.CELL_SIZE + 0.5f), (int) (y / G.CELL_SIZE + 0.5f), false);
        }
    }

    public void spawnBatteryInSocket(String batteryType, E socket) {
        socketSystem.socket(assembleBattery(socket.posX(), socket.posY(), batteryType), socket);
        powerSystem.powerMapCoordsAround((int) (socket.posX() / G.CELL_SIZE + 0.5f), (int) (socket.posY() / G.CELL_SIZE + 0.5f), true);
    }

    Vector2 v2 = new Vector2();

    private void assemblePlayer(float x, float y, ShipData shipData) {
        int gracepaddingX = 16;
        int gracepaddingY = 4;
        E playerShip = E().anim("player-idle")
                .pos(x - 14, y)
                .physics()
                .render(G.LAYER_PLAYER)
                .mortal()
                //.gravity()
                .wallSensor()
                .player()
                .diesFromWalls(true)
                .teamTeam(G.TEAM_PLAYERS)
                .tag("player")
                .shipControlled()
                .shieldHp(shipData.hp)
                .shieldMaxHp(shipData.hp);

        E().anim("thruster")
                .pos(x,y)
                .attachedXo(14)
                .attachedYo(-18)
                .attachedParent(playerShip.id())
                .renderLayer(G.LAYER_PLAYER-1);

        E().anim("thruster")
                .pos(x,y)
                .attachedXo(26)
                .attachedYo(-18)
                .attachedParent(playerShip.id())
                .renderLayer(G.LAYER_PLAYER-1);


        gameScreenAssetSystem.boundToAnim(playerShip.id(), gracepaddingX, gracepaddingY);

        pickupSystem.upgradeGuns(playerShip);

        spawnCamera(playerShip, x, y);
    }

    private void spawnCamera(E playerShip, float x, float y) {
        E()
                .pos(x, y)
                .cameraFocus()
                .tag("camera")
                .physicsVy(G.CAMERA_SCROLL_SPEED)
                .ethereal(true)
                .physicsFriction(0);

    }

    public void addArsenal(E ship, String group, int team, int shipFacingAngle, String arsenal, boolean frozen) {
        if (arsenal != null) {
            ArsenalData data = arsenalDataSystem.get(arsenal);
            if (data.guns != null) {
                for (GunData gun : data.guns) {
                    addGun(ship, gun, group, team, shipFacingAngle, frozen);
                }
            }
        }
    }

    private void addGun(E e, GunData gunData, String group, int team, int shipFacingAngle, boolean frozen) {
        float angle = gunData.angle + shipFacingAngle + 90;
        v2.set(0, gunData.x).rotate(angle);
        E gun = E()
                .pos(e.posX(), e.posY())
                .bounds(0, 0, 5, 5)
                .group(group)
                .attachedXo((int) v2.x + (int) e.boundsCx() - 3)
                .attachedYo((int) v2.y + (int) e.boundsCy() - 3)
                .attachedParent(e.id())
                .teamTeam(team)
                .spoutAngle(0)
                .spoutType(Spout.Type.BULLET)
                .spoutSprayInterval(60f / gunData.rpm)
                .gunData(gunData)
                .angleRotate(angle);

        if ( gunData.cooldown > 0 ) {
            gun.spoutCooldown(gunData.cooldown);
        }
        if ( gunData.duration > 0 ) {
            gun.spoutSprayDuration(gunData.duration);
        }

        if (frozen) {
            gun.frozen();
        }

        if (team == TEAM_ENEMIES) {
            gun.shooting(true); // ai always shoots.
        }
    }

    private void assembleExit(float x, float y) {
        E().anim("exit")
                .pos(x, y)
                .render(G.LAYER_PLAYER - 100)
                .exit()
                .bounds(0, 0, 16, 16);
    }

    private E assembleBattery(float x, float y, String batteryType) {
        return E().anim(batteryType)
                .pos(x, y)
                .physics().pickup()
                .type(batteryType)
                .render(G.LAYER_PLAYER - 1)
                .gravity()
                .bounds(-8, -8, 24, 24)
                .wallSensor();
    }

    private E assembleRobot(float x, float y) {
        E robot = E().anim("robot-idle")
                .pos(x, y)
                .physics()
                .charge()
                .socketAnimSocketed(null)
                .socketAnimEmpty(null)
                .socketSfxSocketed("battery_eaten")
                .type("battery2")
                .running()
                .robot()
                .teamTeam(G.TEAM_PLAYERS)
                .render(G.LAYER_PLAYER_ROBOT)
                .follow()
                .footstepsStepSize(8)
                .footstepsSfx("footsteps_robot")
                .gravity()
                .platform()
                .bounds(0, 0, 39, 43)
                .tag("robot")
                .wallSensor();

        E().anim("robot-idle")
                .tag("robot-charge").pos(x, y).bounds(0, 0, 25, 12);

        return robot;
    }

    private void assembleEnemy(float x, float y, ShipData shipData) {
        int gracepaddingX = 2;
        int gracepaddingY = 0;
        E enemyShip = E()
                .pos(x, y)
                .physics()
                .physicsFriction(0)
                .mortal()
                .shipData(shipData)
                //.physicsVr(50)
                .angle()
                .deadly()
                .flightPatternData(flightPatternDataSystem.get(shipData.flight))
                .teamTeam(TEAM_ENEMIES)
                .render(G.LAYER_GREMLIN + shipData.layerOffset)
                .shieldHp(shipData.hp)
                .origin(shipData.originX, shipData.originY)
                .flying()
                .frozen()
                .anim(shipData.anim);

        if ( "boss".equals(shipData.id) ) {
            enemyShip.tag("boss");
        }

        gameScreenAssetSystem.boundToAnim(enemyShip.id(), gracepaddingX, gracepaddingY);
        enemyShip.pos(x - enemyShip.boundsCx(), y - enemyShip.boundsCy());

        addArsenal(enemyShip, "enemy-guns", G.TEAM_ENEMIES, -180, shipData.arsenal, true);
    }


    public void spawnGremlin(float x, float y) {
        E robot = E().anim("gremlin-1-idle")
                .pos(x, y)
                .physics()
                .mortal()
                .jumpAttack()
                .deadly()
                .render(G.LAYER_GREMLIN)
                .footstepsStepSize(4)
                .gravity()
                .bounds(0, 0, 24, 24)
                .wallSensor();
    }
}
