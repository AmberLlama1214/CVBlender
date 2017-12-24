import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraFeeder extends CVFeeder {
    private VideoCapture camera;
    private int camWidth, camHeight;
    private Mat camMat;
    public CameraFeeder() {
        super("Camera");
        camera = new VideoCapture(0);
        camWidth = (int)camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
        camHeight = (int)camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        camMat = new Mat(camHeight, camWidth, CvType.CV_8UC3);
    }
    public int getWidth() {
        return camWidth;
    }
    public int getCamHeight() {
        return camHeight;
    }
    public Mat get() {
        if (enabledp.get()) {
            camera.read(camMat);
        }
        return camMat;
    }
}
