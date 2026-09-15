package stegovault.models;

public class CapacityInfo {

    private final int width;
    private final int height;
    private final long pixelCount;
    private final long totalBits;
    private final long usableBytes;

    public CapacityInfo(
            int width,
            int height,
            long pixelCount,
            long totalBits,
            long usableBytes
    ) { // objectil save chyumme
        this.width = width;
        this.height = height;
        this.pixelCount = pixelCount;
        this.totalBits = totalBits;
        this.usableBytes = usableBytes;
    }
// getters...private fields directly access chyan pattila
 //  appo  getters use chyunne

    public int getWidth() {
        return width;

    }

    public int getHeight() {
        return height;
    }

    public long getPixelCount() {
        return pixelCount;
    }

    public long getTotalBits() {
        return totalBits;
    }
    public long getUsableBytes () {
        return usableBytes;
    }


    }







