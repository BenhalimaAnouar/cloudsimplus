/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.List;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * @author aaaaa
 */
class WeightedRoundRobinBroker implements VmSelectionPolicy {

  private int currentIndex = -1;
  private int currentWeightCount = 0;
  private final int[] weights;
  private int totalWeight = 0;

  public WeightedRoundRobinBroker(int[] weights) {
    // super(simulation);
    this.weights = null;
    for (int w : weights) {
      totalWeight += w;
    }
  }

  @Override
  public String toString() {
    return "Weighted Round Robin";
  }

  @Override
  public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {

    if (vmList.isEmpty()) {
      return Vm.NULL;
    }

    while (true) {
      currentIndex = (currentIndex + 1) % vmList.size();
      if (currentIndex == 0) {
        currentWeightCount = (currentWeightCount + 1) % totalWeight;
      }
      if (currentWeightCount < weights[currentIndex]) {
        return vmList.get(currentIndex);
      }
    }
  }
}
