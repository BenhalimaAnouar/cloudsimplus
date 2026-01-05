/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.vms.Vm;

public class AntColonyBroker implements VmSelectionPolicy{
    private final List<Double> pheromones;   // pheromone level for each VM
    private final double alpha = 1.0;        // pheromone importance
    private final double beta = 2.0;         // heuristic importance
    private final double evaporation = 0.1;  // pheromone evaporation rate
    private final Random rand = new Random();

    public AntColonyBroker(int numVms) {
        //super(simulation);
        pheromones = new ArrayList<>();
        for (int i = 0; i < numVms; i++) {
            pheromones.add(1.0); // initialize pheromones equally
        }
    }



    private void updatePheromones(int vmIndex) {
        // Evaporation
        for (int i = 0; i < pheromones.size(); i++) {
            pheromones.set(i, (1 - evaporation) * pheromones.get(i));
        }
        // Reinforce chosen VM
        pheromones.set(vmIndex, pheromones.get(vmIndex) + 1.0);
    }
    
    @Override
    public String toString() {
        return "Ant Colony";
    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
  
       
        if (vmList.isEmpty()) return Vm.NULL;

        // Compute probability for each VM
        double[] probs = new double[vmList.size()];
        double sum = 0.0;
        for (int i = 0; i < vmList.size(); i++) {
            double tau = Math.pow(pheromones.get(i), alpha);
            double eta = Math.pow(vmList.get(i).getMips(), beta); // heuristic: VM capacity
            probs[i] = tau * eta;
            sum += probs[i];
        }

        // Normalize probabilities
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }

        // Roulette-wheel selection
        double r = rand.nextDouble();
        double cumulative = 0.0;
        int chosenIndex = 0;
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r <= cumulative) {
                chosenIndex = i;
                break;
            }
        }

        // Update pheromones (reinforcement)
        updatePheromones(chosenIndex);

        return vmList.get(chosenIndex);

        
    }
}
