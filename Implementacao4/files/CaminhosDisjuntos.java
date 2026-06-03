import java.util.*;
import java.io.*;

public class CaminhosDisjuntos {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java CaminhosDisjuntos <arquivo_grafo>");
            System.err.println("Exemplo: java CaminhosDisjuntos grafo01.txt");
            System.exit(1);
        }

        String caminhoArquivo = args[0];
        executar(caminhoArquivo, true);
    }

    public static ResultadoExecucao executar(String caminhoArquivo, boolean exibirSaida) {
        try {
            // Leitura do grafo
            LeitorGrafo leitor = new LeitorGrafo(caminhoArquivo);
            LeitorGrafo.ResultadoLeitura resultadoLeitura = leitor.lerGrafo();

            Grafo grafo = resultadoLeitura.getGrafo();
            int origem = resultadoLeitura.getVerticeOrigem();
            int destino = resultadoLeitura.getVerticeDestino();

            if (exibirSaida) {
                System.out.println("=================================================");
                System.out.println("   CAMINHOS DISJUNTOS EM ARESTAS");
                System.out.println("=================================================");
                System.out.println("Arquivo: " + caminhoArquivo);
                System.out.println("Vértices: " + grafo.getNumeroVertices());
                System.out.println("Origem: " + origem + "  |  Destino: " + destino);
                System.out.println("-------------------------------------------------");
            }

            FluxoMaximo fluxo = new FluxoMaximo(grafo);

            long tempoInicio = System.nanoTime();
            int quantidadeCaminhos = fluxo.calcularFluxoMaximo(origem, destino);
            long tempoFluxo = System.nanoTime() - tempoInicio;

            long tempoInicioExtracao = System.nanoTime();
            List<List<Integer>> caminhos = fluxo.extrairCaminhosDisjuntos(origem, destino);
            long tempoExtracao = System.nanoTime() - tempoInicioExtracao;

            long tempoTotal = tempoFluxo + tempoExtracao;

            if (exibirSaida) {
                System.out.println("Quantidade de caminhos disjuntos em arestas: " + quantidadeCaminhos);
                System.out.println("-------------------------------------------------");

                if (caminhos.isEmpty()) {
                    System.out.println("Nenhum caminho encontrado entre " + origem + " e " + destino + ".");
                } else {
                    System.out.println("Caminhos encontrados:");
                    for (int i = 0; i < caminhos.size(); i++) {
                        System.out.print("  Caminho " + (i + 1) + ": ");
                        System.out.println(formatarCaminho(caminhos.get(i)));
                    }
                }

                System.out.println("-------------------------------------------------");
                System.out.printf("Tempo de execucao (fluxo):   %.3f ms%n", tempoFluxo / 1_000_000.0);
                System.out.printf("Tempo de execucao (extracao): %.3f ms%n", tempoExtracao / 1_000_000.0);
                System.out.printf("Tempo total:                  %.3f ms%n", tempoTotal / 1_000_000.0);
                System.out.println("=================================================");
            }

            return new ResultadoExecucao(quantidadeCaminhos, caminhos, tempoTotal, grafo.getNumeroVertices());

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    private static String formatarCaminho(List<Integer> caminho) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < caminho.size(); i++) {
            sb.append(caminho.get(i));
            if (i < caminho.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    public static class ResultadoExecucao {
        public final int quantidadeCaminhos;
        public final List<List<Integer>> caminhos;
        public final long tempoNanosegundos;
        public final int numeroVertices;

        public ResultadoExecucao(int quantidadeCaminhos, List<List<Integer>> caminhos,
                                  long tempoNanosegundos, int numeroVertices) {
            this.quantidadeCaminhos = quantidadeCaminhos;
            this.caminhos = caminhos;
            this.tempoNanosegundos = tempoNanosegundos;
            this.numeroVertices = numeroVertices;
        }

        public double getTempoMillissegundos() {
            return tempoNanosegundos / 1_000_000.0;
        }
    }
}
