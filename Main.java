import java.util.*;

/**
 * Experimentos de tempo para comparar os dois métodos de detecção de pontes
 * aplicados ao algoritmo de Fleury em grafos aleatórios de tamanhos variados.
 *
 * Tamanhos testados: 100, 1.000, 10.000, 100.000 vértices
 * Tipos de grafo:    Euleriano, Semi-euleriano, Nao euleriano
 * Estratégias:       Naïve, Tarjan
 *
 * Referência para metodologia de benchmarking em Java:
 * - Georges, A. et al. (2007). "Statistically Rigorous Java Performance Evaluation."
 *   OOPSLA 2007. DOI:10.1145/1297027.1297033
 */
public class Main {

    private final int REPETICOES = 3;
    private final int[] TAMANHOS = {100, 1_000, 10_000, 100_000};
    private final GeradorDeGrafos gerador = new GeradorDeGrafos();

    public void executar() {
        //Prints gerados por IA para a visualização no terminal ficar mais organizada
        System.out.println("=".repeat(80));
        System.out.println("  EXPERIMENTO: Pontes e Caminhos Eulerianos");
        System.out.println("  Algoritmos: Naive vs Tarjan (Fleury)");
        System.out.println("=".repeat(80));

        System.out.println("\n--- DEMONSTRACAO COM GRAFO PEQUENO ---\n");
        demonstrarGrafoPequeno();
        System.out.println("\n--- EXPERIMENTOS DE TEMPO (ms) ---\n");
        System.out.printf("%-12s %-15s %-20s %-20s%n",
                "Vertices", "Tipo", "Naive (ms)", "Tarjan (ms)");
        System.out.println("-".repeat(70));

        for (int tamanho : TAMANHOS) {
            executarExperimentosParaTamanho(tamanho);
        }

        System.out.println("\nFim dos experimentos.");
    }

    private void demonstrarGrafoPequeno() {
        // Grafo euleriano: ciclo 0-1-2-3-4-0 + ciclo extra 1-3-2-1
        // Graus resultantes: todos pares => circuito euleriano
        Grafo g = new Grafo(5);
        g.adicionarAresta(0, 1); g.adicionarAresta(1, 2); g.adicionarAresta(2, 3);
        g.adicionarAresta(3, 4); g.adicionarAresta(4, 0);
        g.adicionarAresta(1, 3); g.adicionarAresta(3, 2); g.adicionarAresta(2, 1);

        AlgoritmoFleury fleury = new AlgoritmoFleury(AlgoritmoFleury.Estrategia.NAIVE);

        System.out.println("Grafo de demonstracao (V=5, E=" + g.numeroDeArestas() + ")");
        System.out.println("Classificacao: " + fleury.classificarGrafo(g));

        DetectorDePontesNaive detectorNaive = new DetectorDePontesNaive(g);
        List<int[]> pontesNaive = detectorNaive.encontrarTodasAsPontes();
        System.out.print("Pontes (Naive): ");
        if (pontesNaive.isEmpty()) System.out.println("nenhuma");
        else { for (int[] p : pontesNaive) System.out.print("[" + p[0] + "-" + p[1] + "] "); System.out.println(); }

        DetectorDePontesTarjan detectorTarjan = new DetectorDePontesTarjan(g);
        List<int[]> pontesTarjan = detectorTarjan.obterPontes();
        System.out.print("Pontes (Tarjan): ");
        if (pontesTarjan.isEmpty()) System.out.println("nenhuma");
        else { for (int[] p : pontesTarjan) System.out.print("[" + p[0] + "-" + p[1] + "] "); System.out.println(); }

        AlgoritmoFleury fleuryNaive  = new AlgoritmoFleury(AlgoritmoFleury.Estrategia.NAIVE);
        AlgoritmoFleury fleuryTarjan = new AlgoritmoFleury(AlgoritmoFleury.Estrategia.TARJAN);

        List<Integer> caminhoNaive  = fleuryNaive.encontrarCaminhoEuleriano(g);
        List<Integer> caminhoTarjan = fleuryTarjan.encontrarCaminhoEuleriano(g);

        System.out.println("Caminho Euleriano (Fleury + Naive):  " +
                (caminhoNaive  != null ? caminhoNaive  : "nao existe"));
        System.out.println("Caminho Euleriano (Fleury + Tarjan): " +
                (caminhoTarjan != null ? caminhoTarjan : "nao existe"));

        System.out.println();
        Grafo gNE = new Grafo(4);
        gNE.adicionarAresta(0, 1); gNE.adicionarAresta(1, 2); gNE.adicionarAresta(2, 3);
        System.out.println("Grafo nao euleriano (caminho linear 0-1-2-3)");
        System.out.println("Classificacao: " + fleuryNaive.classificarGrafo(gNE));
        List<Integer> caminhoNE = fleuryNaive.encontrarCaminhoEuleriano(gNE);
        System.out.println("Caminho Euleriano (Fleury + Naive): " +
                (caminhoNE != null ? caminhoNE : "nao existe"));
    }

    private void executarExperimentosParaTamanho(int V) {
        String[] tipos = {"Euleriano", "Semi-Euler.", "Nao-Euler."};

        for (String tipo : tipos) {
            Grafo g = gerarGrafo(tipo, V);

            double tempoNaive  = medirFleury(g, AlgoritmoFleury.Estrategia.NAIVE);
            double tempoTarjan = medirFleury(g, AlgoritmoFleury.Estrategia.TARJAN);

            String strNaive  = String.format("%.2f", tempoNaive);
            String strTarjan = String.format("%.2f", tempoTarjan);

            System.out.printf("%-12d %-15s %-20s %-20s%n", V, tipo, strNaive, strTarjan);
        }
        System.out.println();
    }

    private Grafo gerarGrafo(String tipo, int V) {
        return switch (tipo) {
            case "Euleriano"   -> gerador.gerarEuleriano(V);
            case "Semi-Euler." -> gerador.gerarSemiEuleriano(V);
            case "Nao-Euler."  -> gerador.gerarNaoEuleriano(V);
            default            -> gerador.gerarEuleriano(V);
        };
    }

    private double medirFleury(Grafo g, AlgoritmoFleury.Estrategia estrategia) {
        AlgoritmoFleury fleury = new AlgoritmoFleury(estrategia);
        long total = 0;

        for (int r = 0; r < REPETICOES; r++) {
            long inicio = System.nanoTime();
            fleury.encontrarCaminhoEuleriano(g);
            long fim = System.nanoTime();
            total += (fim - inicio);
        }

        return (total / (double) REPETICOES) / 1_000_000.0;
    }

    public static void main(String[] args) {
        new Main().executar();
    }
}