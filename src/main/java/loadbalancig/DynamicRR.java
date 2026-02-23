/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.List;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.events.SimEvent;

import org.cloudsimplus.vms.Vm;

public class DynamicRR implements VmSelectionPolicy {

    private int lastVmIndex = -1;

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
        // Example: pick VM with least cloudlets assigned
        System.out.println("I am Dynamic Round Robin ");
        return vmList.stream()
                .min((vm1, vm2) -> Integer.compare(vm1.getCloudletScheduler().getCloudletList().size(),
                vm2.getCloudletScheduler().getCloudletList().size()))
                .orElse(Vm.NULL);
    }

    @Override
    public String toString() {
        return "Dynamic Round Robin";
    }
}
