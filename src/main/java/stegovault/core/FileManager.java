package stegovault.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {


    public Payload createPayload(Path file)
            throws IOException {


        byte[] data =
                Files.readAllBytes(file);


        PayloadMetadata metadata =
                new PayloadMetadata(
                        file.getFileName().toString(),
                        Files.probeContentType(file)
                );


        return new Payload(
                metadata,
                data
        );
    }


    public void savePayload(
            Payload payload,
            Path output
    ) throws IOException {


        Files.write(
                output,
                payload.data()
        );
    }
}