package net.mostlyoriginal.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.evo.NEAT.Environment;
import com.evo.NEAT.Genome;
import com.evo.NEAT.Pool;
import com.evo.NEAT.Species;
import net.mostlyoriginal.game.component.G;
import net.mostlyoriginal.game.component.Settings;
import net.mostlyoriginal.game.screen.GameScreen;

import java.util.ArrayList;

import static com.badlogic.gdx.Input.Keys.F11;

public abstract class GdxArtemisGame extends Game {

    private static GdxArtemisGame instance;
    private ArrayList<Genome> allGenome;

    public abstract void slowFramerate();

    public abstract void fastFramerate();

    public class SpaceEnvironment implements Environment {

        @Override
        public void evaluateFitness(ArrayList<Genome> population) {
            for (Genome genome : population) {
            }
        }
    }

    private Genome topGenome = new Genome();
    private int generation = 0;
    private int index = 0;
    private Pool pool = new Pool();

    @Override
    public void create() {
        instance = this;
        G.settings = (new Json()).fromJson(Settings.class, Gdx.files.internal("settings.json"));

        pool.initializePool();

        nextCycle();
        testNextGenomeFitness();
    }

    @Override
    public void render() {
        super.render();
        if (Gdx.input.isKeyJustPressed(F11) && topGenome != null) {
            slowFramerate();
            setScreen(new GameScreen(topGenome, false));
        }
    }

    private void nextCycle() {
        index = 0;
        if (allGenome != null) {
            pool.rankGlobally();
            topGenome = pool.getTopGenome();
            if (topGenome.getPoints() > 90000) {
                setScreen(null);
                return;
            }
            System.out.println("Generation: " + generation + " Fitness: " + topGenome.getPoints());
            pool.breedNewGeneration();
            generation++;
        }

        allGenome = new ArrayList<>();
        for (Species s : pool.getSpecies()) {
            allGenome.addAll(s.getGenomes());
        }
    }

    public void testNextGenomeFitness() {
        G.level = G.settings.startingLevel;
        Genome genome = allGenome.get(index);

        fastFramerate();
        setScreen(new GameScreen(genome, true));
        if (++index >= allGenome.size()) {
            nextCycle();
        }
    }

    public static GdxArtemisGame getInstance() {
        return instance;
    }
}
