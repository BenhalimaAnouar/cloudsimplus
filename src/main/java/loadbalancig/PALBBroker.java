/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.cloudsimplus.cloudlets.Cloudlet;

import org.cloudsimplus.vms.Vm;

public class PALBBroker implements VmSelectionPolicy {

    private final Random random = new Random();
    private final double explorationProbability; // chance to explore random VM

    public PALBBroker(double explorationProbability) {
        //super(simulation);
        this.explorationProbability = explorationProbability; // e.g., 0.2 = 20% random assignment
    }

    @Override
    public String toString() {
        return "PAlB Algorithm ";
    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {

        if (vmList.isEmpty()) {
            return Vm.NULL;
        }

        // Exploration: assign to random VM with probability
        if (random.nextDouble() < explorationProbability) {
            return vmList.get(random.nextInt(vmList.size()));
        }

        // Exploitation: assign to least loaded VM (fewest Cloudlets queued)
        Optional<Vm> bestVm = vmList.stream()
                .min(Comparator.comparingInt(vm -> vm.getCloudletScheduler().getCloudletExecList().size()
                + vm.getCloudletScheduler().getCloudletWaitingList().size()));
        return bestVm.orElse(vmList.get(0));
    }
}
