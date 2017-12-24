import com.sun.org.apache.bcel.internal.generic.Select;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import javax.lang.model.util.SimpleElementVisitor6;

abstract public class CVControl {
        private String name;
        private BooleanProperty enabled;
    private SimpleListProperty<Value> values;
    public CVControl(String name) {
        this.name = name;
        values = getValues();
        enabled = new SimpleBooleanProperty();
        enabled.setValue(true);
    }

    abstract protected SimpleListProperty<Value> getValues();

    Parent render() {
        VBox parent = new VBox();
        Label cname = new Label(name);
        CheckBox enable = new CheckBox("Enabled");
        enable.selectedProperty().bindBidirectional(enabled);
        parent.getChildren().addAll(cname, enable);
        for (Value value : values) {
            VBox node = new VBox();
            Label description = new Label(value.getDescription());
            if (value instanceof SliderValue) {
                SliderValue slv = (SliderValue) value;
                Label svalue = new Label(String.format("%d", slv.getValue()));
                Slider slider = new Slider((double) slv.minProperty().getValue(), (double) slv.maxProperty().getValue(), (double) slv.getValue());
                slv.bindBidirectional(slider.valueProperty());
                slider.maxProperty().bind(slv.maxProperty());
                slider.minProperty().bind(slv.minProperty());
                slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    Platform.runLater(() -> svalue.setText(String.format("%d", newValue.intValue())));
                });
                node.getChildren().addAll(description, svalue, slider);
            } else if (value instanceof CheckValue) {
                CheckValue cv = (CheckValue) value;
                CheckBox cb = new CheckBox(cv.getDescription());
                cb.selectedProperty().bindBidirectional(cv);
                node.getChildren().addAll(cb);
            } else if (value instanceof SelectValue) {
                SelectValue selv = (SelectValue) value;
                ComboBox<String> selection = new ComboBox<>(selv.listProp);
                selection.getSelectionModel().select(selv.getValue());
                selv.bind(selection.getSelectionModel().selectedIndexProperty());
                node.getChildren().addAll(description, selection);
            } else if (value instanceof CustomValue) {
                CustomValue custv = (CustomValue) value;
                node.getChildren().addAll(description, custv.getNode());
            }
            parent.getChildren().add(node);
        }
        return parent;
    }
}
