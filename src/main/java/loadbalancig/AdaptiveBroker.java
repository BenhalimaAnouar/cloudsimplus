/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 /**
 * AdaptiveBroker dynamically switches VM selection policies
 * based on live performance metrics such as throughput and response time.

It uses configuration thresholds (deadline, tolerance, response time)
 * to determine when to adjust scheduling behavior via IaCloudApi.

* The {@code AdaptiveBroker} class extends {@link DatacenterBrokerSimple}
 * to provide dynamic and intelligent VM selection and scheduling
 * in CloudSim Plus simulations.
 *
 * <p>This broker monitors live system metrics such as:
 * <ul>
 *   <li>Throughput</li>
 *   <li>Average response time</li>
 *   <li>Resource utilization (CPU)</li>
 *   <li>Tolerance satisfaction rate</li>
 * </ul>
 *
 * Based on these metrics, it automatically adapts the VM selection
 * policy at runtime using the {@code IaCloudApi} component.
 * The decision logic is guided by thresholds defined in a
 * configuration file ({@code config.properties}) loaded via {@link ConfigManager}.
 *
 * <p>Typical use case:
 * <pre>{@code
 * CloudSimPlus sim = new CloudSimPlus();
 * VmSelectionPolicy initialPolicy = new VmSelectionPolicySimple();
 * AdaptiveBroker broker = new AdaptiveBroker(sim, initialPolicy);
 * broker.submitVmList(vms);
 * broker.submitCloudletList(cloudlets);
 * sim.start();
 * }</pre>
 *
 * <p>This class is mainly designed for experimental load balancing
 * and adaptive scheduling research.




 */
package loadbalancig;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

public class AdaptiveBroker extends DatacenterBrokerSimple {

  /** The averge of host utilization. */
  private static double avgHostUtilization;

  /** Data-center utilization. */
  private static double datacenterUtilization;

  /** The selected policy. */
  private VmSelectionPolicy policy;

  /** Time Threshold. */
  // private double timeThreshold = -1;
  /** throughput Threshold. */
  private double throughputThreshold = -1;

  /** CloudSimPlus simulation instance. */
  private CloudSimPlus simulation;

  /** Total of current cloudlets. */
  private int totalCloudlets;

  /** Averge response of the cloudlets. */
  private double avgResponse;

  /** Averge waiting of the cloudlets. */
  private double avgWaiting;

  /** Averge Turnaround of the cloudlets. */
  private double avgTurnaround;

  /** Current throughput. */
  private double throughput;

  /** Current makespan. */
  private double makespan;

  /** Temporary varriable of loaded metrics. */
  private static int[] temp;

  /** The file name config. */
  private static final String CONFIG_PATH = "config.properties";

  /** deadline used to calculate tolerance. */
  public static final double DEADLINE;

  /** Threshold of tolerance , it used to account failed executed cloudlets. */
  public static final double TOLERANCE_THRESHOLD;

  /** Threshold of response time. */
  public static final double RESPONSE_TIME_THRESHOLD;

  /** Current ressource utilization. */
  public static final double RESSOURCE_UTILIZATION;

  /** Current tolerance Threshold. */
  public static final double TOLERANCE;

  /** Flag to activate or deactivate reading throughput metric. */
  public static double FLAG_TROUGHTPUT = 0;

  /** Flag to activate or deactivate reading tolerance metric. */
  public static double FLAG_TOLERANCE = 0;

  /** Flag to activate or deactivate reading ressource utilization metric. */
  public static double FLAG_RU = 0;

  /** Flag to activate or deactivate reading response metric. */
  public static double FLAG_RESPONSE_TIME = 0;

  /**
   * Gets the current average CPU utilization of the entire datacenter.
   *
   * <p>This value represents the aggregated utilization calculated from all hosts in the datacenter
   * during the simulation.
   *
   * @return the average datacenter CPU utilization percentage.
   */
  public static double getDatacenterUtilization() {
    return datacenterUtilization;
  }

  /**
   * Sets the average CPU utilization of the datacenter.
   *
   * <p>This value is typically computed dynamically during simulation when evaluating host resource
   * usage.
   *
   * @param datacenterUtilization the calculated average CPU utilization of the datacenter.
   */
  public static void setDatacenterUtilization(double datacenterUtilization) {
    AdaptiveBroker.datacenterUtilization = datacenterUtilization;
  }

  /**
   * Gets the average CPU utilization of all hosts in the datacenter.
   *
   * <p>The value is calculated based on the CPU usage of VMs running on each host.
   *
   * @return the average host CPU utilization percentage.
   */
  public static double getAvgHostUtilization() {
    return avgHostUtilization;
  }

  /**
   * Updates the average CPU utilization value of hosts in the datacenter.
   *
   * <p>This value is used by the adaptive scheduling logic to determine when resource utilization
   * thresholds are reached.
   *
   * @param avgHostUtilization the calculated average CPU utilization across all hosts.
   */
  public static void setAvgHostUtilization(double avgHostUtilization) {
    AdaptiveBroker.avgHostUtilization = avgHostUtilization;
  }

  static {
    double deadline = -1;
    double toleranceMax = -1;
    double tolerance = -1;
    double responseTime = -1;
    double ressourcecpu = -1;

    try {
      ConfigManager ctx = new ConfigManager(CONFIG_PATH);
      deadline = ctx.getDouble("deadline", -1);
      tolerance = ctx.getDouble("tolerance", -1);
      toleranceMax = ctx.getDouble("toleranceMax", -1);
      responseTime = ctx.getDouble("responseTimeMAX", -1);
      ressourcecpu = ctx.getDouble("ressourceCpuMax", -1);
      // System.out.println("Loaded thresholds from config: deadline=" + deadline + ", tolerance=" +
      // tolerance);
    } catch (IOException e) {
      System.err.println("️ Failed to load config thresholds: " + e.getMessage());
    }

    RESSOURCE_UTILIZATION = ressourcecpu;
    DEADLINE = deadline;
    TOLERANCE = tolerance;
    TOLERANCE_THRESHOLD = toleranceMax;
    RESPONSE_TIME_THRESHOLD = responseTime;
  }

  /**
   * Constructs an {@code AdaptiveBroker} instance with a given simulation and an initial VM
   * selection policy.
   *
   * @param simulation the {@link CloudSimPlus} simulation instance.
   * @param initialPolicy the initial {@link VmSelectionPolicy} to apply.
   * @throws IOException if configuration loading fails.
   */
  public AdaptiveBroker(CloudSimPlus simulation, VmSelectionPolicy initialPolicy)
      throws IOException {

    /**
     * Dynamically registers a listener that reacts to every clock tick. It continuously evaluates
     * live metrics and adjusts scheduling when thresholds are crossed.
     *
     * <p>Metrics evaluated:
     *
     * <ul>
     *   <li>Throughput
     *   <li>Response time
     *   <li>Tolerance ratio
     *   <li>Resource utilization
     * </ul>
     *
     * @throws IOException if metric evaluation or configuration access fails.
     */
    super(simulation);

    this.simulation = simulation;

    if (initialPolicy != null) {
      this.policy = initialPolicy;

    } else {

      IaCloudApi.init();

      System.out.println(
          "Init Policy based on your configuration  :"
              + IaCloudApi.getOptimizedAlgorithm().get("result"));
      this.policy =
          LoadBalancerFactory.getFactoryPolicy(IaCloudApi.getOptimizedAlgorithmAsNumber());
      try {
        ConfigManager ctx = new ConfigManager("config.properties");

        // timeThreshold = Integer.parseInt(ctx.getString("timeMax"));
        throughputThreshold = ctx.getDouble("throughputMax", -1);
        // System.out.println("HH"+ ctx.getString("responseTimeMAX"));
        // responseTimeThreshold=ctx.getDouble("responseTimeMAX",-1);

        addMetricsCondition();

      } catch (IOException ex) {
        System.err.println("");
      }
    }

    displayThresholdsMetrics();
  }

  /**
   * Gets the total number of Cloudlets that have finished execution and were used to compute
   * performance metrics.
   *
   * @return the total number of processed Cloudlets.
   */
  public double getTotalCloudlets() {
    return totalCloudlets;
  }

  /**
   * Gets the average response time of all finished Cloudlets.
   *
   * <p>Response time is defined as the time between a Cloudlet's arrival at the datacenter and its
   * completion.
   *
   * @return the average response time in seconds.
   */
  public double getAvgResponse() {
    return avgResponse;
  }

  /**
   * Sets the average response time value.
   *
   * @param avgResponse the computed average response time of finished Cloudlets.
   */
  public void setAvgResponse(double avgResponse) {
    this.avgResponse = avgResponse;
  }

  /**
   * Gets the average waiting time of all Cloudlets.
   *
   * <p>Waiting time represents the time a Cloudlet spends in the queue before starting execution.
   *
   * @return the average waiting time in seconds.
   */
  public double getAvgWaiting() {
    return avgWaiting;
  }

  /**
   * Sets the average waiting time value.
   *
   * @param avgWaiting the computed average waiting time of Cloudlets.
   */
  public void setAvgWaiting(double avgWaiting) {
    this.avgWaiting = avgWaiting;
  }

  /**
   * Gets the average turnaround time of all Cloudlets.
   *
   * <p>Turnaround time is the total time from Cloudlet submission until its completion.
   *
   * @return the average turnaround time in seconds.
   */
  public double getAvgTurnaround() {
    return avgTurnaround;
  }

  /**
   * Sets the average turnaround time value.
   *
   * @param avgTurnaround the computed average turnaround time.
   */
  public void setAvgTurnaround(double avgTurnaround) {
    this.avgTurnaround = avgTurnaround;
  }

  /**
   * Gets the current throughput of the system.
   *
   * <p>Throughput is calculated as the number of completed Cloudlets divided by the total
   * simulation makespan.
   *
   * @return the throughput value (Cloudlets per second).
   */
  public double getThroughput() {
    return throughput;
  }

  /**
   * Updates the throughput value.
   *
   * @param throughput the calculated throughput of the system.
   */
  public void setThroughput(double throughput) {
    this.throughput = throughput;
  }

  /**
   * Gets the makespan of the simulation.
   *
   * <p>Makespan is defined as the total time between the start of the first Cloudlet execution and
   * the completion of the last one.
   *
   * @return the makespan in seconds.
   */
  public double getMakespan() {
    return makespan;
  }

  /**
   * Sets the makespan value.
   *
   * @param makespan the calculated makespan of the simulation.
   */
  public void setMakespan(double makespan) {
    this.makespan = makespan;
  }

  /**
   * Displays the configured threshold values used by the adaptive scheduling mechanism.
   *
   * <p>The thresholds include:
   *
   * <ul>
   *   <li>Throughput threshold
   *   <li>Average response time threshold
   *   <li>Tolerance threshold and deadline
   *   <li>Maximum resource utilization
   * </ul>
   *
   * <p>These values are loaded from the {@code config.properties} configuration file and used to
   * determine when scheduling policies should be dynamically adjusted.
   */
  public void displayThresholdsMetrics() {
    System.out.println("Throughput Threshold: " + throughputThreshold);
    System.out.println("Response Time Threshold (Average): " + RESPONSE_TIME_THRESHOLD);
    System.out.println(
        "Tolerance Threshold: " + TOLERANCE_THRESHOLD + " with Deadline " + DEADLINE);
    System.out.println("Resource Utilization (%): " + RESSOURCE_UTILIZATION);
  }

  /**
   * Calculates all key performance metrics from finished Cloudlets.
   *
   * @implNote This method updates average response time, waiting time, turnaround time, throughput,
   *     and makespan.
   */
  protected void addMetricsCondition() throws IOException {

    simulation.addOnClockTickListener(
        (info) -> {

          // System.out.println("Add Onclick Tick Listners ....");
          AdaptiveBroker.temp = IaCloudApi.getMetrics().clone();
          try {
            CalculMetrics();

            if (RESSOURCE_UTILIZATION != 1
                && RESSOURCE_UTILIZATION <= getAvgHostUtilization()
                && FLAG_RU == 0) {
              IaCloudApi.adjustMetrics(6);
              FLAG_RU = 1;
            } else if (RESSOURCE_UTILIZATION > getThroughput()) {
              FLAG_RU = 0;
            }

            if (RESPONSE_TIME_THRESHOLD != 1
                && RESPONSE_TIME_THRESHOLD <= getAvgResponse()
                && FLAG_RESPONSE_TIME == 0) {
              IaCloudApi.adjustMetrics(5);
              FLAG_RESPONSE_TIME = 1;
            } else if (RESPONSE_TIME_THRESHOLD > getThroughput()) {
              FLAG_RESPONSE_TIME = 0;
            }

            // System.out.println("TEST"+ TOLERANCE_THRESHOLD);
            // System.out.println(getTolerance(DEADLINE,TOLERANCE));
            if (TOLERANCE_THRESHOLD != 1
                && TOLERANCE_THRESHOLD <= getTolerance(DEADLINE, TOLERANCE)
                && FLAG_TOLERANCE == 0) {
              IaCloudApi.adjustMetrics(3);
              FLAG_TOLERANCE = 1;

            } else if (TOLERANCE_THRESHOLD > getThroughput()) {
              FLAG_TOLERANCE = 0;
            }

            if (throughputThreshold != 1
                && getThroughput() != 0
                && throughputThreshold <= getThroughput()
                && FLAG_TROUGHTPUT == 0) {
              // temp=IaCloudApi.getMetrics();
              IaCloudApi.adjustMetrics(1);
              FLAG_TROUGHTPUT = 1;
              System.out.println("HERE");
            } else if (throughputThreshold > getThroughput()) {
              FLAG_TROUGHTPUT = 0;
            }

            if (!IaCloudApi.ifMetricsChanged(temp)) {

              // temp=IaCloudApi.getMetrics();
              // System.out.println("Changing To  :"+
              // IaCloudApi.getOptimizedAlgorithm().get("result"));
              IaCloudApi.init();
              setPolicy(
                  LoadBalancerFactory.getFactoryPolicy(IaCloudApi.getOptimizedAlgorithmAsNumber()));

              List<Cloudlet> unfinished = resendUnfinishedCloudlets();

              for (Cloudlet c : unfinished) {
                if (c instanceof CustomCloudlet) {
                  ((CustomCloudlet) c)
                      .setPolicy(
                          LoadBalancerFactory.getFactoryPolicy(
                                  IaCloudApi.getOptimizedAlgorithmAsNumber())
                              .toString());
                }
              }

              if (unfinished.isEmpty()) {
                System.out.printf(
                    "[%.2f] ✅ No unfinished Cloudlets to resend%n", getSimulation().clock());
              }

              submitCloudletList(unfinished);
            }
          } catch (Exception ex) {
            // Logger.getLogger(AdaptiveBroker.class.getName()).log(Level.SEVERE, null, ex);
          }
        });
  }

  /**
   * public List<Cloudlet> getRunningCloudlets() { return getCloudletSubmittedList() .stream()
   * .filter(cl -> cl.getStatus() == CloudletStatus.INEXEC) .collect(Collectors.toList()); }
   */
  /**
   * Calculates the average response, waiting, and turnaround times for all finished Cloudlets, and
   * updates simulation metrics.
   *
   * @return void
   */
  public void CalculMetrics() {
    getDatacenterUtilizationCpu();
    List<Cloudlet> finished = getCloudletFinishedList();

    if (finished.isEmpty()) {
      System.out.println("️ No finished Cloudlets found!");
      return;
    }

    double totalResponse = 0;
    double totalWaiting = 0;
    double totalTurnaround = 0;

    double firstStart = finished.stream().mapToDouble(Cloudlet::getStartTime).min().orElse(0.0);

    double lastFinish = finished.stream().mapToDouble(Cloudlet::getFinishTime).max().orElse(0.0);

    makespan = lastFinish - firstStart;

    System.out.println("\n========== Cloudlet Metrics ==========");
    System.out.printf(
        "%-10s %-15s %-15s %-15s %-15s %-15s %-20s%n",
        "Cloudlet", "Start", "Finish", "Response", "Waiting", "Turnaround", "Policy");

    for (Cloudlet c : finished) {
      double response = c.getFinishTime() - c.getDcArrivalTime();
      double waiting = c.getStartTime() - c.getDcArrivalTime();
      double turnaround = c.getFinishTime() - c.getDcArrivalTime();

      totalResponse += response;
      totalWaiting += waiting;
      totalTurnaround += turnaround;

      System.out.printf(
          "%-10d %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f %-20s%n",
          c.getId(),
          c.getStartTime(),
          c.getFinishTime(),
          response,
          waiting,
          turnaround,
          ((CustomCloudlet) c).getPolicy() // Policy added here
          );
    }

    totalCloudlets = finished.size();
    avgResponse = totalResponse / totalCloudlets;
    avgWaiting = totalWaiting / totalCloudlets;
    avgTurnaround = totalTurnaround / totalCloudlets;
    throughput = totalCloudlets / makespan;

    ptintMetrics();
  }

  public static void printVmUtilization(List<Vm> vmList) {
    if (vmList.isEmpty()) {
      System.out.println("⚠️ No VMs found!");
      return;
    }

    double totalUtilization = 0;

    System.out.println("\n========== VM CPU Utilization ==========");
    for (Vm vm : vmList) {
      double utilization = vm.getCpuPercentUtilization(vm.getSimulation().clock());
      totalUtilization += utilization;
      System.out.printf("VM %d utilization: %.2f%%%n", vm.getId(), utilization * 100);
    }

    double avgUtilization = (totalUtilization / vmList.size()) * 100;
    System.out.printf("Average VM Utilization: %.2f%%%n", avgUtilization);
  }

  /** Compute Host/Datacenter resource utilization. */
  public void printDatacenterUtilization() {
    double totalHostUtilization = 0;
    int count = 0;

    System.out.println("\n========== Datacenter Resource Utilization ==========");

    for (Datacenter datacenter : getDatacenterList()) {

      for (Host host : datacenter.getHostList()) {
        double hostUtilization = 0;
        int vmsOnHost = host.getVmList().size();

        if (vmsOnHost == 0) {
          continue;
        }

        for (Vm vm : host.getVmList()) {
          hostUtilization += vm.getCpuPercentUtilization(vm.getSimulation().clock());
        }

        double avgHostUtilization = (hostUtilization / vmsOnHost) * 100;
        totalHostUtilization += avgHostUtilization;
        count++;

        System.out.printf(
            "Host %d average CPU utilization: %.2f%%%n", host.getId(), avgHostUtilization);
      }
    }

    datacenterUtilization = totalHostUtilization / count;
    System.out.printf("Datacenter Average Utilization: %.2f%%%n", datacenterUtilization);
  }

  /**
   * Computes CPU utilization for all hosts in all datacenters and updates the average datacenter
   * utilization.
   */
  public void getDatacenterUtilizationCpu() {
    double totalHostUtilization = 0;
    int count = 0;

    System.out.println("\n========== Datacenter Resource Utilization ==========");

    for (Datacenter datacenter : getDatacenterList()) {

      for (Host host : datacenter.getHostList()) {
        double hostUtilization = 0;
        int vmsOnHost = host.getVmList().size();

        if (vmsOnHost == 0) {
          continue;
        }

        for (Vm vm : host.getVmList()) {
          hostUtilization += vm.getCpuPercentUtilization(vm.getSimulation().clock());
        }

        avgHostUtilization = (hostUtilization / vmsOnHost) * 100;
        totalHostUtilization += avgHostUtilization;
        count++;

        // System.out.printf("Host %d average CPU utilization: %.2f%%%n", host.getId(),
        // avgHostUtilization);
      }
    }

    datacenterUtilization = totalHostUtilization / count;
    // System.out.printf("Datacenter Average Utilization: %.2f%%%n", datacenterUtilization);
  }

  public double getTolerance(double deadline, double tolerance) {

    int failedVms = getVmFailedList().size();
    int withinDeadline = 0, withinTolerance = 0;
    List<Cloudlet> finished = getCloudletFinishedList();

    for (Cloudlet c : finished) {
      double finish = c.getFinishTime();
      if (finish <= deadline) {
        withinDeadline++;
      } else if (finish <= deadline + tolerance) {
        withinTolerance++;
      }
    }

    withinTolerance = withinTolerance + failedVms;

    return withinTolerance;
  }

  /**
   * Calculates the number of Cloudlets finished within the deadline and within tolerance limits.
   *
   * @param deadline the defined deadline threshold.
   * @param tolerance the additional allowed delay after the deadline.
   * @return the count of Cloudlets that meet the tolerance condition.
   */
  public void getAllTolerance(double deadline, double tolerance) {
    int withinDeadline = 0, withinTolerance = 0;
    List<Cloudlet> finished = getCloudletFinishedList();

    for (Cloudlet c : finished) {
      double finish = c.getFinishTime();
      if (finish <= deadline) {
        withinDeadline++;
      } else if (finish <= deadline + tolerance) {
        withinTolerance++;
      }
    }

    int total = finished.size();
    double dsr = (double) (withinDeadline + withinTolerance) / total * 100;

    System.out.println("\n========== Tolerance Metrics ==========");
    System.out.printf("Deadline: %.2f sec%n", deadline);
    System.out.printf("Tolerance: %.2f sec%n", tolerance);
    System.out.printf("Cloudlets within deadline: %d%n", withinDeadline);
    System.out.printf("Cloudlets within tolerance: %d%n", withinTolerance);
    System.out.printf("Deadline Satisfaction Ratio: %.2f%%%n", dsr);
  }

  /**
   * Prints summary metrics (response time, throughput, tolerance, etc.) for the current simulation
   * state.
   */
  private void ptintMetrics() {

    System.out.println("\n==================== Summary Metrics ====================");
    System.out.printf("LiveMakespan: %.2f secn", makespan);
    System.out.printf("Live Average Response Time: %.2f sec%n", avgResponse);
    System.out.printf("Live Average Waiting Time: %.2f sec%n", avgWaiting);
    System.out.printf("LiveAverage Turnaround Time: %.2f sec%n", avgTurnaround);
    System.out.println("Live Throughput : " + throughput);
    System.out.println("Cpu  :" + avgHostUtilization);
    System.out.println(" Live Tolerance " + getTolerance(DEADLINE, TOLERANCE));
  }

  public static void exportToCSV(List<Cloudlet> finished, String fileName) {
    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write("CloudletID,VMID,Status,Submission,Start,Finish,Response,Waiting,Turnaround\n");

      for (Cloudlet c : finished) {
        /*
        double response = c.getFinishTime() - c.getSubmissionTime();
        double waiting = c.getExecStartTime() - c.getSubmissionTime();
        double turnaround = c.getFinishTime() - c.getSubmissionTime();

        writer.write(String.format("%d,%d,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                c.getId(), c.getVm().getId(), c.getStatus(),
                c.getSubmissionTime(), c.getExecStartTime(), c.getFinishTime(),
                response, waiting, turnaround));
         */
      }

      System.out.println("✅ Metrics exported to: " + fileName);
    } catch (IOException e) {
      System.err.println("❌ Error writing CSV: " + e.getMessage());
    }
  }

  public VmSelectionPolicy getPolicy() {
    return this.policy;
  }

  /**
   * Switches to a new VM selection policy dynamically.
   *
   * @param newPolicy the new {@link VmSelectionPolicy} to use.
   */
  public void setPolicy(VmSelectionPolicy newPolicy) {
    this.policy = newPolicy;

    // defaultVmMapper((Cloudlet) getCloudletCreatedList());
    System.out.println(
        "Switched policy to: "
            + newPolicy.getClass().getSimpleName()
            + " at time "
            + getSimulation().clock());
  }

  public List<Cloudlet> resendUnfinishedCloudlets() {
    // Collect all Cloudlets that are not yet finished
    List<Cloudlet> unfinished =
        getCloudletSubmittedList().stream().filter(c -> !c.isFinished()).toList();

    return unfinished;
  }

  /**
   * Overrides the default VM mapper of {@link DatacenterBrokerSimple}. Selects a VM for a Cloudlet
   * using the current {@code VmSelectionPolicy}.
   *
   * @param cloudlet the Cloudlet to assign.
   * @return the selected {@link Vm}.
   */
  @Override
  protected Vm defaultVmMapper(Cloudlet cloudlet) {

    Vm selected = policy.selectVmForCloudlet(cloudlet, getVmCreatedList());
    System.out.println(
        "Mapping cloudlet "
            + cloudlet.getId()
            + " with "
            + policy.getClass().getSimpleName()
            + " -> VM "
            + (selected == Vm.NULL ? "NULL" : selected.getId()));
    return selected;
  }

  public Vm selectVmForCloudlet(Cloudlet cloudlet) {
    return policy.selectVmForCloudlet(cloudlet, getVmCreatedList());
  }
}
