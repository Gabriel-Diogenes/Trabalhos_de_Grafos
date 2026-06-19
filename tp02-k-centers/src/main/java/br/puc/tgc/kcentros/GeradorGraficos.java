package br.puc.tgc.kcentros;

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
import java.util.List;

public final class GeradorGraficos {

    private GeradorGraficos() {
    }

    public static void gerarGraficos(List<Experimentos.LinhaExperimento> linhas, Path diretorioFiguras) throws IOException {
        Files.createDirectories(diretorioFiguras);

        plotarComparacaoRaios(linhas, diretorioFiguras);
        System.out.println("  raios_comparacao.png");

        plotarComparacaoTempos(linhas, diretorioFiguras);
        System.out.println("  tempos_execucao.png");

        plotarGap(linhas, diretorioFiguras);
        System.out.println("  gap_aproximado.png");

        System.out.println("Graficos salvos em " + diretorioFiguras);
    }

    private static void plotarComparacaoRaios(List<Experimentos.LinhaExperimento> linhas, Path diretorioFiguras) throws IOException {
        XYSeries otimo = new XYSeries("Raio otimo (referencia)");
        XYSeries aproximado = new XYSeries("Algoritmo aproximado (Gonzalez)");

        for (Experimentos.LinhaExperimento linha : linhas) {
            otimo.add(linha.instancia(), linha.raioOtimo());
            aproximado.add(linha.instancia(), linha.raioAprox());
        }

        XYSeriesCollection conjuntoDados = new XYSeriesCollection();
        conjuntoDados.addSeries(otimo);
        conjuntoDados.addSeries(aproximado);

        JFreeChart grafico = ChartFactory.createXYLineChart(
                "Comparacao de raios: otimo vs aproximado",
                "Instancia",
                "Raio da solucao",
                conjuntoDados,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        salvarGrafico(grafico, diretorioFiguras.resolve("raios_comparacao.png"), 1200, 500);
    }

    private static void plotarComparacaoTempos(List<Experimentos.LinhaExperimento> linhas, Path diretorioFiguras) throws IOException {
        XYSeries exato = new XYSeries("Algoritmo exato (PLI + backtracking)");
        XYSeries aproximado = new XYSeries("Algoritmo aproximado (Gonzalez)");

        for (Experimentos.LinhaExperimento linha : linhas) {
            if (!Double.isNaN(linha.valorTempoExato())) {
                exato.add(linha.n(), linha.valorTempoExato());
            }
            aproximado.add(linha.n(), linha.valorTempoAprox());
        }

        XYSeriesCollection conjuntoDados = new XYSeriesCollection();
        conjuntoDados.addSeries(exato);
        conjuntoDados.addSeries(aproximado);

        JFreeChart grafico = ChartFactory.createXYLineChart(
                "Tempo de execucao em funcao do tamanho da instancia",
                "Numero de vertices |V|",
                "Tempo de execucao (s)",
                conjuntoDados,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        grafico.getXYPlot().getRangeAxis().setRange(0.0001, obterTempoMaximo(linhas) * 1.2);

        salvarGrafico(grafico, diretorioFiguras.resolve("tempos_execucao.png"), 1200, 500);
    }

    private static double obterTempoMaximo(List<Experimentos.LinhaExperimento> linhas) {
        double maximo = 0.0;
        for (Experimentos.LinhaExperimento linha : linhas) {
            maximo = Math.max(maximo, linha.valorTempoAprox());
            if (!Double.isNaN(linha.valorTempoExato())) {
                maximo = Math.max(maximo, linha.valorTempoExato());
            }
        }
        return maximo;
    }

    private static void plotarGap(List<Experimentos.LinhaExperimento> linhas, Path diretorioFiguras) throws IOException {
        DefaultCategoryDataset conjuntoDados = new DefaultCategoryDataset();
        for (Experimentos.LinhaExperimento linha : linhas) {
            conjuntoDados.addValue(linha.valorGap(), "Gap (%)", Integer.toString(linha.instancia()));
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                "Gap do algoritmo aproximado em relacao ao raio otimo",
                "Instancia",
                "Gap em relacao ao otimo (%)",
                conjuntoDados,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        grafico.setBackgroundPaint(Color.WHITE);

        salvarGrafico(grafico, diretorioFiguras.resolve("gap_aproximado.png"), 1200, 500);
    }

    private static void salvarGrafico(JFreeChart grafico, Path caminhoSaida, int largura, int altura) throws IOException {
        ChartUtils.saveChartAsPNG(caminhoSaida.toFile(), grafico, largura, altura);
    }
}
