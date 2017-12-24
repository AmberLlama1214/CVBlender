import javafx.beans.property.SimpleListProperty;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.ArrayList;

public class FileFeeder extends CVFeeder {
    VideoCapture src;
    ArrayList<String> files;
    SliderValue location;
    Rectangle drop;
    Mat fileMat;
    boolean movieMode;
    private boolean stopFlag;

    public FileFeeder() {
        super("File Feeder");
        src = new VideoCapture();
        files = new ArrayList<>();
        // 임의로 Mat allocate
        fileMat = new Mat(200, 200, CvType.CV_8UC3, new Scalar(255, 255, 255));
        movieMode = false;
        stopFlag = false;
    }

    private void addFile(String fileName) {
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos < 0) {
            System.out.println("Cannot find file format : " + fileName);
            return;
        }
        if (!src.open(fileName)) {
            System.out.println("Error during opening file : " + fileName);
            return;
        }
        // 확장자
        String rest = fileName.substring(dotPos + 1).toLowerCase();
        if (rest.equals("mp4") || rest.equals("avi")) {
            // 동영상은 하나만 받도록
            files.clear();
            files.add(fileName);
            movieMode = true;
            double frames = src.get(Videoio.CAP_PROP_FRAME_COUNT);
            location.setValues(0, (int)frames, 0);
            // System.out.format("%s: %f frames\n", fileName, frames);
        } else if (rest.equals("jpg") || rest.equals("png")) {
            if (movieMode) {
                files.clear();
            }
            files.add(fileName);
            movieMode = false;
            // System.out.println(files);
            // System.out.format("%d files => (%d/%d)\n", files.size(), location.getMax(), location.getValue());
            location.setValues(0, files.size()-1, files.size()-1);
        } else {
            System.out.format("Unknown file format : '%s'\n", rest);
        }
    }

    @Override
    protected SimpleListProperty<Value> getValues() {
        SimpleListProperty<Value> values = new SimpleListProperty<>();
        // 동영상 위치, 이미지 파일 선택
        location = new SliderValue("Location", 0, 0, 0);
        values.add(location);

        // 드랙 드롭 받는 Shape
        drop = new Rectangle(50, 50);
        drop.setFill(Color.BLUE);
        drop.setOnDragOver((e) -> {
            Dragboard db = e.getDragboard();
            if (db.hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            } else {
                e.consume();
            }
        });
        drop.setOnDragDropped((e) -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                stopFlag = true;
                for (File file : db.getFiles()) {
                    // System.out.println(file.getAbsolutePath());
                    addFile(file.getAbsolutePath());
                }
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
            stopFlag = false;
        });
        CustomValue dropValue = new CustomValue("Drop File") {
            @Override
            public Node getNode() {
                return drop;
            }
        };
        values.add(dropValue);
        return values;
    }

    public Mat get() {
        if (!stopFlag && enabledp.get()) {
            if (movieMode) {
                if (src.isOpened()) {
                    double nextFrame = (double) location.getValue();
                    src.set(Videoio.CV_CAP_PROP_POS_FRAMES, nextFrame);

                    location.setValue(location.getValue() + 1);
                    if (nextFrame == (double) location.getMax()) {
                        location.setValue(0);
                    }
                    src.read(fileMat);
                }
            } else {
                String selected = files.get(location.getValue());
                fileMat = Imgcodecs.imread(selected);
            }
            return fileMat;
        } else {
            return fileMat;
        }
    }
}
