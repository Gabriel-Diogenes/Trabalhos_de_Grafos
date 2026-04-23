import java.util.*;

/**
 * Representação de grafo não-direcionado usando lista de adjacência.
 *
 * Referência estrutural:
 * - Sedgewick, R. & Wayne, K. (2011). Algorithms, 4th Edition. Addison-Wesley.
 *   Capítulo 4 (Graphs) — estrutura de lista de adjacência com Bag/LinkedList.
 */
public class Grafo {
    private final int numeroVertices;
    private int numeroArestas;
    private List<Integer>[] adjacencia;

    @SuppressWarnings("unchecked")
    public Grafo(int numeroVertices) {
        this.numeroVertices = numeroVertices;
        this.numeroArestas  = 0;
        adjacencia = new ArrayList[numeroVertices];
        for (int v = 0; v < numeroVertices; v++)
            adjacencia[v] = new ArrayList<>();
    }

    /** Cópia profunda de outro grafo. */
    public Grafo(Grafo g) {
        this.numeroVertices = g.numeroVertices;
        this.numeroArestas  = g.numeroArestas;
        adjacencia = new ArrayList[numeroVertices];
        for (int v = 0; v < numeroVertices; v++)
            adjacencia[v] = new ArrayList<>(g.adjacencia[v]);
    }

    public int numeroDeVertices() { return numeroVertices; }
    public int numeroDeArestas()  { return numeroArestas; }

    /** Adiciona aresta não-direcionada u-v. */
    public void adicionarAresta(int u, int v) {
        adjacencia[u].add(v);
        adjacencia[v].add(u);
        numeroArestas++;
    }

    /** Remove UMA ocorrência da aresta u-v (não-direcionada). */
    public void removerAresta(int u, int v) {
        adjacencia[u].remove(Integer.valueOf(v));
        adjacencia[v].remove(Integer.valueOf(u));
        numeroArestas--;
    }

    public List<Integer> vizinhos(int v) { return adjacencia[v]; }

    public int grau(int v) { return adjacencia[v].size(); }

    /**
     * Verifica conectividade ignorando vértices isolados (grau 0).
     * Usado pelo método naïve de detecção de pontes.
     *
     * Referência: BFS para conectividade —
     * Cormen et al. (2009). Introduction to Algorithms, 3rd ed. MIT Press.
     * Seção 22.2 (Busca em Largura).
     */
    public boolean eConexo() {
        if (numeroVertices == 0) return true;

        // Encontra um vértice com grau > 0
        int inicio = -1;
        for (int v = 0; v < numeroVertices; v++) {
            if (grau(v) > 0) { inicio = v; break; }
        }
        // grafo sem arestas
        if (inicio == -1) return true;

        boolean[] visitado = new boolean[numeroVertices];
        Queue<Integer> fila = new ArrayDeque<>();
        fila.add(inicio);
        visitado[inicio] = true;

        while (!fila.isEmpty()) {
            int u = fila.poll();
            for (int w : adjacencia[u]) {
                if (!visitado[w]) {
                    visitado[w] = true;
                    fila.add(w);
                }
            }
        }

        // Todos os vértices com grau > 0 devem estar visitados
        for (int v = 0; v < numeroVertices; v++)
            if (grau(v) > 0 && !visitado[v]) return false;

        return true;
    }

    /** Retorna o grau de cada vértice. */
    public int[] graus() {
        int[] deg = new int[numeroVertices];
        for (int v = 0; v < numeroVertices; v++)
            deg[v] = adjacencia[v].size();
        return deg;
    }
}
