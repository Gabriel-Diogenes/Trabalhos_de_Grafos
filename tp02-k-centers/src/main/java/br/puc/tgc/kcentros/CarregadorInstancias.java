package br.puc.tgc.kcentros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public final class CarregadorInstancias {

    private static final double INFINITO = Double.POSITIVE_INFINITY;

    private CarregadorInstancias() {
    }

    public static DadosInstancia analisarArquivoPmed(Path caminhoArquivo) throws IOException {
        List<String> linhas = Files.readAllLines(caminhoArquivo).stream()
                .map(String::trim)
                .filter(linha -> !linha.isEmpty())
                .toList();

        String[] cabecalho = linhas.get(0).split("\\s+");
        int n = Integer.parseInt(cabecalho[0]);
        int k = Integer.parseInt(cabecalho[2]);

        double[][] distancias = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = i == j ? 0.0 : INFINITO;
            }
        }

        for (int indiceLinha = 1; indiceLinha < linhas.size(); indiceLinha++) {
            String[] partes = linhas.get(indiceLinha).split("\\s+");
            if (partes.length < 3) {
                continue;
            }
            int i = Integer.parseInt(partes[0]) - 1;
            int j = Integer.parseInt(partes[1]) - 1;
            double custo = Double.parseDouble(partes[2]);
            distancias[i][j] = custo;
            distancias[j][i] = custo;
        }

        floydWarshall(distancias, n);
        return new DadosInstancia(0, n, k, distancias);
    }

    public static void floydWarshall(double[][] distancias, int n) {
        for (int pivo = 0; pivo < n; pivo++) {
            for (int i = 0; i < n; i++) {
                double distanciaIp = distancias[i][pivo];
                if (distanciaIp == INFINITO) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    double alternativa = distanciaIp + distancias[pivo][j];
                    if (alternativa < distancias[i][j]) {
                        distancias[i][j] = alternativa;
                    }
                }
            }
        }
    }

    public static double calcularRaioSolucao(double[][] distancias, List<Integer> centros) {
        int n = distancias.length;
        double raio = 0.0;
        for (int vertice = 0; vertice < n; vertice++) {
            double distanciaMinima = INFINITO;
            for (int centro : centros) {
                distanciaMinima = Math.min(distanciaMinima, distancias[vertice][centro]);
            }
            raio = Math.max(raio, distanciaMinima);
        }
        return raio;
    }

    public static List<Double> obterRaiosCandidatos(double[][] distancias) {
        int n = distancias.length;
        TreeSet<Double> valores = new TreeSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distancias[i][j] < INFINITO) {
                    valores.add(distancias[i][j]);
                }
            }
        }
        return new ArrayList<>(valores);
    }

    public static List<DadosInstancia> carregarTodasInstancias(Path diretorioDados) throws IOException {
        List<DadosInstancia> instancias = new ArrayList<>();
        for (int id = 1; id <= 40; id++) {
            Path arquivo = diretorioDados.resolve("pmed" + id + ".txt");
            if (!Files.exists(arquivo)) {
                throw new IOException("Arquivo nao encontrado: " + arquivo);
            }
            DadosInstancia dados = analisarArquivoPmed(arquivo);
            instancias.add(new DadosInstancia(id, dados.n(), dados.k(), dados.distancias()));
        }
        return instancias;
    }
}
