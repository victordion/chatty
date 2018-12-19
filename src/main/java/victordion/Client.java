package victordion;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static victordion.Constants.byteBufferCapacity;
import static victordion.Constants.printByteBuffer;
import static victordion.Constants.readToLength;

public class Client {

    static Duration timeout = Duration.ofSeconds(5);
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String argv[]) throws Exception {

        String op = argv[0];
        System.out.println("Operator: " + op);

        Double a = Double.parseDouble(argv[1]);
        System.out.println("First operand: " + a.toString());

        Double b = Double.parseDouble(argv[2]);
        System.out.println("Second operand: " + b.toString());

        Socket clientSocket = new Socket("localhost", Constants.port);
        OutputStream os = clientSocket.getOutputStream();
        InputStream is = clientSocket.getInputStream();

        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferCapacity);

        byteBuffer.put(op.getBytes(), 0, Math.min(byteBufferCapacity, op.getBytes().length));
        printByteBuffer("Operation byte buffer", byteBuffer);
        os.write(byteBuffer.array());

        byteBuffer.clear();
        byteBuffer.putDouble(a);
        printByteBuffer("First operand", byteBuffer);
        os.write(byteBuffer.array());

        byteBuffer.clear();
        byteBuffer.putDouble(b);
        printByteBuffer("Second operand", byteBuffer);
        os.write(byteBuffer.array());

        os.flush();
        System.out.println("All data is sent");

        byteBuffer.clear();

        Future<?> f = executor.submit(() -> {
            try {
                readToLength(is, byteBuffer, byteBufferCapacity);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception thrown while reading from connection");
            }
        });

        try {
            f.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            printByteBuffer("Result", byteBuffer);
            Double ret = byteBuffer.getDouble();
            System.out.println("Result is: " + ret.toString());
        } catch (TimeoutException e) {
            System.out.println("Timeout reading back a response");
        }

        System.out.println("Closing connections");
        os.close();
        System.out.println("Closed os");
        is.close();
        System.out.println("Closed is");
        clientSocket.close();
        System.out.println("Closed socket");

        executor.shutdown();
    }
}
