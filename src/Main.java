import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        AnchorPane center = new AnchorPane();
        MatViewer matViewer = new MatViewer();
        matViewer.setMat(hSBMat());
        center.getChildren().add(matViewer);
        root.setCenter(center);
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("CV Blender");
        primaryStage.show();
    }
    public Mat hSBMat() {
        Mat mat = new Mat(255, 360, CvType.CV_8UC3);
        byte[] buffer = new byte[3];
        for (int i = 0; i <= 360; i++) {
            for (int j = 0; j <= 255; j++) {
                buffer[0] = (byte) (i / 2);
                buffer[1] = (byte) j;
                buffer[2] = (byte) 255;
                mat.put(255 - j, i, buffer);
            }
        }
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_HSV2BGR);
        return  mat;
    }
}
