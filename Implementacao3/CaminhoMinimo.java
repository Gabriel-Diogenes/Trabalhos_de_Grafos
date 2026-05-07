/*
  DESCRIÇÃO:
  Este programa implementa o algoritmo de Dijkstra para encontrar
  o caminho mínimo entre dois vértices de um grafo direcionado e
  ponderado.

  IMPORTANTE: Para não ter erro de arquivo não encontrado, crie a pasta Implementacao3 e jogue os arquivos dentro, caso não estejam.
               
  DIFERENCIAL DA IMPLEMENTAÇÃO:
  Além do menor custo, o algoritmo também
  considera como critério de desempate a menor quantidade de arestas. Isso é útil em cenários onde, mesmo com
  o mesmo custo, caminhos com menos etapas são preferíveis.
 
  O grafo é carregado a partir de arquivos de texto escolhidos pelo
  usuário, contendo:
  - Número de vértices e arestas
  - Lista de arestas (origem, destino, peso)
  - Origem e destino do caminho desejado
 
  O resultado exibido inclui:
  - Distância mínima
  - Quantidade de arestas do caminho
  - Caminho completo da origem ao destino
 
  ESTRUTURAS UTILIZADAS:
  - Lista de adjacência para representação do grafo
  - PriorityQueue para otimização do Dijkstra
  - Arrays para controle de distâncias, predecessores e arestas
 
  COMPLEXIDADE:
  O algoritmo roda em O((V + E) log V), devido ao uso de heap
  na fila de prioridade.
 
  FONTES TEÓRICAS:
  - Cormen, T. H. et al. "Introduction to Algorithms (CLRS)"
    Capítulo de algoritmos em grafos (Dijkstra)
 
  - Sedgewick, R.; Wayne, K. "Algorithms"
    Seção de shortest path algorithms
 
  - GeeksforGeeks: Dijkstra’s Algorithm
    https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm/
 */


import java.io.*;
import java.util.*;

class Aresta {
    int destino;
    int peso;

    public Aresta(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }
}

class No implements Comparable<No> {
    int vertice;
    int distancia;
    int arestas;

    public No(int vertice, int distancia, int arestas) {
        this.vertice = vertice;
        this.distancia = distancia;
        this.arestas = arestas;
    }

    @Override
    public int compareTo(No outro) {

        if (this.distancia != outro.distancia) {
            return Integer.compare(this.distancia, outro.distancia);
        }

        return Integer.compare(this.arestas, outro.arestas);
    }
}

public class CaminhoMinimo {

    static List<List<Aresta>> grafo;
    static int vertices;
    static int arestas;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("ESCOLHA O ARQUIVO:");
        System.out.println("1 - grafo_denso.txt");
        System.out.println("2 - grafo_esparso.txt");

        int opcao = scanner.nextInt();

        String arquivo = "";

        switch (opcao) {

            case 1:
                arquivo = "Implementacao3/grafo_denso.txt";
                break;

            case 2:
                arquivo = "Implementacao3/grafo_esparso.txt";
                break;

            default:
                System.out.println("Opção inválida.");
                return;
        }

        lerArquivo(arquivo);

        scanner.close();
    }

    public static void lerArquivo(String nomeArquivo) {

        try {

            BufferedReader br = new BufferedReader(new FileReader(nomeArquivo));

            String[] primeiraLinha = br.readLine().split(" ");

            vertices = Integer.parseInt(primeiraLinha[0]);
            arestas = Integer.parseInt(primeiraLinha[1]);

            grafo = new ArrayList<>();

            for (int i = 0; i < vertices; i++) {
                grafo.add(new ArrayList<>());
            }

            for (int i = 0; i < arestas; i++) {

                String[] linha = br.readLine().split(" ");

                int origem = Integer.parseInt(linha[0]);
                int destino = Integer.parseInt(linha[1]);
                int peso = Integer.parseInt(linha[2]);

                grafo.get(origem).add(new Aresta(destino, peso));
            }

            String[] ultimaLinha = br.readLine().split(" ");

            int origem = Integer.parseInt(ultimaLinha[0]);
            int destino = Integer.parseInt(ultimaLinha[1]);

            br.close();

            dijkstra(origem, destino);

        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    public static void dijkstra(int origem, int destino) {

        int[] distancia = new int[vertices];
        int[] quantidadeArestas = new int[vertices];
        int[] anterior = new int[vertices];

        Arrays.fill(distancia, Integer.MAX_VALUE);
        Arrays.fill(quantidadeArestas, Integer.MAX_VALUE);
        Arrays.fill(anterior, -1);

        distancia[origem] = 0;
        quantidadeArestas[origem] = 0;

        PriorityQueue<No> fila = new PriorityQueue<>();

        fila.add(new No(origem, 0, 0));

        while (!fila.isEmpty()) {

            No atual = fila.poll();

            int verticeAtual = atual.vertice;

            for (Aresta aresta : grafo.get(verticeAtual)) {

                int novoDestino = aresta.destino;
                int novaDistancia = distancia[verticeAtual] + aresta.peso;
                int novasArestas = quantidadeArestas[verticeAtual] + 1;

                if (novaDistancia < distancia[novoDestino]) {

                    distancia[novoDestino] = novaDistancia;
                    quantidadeArestas[novoDestino] = novasArestas;
                    anterior[novoDestino] = verticeAtual;

                    fila.add(new No(novoDestino, novaDistancia, novasArestas));

                } else if (novaDistancia == distancia[novoDestino]
                        && novasArestas < quantidadeArestas[novoDestino]) {

                    quantidadeArestas[novoDestino] = novasArestas;
                    anterior[novoDestino] = verticeAtual;

                    fila.add(new No(novoDestino, novaDistancia, novasArestas));
                }
            }
        }

        if (distancia[destino] == Integer.MAX_VALUE) {

            System.out.println("Não existe caminho.");
            return;
        }

        List<Integer> caminho = new ArrayList<>();

        int atual = destino;

        while (atual != -1) {

            caminho.add(atual);
            atual = anterior[atual];
        }

        Collections.reverse(caminho);

        System.out.println("\n===== RESULTADO =====");

        System.out.println("Origem: " + origem);
        System.out.println("Destino: " + destino);

        System.out.println("Distância mínima: " + distancia[destino]);

        System.out.println("Quantidade de arestas: "
                + quantidadeArestas[destino]);

        System.out.print("Caminho: ");

        for (int i = 0; i < caminho.size(); i++) {

            System.out.print(caminho.get(i));

            if (i < caminho.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }
}