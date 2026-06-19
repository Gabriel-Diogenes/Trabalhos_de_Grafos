package br.puc.tgc.kcenters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public final class InstanceLoader {

    private static final double INF = Double.POSITIVE_INFINITY;

    private InstanceLoader() {
    }

    public static InstanceData parsePmedFile(Path filepath) throws IOException {
        List<String> lines = Files.readAllLines(filepath).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        String[] header = lines.get(0).split("\\s+");
        int n = Integer.parseInt(header[0]);
        int k = Integer.parseInt(header[2]);

        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = i == j ? 0.0 : INF;
            }
        }

        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String[] parts = lines.get(lineIndex).split("\\s+");
            if (parts.length < 3) {
                continue;
            }
            int i = Integer.parseInt(parts[0]) - 1;
            int j = Integer.parseInt(parts[1]) - 1;
            double cost = Double.parseDouble(parts[2]);
            dist[i][j] = cost;
            dist[j][i] = cost;
        }

        floydWarshall(dist, n);
        return new InstanceData(0, n, k, dist);
    }

    public static void floydWarshall(double[][] dist, int n) {
        for (int pivot = 0; pivot < n; pivot++) {
            for (int i = 0; i < n; i++) {
                double dik = dist[i][pivot];
                if (dik == INF) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    double alt = dik + dist[pivot][j];
                    if (alt < dist[i][j]) {
                        dist[i][j] = alt;
                    }
                }
            }
        }
    }

    public static double solutionRadius(double[][] dist, List<Integer> centers) {
        int n = dist.length;
        double radius = 0.0;
        for (int v = 0; v < n; v++) {
            double minDist = INF;
            for (int center : centers) {
                minDist = Math.min(minDist, dist[v][center]);
            }
            radius = Math.max(radius, minDist);
        }
        return radius;
    }

    public static List<Double> candidateRadii(double[][] dist) {
        int n = dist.length;
        TreeSet<Double> values = new TreeSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (dist[i][j] < INF) {
                    values.add(dist[i][j]);
                }
            }
        }
        return new ArrayList<>(values);
    }

    public static List<InstanceData> loadAllInstances(Path dataDir) throws IOException {
        List<InstanceData> instances = new ArrayList<>();
        for (int id = 1; id <= 40; id++) {
            Path file = dataDir.resolve("pmed" + id + ".txt");
            if (!Files.exists(file)) {
                throw new IOException("Arquivo nao encontrado: " + file);
            }
            InstanceData data = parsePmedFile(file);
            instances.add(new InstanceData(id, data.n(), data.k(), data.dist()));
        }
        return instances;
    }
}
