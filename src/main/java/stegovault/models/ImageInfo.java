package stegovault.models;

public class ImageInfo {

    private final int width;
    private final int height;
    private final String foramt;
    private final boolean hasAlpha;
    private final long pixelCount;

    public ImageInfo(int width,
                     int height,
                     String format,
                     boolean  hasAlpha,
                     long pixelCount) {

        this.width = width;
        this.height = height;
        this.foramt = format;
        this.hasAlpha = hasAlpha;
        this.pixelCount = pixelCount;
    }

    public  int getWidth() {
        return width;
    }

    public  int getHeight() {
        return height;
    }

    public String getFormat() {
        return foramt;
    }
    public boolean hasAlpha() {
        return hasAlpha;
    }
    public long getPixelCount() {
        return  pixelCount;



    }


}