import java.awt.image.BufferedImage;

/**
 * Created by rodsouza9 on 12/23/2016.
 */
public class Image {
    private BufferedImage image;
    private boolean[][] visited;

    public Image (BufferedImage image) {
        this.image = this.image;
        visited = new boolean[][] = new boolean[image.getWidth()][image.getHeight()];
        //make boolean image: logs which pixels have been recorded into

        //set all boolean values to false
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                visited[i][j] = false;
            }
        }

    }
}
