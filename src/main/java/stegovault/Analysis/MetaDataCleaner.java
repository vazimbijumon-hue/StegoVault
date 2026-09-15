package stegovault.Analysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class MetaDataCleaner {

    private MetaDataCleaner() {
    }

    public static BufferedImage clean(File imageFile)
            throws IOException {

        BufferedImage original =
                ImageIO.read(imageFile);

        if (original == null) {
            throw new IOException(
                    "Unable to read the selected image."
            );
        }

        BufferedImage cleanImage =
                new BufferedImage(
                        original.getWidth(),
                        original.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {

                cleanImage.setRGB(
                        x,
                        y,
                        original.getRGB(x, y)
                );
            }
        }

        return cleanImage;
    }
}