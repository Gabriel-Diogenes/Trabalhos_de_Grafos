public class Aresta {
    private int origem;
    private int destino;
    private int capacidade;
    private int fluxo;
    private Aresta arestaReversa;

    public Aresta(int origem, int destino, int capacidade) {
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
        this.fluxo = 0;
        this.arestaReversa = null;
    }

    public int getOrigem() {
        return origem;
    }

    public int getDestino() {
        return destino;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getFluxo() {
        return fluxo;
    }

    public void setFluxo(int fluxo) {
        this.fluxo = fluxo;
    }

    public Aresta getArestaReversa() {
        return arestaReversa;
    }

    public void setArestaReversa(Aresta arestaReversa) {
        this.arestaReversa = arestaReversa;
    }

    public int getCapacidadeResidual() {
        return capacidade - fluxo;
    }

    public boolean isArestaOriginal() {
        return capacidade > 0;
    }

    @Override
    public String toString() {
        return String.format("(%d -> %d, cap=%d, fluxo=%d)", origem, destino, capacidade, fluxo);
    }
}
