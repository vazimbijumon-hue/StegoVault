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
import stegovault.Analysis.ImageAnalyser;
import stegovault.Analysis.ImageValidator;
import stegovault.core.StegoVaultService;
import stegovault.models.CapacityInfo;
import stegovault.models.ImageInfo;
import stegovault.models.PayloadDetectionResult;
import stegovault.steganography.CapacityCalculator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * "Analyze Image" screen.
 *
 * Lets the user pick any PNG, shows useful image information (dimensions,
 * file size, pixel count, embedding capacity), and reports whether the
 * image contains a StegoVault payload plus its integrity status.
 *
 * All steganography/format logic is delegated to existing classes
 * (ImageAnalyser, ImageValidator, CapacityCalculator, StegoVaultService) —
 * this view only wires them into the UI.
 */
public class AnalyzeView {

    private final ScrollPane view;

    private File selectedImage;

    private final StegoVaultService stegoVaultService;

    public AnalyzeView(StegoVaultService stegoVaultService) {

        this.stegoVaultService = stegoVaultService;

        VBox content = new VBox(28);

        content.setFillWidth(true);
        content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("dashboard");

        // =========================================
        // PAYLOAD STATUS (built first so the image
        // picker below can reset it on a new selection)
        // =========================================

        VBox payloadCard = new VBox(10);

        payloadCard.setAlignment(Pos.TOP_LEFT);
        payloadCard.getStyleClass().add("glass-card");
        payloadCard.setMaxWidth(Double.MAX_VALUE);
        payloadCard.setVisible(false);
        payloadCard.setManaged(false);

        Label payloadTitle = new Label("Payload Detection");
        payloadTitle.getStyleClass().add("dashboard-title");

        Label payloadDescription = new Label(
                "Whether this image contains a StegoVault payload, "
                        + "verified against its stored SHA-256 checksum."
        );
        payloadDescription.getStyleClass().add("text-primary");

        Label payloadStatusDetail = new Label("Status: —");
        payloadStatusDetail.getStyleClass().add("text-primary");

        Label payloadFileNameDetail = new Label("Original File: —");
        payloadFileNameDetail.getStyleClass().add("text-primary");

        Label payloadMimeTypeDetail = new Label("MIME Type: —");
        payloadMimeTypeDetail.getStyleClass().add("text-primary");

        Label payloadSizeDetail = new Label("Payload Size: —");
        payloadSizeDetail.getStyleClass().add("text-primary");

        payloadCard.getChildren().addAll(
                payloadTitle,
                payloadDescription,
                payloadStatusDetail,
                payloadFileNameDetail,
                payloadMimeTypeDetail,
                payloadSizeDetail
        );

        // =========================================
        // IMAGE TO ANALYSE
        // =========================================

        VBox uploadCard = new VBox(16);

        uploadCard.setAlignment(Pos.CENTER);
        uploadCard.setPrefHeight(420);
        uploadCard.setMaxWidth(Double.MAX_VALUE);

        uploadCard.getStyleClass().add("glass-card");

        Label title = new Label("Image to Analyse");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "Choose a PNG to inspect its properties and check for hidden data."
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
        detailsCard.setMaxHeight(280);

        detailsCard.getStyleClass().add("image-details-card");

        detailsCard.setVisible(false);
        detailsCard.setManaged(false);

        Label detailsTitle = new Label("Image Details");
        detailsTitle.getStyleClass().add("dashboard-title");

        Label fileNameDetail = new Label("Name: —");
        fileNameDetail.getStyleClass().add("text-primary");

        Label formatDetail = new Label("Format: —");
        formatDetail.getStyleClass().add("text-primary");

        Label dimensionsDetail = new Label("Dimensions: —");
        dimensionsDetail.getStyleClass().add("text-primary");

        Label fileSizeDetail = new Label("File Size: —");
        fileSizeDetail.getStyleClass().add("text-primary");

        Label pixelCountDetail = new Label("Pixel Count: —");
        pixelCountDetail.getStyleClass().add("text-primary");

        Label capacityDetail = new Label("Embedding Capacity: —");
        capacityDetail.getStyleClass().add("text-primary");

        detailsCard.getChildren().addAll(
                detailsTitle,
                fileNameDetail,
                formatDetail,
                dimensionsDetail,
                fileSizeDetail,
                pixelCountDetail,
                capacityDetail
        );

        imageContent.getChildren().addAll(
                imagePreview,
                detailsCard
        );

        Button chooseImage = new Button("Choose Image");
        chooseImage.getStyleClass().add("primary-button");

        chooseImage.setOnAction(event -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Choose Image to Analyse");

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "PNG Images",
                            "*.png",
                            "*.PNG"
                    )
            );

            Window window = chooseImage.getScene().getWindow();

            File file = chooser.showOpenDialog(window);

            if (file == null) {
                return;
            }

            selectedImage = file;

            // Reset payload card until analysis completes below.
            payloadCard.setVisible(false);
            payloadCard.setManaged(false);

            if (!ImageValidator.isValid(file)) {

                detailsCard.setVisible(false);
                detailsCard.setManaged(false);

                imagePreview.setVisible(false);
                imagePreview.setManaged(false);

                showError(
                        "Unsupported image",
                        "StegoVault can only analyse PNG (or BMP) images."
                );

                return;
            }

            Image previewImage = new Image(
                    file.toURI().toString()
            );

            imagePreview.setImage(previewImage);
            imagePreview.setVisible(true);
            imagePreview.setManaged(true);

            // -----------------------------------------
            // Image details (delegates to ImageAnalyser
            // and CapacityCalculator — no duplicated logic)
            // -----------------------------------------

            ImageInfo imageInfo;
            CapacityInfo capacityInfo;

            try {

                imageInfo = ImageAnalyser.analyze(file);
                capacityInfo = CapacityCalculator.calculateCapacity(file);

            } catch (RuntimeException e) {

                detailsCard.setVisible(false);
                detailsCard.setManaged(false);

                showError(
                        "Failed to analyse image",
                        e.getMessage()
                );

                return;
            }

            fileNameDetail.setText(
                    "Name: " + file.getName()
            );

            formatDetail.setText(
                    "Format: " + imageInfo.getFormat()
                            + (imageInfo.hasAlpha() ? " (with alpha)" : "")
            );

            dimensionsDetail.setText(
                    "Dimensions: "
                            + imageInfo.getWidth()
                            + " × "
                            + imageInfo.getHeight()
            );

            fileSizeDetail.setText(
                    "File Size: " + formatFileSize(file.length())
            );

            pixelCountDetail.setText(
                    "Pixel Count: " + imageInfo.getPixelCount()
            );

            capacityDetail.setText(
                    "Embedding Capacity: "
                            + formatFileSize(capacityInfo.getUsableBytes())
            );

            detailsCard.setVisible(true);
            detailsCard.setManaged(true);

            // -----------------------------------------
            // Payload detection (delegates to
            // StegoVaultService.detectPayload)
            // -----------------------------------------

            BufferedImage bufferedImage;

            try {

                bufferedImage = javax.imageio.ImageIO.read(file);

            } catch (IOException e) {

                showError(
                        "Failed to read image",
                        e.getMessage()
                );

                return;
            }

            if (bufferedImage == null) {

                showError(
                        "Unsupported image",
                        "The selected file could not be read as an image."
                );

                return;
            }

            PayloadDetectionResult result =
                    stegoVaultService.detectPayload(bufferedImage);

            if (result.isPayloadDetected()) {

                payloadStatusDetail.setText(
                        "Status: \u2713 StegoVault payload found — integrity verified"
                );

                payloadFileNameDetail.setText(
                        "Original File: " + result.fileName()
                );

                payloadMimeTypeDetail.setText(
                        "MIME Type: " + result.mimeType()
                );

                payloadSizeDetail.setText(
                        "Payload Size: "
                                + formatFileSize(result.payloadSizeBytes())
                );

            } else {

                payloadStatusDetail.setText(
                        "Status: \u2717 No StegoVault payload detected"
                );

                payloadFileNameDetail.setText(
                        "Original File: —"
                );

                payloadMimeTypeDetail.setText(
                        "MIME Type: —"
                );

                payloadSizeDetail.setText(
                        "Payload Size: —"
                );
            }

            payloadCard.setVisible(true);
            payloadCard.setManaged(true);
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
                payloadCard
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
