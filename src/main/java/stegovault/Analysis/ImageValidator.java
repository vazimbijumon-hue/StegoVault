package stegovault.Analysis;

import stegovault.models.ImageInfo;
import java.io.File;

public class ImageValidator {

    public static boolean isValid(File imageFile) {
        try {

            ImageInfo info = ImageAnalyser.analyze(imageFile);

            String format = info.getFormat();

            return format.equals("PNG")
                    || format.equals("BMP");

        } catch (Exception e) {
            return false;
        }
    }
}