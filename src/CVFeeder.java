import javafx.beans.property.SimpleListProperty;
import org.opencv.core.Mat;

abstract public class CVFeeder extends CVControl {
    public CVFeeder(String name) {
        super(name);
    }

    @Override
    protected SimpleListProperty<Value> getValues() {
        return new SimpleListProperty<>();
    }
    abstract public Mat get();
}
