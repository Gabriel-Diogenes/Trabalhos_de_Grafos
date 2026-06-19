package br.puc.tgc.kcentros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Experimentos {

    private static final double LIMITE_TEMPO_EXATO_SEGUNDOS = 300.0;

    private Experimentos() {
    }

    public record LinhaExperimento(
            int instancia,
            int n,
            int k,
            int raioOtimo,
            String raioExato,
            String tempoExatoS,
            int raioAprox,
            String tempoAproxS,
            String gapAproxPct,
            String exatoTimeout
    ) {
        String paraLinhaCsv() {
            return String.join(",",
                    Integer.toString(instancia),
                    Integer.toString(n),
                    Integer.toString(k),
                    Integer.toString(raioOtimo),
                    raioExato,
                    tempoExatoS,
                    Integer.toString(raioAprox),
                    tempoAproxS,
                    gapAproxPct,
                    exatoTimeout
            );
        }

        double valorGap() {
            return Double.parseDouble(gapAproxPct);
        }

        double valorTempoAprox() {
            return Double.parseDouble(tempoAproxS);
        }

        double valorTempoExato() {
            if (tempoExatoS.startsWith(">") || tempoExatoS.equals("erro")) {
                return Double.NaN;
            }
            return Double.parseDouble(tempoExatoS);
        }
    }

    public static void escreverCsv(Path csvSaida, List<LinhaExperimento> linhas) throws IOException {
        Files.createDirectories(csvSaida.getParent());
        List<String> conteudo = new ArrayList<>();
        conteudo.add("instancia,n,k,raio_otimo,raio_exato,tempo_exato_s,raio_aprox,tempo_aprox_s,gap_aprox_pct,exato_timeout");
        for (LinhaExperimento linha : linhas) {
            conteudo.add(linha.paraLinhaCsv());
        }
        Files.write(csvSaida, conteudo);
        System.out.println("Resultados salvos em " + csvSaida);
    }

    public static List<LinhaExperimento> executarExperimentos(Path diretorioDados) throws IOException {
        List<DadosInstancia> instancias = CarregadorInstancias.carregarTodasInstancias(diretorioDados);
        List<LinhaExperimento> linhas = new ArrayList<>();

        for (DadosInstancia instancia : instancias) {
            int id = instancia.id();
            int n = instancia.n();
            int k = instancia.k();
            double[][] distancias = instancia.distancias();
            int otimo = RaiosOtimos.obter(id);

            System.out.printf(Locale.US, "Instancia %02d: |V|=%d, k=%d%n", id, n, k);

            long inicioAprox = System.nanoTime();
            KCentrosExato.Solucao solucaoAprox = KCentrosAproximado.resolver(distancias, k);
            double tempoAprox = (System.nanoTime() - inicioAprox) / 1_000_000_000.0;
            int raioAprox = (int) Math.round(solucaoAprox.raio());
            double gap = 100.0 * (solucaoAprox.raio() - otimo) / otimo;

            String valorRaioExato;
            String valorTempoExato;
            String flagTimeout = "nao";

            long inicioExato = System.nanoTime();
            try {
                KCentrosExato.Solucao solucaoExata = KCentrosExato.resolver(distancias, k);
                double tempoExato = (System.nanoTime() - inicioExato) / 1_000_000_000.0;
                if (tempoExato > LIMITE_TEMPO_EXATO_SEGUNDOS) {
                    flagTimeout = "sim";
                    valorRaioExato = "timeout";
                    valorTempoExato = String.format(Locale.US, ">%.0f", LIMITE_TEMPO_EXATO_SEGUNDOS);
                } else {
                    valorRaioExato = Integer.toString((int) Math.round(solucaoExata.raio()));
                    valorTempoExato = String.format(Locale.US, "%.4f", tempoExato);
                }
            } catch (RuntimeException excecao) {
                flagTimeout = "erro";
                valorRaioExato = excecao.getMessage() == null ? "erro" : excecao.getMessage();
                valorTempoExato = "erro";
            }

            LinhaExperimento linha = new LinhaExperimento(
                    id,
                    n,
                    k,
                    otimo,
                    valorRaioExato,
                    valorTempoExato,
                    raioAprox,
                    String.format(Locale.US, "%.4f", tempoAprox),
                    String.format(Locale.US, "%.2f", gap),
                    flagTimeout
            );
            linhas.add(linha);

            System.out.printf(Locale.US,
                    "  exato=%s (%ss) | aprox=%d (%.4fs) | gap=%s%%%n",
                    linha.raioExato(),
                    linha.tempoExatoS(),
                    linha.raioAprox(),
                    tempoAprox,
                    linha.gapAproxPct()
            );
        }

        return linhas;
    }

    public static void imprimirTabelaResultados(List<LinhaExperimento> linhas) {
        System.out.printf(Locale.US,
                "%-4s %4s %3s %6s %8s %10s %6s %10s %8s %8s%n",
                "Inst", "|V|", "k", "Otimo", "Exato", "T.Exato(s)", "Aprox", "T.Aprox(s)", "Gap(%)", "Timeout"
        );
        System.out.println("-".repeat(82));

        for (LinhaExperimento linha : linhas) {
            System.out.printf(Locale.US,
                    "%-4d %4d %3d %6d %8s %10s %6d %10s %8s %8s%n",
                    linha.instancia(),
                    linha.n(),
                    linha.k(),
                    linha.raioOtimo(),
                    linha.raioExato(),
                    linha.tempoExatoS(),
                    linha.raioAprox(),
                    linha.tempoAproxS(),
                    linha.gapAproxPct(),
                    linha.exatoTimeout()
            );
        }
    }

    public static void imprimirResumo(List<LinhaExperimento> linhas) {
        int aproxIgualOtimo = 0;
        int exatoIgualOtimo = 0;
        double somaGap = 0.0;
        double gapMaximo = 0.0;
        double tempoTotalExato = 0.0;
        double tempoTotalAprox = 0.0;
        int exatoComTimeout = 0;

        for (LinhaExperimento linha : linhas) {
            double gap = linha.valorGap();
            somaGap += gap;
            gapMaximo = Math.max(gapMaximo, gap);
            tempoTotalAprox += linha.valorTempoAprox();

            if (linha.raioExato().equals(Integer.toString(linha.raioOtimo()))) {
                exatoIgualOtimo++;
            }
            if (linha.raioAprox() == linha.raioOtimo()) {
                aproxIgualOtimo++;
            }
            if (!Double.isNaN(linha.valorTempoExato())) {
                tempoTotalExato += linha.valorTempoExato();
            }
            if (linha.exatoTimeout().equals("sim") || linha.exatoTimeout().equals("erro")) {
                exatoComTimeout++;
            }
        }

        double gapMedio = somaGap / linhas.size();

        System.out.printf(Locale.US, "Instancias processadas:        %d%n", linhas.size());
        System.out.printf(Locale.US, "Exato = otimo conhecido:       %d / %d%n", exatoIgualOtimo, linhas.size());
        System.out.printf(Locale.US, "Aprox = otimo conhecido:       %d / %d%n", aproxIgualOtimo, linhas.size());
        System.out.printf(Locale.US, "Gap medio (aprox vs otimo):    %.2f%%%n", gapMedio);
        System.out.printf(Locale.US, "Gap maximo:                    %.2f%%%n", gapMaximo);
        System.out.printf(Locale.US, "Tempo total exato:             %.4f s%n", tempoTotalExato);
        System.out.printf(Locale.US, "Tempo total aproximado:        %.4f s%n", tempoTotalAprox);
        if (exatoComTimeout > 0) {
            System.out.printf(Locale.US, "Instancias com timeout/erro:   %d%n", exatoComTimeout);
        }
    }
}
