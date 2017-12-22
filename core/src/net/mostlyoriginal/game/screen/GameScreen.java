package net.mostlyoriginal.game.screen;

import com.artemis.*;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.manager.FontManager;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraShakeSystem;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.physics.*;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.api.system.render.LabelRenderSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.system.*;
import net.mostlyoriginal.game.system.detection.*;
import net.mostlyoriginal.game.system.map.*;
import net.mostlyoriginal.game.system.render.*;
import net.mostlyoriginal.game.system.view.*;
import net.mostlyoriginal.plugin.OperationsPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

    public static final String BACKGROUND_COLOR_HEX = "0000FF";

    @Override
    protected World createWorld() {
        RenderBatchingSystem renderBatchingSystem;
        return new World(new WorldConfigurationBuilder()
                .dependsOn(
                        EntityLinkManager.class,
                        ProfilerPlugin.class,
                        OperationsPlugin.class,
                        FluidEntityPlugin.class)
                .with(
                        new SuperMapper(),
                        new TagManager(),
                        new GroupManager(),

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
                        new FarewellSystem(),
                        new SpoutSystem(),

                        // Control and logic.
                        new CameraUnfreezeSystem(),
                        new EnemyCleanupSystem(),
                        new FollowSystem(),
                        new FlightPatternControlSystem(),
                        new ShipControlSystem(),
                        new AttachmentSystem(),

                        // Physics.
                        new GravitySystem(),
                        new MapCollisionSystem(),
                        new PlatformCollisionSystem(),
                        new PhysicsSystem(),

                        // Effects.
                        new CarriedSystem(),
                        new SocketSystem(),
                        new PickupSystem(),

                        // Camera.
                        new CameraFollowSystem(),
                        new CameraShakeSystem(),
                        new CameraClampToMapSystem(),
                        new CameraSystem(G.CAMERA_ZOOM),
                        new PriorityAnimSystem(),

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
                        new ExitSystem(),
                        new DeathSystem(),
                        new HealthUISystem(),
                        new TransitionSystem(GdxArtemisGame.getInstance())
                ).build());
    }

}
