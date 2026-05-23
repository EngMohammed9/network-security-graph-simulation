# Network Security Graph Simulation

A JavaFX desktop application that applies graph theory to computer network security.

The project visualizes a computer network as a directed graph, randomly selects an attacker device, and uses Breadth-First Search (BFS) to discover whether an attack path exists to a selected target device. It also compares BFS with Union-Find for connectivity analysis and performance measurement.

## Key Features

* Interactive JavaFX network visualization
* Random directed graph generation for network topology
* Target device selection through the GUI
* Random attacker selection for each simulation
* BFS-based attack path discovery
* Visual highlighting for checking, visited, attacker, target, and attack path states
* Union-Find connectivity analysis with path compression and union by rank
* Performance comparison between BFS and Union-Find
* Console output for worked examples, timing results, memory usage, and complexity notes

## Why This Project Matters

Computer networks can be modeled as graphs where devices are vertices and connections are edges. This project demonstrates how graph algorithms can help analyze reachability, segmentation, and possible attacker movement inside a network.

BFS is used when the actual path matters, while Union-Find is used when repeated connectivity queries need to be answered quickly.

## Algorithms Used

### Breadth-First Search

BFS explores the network level by level from the attacker device. In this project, it is used to find whether a path exists from the attacker to the selected target.

Time complexity per query:

```text
O(V + E)
```

### Union-Find

Union-Find groups connected devices into components. It is useful for fast repeated connectivity checks.

Optimizations used:

* Path compression
* Union by rank

Approximate query complexity:

```text
O(alpha(V))
```

## Visualization Legend

| Color | Meaning |
| --- | --- |
| White | Not visited device |
| Yellow | Currently being checked |
| Light green | Visited device |
| Green | Target device |
| Red | Attacker or final attack path |

## Project Structure

```text
src/main/java/
  Main.java
  NetworkSecurityApp.java
  GraphAlgorithms.java
  UnionFind.java
  PerformanceAnalyzer.java
  PerformanceResult.java
  EdgeView.java
```

## Class Responsibilities

| Class | Purpose |
| --- | --- |
| `Main` | Application entry point |
| `NetworkSecurityApp` | JavaFX GUI and attack path animation |
| `GraphAlgorithms` | BFS, graph construction, path reconstruction, and random query generation |
| `UnionFind` | Disjoint Set Union implementation |
| `PerformanceAnalyzer` | Runs experiments and prints performance analysis |
| `PerformanceResult` | Stores benchmark results |
| `EdgeView` | Represents a visual directed edge with an arrow |

## Requirements

* Java 17 or later
* Maven

Dependencies are managed through Maven:

* JavaFX
* JGraphT

## How to Run

Compile the project:

```bash
mvn clean compile
```

Run the JavaFX application:

```bash
mvn javafx:run
```

## Output

The application opens a JavaFX window that displays the network graph and controls for selecting a target device.

The console also prints:

* Worked connectivity example
* Union-Find and BFS results
* Performance analysis table
* Memory usage comparison
* Complexity notes
* Additional algorithm discussion

## Example Use Case

This project can be used to simulate whether a device in one part of a network can reach a protected device in another part of the network.

For example, if a guest device can reach an internal server, BFS may reveal an attack path. This can represent a possible segmentation weakness or security risk.

## Future Improvements

* Add weighted edges and shortest path algorithms such as Dijkstra
* Allow users to manually add or remove devices and connections
* Export simulation results to a file
* Add screenshots and GIF demos to the README
* Add unit tests for graph algorithms
* Support custom network datasets

## Topics

`java` `javafx` `graph-theory` `bfs` `union-find` `network-security` `simulation` `jgrapht`
