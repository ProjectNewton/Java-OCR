/**
 * Optical Character Recognition
 * 
 * @author Ethan Zheng, Rohan D'Souza
 * @version 101716
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
    private static int[] imageHistogram(BufferedImage input)
    {
        int[] histogram = new int[256];
        
        for(int i=0; i<histogram.length; i++)
            histogram[i] = 0;
            
        for(int i=0; i<input.getWidth(); i++)
            for(int j=0; j<input.getHeight(); j++)
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
        for(int i=0; i<256; i++) 
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
 
        for(int i=0; i<o.getWidth(); i++)
            for(int j=0; j<o.getHeight(); j++)
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
    
    private static BufferedImage reduceNoise(BufferedImage o)
    {
        BufferedImage r = new BufferedImage(o.getWidth(), o.getHeight(), o.getType());
        return r;
    }
    
    private static int toRGB(int alpha, int red, int green, int blue)
    {
        int pixel = 0;

        pixel += alpha;    pixel = pixel << 8;
        pixel += red;      pixel = pixel << 8;
        pixel += green;    pixel = pixel << 8;
        pixel += blue;
 
        return pixel;
    }
    
    public static void main(String[] args) throws IOException
    {
        String filename = "alphabet.jpg";
        int index = filename.indexOf(".");
        String type = filename.substring(index + 1);
        
        File orig = new File(filename);
        File gray = new File(filename.substring(0, index) + "_gray." + type);
        File otsu = new File(filename.substring(0, index) + "_otsu." + type);
        File nred = new File(filename.substring(0, index) + "_nred." + type);
        
        BufferedImage origImg = ImageIO.read(orig);
        BufferedImage grayImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        BufferedImage otsuImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        BufferedImage nredImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
        
        grayImg = grayscale(origImg);
        otsuImg = binarize(grayImg);
        nredImg = reduceNoise(otsuImg);
        
        ImageIO.write(grayImg, type, gray);
        ImageIO.write(otsuImg, type, otsu);
        ImageIO.write(nredImg, type, nred);
    }
}
