import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MatViewer extends ImageView {
    private Mat reloaded;
    private WritableImage image;
    public MatViewer() {
        reloaded = new Mat();
        image = null;

    }

    public void setMat(Mat mat) {
        int cols = mat.cols(), rows = mat.rows();
        reloaded.create(mat.size(), CvType.CV_8UC3);
        if (mat.channels() == 3) {
            Imgproc.cvtColor(mat, reloaded, Imgproc.COLOR_BGR2RGB);
        } else {
            Imgproc.cvtColor(mat, reloaded, Imgproc.COLOR_GRAY2RGB);
        }
        byte[] data = new byte[cols * rows * 3];
        reloaded.get(0,0, data);
        if (image == null || image.getWidth() != cols || image.getHeight() != rows) {
            image = new WritableImage(cols, rows);
        }
        image.getPixelWriter().setPixels(0,0,cols,rows, PixelFormat.getByteRgbInstance(), data, 0, (int) reloaded.step1());
        setImage(image);
    }

    public String getValue(int xPos, int yPos) {
        if (xPos < reloaded.cols() && xPos < reloaded.rows()) {
            byte[] values = new byte[3];
            reloaded.get(yPos, xPos, values);
            return String.format("%d %d %d", values[0] & 0xff, values[1] & 0xff, values[2] & 0xff);
        } else {
            return "";
        }
    }
}
