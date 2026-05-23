import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class PerformanceAnalyzer {
    private PerformanceAnalyzer() {
    }

    public static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static PerformanceResult runExperiment(int n, int edgeCount, int queryCount) {
        List<int[]> edges = GraphAlgorithms.generateUniqueEdges(n, edgeCount);
        List<int[]> queries = GraphAlgorithms.generateQueries(n, queryCount);
        Map<Integer, List<Integer>> graph = GraphAlgorithms.buildGraph(n, edges);

        long beforeUfMemory = usedMemory();
        UnionFind uf = new UnionFind(n);
        long startUfBuild = System.nanoTime();
        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }
        long endUfBuild = System.nanoTime();
        long afterUfMemory = usedMemory();

        long startUfQuery = System.nanoTime();
        int ufTrueCount = 0;
        for (int[] query : queries) {
            if (uf.connected(query[0], query[1])) {
                ufTrueCount++;
            }
        }
        long endUfQuery = System.nanoTime();

        long beforeBfsMemory = usedMemory();
        long startBfsQuery = System.nanoTime();
        int bfsTrueCount = 0;
        for (int[] query : queries) {
            if (GraphAlgorithms.isConnectedBFS(graph, query[0], query[1])) {
                bfsTrueCount++;
            }
        }
        long endBfsQuery = System.nanoTime();
        long afterBfsMemory = usedMemory();

        return new PerformanceResult(
                n,
                edges.size(),
                endUfBuild - startUfBuild,
                endUfQuery - startUfQuery,
                endBfsQuery - startBfsQuery,
                Math.max(0, afterUfMemory - beforeUfMemory),
                Math.max(0, afterBfsMemory - beforeBfsMemory),
                ufTrueCount,
                bfsTrueCount
        );
    }

    public static void printGraphExample() {
        int n = 6;
        List<int[]> edges = new ArrayList<>();
        edges.add(new int[]{0, 1});
        edges.add(new int[]{1, 2});
        edges.add(new int[]{3, 4});
        Map<Integer, List<Integer>> graph = GraphAlgorithms.buildGraph(n, edges);
        UnionFind uf = new UnionFind(n);
        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        System.out.println("================ Worked Example ================");
        System.out.println("Nodes: 0, 1, 2, 3, 4, 5");
        System.out.println("Edges: (0,1), (1,2), (3,4)");
        System.out.println("Union-Find -> connected(0,2): " + uf.connected(0, 2));
        System.out.println("Union-Find -> connected(0,4): " + uf.connected(0, 4));
        System.out.println("BFS        -> connected(0,2): " + GraphAlgorithms.isConnectedBFS(graph, 0, 2));
        System.out.println("BFS        -> connected(0,4): " + GraphAlgorithms.isConnectedBFS(graph, 0, 4));
        System.out.println();
    }

    public static void printResultsTable(List<PerformanceResult> results, int queryCount) {
        System.out.println("================ Performance Analysis ================");
        System.out.printf("%-10s %-10s %-15s %-15s %-15s %-15s %-15s %-12s %-12s%n",
                "Nodes", "Edges", "UF Build(ns)", "UF Query(ns)", "BFS Query(ns)",
                "UF Mem(B)", "BFS Mem(B)", "UF True", "BFS True");

        for (PerformanceResult r : results) {
            System.out.printf("%-10d %-10d %-15d %-15d %-15d %-15d %-15d %-12d %-12d%n",
                    r.size, r.edges, r.ufBuildTimeNs, r.ufQueryTimeNs, r.bfsQueryTimeNs,
                    r.ufMemoryBytes, r.bfsMemoryBytes, r.connectedQueries, r.bfsConnectedQueries);
        }

        System.out.println();
        System.out.println("Each algorithm used the same graph and the same " + queryCount + " connectivity queries.");
        System.out.println("UF Build(ns) = time to build connected components using Union-Find.");
        System.out.println("UF Query(ns) = time to answer all connectivity queries using Union-Find.");
        System.out.println("BFS Query(ns) = time to answer the same queries using BFS.");
        System.out.println();
    }

    public static void printComplexityNotes() {
        System.out.println("================ Complexity Notes ================");
        System.out.println("Union-Find build time: near O(E * alpha(V)) with path compression and union by rank.");
        System.out.println("Union-Find query time: near O(alpha(V)) per query.");
        System.out.println("BFS query time: O(V + E) per query.");
        System.out.println("This is why Union-Find is usually faster for repeated connectivity queries.");
        System.out.println();
    }

    public static void runPerformanceTest() {
        printGraphExample();
        int[] sizes = {20, 200, 2000};
        int queryCount = 100;
        List<PerformanceResult> results = new ArrayList<>();
        Random random = new Random();

        for (int size : sizes) {
            int edgeCount = (size * 2) + random.nextInt(size);
            results.add(runExperiment(size, edgeCount, queryCount));
        }

        printResultsTable(results, queryCount);
        printComplexityNotes();

        System.out.println("================ Additional Algorithms for Discussion ================");
        System.out.println("1. DFS: useful for traversal, cycle detection, and component exploration.");
        System.out.println("2. Kruskal's Algorithm: uses Union-Find to build a minimum spanning tree.");
        System.out.println("3. Bidirectional Search: can reduce search time by exploring from both ends.");
    }
}
