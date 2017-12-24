import javafx.beans.property.*;
import javafx.scene.Node;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.List;

public interface Value {
    String getDescription();
}

class CheckValue extends SimpleBooleanProperty implements Value {
    private final String description;
    public CheckValue(String description, boolean value) {
        super();
        this.description = description;
        setValue(value);
    }
    public String getDescription() {
        return description;
    }
}

class SliderValue extends SimpleIntegerProperty implements Value {
    private final String description;
    private ReadOnlyIntegerWrapper maxProp, minProp;
    public SliderValue(String description, int max, int min, int value) {
        super();
        this.description = description;
        setValue(value);
        maxProp = new ReadOnlyIntegerWrapper();
        minProp = new ReadOnlyIntegerWrapper();
        maxProp.setValue(max);
        minProp.setValue(min);
    }
    public ReadOnlyIntegerProperty maxProperty() {
        return maxProp.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty minProperty() {
        return minProp.getReadOnlyProperty();
    }

    public void setValues(int max, int min, int value) {
        maxProp.setValue(max);
        minProp.setValue(min);
        setValue(value);
    }

    public String getDescription() {
        return description;
    }
}

class SelectValue extends SimpleIntegerProperty implements Value {
    private final String description;
    public SimpleListProperty<String> listProp;
    public SelectValue(String description, List<String> list, int value) {
        super();
        this.description = description;
        listProp = new SimpleListProperty<>();
        listProp.setAll(list);
        setValue(value);
    }
    public String getDescription() {
        return description;
    }
}

abstract class CustomValue implements Value {
    private final String description;
    public CustomValue(String description) {
        super();
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    abstract public Node getNode();
}