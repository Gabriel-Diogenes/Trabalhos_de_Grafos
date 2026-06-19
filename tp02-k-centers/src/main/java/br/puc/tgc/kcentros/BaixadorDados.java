package br.puc.tgc.kcentros;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class BaixadorDados {

    private static final String URL_BASE = "https://people.brunel.ac.uk/~mastjjb/jeb/orlib/files";

    private BaixadorDados() {
    }

    public static void garantirDados(Path diretorioDados) throws IOException {
        Files.createDirectories(diretorioDados);

        int existentes = 0;
        int baixados = 0;

        for (int indice = 1; indice <= 40; indice++) {
            String nomeArquivo = "pmed" + indice + ".txt";
            Path destino = diretorioDados.resolve(nomeArquivo);
            if (Files.exists(destino)) {
                existentes++;
                continue;
            }

            String url = URL_BASE + "/" + nomeArquivo;
            System.out.printf("Baixando %s ...%n", url);
            try (InputStream entrada = URI.create(url).toURL().openStream()) {
                Files.copy(entrada, destino);
            }
            baixados++;
            System.out.printf("  Salvo em %s%n", destino);
        }

        System.out.printf(
                Locale.US,
                "Instancias prontas: %d (ja existiam: %d, baixadas agora: %d)%n",
                existentes + baixados,
                existentes,
                baixados
        );
    }
}
