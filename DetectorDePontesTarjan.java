import java.util.*;

/**
 * Algoritmo de Tarjan (1974) para encontrar pontes em grafos não-direcionados.
 *
 * Ideia central:
 * Durante a busca em profundidade, cada vértice recebe um "tempo de descoberta" (tempoDescoberta[]).
 * O valor baixo[] de um vértice v representa o menor tempoDescoberta[] alcançável a partir
 * da subárvore de busca em profundidade de v, incluindo arestas de retorno ("back edges").
 *
 * Uma aresta (u, v) — onde v é filho de u na busca em profundidade — é uma PONTE se e somente se:
 *     baixo[v] > tempoDescoberta[u]
 * Isso significa que nenhum vértice na subárvore de v pode alcançar u ou um
 * ancestral de u por outra rota, logo remover (u,v) desconecta o grafo.
 *
 * Complexidade: O(V + E) — uma única busca em profundidade.
 *
 * Referências:
 * - Tarjan, R.E. (1974). "A note on finding the bridges of a graph."
 *   Information Processing Letters, 2(6), 160-161. DOI:10.1016/0020-0190(74)90047-1
 * - Explicação didática:
 *   Sedgewick, R. & Wayne, K. (2011). Algorithms, 4th Edition. Addison-Wesley.
 *   Seção 4.1 — "Bridges in a graph".
 * - Implementação de referência (adaptada):
 *   GeeksForGeeks — "Bridge in a graph" (consultado 2024).
 *   https://www.geeksforgeeks.org/bridge-in-a-graph/
 */
public class DetectorDePontesTarjan {

    private final Grafo grafo;
    private final int numeroVertices;
    private int[] tempoDescoberta;
    private int[] baixo;
    private boolean[] visitado;
    private int temporizador;
    private final List<int[]> pontes;

    public DetectorDePontesTarjan(Grafo grafo) {
        this.grafo          = grafo;
        this.numeroVertices = grafo.numeroDeVertices();
        tempoDescoberta     = new int[numeroVertices];
        baixo               = new int[numeroVertices];
        visitado            = new boolean[numeroVertices];
        pontes              = new ArrayList<>();
        temporizador        = 0;

        // Garante que todos os componentes sejam visitados
        for (int v = 0; v < numeroVertices; v++)
            if (!visitado[v])
                buscaEmProfundidade(v, -1);
    }

    /**
     * Busca em profundidade recursiva.
     *
     * @param u      vértice atual
     * @param pai    vértice pai na árvore de busca em profundidade (-1 se raiz)
     */
    private void buscaEmProfundidade(int u, int pai) {
        visitado[u] = true;
        tempoDescoberta[u] = baixo[u] = temporizador++;

        for (int v : grafo.vizinhos(u)) {
            if (!visitado[v]) {
                // v é filho de u na árvore de busca em profundidade
                buscaEmProfundidade(v, u);

                // Atualiza baixo[u] com o que v pode alcançar
                baixo[u] = Math.min(baixo[u], baixo[v]);

                // Condição de ponte: nenhum caminho alternativo de v para u ou acima
                if (baixo[v] > tempoDescoberta[u]) {
                    pontes.add(new int[]{u, v});
                }

            } else if (v != pai) {
                // Aresta de retorno: atualiza baixo[u] com o tempo de descoberta de v
                baixo[u] = Math.min(baixo[u], tempoDescoberta[v]);
            }
        }
    }

    /** Retorna todas as pontes encontradas como lista de pares [u, v]. */
    public List<int[]> obterPontes() {
        return pontes;
    }

    /**
     * Verifica se uma aresta específica (u, v) é ponte,
     * reconstruindo o detector sobre o grafo atual.
     */
    public boolean ehPonte(int u, int v) {
        DetectorDePontesTarjan detector = new DetectorDePontesTarjan(grafo);
        for (int[] ponte : detector.obterPontes()) {
            if ((ponte[0] == u && ponte[1] == v) || (ponte[0] == v && ponte[1] == u))
                return true;
        }
        return false;
    }
}
