import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.*;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rodsouza9 on 12/23/2016.
 */
public class Image extends BufferedImage{
    static final int BLACK = toRGB (255,0,0,0);
    static final int WHITE = toRGB (255,255,255,255);
    static final int RED   = toRGB (255,255,0,0);
    static final int BLUE  = toRGB (255,0,0,255);


    //private BufferedImage image;
    private boolean[][] visited;

    public Image(BufferedImage img) {
        super (img.getColorModel(), img.copyData(null), img.isAlphaPremultiplied(), null);
        visited = new boolean[super.getWidth()][super.getHeight()];
        //make boolean image: logs which pixels have been recorded into

        //set all boolean values to false
        for (int i = 0; i < super.getWidth(); i++) {
            for (int j = 0; j < super.getHeight(); j++) {
                visited[i][j] = false;
            }
        }
    }


    public boolean[][] getVisited() {
        return visited;
    }

    public boolean getVisited(Point point) {
        return visited[point.x][point.y];
    }

    public void setVisited(int x, int y, boolean value) {
        this.visited[x][y] = value;
    }

    public void setVisited(Point point, boolean value) {
        this.visited[point.x][point.y] = value;
    }


    public static boolean toBinary(int rgb) {
        return (rgb == BLACK);
    }

    public static int toRGB(int alpha, int red, int green, int blue)
    {
        Color pixel = new Color(red, green, blue, alpha);
        return pixel.getRGB();
    }

    public static void setBufferedImage (BufferedImage image, int color) {
        for (int x = 0; x < image.getWidth (); x++) {
            for (int y = 0; y < image.getHeight (); y++) {
                image.setRGB ( x,y,color );
            }
        }
    }


    /**
     * @param point the point that surrounding
     * @param width the width of the image that the point lies in
     * @param height the height of the image that the point lies in, used to check if the
     *               surrounding point lies in the images dimension
     * @return  an Array list of pixel coordination represented by an integer
     *          array of size 2. Index 0 represents the x coordinate and index
     *          1 represents the y coordinate.
     */
    public static Set<Point> getSurroundPixels(Point point, int width, int height) {
        Set<Point> points = new HashSet<>( );
        for (int i = point.x - 1; i < point.x + 2; i++) {
            for (int j = point.y - 1; j < point.y + 2; j++) {
                //checks if the surrounding coordinates are in bounds
                if (i >= 0 && j >= 0 && i < width && j < height) {
                    points.add(new Point(i,j));
                }
            }
        }
        return points;
    }

    public Set<Point> getSurroundPixels(Point point) {
        Set<Point> points = new HashSet<>( );
        for (int i = point.x - 1; i < point.x + 2; i++) {
            for (int j = point.y - 1; j < point.y + 2; j++) {
                //checks if the surrounding coordinates are in bounds
                if (i >= 0 && j >= 0 && i < getWidth() && j < getHeight()) {
                    points.add(new Point(i,j));
                }
            }
        }
        return points;
    }





}
