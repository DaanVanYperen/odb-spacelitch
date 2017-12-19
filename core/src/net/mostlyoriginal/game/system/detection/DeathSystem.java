package net.mostlyoriginal.game.system.detection;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Attached;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.system.camera.CameraShakeSystem;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.physics.SocketSystem;
import net.mostlyoriginal.game.api.EBag;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.FollowSystem;
import net.mostlyoriginal.game.system.GenomeSystem;
import net.mostlyoriginal.game.system.ShipControlSystem;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;
import net.mostlyoriginal.game.system.map.EntitySpawnerSystem;
import net.mostlyoriginal.game.system.map.MapCollisionSystem;
import net.mostlyoriginal.game.system.render.MyAnimRenderSystem;
import net.mostlyoriginal.game.system.render.TransitionSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * @author Daan van Yperen
 */
public class DeathSystem extends FluidIteratingSystem {

    public TransitionSystem transitionSystem;
    private FollowSystem followSystem;
    private MapCollisionSystem mapCollisionSystem;
    private EntitySpawnerSystem entitySpawnerSystem;
    private ParticleSystem particleSystem;
    private GameScreenAssetSystem assetSystem;
    private DialogSystem dialogSystem;
    private CameraSystem cameraSystem;
    private EBag deadlies;
    private SocketSystem socketSystem;
    private CameraShakeSystem cameraShakeSystem;
    private ShipControlSystem shipControlSystem;
    private GenomeSystem genomeSystem;

    public DeathSystem() {
        super(Aspect.all(Pos.class).one(Mortal.class));
    }

    @Override
    protected void begin() {
        super.begin();
        deadlies = allEntitiesWith(Deadly.class);
    }

    Tint BLINK = new Tint(1f, 0f, 0f, 1f);
    Tint WHITE = new Tint(1f, 1f, 1f, 1f);

    @Override
    protected void process(E e) {
//
//        E enemy = touchingDeadlyStuffs(e, false);
//        if (enemy != null) {
//            e.chargeDecrease(0.2f);
//            particleSystem.bloodExplosion(enemy.posX() + enemy.boundsCx(), enemy.posY() + enemy.boundsCy());
//            enemy.deleteFromWorld();
//        }

        if (!e.hasDead()) {
            E deadlyStuffs = touchingDeadlyStuffs(e, false);

            if (mapCollisionSystem.isLava(e.posX(), e.posY()) || deadlyStuffs != null) {
                damage(e, deadlyStuffs, true, deadlyStuffs != null && deadlyStuffs.hasGun() ? deadlyStuffs.gunData().damage : 1);
            }

            float halfScreenWidth = (Gdx.graphics.getWidth() / G.CAMERA_ZOOM) * 0.5f + 16;

            if (e.hasRunning() && e.posX() + e.boundsMaxx() < cameraSystem.camera.position.x - halfScreenWidth) {
                e.dead();
            }

            if (e.hasDead() && e.isShipControlled()) {
                doExit();
                e.removeDead().removeMortal();
            }
        } else {
            e.deadCooldown(e.deadCooldown() - world.delta);
            if (!e.hasInvisible()) {
                particleSystem.explosion(e.posX() + e.boundsCx(), e.posY() + e.boundsCy());

                if ( e.hasShip() && e.shipData() != null ) {
                    if (e.shipData().corpseAnim != null) {
                        particleSystem.explosion(e.posX() + e.boundsCx() + MathUtils.random(-20f, 20f), e.posY() + e.boundsCy() + MathUtils.random(-20f, 20f));
                        particleSystem.explosion(e.posX() + e.boundsCx() + MathUtils.random(-20f, 20f), e.posY() + e.boundsCy() + MathUtils.random(-20f, 20f));
                        particleSystem.explosion(e.posX() + e.boundsCx() + MathUtils.random(-20f, 20f), e.posY() + e.boundsCy() + MathUtils.random(-20f, 20f));
                        E.E().posX(e.posX()).posY(e.posY()).anim(e.shipData().corpseAnim).renderLayer(e.renderLayer());
                    }

                    if ("boss".equals(e.shipData().id)) {
                        shipControlSystem.scrolling = true;
                        assetSystem.playMusicInGame("something1.mp3");
                    }

                    purgeGuns(e.id());
                }

                if (e.hasSocket()) {
                    e.socketEntityId(0);
                    socketSystem.power(e, false);
                    e.removeMortal();
                    return;
                }

                if (e.teamTeam() == 2) {
                    assetSystem.stopMusic();
                    assetSystem.playSfx("deathsound");
                    assetSystem.playSfx("death_jingle");
                } else {
                    assetSystem.playSfx("gremlin_death");
                }
                e.invisible();
            }
            if (e.deadCooldown() <= 0) {

                // unpower sockets.
                if (e.isShipControlled()) {
                    doExit();
                    e.removeDead().removeMortal();
                } else {
                    genomeSystem.addBonusFitness(100); // Bonus points for kills.
                    e.deleteFromWorld();
                }
            }

        }
    }

    private void purgeGuns(int id) {
        for (E e : allEntitiesWith(Attached.class)) {
            if ( e.attachedParent() == id )
                e.deleteFromWorld();
        }
    }

    private void damage(E victim, E cause, boolean damageCause, int damage) {

        // prevent same thing damaging the same thing twice.
        if (cause != null && cause.hasBounce()) {
            // do not bounce on same entity twice.
            if (cause.bounceLastEntityId() == victim.id())
                return;

            if (cause.bounceCount() > 0) {
                attemptBounce(cause, victim);
            }
        }

        if (victim.hasPlayer()) {
            if ( cause != null ){
                particleSystem.explosion(cause.posX() + cause.boundsCx(), cause.posY() + cause.boundsCy());
            }
            cameraShakeSystem.shake(damage/4 + 1);
        }

        if (victim.hasShield() && victim.shieldHp() > damage) {
            damageShield(victim, damage);
            if (cause != null && damageCause) {
                damage(cause, victim, false, 1);
            }
        } else {
            if (victim.hasBounce() && victim.bounceCount() > 0) {
                // do nothing.
            } else if (!victim.isMortal()) {
                victim.deleteFromWorld();
            } else victim.dead();
        }
    }

    private void damageShield(E victim, int damage) {
        victim.shieldHp(victim.shieldHp() - damage);
        victim.script(JamOperationFactory.tintBetween(BLINK, WHITE, 0.1f));
        if (victim.shipData() != null) {
            E.E()
                    .posX(victim.posX())
                    .posY(victim.posY())
                    .angleRotation(victim.angleRotation())
                    .originX(victim.originX())
                    .originY(victim.originY())
                    .renderLayer(victim.renderLayer() + 5)
                    .attachedParent(victim.id())
                    .glowAnim(victim.shipData().shieldAnim)
                    .attachedYo(-2)
                    .tint(1f, 1f, 1f, 0.5f)
                    .script(sequence(
                            delay(0.1f),
                            JamOperationFactory.tintBetween(WHITE, Tint.TRANSPARENT, 0.4f),
                            deleteFromWorld()
                    ))
                    .anim(victim.shipData().shieldAnim);
        }
    }


    Vector2 v2 = new Vector2();

    private void attemptBounce(E e, E victim) {
        e.bounceCount(e.bounceCount() - 1);
        v2.set(e.physicsVx(), e.physicsVy()).rotate(180);
        e.bounceLastEntityId(victim.id());
        e.physicsVx(v2.x);
        e.physicsVy(v2.y);
        mapCollisionSystem.alterBulletTeamAndColor(e);
    }

    private E touchingDeadlyStuffs(E e, boolean onlyMortals) {
        for (E o : deadlies) {
            if (o != e && !o.hasDead() && o.teamTeam() != e.teamTeam() && overlaps(o, e) && (!onlyMortals || o.hasMortal()))
                return o;
        }

        return null;
    }

    void doExit() {
        transitionSystem.transition(GameScreen.class, 0.0001f);
    }
}
