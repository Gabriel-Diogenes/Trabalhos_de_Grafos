import java.io.*;
import java.util.*;

public class GeradorGrafos {

    private static final Random aleatorio = new Random(42); // Semente fixa para reprodutibilidade

    public static void gerarGrafoAleatorio(int numeroVertices, double probabilidade,
                                            int origem, int destino, String caminhoArquivo) throws IOException {
        List<int[]> arestas = new ArrayList<>();
        Set<String> arestasSet = new HashSet<>();

        arestas.add(new int[]{origem, destino});
        arestasSet.add(origem + "," + destino);

        for (int u = 0; u < numeroVertices; u++) {
            for (int v = 0; v < numeroVertices; v++) {
                if (u != v && aleatorio.nextDouble() < probabilidade) {
                    String chave = u + "," + v;
                    if (!arestasSet.contains(chave)) {
                        arestas.add(new int[]{u, v});
                        arestasSet.add(chave);
                    }
                }
            }
        }

        escreverArquivo(caminhoArquivo, numeroVertices, arestas, origem, destino);
    }

    public static void gerarGrafoGrade(int linhas, int colunas, String caminhoArquivo) throws IOException {
        int numeroVertices = linhas * colunas;
        List<int[]> arestas = new ArrayList<>();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int u = i * colunas + j;
                if (j + 1 < colunas) {
                    int v = i * colunas + (j + 1);
                    arestas.add(new int[]{u, v});
                }
                if (i + 1 < linhas) {
                    int v = (i + 1) * colunas + j;
                    arestas.add(new int[]{u, v});
                }
            }
        }

        int origem = 0;
        int destino = numeroVertices - 1;
        escreverArquivo(caminhoArquivo, numeroVertices, arestas, origem, destino);
    }

    private static void escreverArquivo(String caminhoArquivo, int numeroVertices,
                                         List<int[]> arestas, int origem, int destino) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(caminhoArquivo))) {
            escritor.println(numeroVertices);
            escritor.println(arestas.size());
            for (int[] aresta : arestas) {
                escritor.println(aresta[0] + " " + aresta[1]);
            }
            escritor.println(origem + " " + destino);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Gerando arquivos de teste...");

        int[] tamanhoAleatorio = {10, 25, 50, 100};
        double probabilidade = 0.30;

        for (int n : tamanhoAleatorio) {
            String arquivo = "../testes/grafo_aleatorio/aleatorio_" + n + ".txt";
            gerarGrafoAleatorio(n, probabilidade, 0, n - 1, arquivo);
            System.out.println("  Gerado: " + arquivo);
        }

        int[][] tamanhoGrade = {{3, 3}, {5, 5}, {8, 8}, {12, 12}};

        for (int[] dim : tamanhoGrade) {
            int linhas = dim[0], colunas = dim[1];
            String arquivo = "../testes/grafo_grade/grade_" + linhas + "x" + colunas + ".txt";
            gerarGrafoGrade(linhas, colunas, arquivo);
            System.out.println("  Gerado: " + arquivo);
        }

        System.out.println("Concluido!");
    }
}
