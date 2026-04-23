import java.util.*;

/**
 * Gerador de grafos aleatórios com propriedades eulerianas controladas.
 *
 * Referências:
 * - Erdős–Rényi model G(n,m): grafo aleatório com n vértices e m arestas.
 *   Erdős, P. & Rényi, A. (1959). "On random graphs I."
 *   Publicationes Mathematicae Debrecen, 6, 290-297.
 * - Condições eulerianas: Euler (1736); formalização moderna em:
 *   West, D.B. (2001). Introduction to Graph Theory, 2nd ed. Prentice Hall.
 */
public class GeradorDeGrafos {

    private static final Random aleatorio = new Random(42); // semente fixa para reprodutibilidade

    /**
     * Gera um grafo EULERIANO conexo com V vértices.
     * Cria um ciclo base (garante conectividade e grau par mínimo)
     * e acrescenta arestas extras sempre em pares para manter paridade.
     */
    public Grafo gerarEuleriano(int V) {
        Grafo g = new Grafo(V);
        if (V < 3) return g;

        // Ciclo base: 0-1-2-...(V-1)-0  => todos grau 2 (par)
        for (int i = 0; i < V; i++)
            g.adicionarAresta(i, (i + 1) % V);

        int extras = Math.max(0, V / 4);
        Set<String> existentes = construirConjuntoDeArestas(g);

        for (int k = 0; k < extras; k++) {
            int u = aleatorio.nextInt(V);
            int v = aleatorio.nextInt(V);
            if (u == v) continue;

            int w = aleatorio.nextInt(V);
            int x = aleatorio.nextInt(V);
            if (w == x) continue;

            String chave1 = Math.min(u, v) + "-" + Math.max(u, v);
            String chave2 = Math.min(w, x) + "-" + Math.max(w, x);

            if (!existentes.contains(chave1) && !existentes.contains(chave2) && !chave1.equals(chave2)) {
                g.adicionarAresta(u, v);
                g.adicionarAresta(w, x);
                existentes.add(chave1);
                existentes.add(chave2);
            }
        }
        return g;
    }

    /**
     * Gera um grafo SEMI-EULERIANO conexo com V vértices.
     * Parte de um grafo euleriano e adiciona uma aresta extra entre dois vértices,
     * tornando exatamente dois deles com grau ímpar.
     */
    public Grafo gerarSemiEuleriano(int V) {
        Grafo g = gerarEuleriano(V);
        if (V < 2) return g;

        // Adiciona UMA aresta extra => dois vértices ficam com grau ímpar
        int u = 0;
        int v = V / 2;
        g.adicionarAresta(u, v);
        return g;
    }

    /**
     * Gera um grafo NÃO EULERIANO conexo com V vértices.
     * Parte do ciclo base e adiciona arestas avulsas que quebram a paridade em mais de 2 vértices.
     */
    public Grafo gerarNaoEuleriano(int V) {
        Grafo g = new Grafo(V);
        if (V < 3) return g;

        // Ciclo base
        for (int i = 0; i < V; i++)
            g.adicionarAresta(i, (i + 1) % V);

        // Adiciona 3 arestas extras aleatórias (quebra paridade em até 6 vértices)
        Set<String> existentes = construirConjuntoDeArestas(g);
        int adicionadas = 0;
        int tentativas  = 0;

        while (adicionadas < 3 && tentativas < V * 10) {
            int u = aleatorio.nextInt(V);
            int v = aleatorio.nextInt(V);
            tentativas++;
            if (u == v) continue;
            String chave = Math.min(u, v) + "-" + Math.max(u, v);
            if (!existentes.contains(chave)) {
                g.adicionarAresta(u, v);
                existentes.add(chave);
                adicionadas++;
            }
        }
        return g;
    }

    /** Constrói conjunto de arestas já existentes (para evitar multi-arestas). */
    private Set<String> construirConjuntoDeArestas(Grafo g) {
        Set<String> conjunto = new HashSet<>();
        for (int u = 0; u < g.numeroDeVertices(); u++)
            for (int v : g.vizinhos(u))
                if (u < v)
                    conjunto.add(u + "-" + v);
        return conjunto;
    }
}
