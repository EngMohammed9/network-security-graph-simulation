import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.jgrapht.Graph;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class NetworkSecurityApp extends Application {
    private static final int VISUAL_NODES = 15;
    private static final int MIN_EDGES = 22;
    private static final int MAX_EXTRA_EDGES = 10;
    private static final double GRAPH_WIDTH = 920;
    private static final double GRAPH_HEIGHT = 760;
    private static final double NODE_RADIUS = 30;

    private final Pane graphPane = new Pane();
    private final Map<String, Circle> vertexMap = new HashMap<>();
    private final Map<String, Point2D> vertexPositionMap = new HashMap<>();
    private final Map<DefaultEdge, EdgeView> edgeMap = new HashMap<>();
    private Graph<String, DefaultEdge> visualGraph;

    private ComboBox<String> targetComboBox;
    private Label attackerLabel;
    private Label targetLabel;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        Random random = new Random();
        int randomEdges = MIN_EDGES + random.nextInt(MAX_EXTRA_EDGES);

        System.out.println("================ Visual Network ================");
        System.out.println("Visual Nodes: " + VISUAL_NODES);
        System.out.println("Visual Edges: " + randomEdges);
        System.out.println();

        visualGraph = generateRandomDirectedVisualGraph(VISUAL_NODES, randomEdges);
        buildVisualGraph(visualGraph);

        BorderPane root = new BorderPane();
        root.setCenter(graphPane);
        root.setRight(buildRightSideUI());

        Scene scene = new Scene(root, 1250, 820);
        stage.setTitle("Network Security Simulation");
        stage.setScene(scene);
        stage.show();

        PerformanceAnalyzer.runPerformanceTest();
    }

    private VBox buildRightSideUI() {
        targetComboBox = new ComboBox<>();
        for (int i = 0; i < VISUAL_NODES; i++) {
            targetComboBox.getItems().add("Device " + i);
        }
        targetComboBox.getSelectionModel().selectFirst();
        targetComboBox.setPrefWidth(170);

        Button findButton = new Button("Find Attack Path");
        Button resetButton = new Button("Reset");
        findButton.setPrefWidth(170);
        resetButton.setPrefWidth(120);

        attackerLabel = bold("Attacker:  -");
        targetLabel = bold("Target:  -");
        statusLabel = bold("Status:  -");

        attackerLabel.setTextFill(Color.RED);
        targetLabel.setTextFill(Color.rgb(0, 150, 0));
        statusLabel.setTextFill(Color.rgb(0, 150, 0));

        VBox selectPanel = box("Select Target", 145);
        selectPanel.getChildren().addAll(
                bold("Choose Target Device:"),
                spacer(8),
                targetComboBox,
                spacer(18),
                findButton
        );

        VBox legendPanel = box("Legend", 280);
        legendPanel.getChildren().addAll(
                createLegendItem(Color.rgb(255, 95, 95), "Attacker (Device)"),
                createLegendItem(Color.rgb(120, 230, 120), "Target (Device)"),
                createArrowLegendItem("Attack Path"),
                createLegendItem(Color.rgb(255, 255, 100), "Checking"),
                createLegendItem(Color.rgb(190, 230, 200), "Visited"),
                createLegendItem(Color.WHITE, "Not Visited")
        );

        VBox infoPanel = box("Information", 135);
        infoPanel.getChildren().addAll(
                attackerLabel,
                spacer(10),
                targetLabel,
                spacer(10),
                statusLabel
        );

        VBox rightPanel = sidePanel();
        Region pushDown = new Region();
        VBox.setVgrow(pushDown, Priority.ALWAYS);
        resetButton.setAlignment(Pos.CENTER);
        rightPanel.getChildren().addAll(
                selectPanel,
                spacer(12),
                legendPanel,
                spacer(12),
                infoPanel,
                pushDown,
                resetButton,
                spacer(10)
        );

        findButton.setOnAction(e -> {
            String selectedTarget = targetComboBox.getSelectionModel().getSelectedItem();
            new Thread(() -> {
                try {
                    runSelectedTargetAttack(selectedTarget);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });

        resetButton.setOnAction(e -> resetGraphColors());
        return rightPanel;
    }

    private VBox sidePanel() {
        VBox panel = new VBox();
        panel.setPrefWidth(270);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f5f5f5;");
        return panel;
    }

    private VBox box(String title, int height) {
        Label titleLabel = bold(title);
        VBox panel = new VBox(6);
        panel.setPrefSize(225, height);
        panel.setMaxSize(225, height);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #fafafa; -fx-border-color: #9a9a9a; -fx-border-radius: 3;");
        panel.getChildren().add(titleLabel);
        return panel;
    }

    private Label bold(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, label.getFont().getSize()));
        return label;
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        return region;
    }

    private HBox createLegendItem(Color color, String text) {
        Circle circle = new Circle(12, color);
        circle.setStroke(Color.BLACK);
        Label label = new Label(text);
        HBox row = new HBox(8, circle, label);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(30);
        return row;
    }

    private HBox createArrowLegendItem(String text) {
        Line line = new Line(0, 0, 32, 0);
        line.setStroke(Color.RED);
        line.setStrokeWidth(3);
        Polygon arrowHead = new Polygon(32, 0, 22, -6, 22, 6);
        arrowHead.setFill(Color.RED);
        Group arrow = new Group(line, arrowHead);
        Label label = new Label(text);
        HBox row = new HBox(8, arrow, label);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(30);
        return row;
    }

    private Graph<String, DefaultEdge> generateRandomDirectedVisualGraph(int v, int e) {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(
                SupplierUtil.createStringSupplier(0),
                SupplierUtil.DEFAULT_EDGE_SUPPLIER,
                false
        );
        GnmRandomGraphGenerator<String, DefaultEdge> generator = new GnmRandomGraphGenerator<>(v, e);
        generator.generateGraph(graph);
        return graph;
    }

    private void buildVisualGraph(Graph<String, DefaultEdge> graph) {
        graphPane.setPrefSize(GRAPH_WIDTH, GRAPH_HEIGHT);
        graphPane.setStyle("-fx-background-color: white;");
        graphPane.getChildren().clear();
        vertexMap.clear();
        vertexPositionMap.clear();
        edgeMap.clear();

        List<String> vertices = new ArrayList<>(graph.vertexSet());
        vertices.sort((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));

        double centerX = GRAPH_WIDTH / 2;
        double centerY = GRAPH_HEIGHT / 2;
        double radius = Math.min(GRAPH_WIDTH, GRAPH_HEIGHT) * 0.38;

        for (int i = 0; i < vertices.size(); i++) {
            double angle = (2 * Math.PI * i / vertices.size()) - (Math.PI / 2);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositionMap.put(vertices.get(i), new Point2D(x, y));
        }

        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            EdgeView edgeView = createEdgeView(vertexPositionMap.get(source), vertexPositionMap.get(target));
            edgeMap.put(edge, edgeView);
            graphPane.getChildren().addAll(edgeView.line(), edgeView.arrowHead());
        }

        for (String vertex : vertices) {
            Point2D point = vertexPositionMap.get(vertex);
            Circle circle = new Circle(point.getX(), point.getY(), NODE_RADIUS);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);

            Label label = new Label("Device " + vertex);
            label.setFont(Font.font(12));
            label.setLayoutX(point.getX() - 27);
            label.setLayoutY(point.getY() - 8);
            label.setMouseTransparent(true);

            vertexMap.put(vertex, circle);
            graphPane.getChildren().addAll(circle, label);
        }
    }

    private EdgeView createEdgeView(Point2D source, Point2D target) {
        Point2D direction = target.subtract(source).normalize();
        Point2D start = source.add(direction.multiply(NODE_RADIUS));
        Point2D end = target.subtract(direction.multiply(NODE_RADIUS + 6));

        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1);

        Point2D backward = direction.multiply(-1);
        Point2D perpendicular = new Point2D(-direction.getY(), direction.getX());
        Point2D left = end.add(backward.multiply(12)).add(perpendicular.multiply(7));
        Point2D right = end.add(backward.multiply(12)).subtract(perpendicular.multiply(7));

        Polygon arrowHead = new Polygon(
                end.getX(), end.getY(),
                left.getX(), left.getY(),
                right.getX(), right.getY()
        );
        arrowHead.setFill(Color.BLACK);
        return new EdgeView(line, arrowHead);
    }

    private void resetGraphColors() {
        for (Circle vertex : vertexMap.values()) {
            vertex.setFill(Color.WHITE);
            vertex.setStroke(Color.BLACK);
        }
        for (EdgeView edge : edgeMap.values()) {
            edge.setColor(Color.BLACK, 1);
        }
        attackerLabel.setText("Attacker:  -");
        targetLabel.setText("Target:  -");
        statusLabel.setText("Status:  -");
    }

    private void runSelectedTargetAttack(String targetText) throws InterruptedException {
        runOnFx(this::resetGraphColors);
        Random random = new Random();
        String target = targetText.replace("Device ", "");
        String attacker = String.valueOf(random.nextInt(VISUAL_NODES));

        while (attacker.equals(target)) {
            attacker = String.valueOf(random.nextInt(VISUAL_NODES));
        }

        String finalAttacker = attacker;
        runOnFx(() -> {
            attackerLabel.setText("Attacker:  Device " + finalAttacker);
            targetLabel.setText("Target:  Device " + target);
            statusLabel.setText("Status:  Searching");
        });

        System.out.println();
        System.out.println("================ Attack Simulation ================");
        System.out.println("Attacker Device: " + attacker);
        System.out.println("Target Device: " + target);

        runBFSAttack(attacker, target);
    }

    private void runBFSAttack(String start, String target) throws InterruptedException {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previous = new HashMap<>();

        queue.add(start);
        visited.add(start);
        previous.put(start, null);

        boolean found = false;

        runOnFx(() -> {
            vertexMap.get(start).setFill(Color.rgb(255, 102, 102));
            vertexMap.get(target).setFill(Color.rgb(102, 221, 102));
        });

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (!current.equals(start) && !current.equals(target)) {
                runOnFx(() -> vertexMap.get(current).setFill(Color.rgb(255, 255, 102)));
            }

            Thread.sleep(500);

            if (current.equals(target)) {
                found = true;
                break;
            }

            if (!current.equals(start) && !current.equals(target)) {
                runOnFx(() -> vertexMap.get(current).setFill(Color.rgb(191, 230, 200)));
            }

            for (DefaultEdge edge : visualGraph.outgoingEdgesOf(current)) {
                String neighbor = visualGraph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (found) {
            List<String> path = GraphAlgorithms.buildAttackPath(previous, start, target);
            colorAttackPath(path);
            runOnFx(() -> {
                statusLabel.setText("Status:  Path Found");
                showMessage(
                        "Attack Path Found",
                        "Attacker: " + start + "\nTarget: " + target + "\nPath: " + String.join(" -> ", path)
                );
            });
            System.out.println("Attack Path Found: " + String.join(" -> ", path));
        } else {
            runOnFx(() -> {
                statusLabel.setText("Status:  No Path Found");
                showMessage(
                        "No attack path found",
                        "Attacker: " + start + "\nTarget: " + target
                );
            });
            System.out.println("No attack path found from " + start + " to " + target);
        }
    }

    private void colorAttackPath(List<String> path) throws InterruptedException {
        for (int i = 0; i < path.size(); i++) {
            String node = path.get(i);
            runOnFx(() -> vertexMap.get(node).setFill(Color.RED));

            if (i < path.size() - 1) {
                DefaultEdge edge = visualGraph.getEdge(path.get(i), path.get(i + 1));
                if (edge != null) {
                    runOnFx(() -> edgeMap.get(edge).setColor(Color.RED, 3));
                }
            }

            Thread.sleep(300);
        }
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void runOnFx(Runnable action) {
        Platform.runLater(action);
    }
}
