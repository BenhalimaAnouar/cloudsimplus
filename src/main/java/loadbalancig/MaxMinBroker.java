/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.vms.Vm;

/**
 *
 * @author aaaaa
 */
public class MaxMinBroker implements   VmSelectionPolicy {

private final Map<Vm, Double> vmAvailableTime = new HashMap<>();
    private final Set<Cloudlet> assignedCloudlets = new HashSet<>();

    

    private double estimateCompletionTime(Cloudlet cloudlet, Vm vm, double vmAvailableTime) {
        double execTime = cloudlet.getLength() / vm.getMips();
        return vmAvailableTime + execTime;
    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
     
    
        // Initialize VM availability if not done yet
        if (vmAvailableTime.isEmpty()) {
            for (Vm vm : vmList) {
                vmAvailableTime.put(vm, 0.0);
            }
        }

        // Apply Max–Min selection
        Vm bestVm = null;
        double minTime = Double.MAX_VALUE;
        Cloudlet selectedCloudlet = null;
        double maxMinTime = -1;

        for (Cloudlet c : cloudlet.getBroker().getCloudletSubmittedList()) {
            if (assignedCloudlets.contains(c)) continue;

            double bestCompletion = Double.MAX_VALUE;
            Vm bestForCloudlet = null;

            for (Vm vm : vmList) {
                double completion = estimateCompletionTime(c, vm);
                if (completion < bestCompletion) {
                    bestCompletion = completion;
                    bestForCloudlet = vm;
                }
            }

            // Select cloudlet with the *maximum* of those minimum completion times
            if (bestCompletion > maxMinTime) {
                maxMinTime = bestCompletion;
                selectedCloudlet = c;
                bestVm = bestForCloudlet;
            }
        }

        if (selectedCloudlet != null && bestVm != null) {
            double completion = estimateCompletionTime(selectedCloudlet, bestVm);
            vmAvailableTime.put(bestVm, completion);
            assignedCloudlets.add(selectedCloudlet);
            if (selectedCloudlet.equals(cloudlet)) {
                return bestVm;
            }
        }

        // Fallback if nothing matched
        return vmList.get(0);
    }

    private double estimateCompletionTime(Cloudlet cloudlet, Vm vm) {
        double availableTime = vmAvailableTime.getOrDefault(vm, 0.0);
        double execTime = cloudlet.getLength() / vm.getMips();
        return availableTime + execTime;
    }
        
    
@Override
    public String toString() {
        return "Max Min ";
    }
}
