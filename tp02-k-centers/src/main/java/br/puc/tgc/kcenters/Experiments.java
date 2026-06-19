package br.puc.tgc.kcenters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Experiments {

    private static final double EXACT_TIME_LIMIT_SECONDS = 300.0;

    private Experiments() {
    }

    public record ExperimentRow(
            int instancia,
            int n,
            int k,
            int raioOtimo,
            String raioExato,
            String tempoExatoS,
            int raioAprox,
            String tempoAproxS,
            String gapAproxPct,
            String exatoTimeout
    ) {
        String toCsvLine() {
            return String.join(",",
                    Integer.toString(instancia),
                    Integer.toString(n),
                    Integer.toString(k),
                    Integer.toString(raioOtimo),
                    raioExato,
                    tempoExatoS,
                    Integer.toString(raioAprox),
                    tempoAproxS,
                    gapAproxPct,
                    exatoTimeout
            );
        }
    }

    public static void main(String[] args) throws IOException {
        Path dataDir = ProjectPaths.dataDir();
        Path outputCsv = ProjectPaths.resultsDir().resolve("resultados.csv");
        List<ExperimentRow> rows = runExperiments(dataDir);
        writeCsv(outputCsv, rows);
        System.out.println("\nResultados salvos em " + outputCsv);
    }

    public static List<ExperimentRow> runExperiments(Path dataDir) throws IOException {
        List<InstanceData> instances = InstanceLoader.loadAllInstances(dataDir);
        List<ExperimentRow> rows = new ArrayList<>();

        for (InstanceData instance : instances) {
            int id = instance.id();
            int n = instance.n();
            int k = instance.k();
            double[][] dist = instance.dist();
            int optimal = OptimalRadii.get(id);

            System.out.printf(Locale.US, "Instancia %02d: |V|=%d, k=%d%n", id, n, k);

            long approxStart = System.nanoTime();
            ExactKCenters.Solution approxSolution = ApproximateKCenters.solve(dist, k);
            double timeApprox = (System.nanoTime() - approxStart) / 1_000_000_000.0;
            int radiusApprox = (int) Math.round(approxSolution.radius());
            double gap = 100.0 * (approxSolution.radius() - optimal) / optimal;

            String radiusExactValue;
            String timeExactValue;
            String timeoutFlag = "nao";

            long exactStart = System.nanoTime();
            try {
                ExactKCenters.Solution exactSolution = ExactKCenters.solve(dist, k);
                double timeExact = (System.nanoTime() - exactStart) / 1_000_000_000.0;
                if (timeExact > EXACT_TIME_LIMIT_SECONDS) {
                    timeoutFlag = "sim";
                    radiusExactValue = "timeout";
                    timeExactValue = String.format(Locale.US, ">%.0f", EXACT_TIME_LIMIT_SECONDS);
                } else {
                    radiusExactValue = Integer.toString((int) Math.round(exactSolution.radius()));
                    timeExactValue = String.format(Locale.US, "%.4f", timeExact);
                }
            } catch (RuntimeException ex) {
                timeoutFlag = "erro";
                radiusExactValue = ex.getMessage() == null ? "erro" : ex.getMessage();
                timeExactValue = "erro";
            }

            ExperimentRow row = new ExperimentRow(
                    id,
                    n,
                    k,
                    optimal,
                    radiusExactValue,
                    timeExactValue,
                    radiusApprox,
                    String.format(Locale.US, "%.4f", timeApprox),
                    String.format(Locale.US, "%.2f", gap),
                    timeoutFlag
            );
            rows.add(row);

            System.out.printf(Locale.US,
                    "  exato=%s (%ss) | aprox=%d (%.4fs) | gap=%s%%%n",
                    row.raioExato(),
                    row.tempoExatoS(),
                    row.raioAprox(),
                    timeApprox,
                    row.gapAproxPct()
            );
        }

        return rows;
    }

    private static void writeCsv(Path outputCsv, List<ExperimentRow> rows) throws IOException {
        Files.createDirectories(outputCsv.getParent());
        List<String> lines = new ArrayList<>();
        lines.add("instancia,n,k,raio_otimo,raio_exato,tempo_exato_s,raio_aprox,tempo_aprox_s,gap_aprox_pct,exato_timeout");
        for (ExperimentRow row : rows) {
            lines.add(row.toCsvLine());
        }
        Files.write(outputCsv, lines);
    }
}
