package stegovault.steganography;

import stegovault.models.CapacityInfo;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CapacityCalculator {

    public static CapacityInfo calculateCapacity(File imageFile) {

        try {
            // image memmoriyil load chyum
            BufferedImage image = ImageIO.read(imageFile);

            // image dimension edukkan
            int width = image.getWidth();
            int height = image.getHeight();

            // total pixel count chyan
            long pixelcount = (long) width * height;

            // 1 lsb for 1 rgb appo 3 bits per pixel
            long totalbits = pixelcount * 3;

           // Total raw capacity in bytes
		long rawCapacityBytes = totalbits / 8;

	// StegoVault reserves 42 bytes for the payload header
		long usablebytes = Math.max(0, rawCapacityBytes - 42);

            //calculate chythit result object
            // akkiyit return chyum
            return new CapacityInfo(
                    width,
                    height,
                    pixelcount,
                    totalbits,
                    usablebytes
            );

        } catch (IOException e) {
            throw new RuntimeException("Image Vayikkan Pattiyilla ", e);


        }
    }
}