# Adaptive Load Balancing Strategies in CloudSim Plus

## 📌 Overview

This project implements new  **adaptive load balancing strategies** in a cloud 
computing environment using CloudSim Plus based on machine learning .
Unlike static approaches, the proposed solution dynamically adjusts task scheduling decisions based on:

* Key metrics such as response-time, tolerance and scalability.
* User needs.
* Runtime conditions

The solution provide an autonomous selction of load balancing based on IA.
---

## 🎯 Objectives

* Design and implement **adaptive scheduling algorithms**
* User-Oriented Optimization Objectives.
* Dynamic Strategy Selection via **machine learning**
* Aggregate and integration of Multiple Load Balancing Policies. We unify several well-known load balancing algorithms.
* Automomous Cloud computing

---

## 🧠 Key Concepts

### 🔹 Adaptive Load Balancing

Adaptive load balancing dynamically redistributes workloads across VMs based on real-time metrics such as:

* CPU utilization
* Queue length
* Execution time

---
## ⚙️ Features

* ✅ Adaptive load balancing algorithm implementation
* ✅ AI-Driven Load balancing
* ✅ Auto selection of load balancing based on user preferences
* ✅ Real-time decision-making based on system state
* ✅ Metrics Collecor

---

## 🏗️ System Architecture

```text
+--------------------------------------------------+
|                Interface Layer                   |
|  (REST API, Config Files, HTTP Module)           |
+------------------------+-------------------------+
                         |
+------------------------v-------------------------+
|              Application Layer                  |
|  Use Cases / Orchestrators                      |
|  - RunSimulationUseCase                         |
|  - SelectLoadBalancingStrategy                  |
|  - CollectMetrics                              |
+------------------------+-------------------------+
                         |
+------------------------v-------------------------+
|               Domain Layer (CORE)               |
|  Business Logic (PURE, no frameworks)           |
|                                                |
|  - AdaptiveBroker              |
|  - LoadBalancingStrategy (interface)            |
|  - RoundRobinStrategy                          |
|  - WeightedRRStrategy                          |
|  - ACO Strategy
   - List of strategies.                                |
|  - Models                               |
+------------------------+-------------------------+
                         |
+------------------------v-------------------------+
|            Infrastructure Layer                |
|  External Tools / Frameworks                   |
|                                                |
|  - CloudSim Plus (simulation engine)           |
|  - Logging                                     |
+------------------------------------------------+
```

---

## 🚀 Getting Started

### Prerequisites

* Java 11+
* Maven
* CloudSim Plus

### Installation

```bash
git clone https://github.com/BenhalimaAnouar/IaLoadBalancig.git
cd IaLoadBalancig
mvn clean install
```

### Run Simulation

```bash
mvn exec:java
```
or run the Test java main File

---

## 🧪 Core Implementation

The adaptive scheduling logic is implemented by extending the default broker:

```java
public class AdaptiveBroker extends DatacenterBrokerSimple {

    @Override
    protected void scheduleTaskstoVms() {
        // Example adaptive logic:
        // - Monitor VM load
        // - Select least-loaded VM
        // - Dynamically assign cloudlets
    }
}
```

---



# Step-by-Step Explanation of Adaptive Broker Initialization

This example demonstrates how to initialize a CloudSim Plus simulation and create an adaptive broker with automatic load balancing selection.

---

## Example Code

```java
// 1. Initialize simulation
var simulation = new CloudSimPlus();

// 2. Create Adaptive Broker
var broker = new AdaptiveBroker(simulation, null);
```

---

## Step-by-Step Explanation

### 1. Initialize the Simulation

```java
var simulation = new CloudSimPlus();
```

This line creates the CloudSim Plus simulation environment.

The simulation environment is responsible for managing:

- Datacenters
- Virtual Machines (VMs)
- Cloudlets (Tasks)
- Scheduling and execution events

All cloud computing components will run inside this simulation.

---

### 2. Create the Adaptive Broker

```java
var broker = new AdaptiveBroker(simulation, null);
```

The `AdaptiveBroker` manages:

- Task scheduling
- VM allocation
- Load balancing decisions

---

## Meaning of the `null` Parameter

The second parameter of the constructor is:

```java
null
```

This means:

- No load balancing algorithm is manually specified.
- The broker will automatically choose the load balancing strategy.
- The adaptive mechanism dynamically selects the best algorithm depending on:
  - users needs
  - key metrics
  - Performance conditions

---

## Example of Manual Selection

Instead of `null`, you could manually provide a specific load balancing algorithm:

```java
var broker = new AdaptiveBroker(
    simulation,
    new RoundRobinLoadBalancer()
);
```

In this case:

- The broker will always use the Round Robin algorithm.
- No automatic selection will occur.

---

## Summary

Using:

```java
var broker = new AdaptiveBroker(simulation, null);
```

means that the adaptive broker will automatically determine and apply the most suitable load balancing strategy during the simulation execution.



## 📊 Example Test
```java
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;

import java.util.List;

public class SimulationExample {

    public static void main(String[] args) throws Exception {

        // 1. Initialize simulation
        var simulation = new CloudSimPlus();

        // 2. Create Adaptive Broker
        var broker = new AdaptiveBroker(simulation, null);

        // 3. Create Hosts
        long ram = 10000;     // MB
        long bw = 10000;      // Mbps
        long storage = 100000; // MB

        var host0 = new HostSimple(ram, bw, storage, List.of(new PeSimple(10000)));
        var host1 = new HostSimple(ram, bw, storage, List.of(new PeSimple(10000)));

        // 4. Create Datacenters
        var dc0 = new DatacenterSimple(simulation, List.of(host0));
        var dc1 = new DatacenterSimple(simulation, List.of(host1));

        // 5. Create VMs
        var vm0 = new VmSimple(1000, 1);
        vm0.setRam(1000).setBw(1000).setSize(1000);

        var vm1 = new VmSimple(1000, 1);
        vm1.setRam(1000).setBw(1000).setSize(1000);

        // 6. Create Cloudlets
        var utilizationModel = new UtilizationModelDynamic(0.5);

        var cloudlet0 = new CustomCloudlet(100000, 1);
        var cloudlet1 = new CustomCloudlet(200000, 1);
        var cloudlet2 = new CustomCloudlet(300000, 1);

        var cloudletList = List.of(cloudlet0, cloudlet1, cloudlet2);

        // 7. Submit to Broker
        broker.submitVmList(List.of(vm0, vm1));
        broker.submitCloudletList(cloudletList);

        // 8. Start Simulation
        simulation.start();

        // 9. Print Results
        new CustomCloudletsTableBuilder(broker.getCloudletFinishedList())
                .build();
    }
}
```
---
## 🧪 File configuration
The adaptive load balancing sets up a file config named conf.properties. This file initializes the load balancing config based on the key criteria of the cloud users, including desired metrics such as resource utilization, throughput, and response time. Therefore, it includes the threshold for each metric to define its state, as illustrated in the example. Additionally, it has an endpoint that predicts the optimized load balancing, as we will explain in the next section.

## 🧪 API Model

This framework uses an API to synchronize and select the appropriate load-balancing method.  
The API backend leverages machine learning techniques to predict the most suitable load-balancing algorithm.

The API endpoint response should follow this format:

- `success`
- `message` (predicted load-balancing method)

You can define your API endpoint in the configuration file while respecting the expected response structure containing the following two fields:

```json
{
  "success": true,
  "message": "RoundRobin"
}
```

## 📊 Evaluation Metrics

The system evaluates performance using:

* **Makespan** (total execution time)
* **Average response time**
* **VM utilization**
* **Throughput**
* **scalability**
* **Fault-tolerance**
* **scalability**
* **Security**
* **Performance**

---

## 🔬 Research Contributions

* Proposes a **dynamic adaptive load balancing strategy**
* Demonstrates improved performance over traditional methods
* Provides a **scalable multi-user simulation model**
* Suitable for integration with **AI-based scheduling techniques**

---

## 🔮 Future Work

* enhancement with machine learning models for predictive scheduling
* Graphical User Interface (GUI)
* Deployment on real cloud platforms (e.g., OCI)

---

## 📁 Project Structure

```text
src/
 ├── loadbalancing/       # Adaptive broker implementation
 ├── conf.properties/     # File configuration
 └── cloudsimplus/        # CloudSim Plus dependcies
```

---

## 🤝 Contributing

Contributions are welcome for:

* New scheduling strategies
* Optimization techniques
* Experimental scenarios

---

## 📜 License

MIT License

---

## 👤 Author

Anouar Ben Halima – PhD Student in Cloud Computing & Artificial Intelligence

---
