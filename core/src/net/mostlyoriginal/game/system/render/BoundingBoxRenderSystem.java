package net.mostlyoriginal.game.system.render;

/**
 * @author Daan van Yperen
 */

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.component.basic.*;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Render;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;

/**
 * Render and progress animations.
 *
 * @author Daan van Yperen
 * @see Anim
 */
@All({Pos.class, Anim.class, Render.class})
@Exclude(Invisible.class)
public class BoundingBoxRenderSystem extends DeferredEntityProcessingSystem {

    protected M<Pos> mPos;
    protected M<Anim> mAnim;
    protected M<Tint> mTint;
    protected M<Angle> mAngle;
    protected M<Scale> mScale;
    protected M<Bounds> mBounds;
    protected M<Origin> mOrigin;

    protected CameraSystem cameraSystem;
    protected AbstractAssetSystem abstractAssetSystem;

    protected SpriteBatch batch;
    private Origin DEFAULT_ORIGIN= new Origin(0.5f, 0.5f);
    private Color DEFAULT_BOUNDINGBOX= new Color(0f,1f,0f,0.5f);

    boolean isEnabled = false;

    public BoundingBoxRenderSystem(EntityProcessPrincipal principal) {
        super(principal);
    }

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            isEnabled = !isEnabled;
        }
    }

    @Override
    protected void end() {
        batch.end();
    }

    protected void process(final int e) {
        if ( !isEnabled) return;

        final Anim anim   = mAnim.get(e);
        final Pos pos     = mPos.get(e);
        final Bounds bounds = mBounds.getSafe(e,null);
        final Angle angle = mAngle.getSafe(e, Angle.NONE);
        final float scale = mScale.getSafe(e, Scale.DEFAULT).scale;
        final Origin origin = mOrigin.getSafe(e, DEFAULT_ORIGIN);

        batch.setColor(DEFAULT_BOUNDINGBOX);

        if ( bounds != null ) {
            drawAnimation(anim, angle, origin, pos, "boundingbox",scale, bounds);
        }

        anim.age += world.delta * anim.speed;
    }

    /** Pixel perfect aligning. */
    public float roundToPixels(final float val) {
        // since we use camera zoom rounding to integers doesn't work properly.
        return ((int)(val * cameraSystem.zoom)) / (float)cameraSystem.zoom;
    }

    public void forceAnim(E e, String id) {
        Animation<TextureRegion> anim = abstractAssetSystem.get(id);
        e.priorityAnim(id, anim.getFrameDuration(), anim.getPlayMode() == Animation.PlayMode.LOOP);
        e.priorityAnimCooldown(anim.getFrameDuration() * anim.getKeyFrames().length);
        e.priorityAnimAge(0);
    }

    private void drawAnimation(final Anim animation, final Angle angle, final Origin origin, final Pos position, String id, float scale, Bounds bounds) {

        // don't support backwards yet.
        if ( animation.age < 0 ) return;

        final Animation<TextureRegion> gdxanim = (Animation<TextureRegion>) abstractAssetSystem.get(id);
        if ( gdxanim == null) return;

        final TextureRegion frame = gdxanim.getKeyFrame(animation.age, animation.loop);

        float ox = frame.getRegionWidth() * scale * origin.xy.x;
        float oy = frame.getRegionHeight() * scale * origin.xy.y;
        if ( animation.flippedX && angle.rotation == 0)
        {
            // mirror
            batch.draw(frame.getTexture(),
                    roundToPixels(position.xy.x + bounds.minx),
                    roundToPixels(position.xy.y + bounds.miny),
                    ox,
                    oy,
                    (bounds.maxx-bounds.minx) * scale,
                    (bounds.maxy-bounds.miny) * scale,
                    1f,
                    1f,
                    angle.rotation,
                    frame.getRegionX(),
                    frame.getRegionY(),
                    frame.getRegionWidth(),
                    frame.getRegionHeight(),
                    true,
                    false);

        } else {
            batch.draw(frame,
                    roundToPixels(position.xy.x + bounds.minx),
                    roundToPixels(position.xy.y + bounds.miny),
                    (bounds.maxx-bounds.minx) * scale,
                    (bounds.maxy-bounds.miny) * scale);
        }
    }
}
