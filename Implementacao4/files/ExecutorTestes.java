import java.io.*;
import java.util.*;

public class ExecutorTestes {

    public static void main(String[] args) throws Exception {
        System.out.println("=========================================");
        System.out.println("  EXECUTOR DE TESTES - CAMINHOS DISJUNTOS");
        System.out.println("=========================================\n");

        new File("../testes/grafo_aleatorio").mkdirs();
        new File("../testes/grafo_grade").mkdirs();

        System.out.println("[1/3] Gerando arquivos de teste...");
        gerarArquivosTeste();

        System.out.println("\n[2/3] Executando testes - Grafo Aleatorio...");
        List<LinhaTabela> resultadosAleatorio = executarTestesAleatorio();

        System.out.println("\n[3/3] Executando testes - Grafo Grade...");
        List<LinhaTabela> resultadosGrade = executarTestesGrade();

        System.out.println("\n");
        exibirTabela("TABELA 1 - GRAFO ALEATORIO (p=0.30)", resultadosAleatorio);
        System.out.println();
        exibirTabela("TABELA 2 - GRAFO GRADE (Grade N x N)", resultadosGrade);

        salvarCSV("../testes/resultados_aleatorio.csv", resultadosAleatorio);
        salvarCSV("../testes/resultados_grade.csv", resultadosGrade);

        System.out.println("\nResultados salvos em CSV para o relatorio.");
    }

    private static void gerarArquivosTeste() throws IOException {
        int[] tamanhos = {10, 25, 50, 100};
        double prob = 0.30;
        Random rand = new Random(42);

        for (int n : tamanhos) {
            String arquivo = "../testes/grafo_aleatorio/aleatorio_" + n + ".txt";
            gerarGrafoAleatorio(n, prob, 0, n - 1, arquivo, rand);
        }

        int[][] grades = {{3, 3}, {5, 5}, {8, 8}, {12, 12}};
        for (int[] g : grades) {
            String arquivo = "../testes/grafo_grade/grade_" + g[0] + "x" + g[1] + ".txt";
            gerarGrafoGrade(g[0], g[1], arquivo);
        }

        System.out.println("  Arquivos gerados com sucesso.");
    }

    private static List<LinhaTabela> executarTestesAleatorio() {
        List<LinhaTabela> resultados = new ArrayList<>();
        int[] tamanhos = {10, 25, 50, 100};

        for (int n : tamanhos) {
            String arquivo = "../testes/grafo_aleatorio/aleatorio_" + n + ".txt";
            System.out.println("  Testando: aleatorio_" + n + ".txt (N=" + n + ")");

            int repeticoes = 5;
            long tempoTotal = 0;
            int quantidadeCaminhos = 0;

            for (int r = 0; r < repeticoes; r++) {
                CaminhosDisjuntos.ResultadoExecucao resultado =
                        CaminhosDisjuntos.executar(arquivo, false);
                if (r == 0) quantidadeCaminhos = resultado.quantidadeCaminhos;
                tempoTotal += resultado.tempoNanosegundos;
            }

            double tempoMedio = (tempoTotal / repeticoes) / 1_000_000.0;

            int arestas = contarArestas(arquivo);
            resultados.add(new LinhaTabela("N=" + n, n, arestas, quantidadeCaminhos, tempoMedio));
            System.out.printf("    -> %d caminhos disjuntos, tempo medio: %.4f ms%n", quantidadeCaminhos, tempoMedio);
        }

        return resultados;
    }

    private static List<LinhaTabela> executarTestesGrade() {
        List<LinhaTabela> resultados = new ArrayList<>();
        int[][] grades = {{3, 3}, {5, 5}, {8, 8}, {12, 12}};

        for (int[] g : grades) {
            int linhas = g[0], colunas = g[1];
            int n = linhas * colunas;
            String arquivo = "../testes/grafo_grade/grade_" + linhas + "x" + colunas + ".txt";
            System.out.println("  Testando: grade_" + linhas + "x" + colunas + ".txt (N=" + n + " vertices)");

            int repeticoes = 5;
            long tempoTotal = 0;
            int quantidadeCaminhos = 0;

            for (int r = 0; r < repeticoes; r++) {
                CaminhosDisjuntos.ResultadoExecucao resultado =
                        CaminhosDisjuntos.executar(arquivo, false);
                if (r == 0) quantidadeCaminhos = resultado.quantidadeCaminhos;
                tempoTotal += resultado.tempoNanosegundos;
            }

            double tempoMedio = (tempoTotal / repeticoes) / 1_000_000.0;
            int arestas = contarArestas(arquivo);
            String descricao = linhas + "x" + colunas + " (" + n + " v.)";
            resultados.add(new LinhaTabela(descricao, n, arestas, quantidadeCaminhos, tempoMedio));
            System.out.printf("    -> %d caminhos disjuntos, tempo medio: %.4f ms%n", quantidadeCaminhos, tempoMedio);
        }

        return resultados;
    }

    private static void exibirTabela(String titulo, List<LinhaTabela> linhas) {
        System.out.println("========================================================================");
        System.out.println("  " + titulo);
        System.out.println("========================================================================");
        System.out.printf("%-20s | %-10s | %-10s | %-15s | %-12s%n",
                "Instância", "Vértices", "Arestas", "Qtd. Caminhos", "Tempo (ms)");
        System.out.println("------------------------------------------------------------------------");
        for (LinhaTabela linha : linhas) {
            System.out.printf("%-20s | %-10d | %-10d | %-15d | %-12.4f%n",
                    linha.descricao, linha.vertices, linha.arestas,
                    linha.quantidadeCaminhos, linha.tempoMs);
        }
        System.out.println("========================================================================");
    }

    private static void salvarCSV(String arquivo, List<LinhaTabela> linhas) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println("Instancia,Vertices,Arestas,Quantidade_Caminhos,Tempo_ms");
            for (LinhaTabela linha : linhas) {
                pw.printf("%s,%d,%d,%d,%.4f%n",
                        linha.descricao, linha.vertices, linha.arestas,
                        linha.quantidadeCaminhos, linha.tempoMs);
            }
        }
        System.out.println("  Salvo: " + arquivo);
    }

    private static int contarArestas(String arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            br.readLine(); // vertices
            return Integer.parseInt(br.readLine().trim()); // arestas
        } catch (Exception e) {
            return 0;
        }
    }

    private static void gerarGrafoAleatorio(int n, double p, int origem, int destino,
                                             String arquivo, Random rand) throws IOException {
        List<int[]> arestas = new ArrayList<>();
        Set<String> set = new HashSet<>();
        arestas.add(new int[]{origem, destino});
        set.add(origem + "," + destino);
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && rand.nextDouble() < p) {
                    String chave = u + "," + v;
                    if (!set.contains(chave)) {
                        arestas.add(new int[]{u, v});
                        set.add(chave);
                    }
                }
            }
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println(n);
            pw.println(arestas.size());
            for (int[] a : arestas) pw.println(a[0] + " " + a[1]);
            pw.println(origem + " " + destino);
        }
    }

    private static void gerarGrafoGrade(int linhas, int colunas, String arquivo) throws IOException {
        int n = linhas * colunas;
        List<int[]> arestas = new ArrayList<>();
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int u = i * colunas + j;
                if (j + 1 < colunas) arestas.add(new int[]{u, i * colunas + (j + 1)});
                if (i + 1 < linhas) arestas.add(new int[]{u, (i + 1) * colunas + j});
            }
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println(n);
            pw.println(arestas.size());
            for (int[] a : arestas) pw.println(a[0] + " " + a[1]);
            pw.println("0 " + (n - 1));
        }
    }

    static class LinhaTabela {
        String descricao;
        int vertices, arestas, quantidadeCaminhos;
        double tempoMs;

        LinhaTabela(String descricao, int vertices, int arestas, int quantidadeCaminhos, double tempoMs) {
            this.descricao = descricao;
            this.vertices = vertices;
            this.arestas = arestas;
            this.quantidadeCaminhos = quantidadeCaminhos;
            this.tempoMs = tempoMs;
        }
    }
}
