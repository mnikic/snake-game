package my.project;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageLoader {
    private BufferedImage mouseImage;

    public ImageLoader() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("mouse-small.png")){
            mouseImage = ImageIO.read(in); 
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Was not able to load a mouse image. Aborting!");
        }
    }

    public BufferedImage getMouseImage() {
        return mouseImage;
    }
    
    public ImageObserver getImageObserver() {
        ImageObserver handler = (img, infoflags, a,b,d, height) -> {
                    if ((infoflags & ImageObserver.ALLBITS) != 0) {
                        return false;
                    }
                    return true;
                };
        return handler;
    }
}
