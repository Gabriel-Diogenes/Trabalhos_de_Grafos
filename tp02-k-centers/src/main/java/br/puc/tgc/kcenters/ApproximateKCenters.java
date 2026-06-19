package br.puc.tgc.kcenters;

import java.util.ArrayList;
import java.util.List;

public final class ApproximateKCenters {

    private static final double INF = Double.POSITIVE_INFINITY;

    private ApproximateKCenters() {
    }

    public static ExactKCenters.Solution solve(double[][] dist, int k) {
        int n = dist.length;
        if (k <= 0) {
            return new ExactKCenters.Solution(0.0, List.of());
        }
        if (k >= n) {
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                all.add(i);
            }
            return new ExactKCenters.Solution(InstanceLoader.solutionRadius(dist, all), all);
        }

        List<Integer> centers = new ArrayList<>();
        centers.add(0);

        for (int step = 0; step < k - 1; step++) {
            int farthestVertex = 0;
            double farthestDistance = -1.0;

            for (int v = 0; v < n; v++) {
                double minDist = INF;
                for (int center : centers) {
                    minDist = Math.min(minDist, dist[v][center]);
                }
                if (minDist > farthestDistance) {
                    farthestDistance = minDist;
                    farthestVertex = v;
                }
            }
            centers.add(farthestVertex);
        }

        return new ExactKCenters.Solution(InstanceLoader.solutionRadius(dist, centers), centers);
    }
}
