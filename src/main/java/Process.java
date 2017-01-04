import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rohan D'Souza on 11/8/2016.
 */
public class Process {



    public static int[] imageHistogram(BufferedImage input)
    {
        int[] histogram = new int[256];

        for(int i = 0; i < histogram.length; i++)
            histogram[i] = 0;

        for(int i = 0; i < input.getWidth(); i++)
            for(int j = 0; j < input.getHeight(); j++)
            {
                int red = new Color (input.getRGB (i, j)).getRed();
                histogram[red]++;
            }

        return histogram;
    }

    public static BufferedImage grayscale(BufferedImage o)
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
                pixel = Image.toRGB(alpha, red, red, red);
                g.setRGB(i, j, pixel);
            }

        return g;
    }

    public static int otsuThreshold(BufferedImage o)
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

    public static BufferedImage binarize(BufferedImage o)
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
                pixel = Image.toRGB(alpha, pixel, pixel, pixel);
                binarized.setRGB(i, j, pixel);
            }

        return binarized;
    }

    public static BufferedImage reduceNoise(BufferedImage o) throws IOException
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




    public static BufferedImage outline(BufferedImage img) {
        //vertical test
        int nextVPix = img.getRGB(1,1);
        for (int x = 0; x < img.getWidth()-1; x++) {
            for (int y = 0; y < img.getHeight()-1; y++) {
                if (nextVPix != img.getRGB(x,y)) {
                    img.setRGB(x,y,Image.RED);
                }
                nextVPix = img.getRGB(x+1, y+1);
            }
        }


        //horizontal test
        boolean nextHPix = Image.toBinary(img.getRGB(1,1));
        for (int y = 0; y < img.getHeight()-1; y++) {
            for (int x = 0; x < img.getWidth()-1; x++) {
                if (nextHPix != Image.toBinary(img.getRGB(x,y))) {
                    img.setRGB(x,y,Image.RED);
                }
                nextHPix = Image.toBinary(img.getRGB(x+1,y+1));
            }
        }

        return img;
    }

    /*public static void outlinel(BufferedImage image) {
        for (int x = 0; x < image.getWidth ()-1; x++) {
            for (int y = 0; y < image.getHeight ()-1; y++) {
                Point point = new Point ( x, y );
                int color = image.getRGB ( point.x, point.y );
                Set<Point> pixels = Image.getSurroundPixels ( point, image.getWidth(), image.getHeight() );
                for (Point surrPoint : pixels) {
                    if(Image.toBinary (image.getRGB (surrPoint.x, surrPoint.y)) ^
                            Image.toBinary ( image.getRGB (point.x, point.y) )) {
                        image.setRGB (x,y, Image.RED);
                        break;
                    }

                }
            }
        }
    }*/


    /*
     * All the following methods are dedicated to segmenting the
     * outlined image into an array of BufferedImages that each
     * hold a character
     *
     * All these methods are intended to be run after the folling:
     *      *GrayScale
     *      *NoiseReduction
     *      *Binirization
     *      *Outline
     *
     */


    public static Set<BufferedImage> segment (Image image) {
        Set<BufferedImage> segments = new HashSet<BufferedImage>();
        Set<Polygon> polygons = getPolygons (image);
        for (Polygon polygon: polygons) {
            segments.add ( copyToBuffImg (image,polygon) );
        }
        return segments;
    }
    public static Set<Polygon> getPolygons(Image image) {
        Set<Polygon> polygons = new HashSet<Polygon>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Point point = new Point(x,y);
                if (image.getRGB(x,y) == Image.RED && !image.getVisited()[x][y]) {
                    Polygon polygon = new Polygon();
                    toPolygon (image, point, polygon);
                    polygons.add (polygon);
                }
            }
        }
        return polygons;
    }

    // This method will only work with binarized and outlines images
    static void toPolygon(Image image, Point point, Polygon polygon) {
        if (image.getVisited(point)) {
            image.setVisited (point, true);
        }
        if (!image.getVisited(point) && image.getRGB(point.x, point.y) == Image.RED) {
            image.setVisited(point, true);          //set point to visited
            polygon.addPoint(point.x, point.y);     //added point as vertex
            Set<Point> surrndPnts = image.getSurroundPixels(point); //got surrounding points
            for (Point pnt: surrndPnts) {
                toPolygon (image, pnt, polygon);
            }
        }
    }

    static BufferedImage copyToBuffImg (Image image, Polygon polygon) {
        Rectangle bounds = polygon.getBounds();
        BufferedImage img = new BufferedImage (bounds.width, bounds.height, image.getType());
        Image.setBufferedImage ( img, Image.WHITE );
        for (int x = bounds.x; x < bounds.width; x++) {
            for (int y = bounds.y; y < bounds.height; y++) {
                Point point = new Point (x,y);
                if ( polygon.contains (point) ) {
                    img.setRGB (x - bounds.x ,y - bounds.y, Image.BLACK );
                }
            }
        }
        return img;
    }
    static boolean inPolygons (Iterable<Polygon> polygons, Point point) {
        for (Polygon polygon : polygons) {
            if (polygon.contains(point)) {
                return true;
            }
        }
        return false;
    }





}
