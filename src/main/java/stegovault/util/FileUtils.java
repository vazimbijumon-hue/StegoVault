package stegovault.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

    private FileUtils() {
    }

    public static byte[] readFile(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public static void writeFile(Path path, byte[] data) throws IOException {
        Files.write(path, data);
    }
}