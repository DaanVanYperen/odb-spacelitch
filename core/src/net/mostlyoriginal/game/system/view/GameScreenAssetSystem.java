package net.mostlyoriginal.game.system.view;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.SpriteData;
import net.mostlyoriginal.game.system.dal.SpriteLibrary;

import static net.mostlyoriginal.game.component.G.DEBUG_NO_MUSIC;

/**
 * @author Daan van Yperen
 */
public class GameScreenAssetSystem extends AbstractAssetSystem {

    private SpriteLibrary spriteLibrary;
    private Music music;
    public static final int TILE_SIZE = 32;
    public static final int SMALL_TILE_SIZE = 16;
    public static final int GIANT_TILE_SIZE = 48;

    public GameScreenAssetSystem() {
        super("tileset.png");
        loadSprites();
        loadSounds(
                new String[]{
                        "Explosion_1",
                        "Explosion_2",
                        "Explosion_3",
                        "Explosion_4",
                        "Explosion_5",
                        "bounce_1",
                        "bounce_2",
                        "bounce_3",
                        "bounce_4",
                        "Misc_1",
                        "Misc_2",
                        "bang",
                        "beepboop",
                        "boom",
                        "Detect_1",
                        "Laser_shoot_1",
                        "pew",
                        "woosh_1",
                }
        );

        Texture tiles = new Texture("tileset.png");

//        add("battery", 320, 192, 32,32, 1, 1, tileset);

//        add("power-on", 320-64, 192, 32,32, 1, 1, tileset);
//        add("power-off", 320-32, 192, 32,32, 1, 1, tileset);
//
//        add("player-idle", 258, 0, SMALL_TILE_SIZE, SMALL_TILE_SIZE, 2, 1, tileset);
//        add("player-jetpack", 258 + SMALL_TILE_SIZE * 2, 0, SMALL_TILE_SIZE, SMALL_TILE_SIZE, 1, 1, tileset);
//        add("player-walk", 258 + SMALL_TILE_SIZE * 3, 0, SMALL_TILE_SIZE, SMALL_TILE_SIZE, 4, 1, tileset, 0.2f);
//        add("player-respawning", 258 + SMALL_TILE_SIZE * 7, 0, SMALL_TILE_SIZE, SMALL_TILE_SIZE, 1, 1, tileset);
//
//        add("robot-idle", 458, 0, TILE_SIZE, GIANT_TILE_SIZE, 2, 1, tileset);
//        add("robot-jetpack", 458+TILE_SIZE * 2, 0, TILE_SIZE, GIANT_TILE_SIZE, 1, 1, tileset);
//        add("robot-walk", 458+TILE_SIZE * 3, 0, TILE_SIZE, GIANT_TILE_SIZE, 4, 1, tileset, 0.2f);
//        add("robot-respawning", 458+TILE_SIZE * 7, 0, TILE_SIZE, GIANT_TILE_SIZE, 1, 1, tileset);

//        add("debug-marker", 44, 51, 3, 3, 1, 1,tileset);

        //playMusicTitle();
    }

    private void playMusicTitle() {
        if (DEBUG_NO_MUSIC) return;
        if (music != null) music.stop();
        music = Gdx.audio.newMusic(Gdx.files.internal("sfx/something1.mp3"));
        music.setLooping(true);
        music.play();
        music.setPan(0, 0.1f);

        if (!G.DEBUG_SKIP_INTRO) {
            playSfx("LD_troop_prologue");
        }
    }

    public void playMusicInGame(String song) {
        if (DEBUG_NO_MUSIC) return;
        if (music != null) music.stop();
        music = Gdx.audio.newMusic(Gdx.files.internal("Music/" + song));
        music.setLooping(true);
        music.play();
        music.setPan(0, 0.12f);
    }

    public void stopMusic() {
        if ( music != null ) music.stop();
    }

    @Override
    protected void initialize() {
        super.initialize();
        playMusicInGame("something1.mp3");
    }

    private void loadSprites() {
        final Json json = new Json();
        spriteLibrary = json.fromJson(SpriteLibrary.class, Gdx.files.internal("sprites.json"));
        for (SpriteData sprite : spriteLibrary.sprites) {
            Animation animation = add(sprite.id, sprite.x, sprite.y, sprite.width, sprite.height, sprite.countX, sprite.countY, this.tileset, sprite.milliseconds * 0.001f);
            if ( !sprite.repeat ) animation.setPlayMode(Animation.PlayMode.NORMAL);
        }
    }


    public void boundToAnim(int entity, int gracepaddingX, int gracepaddingY) {
        E e = E.E(entity);
        TextureRegion frame = ((Animation<TextureRegion>) get(e.animId())).getKeyFrame(0);
        e.bounds(gracepaddingX, gracepaddingY, frame.getRegionWidth() -gracepaddingX, frame.getRegionHeight()-gracepaddingY);
    }
}
