import java.util.*;

/**
 * Algoritmo de Fleury para encontrar caminho ou circuito euleriano.
 *
 * Ideia:
 * A partir de um vértice inicial válido, percorre arestas preferindo sempre
 * arestas que NÃO são pontes. Só usa uma ponte quando não há outra escolha.
 * A aresta percorrida é removida do grafo a cada passo.
 *
 * Condições eulerianas (grafo não-direcionado conexo):
 * - Circuito euleriano: todos os vértices têm grau par.
 * - Caminho euleriano: exatamente 2 vértices têm grau ímpar (início e fim).
 * - Não euleriano: mais de 2 vértices com grau ímpar.
 *
 * Complexidade:
 * - Com naïve:  O(E² * (V + E))
 * - Com Tarjan: O(E * (V + E)) — Tarjan recalculado a cada passo
 *
 * Referências:
 * - Fleury (1883). "Deux problèmes de Géométrie de situation."
 *   Journal de mathématiques élémentaires, 2ème série, t. II, pp. 257-261.
 * - Descrição moderna do algoritmo:
 *   Skiena, S. (2008). The Algorithm Design Manual, 2nd ed. Springer.
 *   Seção 5.6.3 — "Eulerian Cycle".
 * - Condições de existência de caminhos eulerianos:
 *   Euler, L. (1736). "Solutio problematis ad geometriam situs pertinentis."
 *   Commentarii Academiae Scientiarum Imperialis Petropolitanae, 8, 128-140.
 * - Implementação adaptada de:
 *   GeeksForGeeks — "Fleury's Algorithm for printing Eulerian Path or Circuit"
 *   https://www.geeksforgeeks.org/fleurys-algorithm-for-printing-eulerian-path/
 */
public class AlgoritmoFleury {

    /** Estratégia para detecção de pontes usada internamente. */
    public enum Estrategia { NAIVE, TARJAN }

    private final Estrategia estrategia;

    public AlgoritmoFleury(Estrategia estrategia) {
        this.estrategia = estrategia;
    }

    /**
     * Determina o tipo euleriano do grafo e executa Fleury se possível.
     *
     * @param original Grafo de entrada (não modificado — usa cópia interna)
     * @return Lista de vértices no caminho/circuito, ou null se não euleriano.
     */
    public List<Integer> encontrarCaminhoEuleriano(Grafo original) {
        int[] graus = original.graus();
        List<Integer> verticesImpares = new ArrayList<>();
        for (int v = 0; v < original.numeroDeVertices(); v++)
            if (graus[v] % 2 != 0) verticesImpares.add(v);

        int quantidadeImpares = verticesImpares.size();

        // não euleriano
        if (quantidadeImpares != 0 && quantidadeImpares != 2)
            return null;

        if (!original.eConexo()) return null;

        // Determina vértice de início
        int verticeInicial;
        if (quantidadeImpares == 0) {
            // Circuito euleriano: começa em qualquer vértice com grau > 0
            verticeInicial = 0;
            for (int v = 0; v < original.numeroDeVertices(); v++)
                if (graus[v] > 0) { verticeInicial = v; break; }
        } else {
            // Caminho euleriano: começa em um dos vértices de grau ímpar
            verticeInicial = verticesImpares.get(0);
        }

        // Trabalha sobre cópia do grafo (Fleury remove as arestas)
        Grafo grafoDeTrabalho = new Grafo(original);
        return executarFleury(grafoDeTrabalho, verticeInicial);
    }

    /**
     * Executa o algoritmo de Fleury sobre o grafo de trabalho.
     */
    private List<Integer> executarFleury(Grafo g, int inicio) {
        List<Integer> caminho = new ArrayList<>();
        caminho.add(inicio);

        int atual = inicio;

        while (g.numeroDeArestas() > 0) {
            List<Integer> vizinhos = new ArrayList<>(g.vizinhos(atual));

            if (vizinhos.isEmpty()) break;

            int proximo = escolherAresta(g, atual, vizinhos);

            caminho.add(proximo);
            g.removerAresta(atual, proximo);
            atual = proximo;
        }

        return caminho;
    }

    /**
     * Escolhe qual vizinho percorrer.
     * Regra de Fleury: prefere aresta não-ponte; usa ponte apenas em último caso.
     */
    private int escolherAresta(Grafo g, int u, List<Integer> vizinhos) {
        if (vizinhos.size() == 1) return vizinhos.get(0);

        for (int v : vizinhos) {
            if (!ehPonte(g, u, v)) return v;
        }

        // Todas são pontes: usa a primeira (inevitável)
        return vizinhos.get(0);
    }

    /**
     * Delega a verificação de ponte para a estratégia escolhida.
     */
    private boolean ehPonte(Grafo g, int u, int v) {
        return switch (estrategia) {
            case NAIVE  -> new DetectorDePontesNaive(g).ehPonte(u, v);
            case TARJAN -> new DetectorDePontesTarjan(g).ehPonte(u, v);
        };
    }

    /**
     * Retorna uma string descrevendo o tipo euleriano do grafo.
     */
    public String classificarGrafo(Grafo g) {
        if (!g.eConexo()) return "Nao conexo (nao euleriano)";

        int[] graus = g.graus();
        int impares = 0;
        for (int d : graus) if (d % 2 != 0) impares++;

        return switch (impares) {
            case 0 -> "Euleriano (circuito euleriano existe)";
            case 2 -> "Semi-euleriano (caminho euleriano existe)";
            default -> "Nao euleriano (" + impares + " vertices de grau impar)";
        };
    }
}
