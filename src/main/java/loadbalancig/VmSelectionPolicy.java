/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package loadbalancig;

import java.util.List;
import org.cloudsimplus.cloudlets.Cloudlet;

import org.cloudsimplus.vms.Vm;

/**
 *
 * @author aaaaa
 */
public interface VmSelectionPolicy {
    
      Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList);
      
}
