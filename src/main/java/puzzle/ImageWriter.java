package puzzle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter implements Closeable {

    public static final Color BACK_COLOR = Color.WHITE;
    public static final Color FORE_COLOR = Color.BLACK;
    public static final String IMAGE_TYPE = "png";

    public final BufferedImage image;
    public final Graphics2D graphics;

    public ImageWriter(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.graphics = this.image.createGraphics();
        this.graphics.setColor(BACK_COLOR);
        this.graphics.fillRect(0, 0, width, height);
        this.graphics.setColor(FORE_COLOR);
    }

    public void writeTo(File output) throws IOException {
        ImageIO.write(image, IMAGE_TYPE, output);
    }

    @Override
    public void close() {
        graphics.dispose();
    }

}
