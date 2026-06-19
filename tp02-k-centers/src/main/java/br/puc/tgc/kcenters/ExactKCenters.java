package br.puc.tgc.kcenters;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ExactKCenters {

    public record Solution(double radius, List<Integer> centers) {
    }

    private ExactKCenters() {
    }

    public static Solution solve(double[][] dist, int k) {
        List<Double> radii = InstanceLoader.candidateRadii(dist);
        if (radii.isEmpty()) {
            return new Solution(0.0, List.of(0));
        }

        int lo = 0;
        int hi = radii.size() - 1;
        double bestRadius = radii.get(hi);
        List<Integer> bestCenters = List.of();

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            double radius = radii.get(mid);
            CoverageResult result = canCoverWithRadius(dist, radius, k);
            if (result.feasible()) {
                bestRadius = radius;
                bestCenters = result.centers();
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        if (!bestCenters.isEmpty()) {
            return new Solution(InstanceLoader.solutionRadius(dist, bestCenters), bestCenters);
        }
        return new Solution(bestRadius, bestCenters);
    }

    private record CoverageResult(boolean feasible, List<Integer> centers) {
    }

    private static CoverageResult canCoverWithRadius(double[][] dist, double radius, int k) {
        CoverageResult ilpResult = canCoverWithIlp(dist, radius, k);
        if (ilpResult.feasible()) {
            return ilpResult;
        }
        return canCoverWithBacktracking(dist, radius, k);
    }

    private static CoverageResult canCoverWithIlp(double[][] dist, double radius, int k) {
        int n = dist.length;
        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[] x = new Variable[n];
        for (int i = 0; i < n; i++) {
            x[i] = model.addVariable("x_" + i).binary();
        }

        var centerLimit = model.addExpression("center_limit").upper(k);
        for (int i = 0; i < n; i++) {
            centerLimit.set(x[i], 1.0);
        }

        for (int j = 0; j < n; j++) {
            var coverage = model.addExpression("cover_" + j).lower(1.0);
            boolean hasCover = false;
            for (int i = 0; i < n; i++) {
                if (dist[i][j] <= radius + 1e-9) {
                    coverage.set(x[i], 1.0);
                    hasCover = true;
                }
            }
            if (!hasCover) {
                return new CoverageResult(false, List.of());
            }
        }

        Optimisation.Result result = model.minimise();
        if (!result.getState().isFeasible()) {
            return new CoverageResult(false, List.of());
        }

        List<Integer> centers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (x[i].getValue().doubleValue() > 0.5) {
                centers.add(i);
            }
        }
        return new CoverageResult(true, centers);
    }

    private static CoverageResult canCoverWithBacktracking(double[][] dist, double radius, int k) {
        int n = dist.length;
        List<Set<Integer>> coverSets = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Set<Integer> covered = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if (dist[i][j] <= radius + 1e-9) {
                    covered.add(j);
                }
            }
            coverSets.add(covered);
        }

        Set<Integer> uncovered = new HashSet<>();
        for (int j = 0; j < n; j++) {
            uncovered.add(j);
        }

        List<Integer> chosen = new ArrayList<>();
        List<Integer> result = backtrack(uncovered, coverSets, chosen, k);
        if (result == null) {
            return new CoverageResult(false, List.of());
        }
        return new CoverageResult(true, result);
    }

    private static List<Integer> backtrack(
            Set<Integer> uncovered,
            List<Set<Integer>> coverSets,
            List<Integer> chosen,
            int maxK
    ) {
        if (uncovered.isEmpty()) {
            return new ArrayList<>(chosen);
        }
        if (chosen.size() >= maxK) {
            return null;
        }

        int target = uncovered.stream()
                .min((a, b) -> Integer.compare(dominators(a, coverSets), dominators(b, coverSets)))
                .orElse(0);

        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < coverSets.size(); i++) {
            if (coverSets.get(i).contains(target)) {
                candidates.add(i);
            }
        }

        candidates.sort((a, b) -> Integer.compare(
                intersectionSize(coverSets.get(b), uncovered),
                intersectionSize(coverSets.get(a), uncovered)
        ));

        for (int center : candidates) {
            Set<Integer> newUncovered = new HashSet<>(uncovered);
            newUncovered.removeAll(coverSets.get(center));
            chosen.add(center);
            List<Integer> solution = backtrack(newUncovered, coverSets, chosen, maxK);
            if (solution != null) {
                return solution;
            }
            chosen.remove(chosen.size() - 1);
        }
        return null;
    }

    private static int dominators(int vertex, List<Set<Integer>> coverSets) {
        int count = 0;
        for (Set<Integer> cover : coverSets) {
            if (cover.contains(vertex)) {
                count++;
            }
        }
        return count;
    }

    private static int intersectionSize(Set<Integer> cover, Set<Integer> uncovered) {
        int count = 0;
        for (int vertex : uncovered) {
            if (cover.contains(vertex)) {
                count++;
            }
        }
        return count;
    }
}
