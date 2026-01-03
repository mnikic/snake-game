package my.project;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageLoader {
    private final BufferedImage mouseImage;
    private final BufferedImage snakeHead;
    private final BufferedImage snakeHead90;
    private final BufferedImage snakeHead180;
    private final BufferedImage snakeHead270;
    private final BufferedImage snakeBody;
    private final BufferedImage snakeBody90;
    private final BufferedImage snakeBody180;
    private final BufferedImage snakeBody270;
    private final BufferedImage snakeTail;
    private final BufferedImage snakeTail90;
    private final BufferedImage snakeTail180;
    private final BufferedImage snakeTail270;
    private final BufferedImage snakeTurn;
    private final BufferedImage snakeTurn90;
    private final BufferedImage snakeTurn180;
    private final BufferedImage snakeTurn270;

    public ImageLoader() {
        mouseImage = loadImage("mouse-small.png");

        snakeHead = loadImage("snakehead.png");
        snakeHead90 = rotateImage(snakeHead, Math.PI / 2);
        snakeHead180 = rotateImage(snakeHead, Math.PI);
        snakeHead270 = rotateImage(snakeHead, -Math.PI / 2);

        snakeBody = loadImage("simplebody.png");
        snakeBody90 = rotateImage(snakeBody, Math.PI / 2);
        snakeBody180 = rotateImage(snakeBody, Math.PI);
        snakeBody270 = rotateImage(snakeBody, -Math.PI / 2);

        snakeTail = loadImage("simpletail.png");
        snakeTail90 = rotateImage(snakeTail, Math.PI / 2);
        snakeTail180 = rotateImage(snakeTail, Math.PI);
        snakeTail270 = rotateImage(snakeTail, -Math.PI / 2);

        snakeTurn = loadImage("simpleturn.png");
        snakeTurn90 = rotateImage(snakeTurn, Math.PI / 2);
        snakeTurn180 = rotateImage(snakeTurn, Math.PI);
        snakeTurn270 = rotateImage(snakeTurn, -Math.PI / 2);
    }

    private BufferedImage loadImage(String imageName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(imageName)) {
            return ImageIO.read(in);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Was not able to load a mouse image. Aborting!");
        }
    }

    private static BufferedImage rotateImage(BufferedImage image, double theta) {
        BufferedImage output = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(theta, image.getWidth() / 2, image.getWidth() / 2);
        double offset = (image.getWidth() - image.getHeight()) / 2;
        affineTransform.translate(offset, offset);
        AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, output);
        return output;
    }

    public BufferedImage getSnakeTail() {
        return snakeTail;
    }

    public BufferedImage getSnakeTail90() {
        return snakeTail90;
    }

    public BufferedImage getSnakeTail180() {
        return snakeTail180;
    }

    public BufferedImage getSnakeTail270() {
        return snakeTail270;
    }

    public BufferedImage getSnakeHead() {
        return snakeHead;
    }

    public BufferedImage getSnakeHead90() {
        return snakeHead90;
    }

    public BufferedImage getSnakeHead180() {
        return snakeHead180;
    }

    public BufferedImage getSnakeHead270() {
        return snakeHead270;
    }

    public BufferedImage getSnakeBody() {
        return snakeBody;
    }

    public BufferedImage getSnakeBody90() {
        return snakeBody90;
    }

    public BufferedImage getSnakeBody180() {
        return snakeBody180;
    }

    public BufferedImage getSnakeBody270() {
        return snakeBody270;
    }

    public BufferedImage getSnakeTurn() {
        return snakeTurn;
    }

    public BufferedImage getSnakeTurn90() {
        return snakeTurn90;
    }

    public BufferedImage getSnakeTurn180() {
        return snakeTurn180;
    }

    public BufferedImage getSnakeTurn270() {
        return snakeTurn270;
    }

    public BufferedImage getMouseImage() {
        return mouseImage;
    }

    public ImageObserver getImageObserver() {
        ImageObserver handler = (img, infoflags, a, b, d, height) -> {
            if ((infoflags & ImageObserver.ALLBITS) != 0) {
                return false;
            }
            return true;
        };
        return handler;
    }
}
