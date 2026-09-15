package stegovault.views;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import stegovault.models.CapacityInfo;
import stegovault.steganography.CapacityCalculator;
import stegovault.core.StegoVaultService;
import stegovault.core.Payload;
import stegovault.core.PayloadMetadata;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HideDataView {

   private final ScrollPane view;

private File selectedCoverImage;
private File selectedPayloadFile;

private final PasswordField passwordField =
                                        
                        new PasswordField();
private final PasswordField confirmPasswordField =
        new PasswordField();

private final StegoVaultService stegoVaultService;

  public HideDataView(StegoVaultService stegoVaultService) {

    this.stegoVaultService = stegoVaultService;

   

    VBox content = new VBox(28);

        content.setFillWidth(true);
	content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("dashboard");

        // =========================================
        // COVER IMAGE
        // =========================================

        VBox uploadCard = new VBox(16);

	uploadCard.setAlignment(Pos.CENTER);
	uploadCard.setMaxWidth(Double.MAX_VALUE);

        uploadCard.getStyleClass().add("glass-card");

        Label title = new Label("Cover Image");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "Choose an image to hide your secret data."
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
	imageContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

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

        Label capacityDetail = new Label("Capacity: —");
        capacityDetail.getStyleClass().add("text-primary");

        detailsCard.getChildren().addAll(
                detailsTitle,
                fileNameDetail,
                formatDetail,
                dimensionsDetail,
                fileSizeDetail,
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

            chooser.setTitle("Choose PNG Cover Image");

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

                selectedCoverImage = file;
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
                                + file.length()
                                + " bytes"
                );

                CapacityInfo info =
                        CapacityCalculator.calculateCapacity(file);

             capacityDetail.setText(
        "Usable Capacity: "
                + formatFileSize(info.getUsableBytes())
);

                detailsCard.setVisible(true);
                detailsCard.setManaged(true);


            }
        });

        uploadCard.getChildren().addAll(
                title,
                description,
                imageContent,
                chooseImage
        );
        // =========================================
        // SECRET MESSAGE
        // =========================================

        VBox messageCard = new VBox(16);

        messageCard.setAlignment(Pos.TOP_LEFT);
        
        messageCard.setMaxWidth(Double.MAX_VALUE);

        messageCard.getStyleClass().add("glass-card");

        Label messageTitle = new Label("Secret Message");
        messageTitle.getStyleClass().add("dashboard-title");

        Label messageDescription = new Label(
                "Enter a secret message or import a file to hide inside the image."
        );
        messageDescription.getStyleClass().add("text-primary");

        Label fileStatus = new Label(
                "No file selected"
        );
        fileStatus.getStyleClass().add("file-status");
        fileStatus.getStyleClass().add("text-primary");


        TextArea messageArea = new TextArea();
        Button importFile = new Button("Import File");
        importFile.getStyleClass().add("primary-button");
        messageArea.setPromptText(
                "Write your secret message here..."
        );

        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(6);
        messageArea.setMaxWidth(Double.MAX_VALUE);
        messageArea.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (selectedPayloadFile != null && !newValue.isEmpty()) {

                        javafx.scene.control.Alert alert =
                                new javafx.scene.control.Alert(
                                        javafx.scene.control.Alert.AlertType.CONFIRMATION
                                );

                        alert.setTitle("Replace File?");
                        alert.setHeaderText("Replace the selected file?");
                        alert.setContentText(
                                "You already selected a file. " +
                                        "Typing a message will replace it."
                        );

                        javafx.scene.control.ButtonType replace =
                                new javafx.scene.control.ButtonType("Replace");

                        javafx.scene.control.ButtonType cancel =
                                new javafx.scene.control.ButtonType("Cancel");

                        alert.getButtonTypes().setAll(
                                replace,
                                cancel
                        );

                        java.util.Optional<javafx.scene.control.ButtonType> result =
                                alert.showAndWait();

                        if (result.isPresent() && result.get() == replace) {

                           selectedPayloadFile = null;

                            fileStatus.setText(
                                    "No file selected"
                            );

                        } else {

                            messageArea.setText(oldValue);
                        }
                    }
                }
        );


        importFile.setOnAction(event -> {

            if (!messageArea.getText().trim().isEmpty()) {

                javafx.scene.control.Alert alert =
                        new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.CONFIRMATION
                        );

                alert.setTitle("Replace Message?");
                alert.setHeaderText("Replace your typed message?");
                alert.setContentText(
                        "You already entered a secret message. " +
                                "Importing a file will replace it."
                );

                javafx.scene.control.ButtonType replace =
                        new javafx.scene.control.ButtonType("Replace");

                javafx.scene.control.ButtonType cancel =
                        new javafx.scene.control.ButtonType("Cancel");

                alert.getButtonTypes().setAll(
                        replace,
                        cancel
                );

                java.util.Optional<javafx.scene.control.ButtonType> result =
                        alert.showAndWait();

                if (result.isEmpty() ||
                        result.get() != replace) {
                    return;
                }

                messageArea.clear();
            }

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Choose File to Hide");

            Window window = importFile.getScene().getWindow();

            File file = chooser.showOpenDialog(window);

            if (file != null) {

                selectedPayloadFile = file;

                fileStatus.setText(
                        "Selected file: " + file.getName()
                                + "\nType: " + getFileExtension(file)
                                + "\nSize: " + formatFileSize(file.length())
                );
            }
        });

        messageCard.getChildren().addAll(
                messageTitle,
                messageDescription,
                importFile,
                messageArea,
                fileStatus
        );

        // =========================================
        // DASHBOARD CONTENT
        // =========================================

        content.getChildren().addAll(
                uploadCard,
                messageCard,
                createEncryptionCard(),
                createHideButton(messageArea)
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

    private VBox createEncryptionCard() {

        VBox encryptionCard = new VBox(16);

        encryptionCard.setAlignment(Pos.TOP_LEFT);
        
        encryptionCard.setMaxWidth(Double.MAX_VALUE);

        encryptionCard.getStyleClass().add("glass-card");

        Label title = new Label("Encryption");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "Protect your secret payload with a password."
        );

        description.getStyleClass().add("text-primary");

        Button configureButton =
                new Button("Configure Encryption");

        BooleanProperty encryptionOptionsVisible =
                new SimpleBooleanProperty(false);
        BooleanProperty passwordVisible =
                new SimpleBooleanProperty(false);

       
        Button showPasswordButton = new Button("👨‍🦯");
        showPasswordButton.getStyleClass().add("password-toggle");

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText(
                "Enter encryption password"
        );
        visiblePasswordField.setMaxWidth(Double.MAX_VALUE);
        visiblePasswordField.textProperty().bindBidirectional(
                passwordField.textProperty()
        );
        showPasswordButton.setOnAction(event ->
                passwordVisible.set(!passwordVisible.get())
        );
        showPasswordButton.textProperty().bind(
                javafx.beans.binding.Bindings.when(passwordVisible)
                        .then("👀")
                        .otherwise("👨‍🦯")
        );
        passwordField.setPromptText(
                "Enter encryption password"
        );



        confirmPasswordField.setPromptText(
                "Confirm encryption password"
        );

        confirmPasswordField.setMaxWidth(
                Double.MAX_VALUE
        );

        Label passwordStatus = new Label();

        passwordStatus.getStyleClass().add("text-primary");
        Label passwordStrength = new Label();

        passwordStrength.getStyleClass().add("text-primary");

        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue.isEmpty()) {
                        passwordStrength.setText("");
                        return;
                    }

                    int score = getPasswordScore(newValue);

                    if (score >= 5) {
                        passwordStrength.setText("Strong password");
                    } else if (score >= 3) {
                        passwordStrength.setText("Fair password");
                    } else {
                        passwordStrength.setText("Weak password");
                    }
                }
        );

        configureButton.getStyleClass().add("primary-button");

        configureButton.setOnAction(event -> {
            encryptionOptionsVisible.set(
                    !encryptionOptionsVisible.get()
            );
        });
        configureButton.textProperty().bind(
                javafx.beans.binding.Bindings.when(encryptionOptionsVisible)
                        .then("Hide Encryption Options")
                        .otherwise("Configure Encryption")
        );

        HBox passwordBox = new HBox(8);
        passwordBox.setMaxWidth(Double.MAX_VALUE);

        passwordBox.visibleProperty().bind(encryptionOptionsVisible);
        passwordBox.managedProperty().bind(encryptionOptionsVisible);

        passwordField.visibleProperty().bind(passwordVisible.not());
        passwordField.managedProperty().bind(passwordVisible.not());
        visiblePasswordField.visibleProperty().bind(passwordVisible);
        visiblePasswordField.managedProperty().bind(passwordVisible);

        passwordStrength.visibleProperty().bind(encryptionOptionsVisible);
        passwordStrength.managedProperty().bind(encryptionOptionsVisible);
        confirmPasswordField.visibleProperty().bind(encryptionOptionsVisible);
        confirmPasswordField.managedProperty().bind(encryptionOptionsVisible);
        passwordStatus.visibleProperty().bind(encryptionOptionsVisible);
        passwordStatus.managedProperty().bind(encryptionOptionsVisible);

        HBox.setHgrow(passwordField, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(visiblePasswordField, javafx.scene.layout.Priority.ALWAYS);

        passwordBox.getChildren().addAll(
                passwordField,
                visiblePasswordField,
                showPasswordButton
        );

        encryptionCard.getChildren().addAll(
                title,
                description,
                configureButton,
                passwordBox,
                passwordStrength,
                confirmPasswordField,
                passwordStatus
        );

        confirmPasswordField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (passwordField.getText().isEmpty()
                            || newValue.isEmpty()) {

                        passwordStatus.setText("");
                        return;
                    }

                    if (passwordField.getText().equals(newValue)) {

                        passwordStatus.setText(
                                "Passwords match"
                        );

                    } else {

                        passwordStatus.setText(
                                "Passwords do not match"
                        );
                    }
                }
        );

        return encryptionCard;
    }

    private Button createHideButton(TextArea messageArea) {

        Button hideButton = new Button("Hide Data");

        hideButton.getStyleClass().add("primary-button");

        hideButton.setMaxWidth(Double.MAX_VALUE);
FileChooser saveChooser = new FileChooser();

saveChooser.setTitle("Save StegoVault Image");

saveChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(
                "PNG Images",
                "*.png"
        )
);
    hideButton.setOnAction(event ->
         {

Window window = hideButton.getScene().getWindow();

File outputFile =
        saveChooser.showSaveDialog(window);

if (outputFile == null) {
    return;
}




    if (selectedCoverImage == null) {
        return;
    }

    String typedMessage = messageArea.getText();
    boolean hasMessage = !typedMessage.trim().isEmpty();

    if (selectedPayloadFile == null && !hasMessage) {
        return;
    }
String password = passwordField.getText();
String confirmPassword = confirmPasswordField.getText();

if (!password.equals(confirmPassword)) {
    javafx.scene.control.Alert alert =
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );

    alert.setTitle("StegoVault");
    alert.setHeaderText("Passwords do not match");
    alert.setContentText(
            "Please make sure both password fields contain the same password."
    );

    alert.showAndWait();
    return;
}

  
 try {

   BufferedImage coverImage =
        stegovault.Analysis.MetaDataCleaner.clean(
                selectedCoverImage
        );

    BufferedImage result;

    if (selectedPayloadFile != null) {

        result =
                stegoVaultService.hideFile(
                        selectedPayloadFile.toPath(),
                        coverImage,
                        passwordField.getText()
                );

    } else {

        byte[] messageBytes =
                typedMessage.getBytes(StandardCharsets.UTF_8);

        PayloadMetadata metadata =
                new PayloadMetadata(
                        "message.txt",
                        "text/plain"
                );

        Payload messagePayload =
                new Payload(
                        metadata,
                        messageBytes
                );

        result =
                stegoVaultService.hidePayload(
                        messagePayload,
                        coverImage,
                        passwordField.getText()
                );
    }

    javax.imageio.ImageIO.write(
            result,
            "png",
            outputFile
    );

javafx.scene.control.Alert alert =
        new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
        );

alert.setTitle("StegoVault");
alert.setHeaderText("Data hidden successfully");
alert.setContentText(
        "Your encrypted data was hidden in:\n"
                + outputFile.getAbsolutePath()
);

alert.showAndWait();

} catch (IOException | IllegalArgumentException | IllegalStateException e) {

    e.printStackTrace();

    javafx.scene.control.Alert alert =
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );

    alert.setTitle("StegoVault");
    alert.setHeaderText("Failed to hide data");
    alert.setContentText(e.getMessage());

    alert.showAndWait();
}

}
);

        return hideButton;
    }
    private String getFileExtension(File file) {

        String name = file.getName();

        int dot = name.lastIndexOf('.');

        if (dot == -1) {
            return "Unknown";
        }

        return name.substring(dot + 1).toUpperCase();
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
    private boolean isSimpleSequence(String password) {

        String value = password.toLowerCase();

        if (value.length() < 4) {
            return false;
        }

        boolean ascending = true;
        boolean descending = true;

        for (int i = 1; i < value.length(); i++) {

            int difference =
                    value.charAt(i) - value.charAt(i - 1);

            if (difference != 1) {
                ascending = false;
            }

            if (difference != -1) {
                descending = false;
            }
        }

        return ascending || descending;
    }
    private int getPasswordScore(String password) {

        int score = 0;
        if (isCommonWeakPassword(password)) {
            return 0;
        }
        if (isSimpleSequence(password)) {
            return 0;
        }

        if (password.length() >= 12) {
            score++;
        }

        if (password.length() >= 16) {
            score++;
        }

        if (password.matches(".*[A-Z].*")) {
            score++;
        }

        if (password.matches(".*[a-z].*")) {
            score++;
        }

        if (password.matches(".*[0-9].*")) {
            score++;
        }

        if (password.matches(".*[^a-zA-Z0-9].*")) {
            score++;
        }

        if (password.chars().distinct().count() == 1) {
            score = 0;
        }

        return score;
    }
    private boolean isStrongPassword(String password) {

        if (password.length() < 12) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        if (!hasUppercase ||
                !hasLowercase ||
                !hasNumber ||
                !hasSpecial) {
            return false;
        }

        // Reject passwords made from one repeated character
        if (password.chars().distinct().count() == 1) {
            return false;
        }

        return true;
    }

    private boolean isCommonWeakPassword(String password) {

        String value = password.toLowerCase();

        String[] commonPasswords = {
                "password",
                "password123",
                "123456",
                "12345678",
                "123456789",
                "qwerty",
                "qwerty123",
                "admin",
                "admin123",
                "letmein",
                "welcome",
                "iloveyou",
                "abc123"
        };

        for (String common : commonPasswords) {

            if (value.equals(common)) {
                return true;
            }
        }

        return false;
    }

    public ScrollPane getView() {
        return view;
    }
}
