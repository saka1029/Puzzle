package test.puzzle;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

class TestImage {

    @Test
    void test() throws IOException {
        int width = 200, height = 200;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        Font font = new Font("TimesRoman", Font.BOLD, 20);
        ig2.setFont(font);
        String message = "Hello world!";
        FontMetrics fontMetrics = ig2.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(message);
        int stringHeight = fontMetrics.getAscent();
        ig2.setPaint(Color.BLACK);
        ig2.drawRect(0, 0, width - 1, height - 1);
        ig2.drawRect(4, 4, width - 9, height - 9);
        ig2.setPaint(Color.BLUE);
        ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);
        ImageIO.write(bi, "PNG", new File("data/test.png"));
    }

}
