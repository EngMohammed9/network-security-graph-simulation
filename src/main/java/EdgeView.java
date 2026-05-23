import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class EdgeView {
    private final Line line;
    private final Polygon arrowHead;

    public EdgeView(Line line, Polygon arrowHead) {
        this.line = line;
        this.arrowHead = arrowHead;
    }

    public Line line() {
        return line;
    }

    public Polygon arrowHead() {
        return arrowHead;
    }

    public void setColor(Color color, double width) {
        line.setStroke(color);
        line.setStrokeWidth(width);
        arrowHead.setFill(color);
    }
}
