# StegoVault

StegoVault is an offline-first Java desktop application for securely hiding and extracting data within images using steganography.

The project combines image-based steganography with encryption and payload integrity checks to provide a privacy-focused way of storing hidden information locally.

## Features

- **Image Steganography**
  - Hide data inside images using Least Significant Bit (LSB) steganography.
  - Extract hidden payloads from supported images.
  - Calculate available image capacity before embedding data.

- **Encryption**
  - AES-based encryption for protected payloads.
  - Password-based key derivation using PBKDF2 with HMAC-SHA256.
  - Random salts and cryptographic keys are used where applicable.

- **Privacy-Focused Design**
  - Designed to operate offline by default.
  - No account or cloud service is required.
  - Sensitive session keys are maintained in memory rather than stored permanently.

- **Payload Integrity**
  - Structured payload headers and metadata.
  - Integrity verification to detect corrupted or invalid hidden data.

- **Image Analysis**
  - Analyze image properties such as dimensions, format, transparency, and pixel count.
  - Validate images before processing.
  - Clean image metadata during the hiding process.

- **Desktop Interface**
  - Built with JavaFX.
  - Dedicated interfaces for hiding data, extracting data, image analysis, and settings.

## Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Application development |
| JavaFX 21 | Desktop user interface |
| Maven | Build and dependency management |
| AES-GCM | Payload encryption |
| PBKDF2-HMAC-SHA256 | Password-based key derivation |
| LSB Steganography | Data embedding and extraction |

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── stegovault/
│   │       ├── Analysis/
│   │       ├── core/
│   │       ├── exception/
│   │       ├── models/
│   │       ├── steganography/
│   │       ├── ui/
│   │       ├── util/
│   │       └── views/
│   └── resources/
│       ├── images/
│       └── styles/
└── test/