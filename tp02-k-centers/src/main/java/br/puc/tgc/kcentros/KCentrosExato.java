package br.puc.tgc.kcentros;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KCentrosExato {

    public record Solucao(double raio, List<Integer> centros) {
    }

    private KCentrosExato() {
    }

    public static Solucao resolver(double[][] distancias, int k) {
        List<Double> raios = CarregadorInstancias.obterRaiosCandidatos(distancias);
        if (raios.isEmpty()) {
            return new Solucao(0.0, List.of(0));
        }

        int inicio = 0;
        int fim = raios.size() - 1;
        double melhorRaio = raios.get(fim);
        List<Integer> melhoresCentros = List.of();

        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            double raio = raios.get(meio);
            ResultadoCobertura resultado = podeCobrirComRaio(distancias, raio, k);
            if (resultado.viavel()) {
                melhorRaio = raio;
                melhoresCentros = resultado.centros();
                fim = meio - 1;
            } else {
                inicio = meio + 1;
            }
        }

        if (!melhoresCentros.isEmpty()) {
            return new Solucao(CarregadorInstancias.calcularRaioSolucao(distancias, melhoresCentros), melhoresCentros);
        }
        return new Solucao(melhorRaio, melhoresCentros);
    }

    private record ResultadoCobertura(boolean viavel, List<Integer> centros) {
    }

    private static ResultadoCobertura podeCobrirComRaio(double[][] distancias, double raio, int k) {
        ResultadoCobertura resultadoPli = podeCobrirComPli(distancias, raio, k);
        if (resultadoPli.viavel()) {
            return resultadoPli;
        }
        return podeCobrirComBacktracking(distancias, raio, k);
    }

    private static ResultadoCobertura podeCobrirComPli(double[][] distancias, double raio, int k) {
        int n = distancias.length;
        ExpressionsBasedModel modelo = new ExpressionsBasedModel();

        Variable[] variaveis = new Variable[n];
        for (int i = 0; i < n; i++) {
            variaveis[i] = modelo.addVariable("x_" + i).binary();
        }

        var limiteCentros = modelo.addExpression("limite_centros").upper(k);
        for (int i = 0; i < n; i++) {
            limiteCentros.set(variaveis[i], 1.0);
        }

        for (int j = 0; j < n; j++) {
            var cobertura = modelo.addExpression("cobrir_" + j).lower(1.0);
            boolean possuiCobertura = false;
            for (int i = 0; i < n; i++) {
                if (distancias[i][j] <= raio + 1e-9) {
                    cobertura.set(variaveis[i], 1.0);
                    possuiCobertura = true;
                }
            }
            if (!possuiCobertura) {
                return new ResultadoCobertura(false, List.of());
            }
        }

        Optimisation.Result resultado = modelo.minimise();
        if (!resultado.getState().isFeasible()) {
            return new ResultadoCobertura(false, List.of());
        }

        List<Integer> centros = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (variaveis[i].getValue().doubleValue() > 0.5) {
                centros.add(i);
            }
        }
        return new ResultadoCobertura(true, centros);
    }

    private static ResultadoCobertura podeCobrirComBacktracking(double[][] distancias, double raio, int k) {
        int n = distancias.length;
        List<Set<Integer>> conjuntosCobertura = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Set<Integer> cobertos = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if (distancias[i][j] <= raio + 1e-9) {
                    cobertos.add(j);
                }
            }
            conjuntosCobertura.add(cobertos);
        }

        Set<Integer> naoCobertos = new HashSet<>();
        for (int j = 0; j < n; j++) {
            naoCobertos.add(j);
        }

        List<Integer> escolhidos = new ArrayList<>();
        List<Integer> resultado = retroceder(naoCobertos, conjuntosCobertura, escolhidos, k);
        if (resultado == null) {
            return new ResultadoCobertura(false, List.of());
        }
        return new ResultadoCobertura(true, resultado);
    }

    private static List<Integer> retroceder(
            Set<Integer> naoCobertos,
            List<Set<Integer>> conjuntosCobertura,
            List<Integer> escolhidos,
            int maximoK
    ) {
        if (naoCobertos.isEmpty()) {
            return new ArrayList<>(escolhidos);
        }
        if (escolhidos.size() >= maximoK) {
            return null;
        }

        int alvo = naoCobertos.stream()
                .min((a, b) -> Integer.compare(contarDominadores(a, conjuntosCobertura), contarDominadores(b, conjuntosCobertura)))
                .orElse(0);

        List<Integer> candidatos = new ArrayList<>();
        for (int i = 0; i < conjuntosCobertura.size(); i++) {
            if (conjuntosCobertura.get(i).contains(alvo)) {
                candidatos.add(i);
            }
        }

        candidatos.sort((a, b) -> Integer.compare(
                tamanhoIntersecao(conjuntosCobertura.get(b), naoCobertos),
                tamanhoIntersecao(conjuntosCobertura.get(a), naoCobertos)
        ));

        for (int centro : candidatos) {
            Set<Integer> novosNaoCobertos = new HashSet<>(naoCobertos);
            novosNaoCobertos.removeAll(conjuntosCobertura.get(centro));
            escolhidos.add(centro);
            List<Integer> solucao = retroceder(novosNaoCobertos, conjuntosCobertura, escolhidos, maximoK);
            if (solucao != null) {
                return solucao;
            }
            escolhidos.remove(escolhidos.size() - 1);
        }
        return null;
    }

    private static int contarDominadores(int vertice, List<Set<Integer>> conjuntosCobertura) {
        int contagem = 0;
        for (Set<Integer> cobertura : conjuntosCobertura) {
            if (cobertura.contains(vertice)) {
                contagem++;
            }
        }
        return contagem;
    }

    private static int tamanhoIntersecao(Set<Integer> cobertura, Set<Integer> naoCobertos) {
        int contagem = 0;
        for (int vertice : naoCobertos) {
            if (cobertura.contains(vertice)) {
                contagem++;
            }
        }
        return contagem;
    }
}
