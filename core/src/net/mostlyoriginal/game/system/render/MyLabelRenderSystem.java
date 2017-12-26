package net.mostlyoriginal.game.system.render;

/**
 * @author Daan van Yperen
 */

import com.artemis.Aspect;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Render;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.ui.BitmapFontAsset;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;

/**
 * Basic label renderer.
 *
 * @author Daan van Yperen
 * @see Label
 */
@All({Pos.class, Label.class, Render.class, BitmapFontAsset.class})
@Exclude(Invisible.class)
public class MyLabelRenderSystem extends DeferredEntityProcessingSystem {

    protected M<Pos> mPos;
    protected M<Label> mLabel;
    protected M<Tint> mTint;
    protected M<BitmapFontAsset> mBitmapFontAsset;

    protected CameraSystem cameraSystem;

    protected SpriteBatch batch;
    private GlyphLayout glyphLayout = new GlyphLayout();

    public MyLabelRenderSystem(EntityProcessPrincipal principal) {
        super(principal);
        batch = new SpriteBatch(1000);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    protected void process(final int e) {

        final Label label = mLabel.get(e);
        final Pos pos = mPos.get(e);

        if (label.text != null) {


            final BitmapFont font = mBitmapFontAsset.get(e).bitmapFont;

            batch.setColor(mTint.getSafe(e, Tint.WHITE).color);

            switch (label.align) {
                case LEFT:
                    font.draw(batch, label.text, pos.xy.x, pos.xy.y);
                    break;
                case RIGHT:
                    glyphLayout.setText(font, label.text);
                    font.draw(batch, label.text, pos.xy.x - glyphLayout.width, pos.xy.y);
                    break;
            }
        }
    }
}
