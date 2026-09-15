package stegovault.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import stegovault.core.AppInfo;
import stegovault.core.CryptoManager;
import stegovault.core.KeyManager;
import stegovault.core.PayloadHeader;


public class SettingsView {

    private final ScrollPane view;

    public SettingsView() {

        VBox content = new VBox(28);

        content.setFillWidth(true);
        content.setMaxWidth(Double.MAX_VALUE);
        content.getStyleClass().add("dashboard");

        content.getChildren().addAll(
                createAboutCard(),
                createSecurityCard(),
                createSteganographyCard()
        );

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

    // =========================================
    // ABOUT
    // =========================================

    private VBox createAboutCard() {

        VBox card = new VBox(10);

        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("glass-card");
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("About");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(AppInfo.DESCRIPTION);
        description.getStyleClass().add("text-primary");
        description.setWrapText(true);

        Label nameDetail = new Label("Application: " + AppInfo.NAME);
        nameDetail.getStyleClass().add("text-primary");

        Label versionDetail = new Label("Version: " + AppInfo.VERSION);
        versionDetail.getStyleClass().add("text-primary");

        Label privacyDetail = new Label(
                "Offline by default. Your files stay on this device."
        );
        privacyDetail.getStyleClass().add("text-primary");

        card.getChildren().addAll(
                title,
                description,
                nameDetail,
                versionDetail,
                privacyDetail
        );

        return card;
    }

    // =========================================
    // SECURITY CONFIGURATION
    // =========================================

    private VBox createSecurityCard() {

        VBox card = new VBox(10);

        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("glass-card");
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Security Configuration");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "These parameters are fixed by the app and shown here "
                        + "for transparency — they are not user-editable yet."
        );
        description.getStyleClass().add("text-primary");
        description.setWrapText(true);

        Label encryptionDetail = new Label(
                "Encryption: " + CryptoManager.algorithm()
                        + " (" + CryptoManager.tagLengthBits() + "-bit tag)"
        );
        encryptionDetail.getStyleClass().add("text-primary");

        Label kdfDetail = new Label(
                "Key Derivation: " + KeyManager.algorithm()
                        + ", " + formatCount(KeyManager.iterations())
                        + " iterations, " + KeyManager.keyLengthBits()
                        + "-bit key"
        );
        kdfDetail.getStyleClass().add("text-primary");

        Label integrityDetail = new Label(
                "Payload Integrity: SHA-256 checksum ("
                        + PayloadHeader.CHECKSUM_LENGTH + " bytes)"
        );
        integrityDetail.getStyleClass().add("text-primary");

        Label formatDetail = new Label(
                "Payload Format Version: " + PayloadHeader.FORMAT_VERSION
        );
        formatDetail.getStyleClass().add("text-primary");

        Label noteBox = new Label(
                "Note: the password fields on the Hide Data screen do not "
                        + "yet derive the active encryption key. Key derivation "
                        + "wiring is a known, tracked future task."
        );
        noteBox.getStyleClass().add("file-status");
        noteBox.getStyleClass().add("text-primary");
        noteBox.setWrapText(true);

        card.getChildren().addAll(
                title,
                description,
                encryptionDetail,
                kdfDetail,
                integrityDetail,
                formatDetail,
                noteBox
        );

        return card;
    }

    // =========================================
    // STEGANOGRAPHY CONFIGURATION
    // =========================================

    private VBox createSteganographyCard() {

        VBox card = new VBox(10);

        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("glass-card");
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Steganography Configuration");
        title.getStyleClass().add("dashboard-title");

        Label description = new Label(
                "How StegoVault hides data inside an image."
        );
        description.getStyleClass().add("text-primary");

        Label techniqueDetail = new Label(
                "Embedding Technique: RGB LSB "
                        + "(1 bit per colour channel, 3 bits per pixel)"
        );
        techniqueDetail.getStyleClass().add("text-primary");

        Label formatsDetail = new Label(
                "Supported Cover Images: PNG (recommended, lossless), BMP"
        );
        formatsDetail.getStyleClass().add("text-primary");

        card.getChildren().addAll(
                title,
                description,
                techniqueDetail,
                formatsDetail
        );

        return card;
    }

    private String formatCount(int value) {
        return String.format("%,d", value);
    }

    public ScrollPane getView() {
        return view;
    }
}
