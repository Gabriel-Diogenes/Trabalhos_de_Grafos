import java.io.*;
import java.util.*;

public class LeitorGrafo {

    private String caminhoArquivo;

    public LeitorGrafo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public ResultadoLeitura lerGrafo() throws IOException {
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
            int numeroVertices = Integer.parseInt(leitor.readLine().trim());

            int numeroArestas = Integer.parseInt(leitor.readLine().trim());

            Grafo grafo = new Grafo(numeroVertices);

            for (int i = 0; i < numeroArestas; i++) {
                String linha = leitor.readLine();
                if (linha == null || linha.trim().isEmpty()) {
                    throw new IOException("Arquivo mal formatado: esperava " + numeroArestas
                            + " arestas mas encontrou apenas " + i);
                }
                String[] partes = linha.trim().split("\\s+");
                int origem = Integer.parseInt(partes[0]);
                int destino = Integer.parseInt(partes[1]);

                if (origem < 0 || origem >= numeroVertices || destino < 0 || destino >= numeroVertices) {
                    throw new IOException("Aresta inválida: vértice fora do intervalo [0, "
                            + (numeroVertices - 1) + "] na linha: " + linha);
                }

                grafo.adicionarAresta(origem, destino);
            }

            String linhaParVertices = leitor.readLine();
            if (linhaParVertices == null || linhaParVertices.trim().isEmpty()) {
                throw new IOException("Arquivo mal formatado: par de vértices de busca não encontrado");
            }
            String[] parVertices = linhaParVertices.trim().split("\\s+");
            int verticeBusca = Integer.parseInt(parVertices[0]);
            int destinoBusca = Integer.parseInt(parVertices[1]);

            return new ResultadoLeitura(grafo, verticeBusca, destinoBusca);
        }
    }

    public static class ResultadoLeitura {
        private Grafo grafo;
        private int verticeOrigem;
        private int verticeDestino;

        public ResultadoLeitura(Grafo grafo, int verticeOrigem, int verticeDestino) {
            this.grafo = grafo;
            this.verticeOrigem = verticeOrigem;
            this.verticeDestino = verticeDestino;
        }

        public Grafo getGrafo() {
            return grafo;
        }

        public int getVerticeOrigem() {
            return verticeOrigem;
        }

        public int getVerticeDestino() {
            return verticeDestino;
        }
    }
}
