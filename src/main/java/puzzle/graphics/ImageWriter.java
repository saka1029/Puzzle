package puzzle.graphics;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class ImageWriter implements Closeable {

    final BufferedImage image;

    public ImageWriter(OutputStream os, int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
    }

    @Override
    public void close() throws IOException {
    }
}
