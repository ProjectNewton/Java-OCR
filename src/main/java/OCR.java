/**
 * Optical Character Recognition
 *
 * @author Ethan Zheng, Rohan D'Souza
 * @version 110816
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class OCR
{
    public static final int BLACK = toRGB(255,0,0,0);
    public static final int RED = toRGB(255,255,0,0);


    private static int[] imageHistogram(BufferedImage input)
    {
        int[] histogram = new int[256];

        for(int i = 0; i < histogram.length; i++)
            histogram[i] = 0;

        for(int i = 0; i < input.getWidth(); i++)
            for(int j = 0; j < input.getHeight(); j++)
            {
                int red = new Color(input.getRGB (i, j)).getRed();
                histogram[red]++;
            }

        return histogram;
    }

    private static BufferedImage grayscale(BufferedImage o)
    {
        int alpha, red, green, blue;
        double burn = 0.8;
        int pixel;
        BufferedImage g = new BufferedImage(o.getWidth(), o.getHeight(), o.getType());

        for(int i = 0; i < o.getWidth(); i++)
            for(int j = 0; j < o.getHeight(); j++)
            {
                alpha = new Color(o.getRGB(i, j)).getAlpha();
                red = new Color(o.getRGB(i, j)).getRed();
                green = new Color(o.getRGB(i, j)).getGreen();
                blue = new Color(o.getRGB(i, j)).getBlue();
                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                if (red < 240)
                    red = (int) (burn * red);
                pixel = toRGB(alpha, red, red, red);
                g.setRGB(i, j, pixel);
            }

        return g;
    }

    private static int otsuThreshold(BufferedImage o)
    {
        int[] histogram = imageHistogram(o);
        int total = o.getHeight() * o.getWidth();

        float sum = 0;
        for(int i = 0; i < 256; i++)
            sum += i * histogram[i];
        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        for(int i = 0 ; i < 256 ; i++)
        {
            wB += histogram[i];
            if(wB == 0)
                continue;
            wF = total - wB;
            if(wF == 0)
                break;
            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
            if(varBetween > varMax)
            {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    private static BufferedImage binarize(BufferedImage o)
    {
        int red;
        int pixel;
        int threshold = otsuThreshold(o);
        BufferedImage binarized = new BufferedImage(o.getWidth(), o.getHeight(), o.getType());

        for(int i = 0; i < o.getWidth(); i++)
            for(int j = 0; j < o.getHeight(); j++)
            {
                red = new Color(o.getRGB(i, j)).getRed();
                int alpha = new Color(o.getRGB(i, j)).getAlpha();
                if(red > threshold)
                    pixel = 255;
                else
                    pixel = 0;
                pixel = toRGB(alpha, pixel, pixel, pixel);
                binarized.setRGB(i, j, pixel);
            }

        return binarized;
    }

    private static BufferedImage reduceNoise(BufferedImage o) throws IOException
    {
        BufferedImage r = new BufferedImage(o.getWidth(), o.getHeight(), o.getType());
        int[] pixel = new int[9];

        for(int i = 0; i < o.getWidth(); i++)
            for(int j = 0; j < o.getHeight(); j++)
            {
                int center = new Color(o.getRGB(i, j)).getRGB();
                if (i == 0)
                {
                    pixel[0] = center;
                    pixel[1] = center;
                    pixel[2] = center;
                }
                if (j == 0)
                {
                    pixel[0] = center;
                    pixel[6] = center;
                    pixel[7] = center;
                }
                if (i == o.getWidth() - 1)
                {
                    pixel[4] = center;
                    pixel[5] = center;
                    pixel[6] = center;
                }
                if (j == o.getHeight() - 1)
                {
                    pixel[2] = center;
                    pixel[3] = center;
                    pixel[4] = center;
                }
                if (pixel[0] != center)     pixel[0] = new Color(o.getRGB(i - 1, j - 1)).getRGB();
                if (pixel[1] != center)     pixel[1] = new Color(o.getRGB(i - 1, j)).getRGB();
                if (pixel[2] != center)     pixel[2] = new Color(o.getRGB(i - 1, j + 1)).getRGB();
                if (pixel[3] != center)     pixel[3] = new Color(o.getRGB(i, j + 1)).getRGB();
                if (pixel[4] != center)     pixel[4] = new Color(o.getRGB(i + 1, j + 1)).getRGB();
                if (pixel[5] != center)     pixel[5] = new Color(o.getRGB(i + 1, j)).getRGB();
                if (pixel[6] != center)     pixel[6] = new Color(o.getRGB(i + 1, j - 1)).getRGB();
                if (pixel[7] != center)     pixel[7] = new Color(o.getRGB(i, j - 1)).getRGB();
                pixel[8] = center;
                Arrays.sort(pixel);
                r.setRGB(i, j, pixel[4]);
            }

        return r;
    }

    private static int toRGB(int alpha, int red, int green, int blue)
    {
        Color pixel = new Color(red, green, blue, alpha);
        return pixel.getRGB();
    }


    static BufferedImage outline(BufferedImage img) {
        //vertical test
        boolean nextVPix = toBinary(img.getRGB(1,1));
        for (int x = 0; x < img.getWidth()-1; x++) {
            for (int y = 0; y < img.getHeight()-1; y++) {
                if (nextVPix != toBinary(img.getRGB(x,y))) {
                    img.setRGB(x,y,RED);
                }
                nextVPix = toBinary(img.getRGB(x+1, y+1));
            }
        }

        //horizontal test
        boolean nextHPix = toBinary(img.getRGB(1,1));
        for (int y = 0; y < img.getHeight()-1; y++) {
            for (int x = 0; x < img.getWidth()-1; x++) {
                if (nextHPix != toBinary(img.getRGB(x,y))) {
                    img.setRGB(x,y,RED);
                }
                nextHPix = toBinary(img.getRGB(x+1,y+1));
            }
        }

        return img;
    }

    static boolean toBinary(int rgb) {
        return rgb == BLACK;
    }



    public static void main(String[] args) throws IOException
    {
        String filename = "Pics/sign1.jpg";
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

        grayImg = grayscale(origImg);
        ImageIO.write(grayImg, type, gray);

        otsuImg = binarize(grayImg);
        ImageIO.write(otsuImg, type, otsu);

        nredImg = reduceNoise(otsuImg);
        nredImg = reduceNoise(nredImg);
        nredImg = reduceNoise(nredImg);
        ImageIO.write(nredImg, type, nred);

        outlImg = outline(nredImg);
        ImageIO.write(outlImg, type, outl);


    }


}

