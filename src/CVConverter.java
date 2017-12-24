import org.opencv.core.Mat;

abstract public class CVConverter extends CVControl {
    public CVConverter(String name) {
        super(name);
    }
    public Mat convert(Mat mat) {
        if (enabled.get()) {
            return realConvert(mat);
        } else {
            return mat;
        }
    }
    abstract protected Mat realConvert(Mat mat);
}
