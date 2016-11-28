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

        grayImg = Image.grayscale(origImg);
        ImageIO.write(grayImg, type, gray);

        otsuImg = Image.binarize(grayImg);
        ImageIO.write(otsuImg, type, otsu);

        nredImg = Image.reduceNoise(otsuImg);
        nredImg = Image.reduceNoise(nredImg);
        nredImg = Image.reduceNoise(nredImg);
        ImageIO.write(nredImg, type, nred);

        outlImg = Image.outline(nredImg);
        ImageIO.write(outlImg, type, outl);


    }
}