package stegovault.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import stegovault.core.FileManager;
import stegovault.core.Payload;
import stegovault.core.StegoVaultService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ExtractView {

    private final ScrollPane view;

    private File selectedStegoImage;

    private final StegoVaultService stegoVaultService;
    private final FileManager fileManager;

    public ExtractView(StegoVaultService stegoVaultService) {

        this.stegoVaultService = stegoVaultService;
        this.fileManager = new FileManager();

        VBox content = new VBox(28);

        content.setFillWidth(true);
        content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("dashboard");

        // =========================================
        // EXTRACTED FILE (built first so the image
        // picker below can reset it on a new selection)
        // =========================================

        VBox extractedCard = new VBox(10);

        extractedCard.setAlignment(Pos.TOP_LEFT);
        extractedCard.getStyleClass().add("glass-card");
        extractedCard.setMaxWidth(Double.MAX_VALUE);
        extractedCard.setVisible(false);
        extractedCard.setManaged(false);

        Label extractedTitle = new Label("Extracted File");
        extractedTitle.getStyleClass().add("dashboard-title");

        Label extractedDescription = new Label(
                "Details about the recovered file."
        );
        extractedDescription.getStyleClass().add("text-primary");

        Label extractedNameDetail = new Label("Name: —");
        extractedNameDetail.getStyleClass().add("text-primary");

        Label extractedTypeDetail = new Label("Type: —");
        extractedTypeDetail.getStyleClass().add("text-primary");

        Label extractedSizeDetail = new Label("Size: —");
        extractedSizeDetail.getStyleClass().add("text-primary");

        Label extractedSavedToDetail = new Label("Saved to: —");
        extractedSavedToDetail.getStyleClass().add("text-primary");

        extractedCard.getChildren().addAll(
                extractedTitle,
                extractedDescription,
                extractedNameDetail,
                extractedTypeDetail,
                extractedSizeDetail,
                extractedSavedToDetail
        );

        // =========================================
        // STEGO IMAGE
        // =========================================

        VBox uploadCard = new VBox(16);

        uploadCard.setAlignment(Pos.CENTER);
        uploadCard.setPrefHeight(420);
        uploadCard.setMaxWidth(Double.MAX_VALUE);

        uploadCard.getStyleClass().add("glass-card");

        Label title = new Label("Stego Image");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "Choose the image containing hidden data."
        );
        description.getStyleClass().add("text-primary");

        ImageView imagePreview = new ImageView();

        imagePreview.setFitWidth(430);
        imagePreview.setFitHeight(280);
        imagePreview.setPreserveRatio(true);
        imagePreview.setSmooth(true);
        imagePreview.setVisible(false);
        imagePreview.setManaged(false);

        HBox imageContent = new HBox(20);

        imageContent.setAlignment(Pos.CENTER);
        imageContent.setMaxWidth(Double.MAX_VALUE);

        VBox detailsCard = new VBox(10);

        detailsCard.setAlignment(Pos.TOP_LEFT);
        detailsCard.setPrefWidth(200);
        detailsCard.setMaxHeight(260);

        detailsCard.getStyleClass().add("image-details-card");

        detailsCard.setVisible(false);
        detailsCard.setManaged(false);

        Label detailsTitle = new Label("Image Details");
        detailsTitle.getStyleClass().add("dashboard-title");

        Label fileNameDetail = new Label("Name: —");
        fileNameDetail.getStyleClass().add("text-primary");

        Label formatDetail = new Label("Format: PNG");
        formatDetail.getStyleClass().add("text-primary");

        Label dimensionsDetail = new Label("Dimensions: —");
        dimensionsDetail.getStyleClass().add("text-primary");

        Label fileSizeDetail = new Label("File Size: —");
        fileSizeDetail.getStyleClass().add("text-primary");

        detailsCard.getChildren().addAll(
                detailsTitle,
                fileNameDetail,
                formatDetail,
                dimensionsDetail,
                fileSizeDetail
        );

        imageContent.getChildren().addAll(
                imagePreview,
                detailsCard
        );

        Button chooseImage = new Button("Choose Image");
        chooseImage.getStyleClass().add("primary-button");

        chooseImage.setOnAction(event -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Choose Stego PNG Image");

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "PNG Images",
                            "*.png",
                            "*.PNG"
                    )
            );

            Window window = chooseImage.getScene().getWindow();

            File file = chooser.showOpenDialog(window);

            if (file != null) {

                selectedStegoImage = file;

                Image image = new Image(
                        file.toURI().toString()
                );

                imagePreview.setImage(image);
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);

                fileNameDetail.setText(
                        "Name: " + file.getName()
                );

                formatDetail.setText(
                        "Format: PNG"
                );

                dimensionsDetail.setText(
                        "Dimensions: "
                                + (int) image.getWidth()
                                + " × "
                                + (int) image.getHeight()
                );

                fileSizeDetail.setText(
                        "File Size: "
                                + formatFileSize(file.length())
                );

                detailsCard.setVisible(true);
                detailsCard.setManaged(true);

                extractedCard.setVisible(false);
                extractedCard.setManaged(false);
            }
        });

        uploadCard.getChildren().addAll(
                title,
                description,
                imageContent,
                chooseImage
        );

        // =========================================
        // DASHBOARD CONTENT
        // =========================================

        content.getChildren().addAll(
                uploadCard,
                extractedCard,
                createExtractButton(
                        extractedCard,
                        extractedNameDetail,
                        extractedTypeDetail,
                        extractedSizeDetail,
                        extractedSavedToDetail
                )
        );

        // =========================================
        // SCROLLING
        // =========================================

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
        );

        view = scrollPane;
    }

    private Button createExtractButton(
            VBox extractedCard,
            Label extractedNameDetail,
            Label extractedTypeDetail,
            Label extractedSizeDetail,
            Label extractedSavedToDetail
    ) {

        Button extractButton = new Button("Extract Data");

        extractButton.getStyleClass().add("primary-button");
        extractButton.setMaxWidth(Double.MAX_VALUE);

        extractButton.setOnAction(event -> {

            if (selectedStegoImage == null) {
                return;
            }

            BufferedImage image;

            try {

                image = javax.imageio.ImageIO.read(selectedStegoImage);

            } catch (IOException e) {

                showError(
                        "Failed to read image",
                        e.getMessage()
                );

                return;
            }

            if (image == null) {

                showError(
                        "Unsupported image",
                        "The selected file could not be read as an image."
                );

                return;
            }

            Payload payload;

            String password = null;

            if (image != null) {

                javafx.scene.control.TextInputDialog pwdDialog =
                        new javafx.scene.control.TextInputDialog(
                                ""
                        );

                pwdDialog.setTitle("Enter Encryption Password");
                pwdDialog.setHeaderText(
                        "Enter the password used to encrypt the hidden data. "
                        + "If the data was hidden without a password, leave blank."
                );
                pwdDialog.setContentText("Password:");

                Optional<String> passwordResult = pwdDialog.showAndWait();

                if (passwordResult.isPresent()) {
                    password = passwordResult.get();
                } else {
                    return;
                }
            }

            try {

                payload = stegoVaultService.extractPayload(image, password);

            } catch (IllegalArgumentException e) {

                showError(
                        "Decryption failed",
                        e.getMessage()
                );

                return;

            } catch (RuntimeException e) {

                showError(
                        "Failed to extract data",
                        "No hidden data could be recovered from this image.\n"
                                + e.getMessage()
                );

                return;
            }

            FileChooser saveChooser = new FileChooser();

            saveChooser.setTitle("Save Extracted File");

            saveChooser.setInitialFileName(
                    payload.metadata().fileName()
            );

            Window window = extractButton.getScene().getWindow();

            File outputFile =
                    saveChooser.showSaveDialog(window);

            if (outputFile == null) {
                return;
            }

            try {

                fileManager.savePayload(
                        payload,
                        outputFile.toPath()
                );

            } catch (IOException e) {

                showError(
                        "Failed to save extracted file",
                        e.getMessage()
                );

                return;
            }

            extractedNameDetail.setText(
                    "Name: " + payload.metadata().fileName()
            );

            extractedTypeDetail.setText(
                    "Type: " + payload.metadata().mimeType()
            );

            extractedSizeDetail.setText(
                    "Size: " + formatFileSize(payload.size())
            );

            extractedSavedToDetail.setText(
                    "Saved to: " + outputFile.getAbsolutePath()
            );

            extractedCard.setVisible(true);
            extractedCard.setManaged(true);

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.INFORMATION
                    );

            alert.setTitle("StegoVault");
            alert.setHeaderText("Data extracted successfully");
            alert.setContentText(
                    "Recovered \"" + payload.metadata().fileName()
                            + "\" and saved it to:\n"
                            + outputFile.getAbsolutePath()
            );

            alert.showAndWait();
        });

        return extractButton;
    }

    private void showError(String header, String message) {

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR
                );

        alert.setTitle("StegoVault");
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private String formatFileSize(long bytes) {

        if (bytes < 1024) {
            return bytes + " B";
        }

        if (bytes < 1024 * 1024) {
            return String.format(
                    "%.1f KB",
                    bytes / 1024.0
            );
        }

        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(
                    "%.1f MB",
                    bytes / (1024.0 * 1024.0)
            );
        }

        return String.format(
                "%.1f GB",
                bytes / (1024.0 * 1024.0 * 1024.0)
        );
    }

    public ScrollPane getView() {
        return view;
    }
}
