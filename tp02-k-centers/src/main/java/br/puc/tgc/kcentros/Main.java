package br.puc.tgc.kcentros;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        imprimirCabecalho();

        Path diretorioDados = CaminhosProjeto.diretorioDados();
        Path csvResultados = CaminhosProjeto.diretorioResultados().resolve("resultados.csv");
        Path diretorioFiguras = CaminhosProjeto.diretorioFiguras();

        System.out.println("=== 1. Preparacao dos dados ===");
        BaixadorDados.garantirDados(diretorioDados);
        System.out.println();

        System.out.println("=== 2. Execucao dos experimentos (40 instancias) ===");
        List<Experimentos.LinhaExperimento> linhas = Experimentos.executarExperimentos(diretorioDados);
        Experimentos.escreverCsv(csvResultados, linhas);
        System.out.println();

        System.out.println("=== 3. Resultados completos ===");
        Experimentos.imprimirTabelaResultados(linhas);
        System.out.println();

        System.out.println("=== 4. Resumo estatistico ===");
        Experimentos.imprimirResumo(linhas);
        System.out.println();

        System.out.println("=== 5. Geracao de graficos ===");
        GeradorGraficos.gerarGraficos(linhas, diretorioFiguras);
        System.out.println();

        System.out.println("=== Concluido ===");
        System.out.println("CSV:      " + csvResultados);
        System.out.println("Graficos: " + diretorioFiguras);
    }

    private static void imprimirCabecalho() {
        System.out.println("============================================================");
        System.out.println("  TP02 - Problema dos k-Centros");
        System.out.println("  Teoria dos Grafos e Computabilidade");
        System.out.println("  Algoritmo exato (PLI) vs aproximado (Gonzalez)");
        System.out.println("============================================================");
        System.out.println();
    }
}
