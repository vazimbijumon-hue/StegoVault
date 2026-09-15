package stegovault.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;



public class Sidebar {

    private final VBox view;

    public Sidebar(
            Consumer<String> navigationHandler
    ) {

        view = new VBox(12);
	view.getStyleClass().add("sidebar");
	view.setPadding(new Insets(20));
	view.setPrefWidth(220);
	view.setMinWidth(220);
	view.setMaxWidth(220);

        
        Button hideButton = new Button("Hide Data");
        Button extractButton = new Button("Extract Data");
        Button analyzeButton = new Button("Analyze");
        Button settingsButton = new Button("About");

        
        hideButton.getStyleClass().add("sidebar-button");
        extractButton.getStyleClass().add("sidebar-button");
        analyzeButton.getStyleClass().add("sidebar-button");
        settingsButton.getStyleClass().add("sidebar-button");


	hideButton.setMaxWidth(Double.MAX_VALUE);
	extractButton.setMaxWidth(Double.MAX_VALUE);
	analyzeButton.setMaxWidth(Double.MAX_VALUE);
	settingsButton.setMaxWidth(Double.MAX_VALUE);

        

      hideButton.setOnAction(e -> {
    setActive("hide");
    navigationHandler.accept("hide");
});

	extractButton.setOnAction(e -> {
    setActive("extract");
    navigationHandler.accept("extract");
});

	analyzeButton.setOnAction(e -> {
    setActive("analyze");
    navigationHandler.accept("analyze");
});

	settingsButton.setOnAction(e -> {
    setActive("settings");
    navigationHandler.accept("settings");
}	);

        view.getChildren().addAll(
                
                hideButton,
                extractButton,
                analyzeButton,
                settingsButton
        );
    }

	public void setActive(String page) {
    view.getChildren().forEach(node -> node.getStyleClass().remove("active"));

    switch (page) {
        case "hide" -> view.getChildren().get(0).getStyleClass().add("active");
        case "extract" -> view.getChildren().get(1).getStyleClass().add("active");
        case "analyze" -> view.getChildren().get(2).getStyleClass().add("active");
        case "settings" -> view.getChildren().get(3).getStyleClass().add("active");
    }
}

    public VBox getView() {
        return view;
    }
}