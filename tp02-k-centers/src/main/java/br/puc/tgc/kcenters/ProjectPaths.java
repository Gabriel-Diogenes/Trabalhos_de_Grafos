package br.puc.tgc.kcenters;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ProjectPaths {

    private static final Path ROOT = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

    private ProjectPaths() {
    }

    public static Path root() {
        return ROOT;
    }

    public static Path dataDir() {
        return ROOT.resolve("data");
    }

    public static Path resultsDir() {
        return ROOT.resolve("results");
    }

    public static Path figuresDir() {
        return ROOT.resolve("relatorio").resolve("figuras");
    }

    public static Path instanceFile(int index) {
        return dataDir().resolve("pmed" + index + ".txt");
    }
}
