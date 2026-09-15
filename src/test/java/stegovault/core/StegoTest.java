package stegovault.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class StegoTest {

    public static void main(String[] args) throws Exception {


        Path inputFile =
                Path.of(
                        "C:\\Users\\HP\\OneDrive\\Desktop\\knife.txt"
                );


        File coverImage =
                new File(
                        "C:\\Users\\HP\\OneDrive\\Desktop\\Logo\\Logo[1].png"
                );


        File outputImage =
                new File(
                        "C:\\Users\\HP\\OneDrive\\Desktop\\Logo\\stego.png"
                );


        Path recoveredFile =
                Path.of(
                        "C:\\Users\\HP\\OneDrive\\Desktop\\recovered.txt"
                );


        BufferedImage image =
                ImageIO.read(coverImage);


        KeyMaterial material =
                KeyManager.generateKeyMaterial(
                        "myPassword123"
                );

        SessionManager session =
                new SessionManager(
                        material.key()
                );


        StegoVaultService service =
                new StegoVaultService(session);


        BufferedImage encoded =
                service.hideFile(
                        inputFile,
                        image,
                        null
                );


        ImageIO.write(
                encoded,
                "png",
                outputImage
        );


        BufferedImage loaded =
                ImageIO.read(outputImage);


        service.extractFile(
                loaded,
                recoveredFile
        );


        System.out.println(
                "File hidden and recovered successfully."
        );
    }
}