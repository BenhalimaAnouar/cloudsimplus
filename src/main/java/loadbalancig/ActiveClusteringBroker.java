package loadbalancig;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * A VM selection policy implementing an Active Clustering load balancing strategy.
 *
 * <p>The algorithm selects a random initiator VM, finds a neighbor VM with similar MIPS, and then
 * chooses the least loaded VM inside the cluster.
 */
public final class ActiveClusteringBroker implements VmSelectionPolicy {
  /** The threshold of cluster. */
  private static final double CLUSTER_THRESHOLD = 0.2;

  /** Random. */
  private final Random random = new Random();

  /** Default constructor. */
  public ActiveClusteringBroker() {}

  @Override
  public String toString() {
    return "Active Clustering";
  }

  /**
   * Selects a VM for a given Cloudlet using the Active Clustering load balancing strategy.
   *
   * @param cloudlet the cloudlet to schedule
   * @param vmList the list of available VMs
   * @return the selected VM or {@link Vm#NULL} if none exists
   */
  @Override
  public Vm selectVmForCloudlet(final Cloudlet cloudlet, final List<Vm> vmList) {

    if (vmList.isEmpty()) {
      return Vm.NULL;
    }

    final Vm initiator = vmList.get(random.nextInt(vmList.size()));

    final Vm neighbor =
        vmList.stream()
            .filter(vm -> vm != initiator)
            .min(
                (a, b) ->
                    Double.compare(
                        Math.abs(a.getMips() - initiator.getMips()),
                        Math.abs(b.getMips() - initiator.getMips())))
            .orElse(initiator);

    final Optional<Vm> bestVm =
        vmList.stream()
            .filter(
                vm ->
                    Math.abs(vm.getMips() - neighbor.getMips())
                        < neighbor.getMips() * CLUSTER_THRESHOLD)
            .min(
                (a, b) ->
                    Integer.compare(
                        a.getCloudletScheduler().getCloudletExecList().size()
                            + a.getCloudletScheduler().getCloudletWaitingList().size(),
                        b.getCloudletScheduler().getCloudletExecList().size()
                            + b.getCloudletScheduler().getCloudletWaitingList().size()));

    return bestVm.orElse(neighbor);
  }
}
