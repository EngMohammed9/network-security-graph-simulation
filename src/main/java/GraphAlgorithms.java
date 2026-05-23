import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public final class GraphAlgorithms {
    private GraphAlgorithms() {
    }

    public static Set<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    public static boolean isConnectedBFS(Map<Integer, List<Integer>> graph, int start, int target) {
        return bfs(graph, start).contains(target);
    }

    public static List<String> buildAttackPath(Map<String, String> previous, String start, String target) {
        List<String> path = new ArrayList<>();
        String current = target;
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);
        return (!path.isEmpty() && path.get(0).equals(start)) ? path : new ArrayList<>();
    }

    public static List<int[]> generateUniqueEdges(int n, int edgesCount) {
        Random random = new Random();
        Set<String> seen = new HashSet<>();
        List<int[]> edges = new ArrayList<>();
        int maxEdges = n * (n - 1) / 2;
        edgesCount = Math.min(edgesCount, maxEdges);

        while (edges.size() < edgesCount) {
            int u = random.nextInt(n);
            int v = random.nextInt(n);
            if (u == v) {
                continue;
            }
            int a = Math.min(u, v);
            int b = Math.max(u, v);
            String key = a + "-" + b;
            if (!seen.contains(key)) {
                seen.add(key);
                edges.add(new int[]{a, b});
            }
        }
        return edges;
    }

    public static Map<Integer, List<Integer>> buildGraph(int n, List<int[]> edges) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
        return graph;
    }

    public static List<int[]> generateQueries(int n, int queryCount) {
        Random random = new Random();
        List<int[]> queries = new ArrayList<>();
        for (int i = 0; i < queryCount; i++) {
            queries.add(new int[]{random.nextInt(n), random.nextInt(n)});
        }
        return queries;
    }
}
