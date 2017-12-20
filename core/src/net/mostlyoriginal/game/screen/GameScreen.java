package net.mostlyoriginal.game.screen;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import com.evo.NEAT.Genome;
import net.mostlyoriginal.api.manager.FontManager;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraShakeSystem;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.physics.*;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.*;
import net.mostlyoriginal.game.system.detection.*;
import net.mostlyoriginal.game.system.map.*;
import net.mostlyoriginal.game.system.render.*;
import net.mostlyoriginal.game.system.view.*;
import net.mostlyoriginal.plugin.OperationsPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

    public static final String BACKGROUND_COLOR_HEX = "0000FF";
    private final Genome genome;
    private boolean updateFitness;

    public GameScreen(Genome genome, boolean updateFitness) {
        this.genome = genome;
        this.updateFitness = updateFitness;
    }

    @Override
    public void dispose() {
        super.dispose();
        if ( world != null ) {
            world.dispose();
            world=null;
        }
    }

    @Override
    protected World createWorld() {
        RenderBatchingSystem renderBatchingSystem;
        return new World(new WorldConfigurationBuilder()
                .dependsOn(EntityLinkManager.class, OperationsPlugin.class)
                .with(
                        new MinDeltaSystem(30),
                        new SuperMapper(),
                        new TagManager(),
                        new GroupManager(),

                        new GenomeSystem(genome, updateFitness),

                        new EntitySpawnerSystem(),
                        new MapSystem(),
                        new ParticleSystem(),
                        new PowerSystem(),
                        new DialogSystem(),

                        new GameScreenAssetSystem(),
                        new ArsenalDataSystem(),
                        new ShipDataSystem(),
                        new DialogDataSystem(),
                        new FlightPatternDataSystem(),
                        new GameScreenSetupSystem(),
                        new FontManager(),

                        // sensors.
                        new WallSensorSystem(),
                        new CollisionSystem(),

                        // spawn
                        new TriggerSystem(),
                        new SpoutSystem(),

                        // Control and logic.
                        new CameraUnfreezeSystem(),
                        new EnemyCleanupSystem(),
                        new FollowSystem(),
                        new FlightPatternControlSystem(),

                        new GenomeSensorSystem(),

                        new ShipControlSystem(),
                        new AttachmentSystem(),
                        new BirdBrainSystem(),

                        // Physics.
                        new GravitySystem(),
                        new MapCollisionSystem(),
                        new PlatformCollisionSystem(),
                        new PhysicsSystem(),

                        // Effects.
                        new FootstepSystem(),
                        new CarriedSystem(),
                        new SocketSystem(),
                        new PickupSystem(),

                        // Camera.
                        new CameraFollowSystem(),
                        new CameraShakeSystem(),
                        new CameraClampToMapSystem(),
                        new CameraSystem(G.CAMERA_ZOOM),
                        new PriorityAnimSystem(),

                        new JumpAttackSystem(),

                        new ClearScreenSystem(Color.valueOf("000000")),
                        new RenderBackgroundSystem(),
                        new MapRenderSystem(),

                        renderBatchingSystem = new RenderBatchingSystem(),
                        new MyAnimRenderSystem(renderBatchingSystem),
                        new BoundingBoxRenderSystem(renderBatchingSystem),
                        new MyLabelRenderSystem(renderBatchingSystem),
                        new AdditiveRenderSystem(),
                        new MapRenderInFrontSystem(),
                        new TerminalSystem(),
                        new DeathSystem(),
                        new HealthUISystem(),
                        new GenomeSensorUISystem(),
                        new TransitionSystem(GdxArtemisGame.getInstance())
                ).build());
    }

}
