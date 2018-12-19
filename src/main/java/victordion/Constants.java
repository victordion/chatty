package victordion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Constants {
    public static int port = 6789;
    public static int byteBufferCapacity = 8;


    static void printByteBuffer(String note, ByteBuffer byteBuffer) {
        System.out.printf(note + " in hex: 0x");
        for (byte b : byteBuffer.array()) {
            System.out.printf("%02x", b);
        }
        System.out.println();
    }

    static void readToLength(InputStream is, ByteBuffer byteBuffer, int length) throws Exception {
        byteBuffer.clear();
        int alreadyRead = 0;
        while (alreadyRead < length) {
            int newlyRead = is.read(byteBuffer.array(), alreadyRead, length - alreadyRead);
            if (newlyRead == -1) {
                throw new IOException("Other side close the connection");
            }
            alreadyRead += newlyRead;
        }
    }
}
