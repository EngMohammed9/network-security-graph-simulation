public class PerformanceResult {
    final int size;
    final int edges;
    final long ufBuildTimeNs;
    final long ufQueryTimeNs;
    final long bfsQueryTimeNs;
    final long ufMemoryBytes;
    final long bfsMemoryBytes;
    final int connectedQueries;
    final int bfsConnectedQueries;

    public PerformanceResult(int size, int edges, long ufBuildTimeNs, long ufQueryTimeNs,
                             long bfsQueryTimeNs, long ufMemoryBytes, long bfsMemoryBytes,
                             int connectedQueries, int bfsConnectedQueries) {
        this.size = size;
        this.edges = edges;
        this.ufBuildTimeNs = ufBuildTimeNs;
        this.ufQueryTimeNs = ufQueryTimeNs;
        this.bfsQueryTimeNs = bfsQueryTimeNs;
        this.ufMemoryBytes = ufMemoryBytes;
        this.bfsMemoryBytes = bfsMemoryBytes;
        this.connectedQueries = connectedQueries;
        this.bfsConnectedQueries = bfsConnectedQueries;
    }
}
