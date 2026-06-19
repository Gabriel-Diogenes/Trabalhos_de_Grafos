package br.puc.tgc.kcenters;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GeneratePlots {

    private GeneratePlots() {
    }

    public static void main(String[] args) throws IOException {
        Path csvPath = ProjectPaths.resultsDir().resolve("resultados.csv");
        Path figuresDir = ProjectPaths.figuresDir();
        Files.createDirectories(figuresDir);

        List<ExperimentRow> rows = readCsv(csvPath);
        plotRadiusComparison(rows, figuresDir);
        plotRuntimeComparison(rows, figuresDir);
        plotGap(rows, figuresDir);
        System.out.println("Graficos salvos em " + figuresDir);
    }

    private record ExperimentRow(
            int instancia,
            int n,
            int raioOtimo,
            int raioAprox,
            double tempoExato,
            double tempoAprox,
            double gap
    ) {
    }

    private static List<ExperimentRow> readCsv(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        List<ExperimentRow> rows = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            int instancia = Integer.parseInt(parts[0]);
            int n = Integer.parseInt(parts[1]);
            int raioOtimo = Integer.parseInt(parts[3]);
            int raioAprox = Integer.parseInt(parts[6]);
            double tempoExato = parseTime(parts[5]);
            double tempoAprox = Double.parseDouble(parts[7]);
            double gap = Double.parseDouble(parts[8]);
            rows.add(new ExperimentRow(instancia, n, raioOtimo, raioAprox, tempoExato, tempoAprox, gap));
        }
        return rows;
    }

    private static double parseTime(String value) {
        if (value.startsWith(">") || value.equals("erro")) {
            return Double.NaN;
        }
        return Double.parseDouble(value);
    }

    private static void plotRadiusComparison(List<ExperimentRow> rows, Path figuresDir) throws IOException {
        XYSeries otimo = new XYSeries("Raio otimo (referencia)");
        XYSeries aprox = new XYSeries("Algoritmo aproximado (Gonzalez)");

        for (ExperimentRow row : rows) {
            otimo.add(row.instancia(), row.raioOtimo());
            aprox.add(row.instancia(), row.raioAprox());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(otimo);
        dataset.addSeries(aprox);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparacao de raios: otimo vs aproximado",
                "Instancia",
                "Raio da solucao",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        saveChart(chart, figuresDir.resolve("raios_comparacao.png"), 1200, 500);
    }

    private static void plotRuntimeComparison(List<ExperimentRow> rows, Path figuresDir) throws IOException {
        XYSeries exato = new XYSeries("Algoritmo exato (backtracking)");
        XYSeries aprox = new XYSeries("Algoritmo aproximado (Gonzalez)");

        for (ExperimentRow row : rows) {
            if (!Double.isNaN(row.tempoExato())) {
                exato.add(row.n(), row.tempoExato());
            }
            aprox.add(row.n(), row.tempoAprox());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(exato);
        dataset.addSeries(aprox);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Tempo de execucao em funcao do tamanho da instancia",
                "Numero de vertices |V|",
                "Tempo de execucao (s)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        chart.getXYPlot().getRangeAxis().setRange(0.0001, maxRuntime(rows) * 1.2);

        saveChart(chart, figuresDir.resolve("tempos_execucao.png"), 1200, 500);
    }

    private static double maxRuntime(List<ExperimentRow> rows) {
        double max = 0.0;
        for (ExperimentRow row : rows) {
            max = Math.max(max, row.tempoAprox());
            if (!Double.isNaN(row.tempoExato())) {
                max = Math.max(max, row.tempoExato());
            }
        }
        return max;
    }

    private static void plotGap(List<ExperimentRow> rows, Path figuresDir) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ExperimentRow row : rows) {
            dataset.addValue(row.gap(), "Gap (%)", Integer.toString(row.instancia()));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Gap do algoritmo aproximado em relacao ao raio otimo",
                "Instancia",
                "Gap em relacao ao otimo (%)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        chart.setBackgroundPaint(Color.WHITE);

        saveChart(chart, figuresDir.resolve("gap_aproximado.png"), 1200, 500);
    }

    private static void saveChart(JFreeChart chart, Path outputPath, int width, int height) throws IOException {
        ChartUtils.saveChartAsPNG(outputPath.toFile(), chart, width, height);
    }
}
