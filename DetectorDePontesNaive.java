import java.util.*;

/**
 * Detecção naïve de pontes: para cada aresta (u,v), remove-a temporariamente
 * e verifica se o grafo permanece conexo via BFS. Se desconectar, é uma ponte.
 *
 * Complexidade: O(E * (V + E)) — para cada aresta, uma BFS completa.
 *
 * Referência:
 * - Definição e abordagem naïve descritas em:
 *   Tarjan, R.E. (1974). "A note on finding the bridges of a graph."
 *   Information Processing Letters, 2(6), 160-161.
 * - Implementação de BFS para conectividade:
 *   Cormen et al. (2009). Introduction to Algorithms, 3rd ed. Seção 22.2.
 */
public class DetectorDePontesNaive {

    private final Grafo grafo;

    public DetectorDePontesNaive(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Retorna true se a aresta (u, v) é uma ponte.
     * Remove a aresta, testa conectividade, depois restaura.
     */
    public boolean ehPonte(int u, int v) {
        grafo.removerAresta(u, v);
        boolean conexo = grafo.eConexo();
        grafo.adicionarAresta(u, v);
        return !conexo;
    }

    /**
     * Retorna lista de todas as pontes do grafo.
     * Cada par [u, v] representa uma aresta-ponte.
     */
    public List<int[]> encontrarTodasAsPontes() {
        List<int[]> pontes = new ArrayList<>();

        for (int u = 0; u < grafo.numeroDeVertices(); u++) {
            List<Integer> vizinhos = new ArrayList<>(grafo.vizinhos(u));
            for (int v : vizinhos) {
                if (u < v && ehPonte(u, v)) {
                    pontes.add(new int[]{u, v});
                }
            }
        }
        return pontes;
    }
}
