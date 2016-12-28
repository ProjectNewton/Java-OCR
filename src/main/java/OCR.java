/**
 * Optical Character Recognition
 *
 * @author Ethan Zheng, Rohan D'Souza
 * @version 110816
 */


import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class OCR
{

    public static void main(String[] args) throws IOException
    {
        String filename = "Pics/alet1.jpg";
        int index = filename.indexOf(".");
        String type = filename.substring(index + 1);

        File orig = new File(filename);
        File gray = new File(filename.substring(0, index) + "_gray." + type);
        File otsu = new File(filename.substring(0, index) + "_otsu." + type);
        File nred = new File(filename.substring(0, index) + "_nred." + type);
        File outl = new File(filename.substring(0, index) + "_outl." + type);

        BufferedImage origImg = ImageIO.read(orig);
        BufferedImage grayImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        BufferedImage otsuImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        BufferedImage nredImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        BufferedImage outlImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());

        grayImg = Process.grayscale(origImg);
        ImageIO.write(grayImg, type, gray);

        otsuImg = Process.binarize(grayImg);
        ImageIO.write(otsuImg, type, otsu);

        int times = 3;
        for (int i = 0; i < times-1; i++) {
            if (i==1) {
                nredImg = Process.reduceNoise(otsuImg);
            }
            else {
                nredImg = Process.reduceNoise(nredImg);
            }
        }
        ImageIO.write(nredImg, type, nred);

        outlImg = Process.outline(nredImg);

        ImageIO.write(outlImg, type, outl);


    }
}