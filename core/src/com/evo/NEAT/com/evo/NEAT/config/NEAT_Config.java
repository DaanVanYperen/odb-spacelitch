package com.evo.NEAT.com.evo.NEAT.config;

import net.mostlyoriginal.game.system.GenomeSensorSystem;
import net.mostlyoriginal.game.system.ShipControlSystem;

/**
 * Created by vishnughosh on 01/03/17.
 */
public class NEAT_Config {

    public static final int INPUTS = GenomeSensorSystem.HEIGHT*GenomeSensorSystem.WIDTH*3;
    public static final int OUTPUTS = ShipControlSystem.CONTROL_COUNT;
    public static final int HIDDEN_NODES = 3;
    public static final int POPULATION = 50;

    public static final float COMPATIBILITY_THRESHOLD = 1;
    public static final float EXCESS_COEFFICENT = 2;
    public static final float DISJOINT_COEFFICENT = 2;
    public static final float WEIGHT_COEFFICENT = 0.4f;

    public static final float STALE_SPECIES = 2;

    public static final float STEPS = 0.1f;
    public static final float PERTURB_CHANCE = 0.4f;
    public static final float WEIGHT_CHANCE = 0.3f;
    public static final float WEIGHT_MUTATION_CHANCE = 0.5f;
    public static final float NODE_MUTATION_CHANCE = 0.03f;
    public static final float CONNECTION_MUTATION_CHANCE = 0.05f;
    public static final float BIAS_CONNECTION_MUTATION_CHANCE = 0.15f;
    public static final float DISABLE_MUTATION_CHANCE = 0.1f;
    public static final float ENABLE_MUTATION_CHANCE = 0.2f;
    public static final float CROSSOVER_CHANCE = 0.75f;

    public static final int STALE_POOL = 20;
}
