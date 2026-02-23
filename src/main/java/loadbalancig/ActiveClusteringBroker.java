/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.vms.Vm;

public class ActiveClusteringBroker implements VmSelectionPolicy {

    private final Random random = new Random();

    public ActiveClusteringBroker() {
        // super(simulation);
    }

    @Override
    public String toString() {
        return "Active Clustering ";
    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {

        if (vmList.isEmpty()) {
            return Vm.NULL;
        }

        // Step 1: Pick a random VM
        Vm initiator = vmList.get(random.nextInt(vmList.size()));

        // Step 2: Find neighbor (here: VM with closest MIPS to initiator)
        Vm neighbor = vmList.stream()
                .filter(vm -> vm != initiator)
                .min((a, b) -> Double.compare(Math.abs(a.getMips() - initiator.getMips()),
                Math.abs(b.getMips() - initiator.getMips())))
                .orElse(initiator);

        // Step 3: Among neighbor’s "cluster", pick least loaded VM
        Optional<Vm> bestVm = vmList.stream()
                .filter(vm -> Math.abs(vm.getMips() - neighbor.getMips()) < neighbor.getMips() * 0.2) // same cluster
                .min((a, b) -> Integer.compare(
                a.getCloudletScheduler().getCloudletExecList().size()
                + a.getCloudletScheduler().getCloudletWaitingList().size(),
                b.getCloudletScheduler().getCloudletExecList().size()
                + b.getCloudletScheduler().getCloudletWaitingList().size()
        ));

        return bestVm.orElse(neighbor);

    }
}
