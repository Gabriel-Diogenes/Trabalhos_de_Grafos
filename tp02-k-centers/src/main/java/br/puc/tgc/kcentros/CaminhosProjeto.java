package br.puc.tgc.kcentros;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class CaminhosProjeto {

    private static final Path RAIZ = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    private CaminhosProjeto() {
    }

    public static Path raiz() {
        return RAIZ;
    }

    public static Path diretorioDados() {
        return RAIZ.resolve("data");
    }

    public static Path diretorioResultados() {
        return RAIZ.resolve("results");
    }

    public static Path diretorioFiguras() {
        return RAIZ.resolve("relatorio").resolve("figuras");
    }

    public static Path arquivoInstancia(int indice) {
        return diretorioDados().resolve("pmed" + indice + ".txt");
    }
}
