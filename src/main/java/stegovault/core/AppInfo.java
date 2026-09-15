package stegovault.core;

/**
 * Static application identity/version info.
 *
 * Kept as one small holder class so screens like Settings don't hardcode
 * this in multiple places; update it here when the pom.xml version changes.
 */
public final class AppInfo {

    public static final String NAME = "StegoVault";

    public static final String VERSION = "1.0-SNAPSHOT";

    public static final String DESCRIPTION =
            "Offline PNG steganography with AES-GCM encrypted, "
                    + "integrity-checked payloads.";

    private AppInfo() {
        // Utility class
    }
}
