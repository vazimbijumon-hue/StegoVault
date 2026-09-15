package stegovault.core;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayloadHeaderTest {
    @Test
    void roundTripsPayloadMetadata() {
        byte[] payload = "private message".getBytes(StandardCharsets.UTF_8);
        PayloadHeader original = PayloadHeader.forPayload(0b0000_0011, payload);

        PayloadHeader restored = PayloadHeader.deserialize(original.serialize());

        assertEquals(3, restored.flags());
        assertEquals(payload.length, restored.payloadLength());
        assertArrayEquals(original.payloadChecksum(), restored.payloadChecksum());
        assertTrue(restored.matchesPayload(payload));
    }

    @Test
    void detectsCorruptedPayload() {
        byte[] payload = "private message".getBytes(StandardCharsets.UTF_8);
        PayloadHeader header = PayloadHeader.forPayload(0, payload);

        payload[0] ^= 1;

        assertFalse(header.matchesPayload(payload));
    }

    @Test
    void rejectsInvalidMagic() {
        byte[] bytes = PayloadHeader.forPayload(0, new byte[0]).serialize();
        bytes[0] = 'X';

        assertThrows(IllegalArgumentException.class, () -> PayloadHeader.deserialize(bytes));
    }

    @Test
    void rejectsNegativePayloadLength() {
        byte[] bytes = PayloadHeader.forPayload(0, new byte[0]).serialize();
        bytes[6] = (byte) 0xFF;
        bytes[7] = (byte) 0xFF;
        bytes[8] = (byte) 0xFF;
        bytes[9] = (byte) 0xFF;

        assertThrows(IllegalArgumentException.class, () -> PayloadHeader.deserialize(bytes));
    }
}
