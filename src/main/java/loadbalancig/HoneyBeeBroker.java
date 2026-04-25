/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

public class HoneyBeeBroker implements VmSelectionPolicy {

  private final List<Double> fitness; // fitness (nectar) of each VM
  private final Random rand = new Random();
  private final double limit = 5; // scout threshold
  private final List<Integer> trial; // unsuccessful attempts per VM

  public HoneyBeeBroker(int numVms) {
    // super(simulation);
    fitness = new ArrayList<>();
    trial = new ArrayList<>();
    for (int i = 0; i < numVms; i++) {
      fitness.add(1.0); // initial nectar
      trial.add(0);
    }
  }

  @Override
  public String toString() {
    return "Honey Bee Foranging ";
  }

  @Override
  public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {

    if (vmList.isEmpty()) {
      return Vm.NULL;
    }

    // Employed bee phase: update nectar (fitness)
    updateFitness(vmList);

    // Onlooker bee phase: probabilistic selection
    double sumFit = fitness.stream().mapToDouble(Double::doubleValue).sum();
    double r = rand.nextDouble();
    double cumulative = 0.0;
    int chosenIndex = 0;

    for (int i = 0; i < vmList.size(); i++) {
      cumulative += fitness.get(i) / sumFit;
      if (r <= cumulative) {
        chosenIndex = i;
        break;
      }
    }

    // Scout bee phase: if a VM is stagnant, reset it
    if (trial.get(chosenIndex) > limit) {
      fitness.set(chosenIndex, 1.0); // reset nectar
      trial.set(chosenIndex, 0);
      chosenIndex = rand.nextInt(vmList.size()); // explore randomly
    } else {
      trial.set(chosenIndex, trial.get(chosenIndex) + 1);
    }

    return vmList.get(chosenIndex);
  }

  private void updateFitness(List<Vm> vmList) {
    for (int i = 0; i < vmList.size(); i++) {
      Vm vm = vmList.get(i);
      // Utilization = number of running cloudlets
      int runningCloudlets = vm.getCloudletScheduler().getCloudletExecList().size();

      // Nectar = capacity / (1 + runningCloudlets)
      double nectar = vm.getTotalMipsCapacity() / (1.0 + runningCloudlets);

      fitness.set(i, Math.max(nectar, 0.1)); // avoid zero
    }
  }
}
