package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evo.NEAT.Genome;
import net.mostlyoriginal.game.component.JumpAttack;
import net.mostlyoriginal.game.system.common.FluidIteratingSystem;

/**
 * @author Daan van Yperen
 */
public class GenomeSystem extends BaseSystem {

    private final Genome genome;
    private final boolean updateFitness;
    Vector2 v2 = new Vector2();
    private float bonusFitness;

    public GenomeSystem(Genome genome, boolean updateFitness) {
        this.genome = genome;
        this.updateFitness = updateFitness;
    }

    public float[] evaluate(float[] inputs) {
        return genome.evaluateNetwork(inputs);
    }

    public void addBonusFitness(float offset)  {
        bonusFitness += offset;
    }

    public void setFitness(float fitness) {
        if ( updateFitness) {
            genome.setFitness(fitness + bonusFitness);
        }
    }

    @Override
    protected void processSystem() {
        addBonusFitness(-world.delta*0.01f); // penalize on time.
    }
}
