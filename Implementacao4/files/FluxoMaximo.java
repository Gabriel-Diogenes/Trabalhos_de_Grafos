import java.util.*;

public class FluxoMaximo {

    private Grafo grafo;
    private int numeroVertices;

    public FluxoMaximo(Grafo grafo) {
        this.grafo = grafo;
        this.numeroVertices = grafo.getNumeroVertices();
    }


    public int calcularFluxoMaximo(int origem, int destino) {
        int fluxoTotal = 0;

        while (true) {
            Aresta[] caminhoAnterior = buscaEmLargura(origem, destino);

            if (caminhoAnterior[destino] == null) {
                break;
            }

            int gargalo = Integer.MAX_VALUE;
            int verticeAtual = destino;
            while (verticeAtual != origem) {
                Aresta aresta = caminhoAnterior[verticeAtual];
                gargalo = Math.min(gargalo, aresta.getCapacidadeResidual());
                verticeAtual = aresta.getOrigem();
            }

            verticeAtual = destino;
            while (verticeAtual != origem) {
                Aresta aresta = caminhoAnterior[verticeAtual];
                aresta.setFluxo(aresta.getFluxo() + gargalo);
                aresta.getArestaReversa().setFluxo(aresta.getArestaReversa().getFluxo() - gargalo);
                verticeAtual = aresta.getOrigem();
            }

            fluxoTotal += gargalo;
        }

        return fluxoTotal;
    }

    private Aresta[] buscaEmLargura(int origem, int destino) {
        Aresta[] caminhoAnterior = new Aresta[numeroVertices];
        boolean[] visitado = new boolean[numeroVertices];

        Queue<Integer> fila = new LinkedList<>();
        fila.add(origem);
        visitado[origem] = true;

        while (!fila.isEmpty() && !visitado[destino]) {
            int vertice = fila.poll();

            for (Aresta aresta : grafo.getListaAdjacencia().get(vertice)) {
                int vizinho = aresta.getDestino();
                if (!visitado[vizinho] && aresta.getCapacidadeResidual() > 0) {
                    visitado[vizinho] = true;
                    caminhoAnterior[vizinho] = aresta;
                    fila.add(vizinho);
                }
            }
        }

        return caminhoAnterior;
    }

    public List<List<Integer>> extrairCaminhosDisjuntos(int origem, int destino) {
        List<List<Integer>> caminhos = new ArrayList<>();

        while (true) {
            List<Integer> caminho = encontrarCaminhoComFluxo(origem, destino);
            if (caminho == null) {
                break;
            }
            caminhos.add(caminho);
        }

        return caminhos;
    }

    private List<Integer> encontrarCaminhoComFluxo(int origem, int destino) {
        boolean[] visitado = new boolean[numeroVertices];
        List<Integer> caminho = new ArrayList<>();
        Map<Integer, Aresta> arestaUsada = new HashMap<>();

        if (dfsComFluxo(origem, destino, visitado, caminho, arestaUsada)) {
            for (Map.Entry<Integer, Aresta> entrada : arestaUsada.entrySet()) {
                Aresta aresta = entrada.getValue();
                aresta.setFluxo(aresta.getFluxo() - 1);
            }
            return caminho;
        }

        return null;
    }

    private boolean dfsComFluxo(int verticeAtual, int destino, boolean[] visitado,
                                 List<Integer> caminho, Map<Integer, Aresta> arestaUsada) {
        visitado[verticeAtual] = true;
        caminho.add(verticeAtual);

        if (verticeAtual == destino) {
            return true;
        }

        for (Aresta aresta : grafo.getListaAdjacencia().get(verticeAtual)) {
            int vizinho = aresta.getDestino();
            if (!visitado[vizinho] && aresta.isArestaOriginal() && aresta.getFluxo() > 0) {
                arestaUsada.put(vizinho, aresta);
                if (dfsComFluxo(vizinho, destino, visitado, caminho, arestaUsada)) {
                    return true;
                }
                arestaUsada.remove(vizinho);
            }
        }

        caminho.remove(caminho.size() - 1);
        return false;
    }
}
