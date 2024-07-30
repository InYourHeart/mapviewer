package coalitionofpowers.Utility;

public class Colors {

    public static int getRed(int color) {
        return (color & 0x0000000000ff0000) >> 16;
    }

    public static int getGreen(int color) {
        return color & 0x000000000000ff00 >> 8;
    }

    public static int getBlue(int color) {
        return color & 0x00000000000000ff;
    }

    public static int getRGB(int color) {
        return color & 0x0000000000ffffff;
    }

    public static double getDevastation(int color) {
        int red = getRed(color);
        int green = getGreen(color);
        int blue = getBlue(color);

        if (red == 255 & green == blue) {
            double diff = red - green;
            return (diff / red) * 100;
        }

        return -1;
    }
}
