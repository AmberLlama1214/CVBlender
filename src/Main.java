import javafx.application.Application;
import javafx.beans.property.SimpleListProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import sun.java2d.pipe.SpanShapeRenderer;

public class Main extends Application {
    SimpleListProperty<CVControl> controls;
    MatViewer matViewer;
    ScheduledService<Void> job;
    public static void main(String[] args) {
        launch(args);
    }
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        controls = new SimpleListProperty<>();
    }
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        AnchorPane center = new AnchorPane();
        matViewer = new MatViewer();
        matViewer.setMat(hSBMat());
        center.getChildren().add(matViewer);
        root.setCenter(center);
        VBox box = new VBox();
        for (CVControl control : controls) {
            box.getChildren().add(control.render());
        }
        ScrollPane nodes = new ScrollPane(box);
        nodes.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setLeft(nodes);
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("CV Blender");
        primaryStage.show();

        job = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        render();
                        return null;
                    }
                };
            }
        };
        job.setDelay(Duration.ONE);
        job.setPeriod(Duration.ZERO);
        job.start();
    }
    public void render() {
        Mat mat = ((CVFeeder) controls.get(0)).get();
        for (int i = 1; i < controls.size(); i++) {
            CVConverter c = (CVConverter) (controls.get(i));
            mat = c.convert(mat);
        }
        matViewer.setMat(mat);
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
