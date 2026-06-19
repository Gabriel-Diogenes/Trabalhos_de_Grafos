package br.puc.tgc.kcenters;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DownloadData {

    private static final String BASE_URL = "https://people.brunel.ac.uk/~mastjjb/jeb/orlib/files";

    private DownloadData() {
    }

    public static void main(String[] args) throws IOException {
        Path dataDir = ProjectPaths.dataDir();
        Files.createDirectories(dataDir);

        for (int index = 1; index <= 40; index++) {
            String filename = "pmed" + index + ".txt";
            Path destination = dataDir.resolve(filename);
            if (Files.exists(destination)) {
                System.out.println("Ja existe: " + destination);
                continue;
            }

            String url = BASE_URL + "/" + filename;
            System.out.println("Baixando " + url + " ...");
            try (InputStream input = URI.create(url).toURL().openStream()) {
                Files.copy(input, destination);
            }
            System.out.println("Salvo em " + destination);
        }
    }
}
