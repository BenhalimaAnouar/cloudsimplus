/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadbalancig;

import java.util.List;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.vms.Vm;

/**
 *
 * @author aaaaa
 */
public class RoundRobinBroker implements VmSelectionPolicy {

    private int lastVmIndex = -1;

    public RoundRobinBroker() {

    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {

        //System.out.println("I am Round Robin ");
        if (vmList.isEmpty()) {
            return Vm.NULL;
        }
        lastVmIndex = (lastVmIndex + 1) % vmList.size();
        return vmList.get(lastVmIndex);
    }

    @Override
    public String toString() {
        return "Round Robin ";
    }

}
