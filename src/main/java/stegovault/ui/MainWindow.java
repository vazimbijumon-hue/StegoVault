package stegovault.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import stegovault.core.StegoVaultService;
import stegovault.models.CapacityInfo;
import stegovault.steganography.CapacityCalculator;
import stegovault.views.AnalyzeView;
import stegovault.views.ExtractView;
import stegovault.views.HideDataView;
import stegovault.views.SettingsView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;



public class MainWindow {

    private final StegoVaultService stegoVaultService;

    public MainWindow(StegoVaultService stegoVaultService) {
        this.stegoVaultService = stegoVaultService;
    }

    private File selectedCoverImage;

    public Scene createScene() {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

Sidebar sidebar = new Sidebar(
        page -> {

            switch (page) {

                case "hide" -> {
                    HideDataView hideDataView =
                            new HideDataView(stegoVaultService);

                    root.setCenter(
                            hideDataView.getView()
                    );
                }

                case "extract" -> {
                    ExtractView extractView =
                            new ExtractView(stegoVaultService);

                    root.setCenter(
                            extractView.getView()
                    );
                }

                case "analyze" -> {
                    AnalyzeView analyzeView =
                            new AnalyzeView(stegoVaultService);

                    root.setCenter(
                            analyzeView.getView()
                    );
                }

                case "settings" -> {
                    SettingsView settingsView =
                            new SettingsView();

                    root.setCenter(
                            settingsView.getView()
                    );
                }
            }
        }
);

        root.setLeft(sidebar.getView());
        sidebar.setActive("hide");

       ImageView logo = new ImageView(
        new Image(
                getClass()
                        .getResourceAsStream("/images/logo.jpg")
        )
);

logo.setFitHeight(55);
logo.setPreserveRatio(true);

Label title = new Label("StegoVault");
title.setStyle(
        "-fx-font-size: 24px; -fx-font-weight: bold;"
);

Label privacyNote =
        new Label(
                "Offline by default. Your files stay on this device."
        );

privacyNote.setStyle(
        "-fx-text-fill: #5f6368;"
);

VBox text = new VBox(
        4,
        title,
        privacyNote
);

HBox topBar = new HBox(
        16,
        logo,
        text
);

topBar.setAlignment(
        javafx.geometry.Pos.CENTER_LEFT
);

root.setTop(topBar);

        HideDataView hideDataView =
                new HideDataView(stegoVaultService);

        root.setCenter(
                hideDataView.getView()
        );

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/styles/stegovault.css")
                        .toExternalForm()
        );

        return scene;
    }

    private void showHomeView(BorderPane root) {

        Button hideButton =
                createActionButton("Hide Data");

        Button extractButton =
                createActionButton("Extract Data");

        Button analyzeButton =
                createActionButton("Analyze Image");

        hideButton.setOnAction(
                event -> showHideDataView(root)
        );

        Label guidance =
                new Label("Choose an action to begin.");

        HBox actions =
                new HBox(
                        12,
                        hideButton,
                        extractButton,
                        analyzeButton
                );

        actions.setAlignment(
                javafx.geometry.Pos.CENTER
        );

        VBox center =
                new VBox(
                        16,
                        guidance,
                        actions
                );

        center.setAlignment(
                javafx.geometry.Pos.CENTER
        );

        root.setCenter(center);
    }

    private void showHideDataView(BorderPane root) {

        HideDataView hideDataView =
                new HideDataView(stegoVaultService);

        root.setCenter(
                hideDataView.getView()
        );
    }

    private void chooseCoverImage(
            BorderPane root,
            TextArea coverStatus,
            Button embedButton,
            TextArea messageArea
    ) {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Choose a PNG Cover Image"
        );

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PNG Images",
                        "*.png",
                        "*.PNG"
                )
        );

        File selectedImage =
                chooser.showOpenDialog(
                        root.getScene().getWindow()
                );

        if (selectedImage != null) {

            selectedCoverImage = selectedImage;

            CapacityInfo info =
                    CapacityCalculator.calculateCapacity(
                            selectedImage
                    );

            coverStatus.setText(
                    "════════════════════\n" +
                    "     IMAGE ANALYSIS\n" +
                    "════════════════════\n\n" +

                    "File:\n" +
                    selectedImage.getName() + "\n\n" +

                    "Dimensions:\n" +
                    info.getWidth() +
                    " x " +
                    info.getHeight() +
                    "\n\n" +

                    "Pixel Count:\n" +
                    info.getPixelCount() +
                    "\n\n" +

                    "Embedding Capacity:\n" +
                    info.getUsableBytes() +
                    " bytes\n\n" +

                    "Status:\n" +
                    "✓ Ready for embedding"
            );

            updateEmbedButton(
                    embedButton,
                    messageArea
            );
        }
    }

    private Button createActionButton(String label) {

        Button button =
                new Button(label);

        button.setPrefWidth(160);

        return button;
    }

    private void updateEmbedButton(
            Button embedButton,
            TextArea messageArea
    ) {

        boolean imageSelected =
                selectedCoverImage != null;

        boolean messageEntered =
                !messageArea
                        .getText()
                        .trim()
                        .isEmpty();

        embedButton.setDisable(
                !(imageSelected && messageEntered)
        );
    }
}