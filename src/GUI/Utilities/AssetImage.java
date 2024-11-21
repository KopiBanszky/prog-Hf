package GUI.Utilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.InputStream;

public class AssetImage extends JLabel {
    private Image img;
    private int imgWidth;
    private int imgHeight;

    public AssetImage(String path, int width, int height) {
        loadImg(path, width, height);
    }
    public AssetImage(String path) {
        loadImg(path, 16, 16);
    }
    public AssetImage(String path, int size) {
        loadImg(path, size, size);
    }

    private void loadImg(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);

            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageObserver observer = new ImageObserver() {
                @Override
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    if ((infoflags & ImageObserver.WIDTH) != 0) {
                        imgWidth = width;
                    }
                    if ((infoflags & ImageObserver.HEIGHT) != 0) {
                        imgHeight = height;
                    }
                    if ((infoflags & ImageObserver.ALLBITS) != 0) {
                        return false; // Stop monitoring
                    }
                    return true;
                }
            };
            imgWidth = newImg.getWidth(observer);
            imgHeight = newImg.getHeight(observer);
            icon = new ImageIcon(newImg);
            setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getImgWidth() {
        return imgWidth;
    }
    public int getImgHeight() {
        return imgHeight;
    }

}
