# Adaptive Load Balancing Strategies in CloudSim Plus

## 📌 Overview

This project implements and evaluates **adaptive load balancing strategies** in a cloud computing environment using CloudSim Plus.

Unlike static approaches, the proposed solution dynamically adjusts task scheduling decisions based on:

* System load
* VM utilization
* Runtime conditions

The simulation models a **multi-tenant cloud environment** using multiple brokers, where each broker represents an independent user competing for shared resources.

---

## 🎯 Objectives

* Design and implement **adaptive scheduling algorithms**
* Improve **resource utilization and system performance**
* Reduce **makespan and response time**
* Ensure **fairness among multiple users (brokers)**

---

## 🧠 Key Concepts

### 🔹 Adaptive Load Balancing

Adaptive load balancing dynamically redistributes workloads across VMs based on real-time metrics such as:

* CPU utilization
* Queue length
* Execution time

### 🔹 Multi-Broker Model

Each `DatacenterBroker` acts as a separate cloud user:

* Submits its own VMs and Cloudlets
* Competes for shared datacenter resources
* Applies independent scheduling policies

---

## ⚙️ Features

* ✅ Adaptive load balancing algorithm implementation
* ✅ Multi-broker (multi-user) simulation
* ✅ Dynamic VM-task allocation
* ✅ Real-time decision-making based on system state
* ✅ Performance comparison with baseline strategies (e.g., FCFS, Round Robin)

---

## 🏗️ System Architecture

```text
+----------------------+
|     Datacenter       |
|  (Hosts & Resources) |
+----------+-----------+
           |
   -----------------------
   |         |           |
+--------+ +--------+ +--------+
|Broker1 | |Broker2 | |BrokerN |
+--------+ +--------+ +--------+
     |          |          |
 Adaptive   Adaptive   Adaptive
 Scheduler  Scheduler  Scheduler
     |          |          |
    VMs        VMs        VMs
     |          |          |
  Cloudlets  Cloudlets  Cloudlets
```

---

## 🚀 Getting Started

### Prerequisites

* Java 11+
* Maven
* CloudSim Plus

### Installation

```bash
git clone https://github.com/your-username/adaptive-load-balancing-cloudsim.git
cd adaptive-load-balancing-cloudsim
mvn clean install
```

### Run Simulation

```bash
mvn exec:java
```

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

## 📊 Evaluation Metrics

The system evaluates performance using:

* **Makespan** (total execution time)
* **Average response time**
* **VM utilization**
* **Throughput**
* **Load distribution fairness**

---

## 📈 Experimental Scenarios

* Single vs Multiple brokers
* Static vs Adaptive scheduling
* Varying workload sizes
* Heterogeneous VM configurations

---

## 🔬 Research Contributions

* Proposes a **dynamic adaptive load balancing strategy**
* Demonstrates improved performance over traditional methods
* Provides a **scalable multi-user simulation model**
* Suitable for integration with **AI-based scheduling techniques**

---

## 🔮 Future Work

* Integration with Machine Learning models for predictive scheduling
* Energy-aware adaptive load balancing
* SLA-aware and priority-based scheduling
* Deployment on real cloud platforms (e.g., OCI)

---

## 📁 Project Structure

```text
src/
 ├── brokers/        # Adaptive broker implementation
 ├── scheduling/     # Load balancing algorithms
 ├── models/         # VM, Cloudlet definitions
 ├── simulation/     # Main simulation setup
 └── utils/          # Helper functions
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

Abdullah – PhD Student in Cloud Computing & Artificial Intelligence

---

