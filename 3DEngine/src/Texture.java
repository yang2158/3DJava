import java.awt.image.BufferedImage;

public class Texture {

    private int[][] data;

    Texture(BufferedImage image) {
        data = new int[image.getWidth()][image.getHeight()];
        for (int r = 0; r < image.getWidth(); r++) {
            for (int c = 0; c < image.getHeight(); c++) {
                data[r][c] = image.getRGB(r, c);
            }
        }
    }

    public int getWidth() { return data.length; }

    public int getHeight() { return data[0].length; }

    public int getRGB(int x, int y) { return data[x][y]; }

}
