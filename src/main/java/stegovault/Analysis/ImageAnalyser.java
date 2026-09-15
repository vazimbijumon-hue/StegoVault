package stegovault.Analysis;

import  stegovault.models.ImageInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageAnalyser {

        public  static  ImageInfo analyze(File imageFile){
            try {
                BufferedImage image = ImageIO.read(imageFile);

                    if (image == null) {
                        throw new IOException("Unsupported image");
                    }
                    int width = image.getWidth();
                    int height = image.getHeight();

                    long pixelCount =(long) width * height;

                    boolean hasAlpha = image.getColorModel().hasAlpha();

                    String format = getExtension(imageFile);

                    return new ImageInfo(
                            width,
                            height,
                            format,
                            hasAlpha,
                            pixelCount
                    );

                } catch (IOException e) {
                throw new RuntimeException("Unable to analyze image.", e);
            }
        }

    private static String getExtension(File file) {

        String name = file.getName();

        int dot = name.lastIndexOf('.');

        if (dot == -1) {
            return "Unknown";
        }

        return name.substring(dot + 1).toUpperCase();
            }
        }

