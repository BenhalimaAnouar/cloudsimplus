/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.vms.Vm;

/**
 *
 * @author aaaaa
 */
public class MinMinBroker implements VmSelectionPolicy{
    private final Map<Vm, Double> vmAvailableTime = new HashMap<>();
    private final Set<Cloudlet> assignedCloudlets = new HashSet<>();

    

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
    // Initialize available times for all VMs
        if (vmAvailableTime.isEmpty()) {
            for (Vm vm : vmList) {
                vmAvailableTime.put(vm, 0.0);
            }
        }

        // Cloudlets waiting for scheduling
        List<Cloudlet> waitingCloudlets = new ArrayList<>(cloudlet.getBroker().getCloudletWaitingList());
        if (waitingCloudlets.isEmpty()) {
            waitingCloudlets.add(cloudlet); // fallback
        }

        Cloudlet selectedCloudlet = null;
        Vm selectedVm = null;
        double globalMinTime = Double.MAX_VALUE;

        for (Cloudlet c : waitingCloudlets) {
            if (assignedCloudlets.contains(c)) continue;

            double bestTime = Double.MAX_VALUE;
            Vm bestVm = null;

            for (Vm vm : vmList) {
                double completion = estimateCompletionTime(c, vm);
                if (completion < bestTime) {
                    bestTime = completion;
                    bestVm = vm;
                }
            }

            // For Min–Min, choose the overall *minimum* completion time
            if (bestTime < globalMinTime) {
                globalMinTime = bestTime;
                selectedCloudlet = c;
                selectedVm = bestVm;
            }
        }

        if (selectedVm == null) {
            selectedVm = vmList.get(0); // fallback
        }

        // Update VM availability and mark this cloudlet as assigned
        double completion = estimateCompletionTime(selectedCloudlet, selectedVm);
        vmAvailableTime.put(selectedVm, completion);
        assignedCloudlets.add(selectedCloudlet);

        // Return the selected VM for the current cloudlet
        return selectedVm;
    }
    


    private double estimateCompletionTime(Cloudlet cloudlet, Vm vm) {
        double availableTime = vmAvailableTime.getOrDefault(vm, 0.0);
        double execTime = cloudlet.getLength() / vm.getMips();
        return availableTime + execTime;
    }

   @Override
    public String toString() {
        return "Min Min ";
    }
}
