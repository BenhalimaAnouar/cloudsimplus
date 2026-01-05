package loadbalancig;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JoinIdleQueueBroker implements VmSelectionPolicy {

    private final Queue<Vm> idleVmQueue = new ConcurrentLinkedQueue<>();
    private final Random random = new Random();

    public JoinIdleQueueBroker() {
        //super(simulation);
    }

   
    
    protected void processCloudletReturn(org.cloudsimplus.core.events.SimEvent ev) {
        //super.processEvent(ev);
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        Vm vm = cloudlet.getVm();

        // When a cloudlet finishes, mark its VM as idle again
        if (!idleVmQueue.contains(vm)) {
            idleVmQueue.offer(vm);
        }
    }

    @Override
    public String toString() {
        return "Join Idle Queue (JIQ)";
    }

    @Override
    public Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
       
        //System.out.println("I am JQI");
        
        if (vmList.isEmpty()) return Vm.NULL;

        // Step 1: If we have idle VMs, assign to one of them
        Vm vm = idleVmQueue.poll();
        if (vm != null) {
            return vm;
        }

        // Step 2: Otherwise, use fallback (e.g., random or round-robin)
        return vmList.get(random.nextInt(vmList.size()));
    
    }
}
