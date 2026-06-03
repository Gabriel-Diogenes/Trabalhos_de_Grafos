import java.util.*;

/**
 * Representa um grafo direcionado com suporte a fluxo máximo.
 * Utilizado para encontrar caminhos disjuntos em arestas.
 */
public class Grafo {
    private int numeroVertices;
    private List<List<Aresta>> listaAdjacencia;

    public Grafo(int numeroVertices) {
        this.numeroVertices = numeroVertices;
        listaAdjacencia = new ArrayList<>();
        for (int i = 0; i < numeroVertices; i++) {
            listaAdjacencia.add(new ArrayList<>());
        }
    }

    public int getNumeroVertices() {
        return numeroVertices;
    }

    public List<List<Aresta>> getListaAdjacencia() {
        return listaAdjacencia;
    }

    public void adicionarAresta(int origem, int destino) {
      
        Aresta arestaIda = new Aresta(origem, destino, 1);

        Aresta arestaVolta = new Aresta(destino, origem, 0);

        arestaIda.setArestaReversa(arestaVolta);
        arestaVolta.setArestaReversa(arestaIda);

        listaAdjacencia.get(origem).add(arestaIda);
        listaAdjacencia.get(destino).add(arestaVolta);
    }
}
