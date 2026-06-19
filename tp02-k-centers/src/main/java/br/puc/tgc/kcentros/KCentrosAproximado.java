package br.puc.tgc.kcentros;

import java.util.ArrayList;
import java.util.List;

public final class KCentrosAproximado {

    private static final double INFINITO = Double.POSITIVE_INFINITY;

    private KCentrosAproximado() {
    }

    public static KCentrosExato.Solucao resolver(double[][] distancias, int k) {
        int n = distancias.length;
        if (k <= 0) {
            return new KCentrosExato.Solucao(0.0, List.of());
        }
        if (k >= n) {
            List<Integer> todos = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                todos.add(i);
            }
            return new KCentrosExato.Solucao(CarregadorInstancias.calcularRaioSolucao(distancias, todos), todos);
        }

        List<Integer> centros = new ArrayList<>();
        centros.add(0);

        for (int passo = 0; passo < k - 1; passo++) {
            int verticeMaisDistante = 0;
            double distanciaMaxima = -1.0;

            for (int vertice = 0; vertice < n; vertice++) {
                double distanciaMinima = INFINITO;
                for (int centro : centros) {
                    distanciaMinima = Math.min(distanciaMinima, distancias[vertice][centro]);
                }
                if (distanciaMinima > distanciaMaxima) {
                    distanciaMaxima = distanciaMinima;
                    verticeMaisDistante = vertice;
                }
            }
            centros.add(verticeMaisDistante);
        }

        return new KCentrosExato.Solucao(CarregadorInstancias.calcularRaioSolucao(distancias, centros), centros);
    }
}
