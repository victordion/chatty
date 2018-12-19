package victordion;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import static victordion.Constants.byteBufferCapacity;
import static victordion.Constants.printByteBuffer;
import static victordion.Constants.readToLength;

public class Server {

    static public void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(Constants.port);
        System.out.println("Start the server...");

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();

            System.out.println("Received connection...");

            InputStream is = connectionSocket.getInputStream();
            OutputStream os = connectionSocket.getOutputStream();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferCapacity);

            readToLength(is, byteBuffer, byteBufferCapacity);
            printByteBuffer("Operator", byteBuffer);
            String operator = new String(byteBuffer.array()).trim();
            System.out.println(operator);

            if(operator.equals("shutdown")) {
                System.out.println("Received shutdown");
                is.close();
                os.close();
                connectionSocket.close();
                return;
            }

            if (!operator.equals("add") && !operator.equals("sub") && !operator.equals("mul") && !operator.equals("div")) {
                System.out.println("Unsupported Operation");
                is.close();
                os.close();
                connectionSocket.close();
                continue;
            }

            System.out.println("Received operation: " + operator);

            readToLength(is, byteBuffer, byteBufferCapacity);
            printByteBuffer("First", byteBuffer);
            Double a = byteBuffer.getDouble();
            System.out.println("First is: " + a.toString());

            readToLength(is, byteBuffer, byteBufferCapacity);
            printByteBuffer("Second", byteBuffer);
            Double b = byteBuffer.getDouble();
            System.out.println("Second is: " + b.toString());

            Double ret;
            switch (operator) {
                case "add":
                    ret = a + b;
                    break;
                case "sub":
                    ret = a - b;
                    break;
                case "mul":
                    ret = a * b;
                    break;
                case "div":
                    ret = a / b;
                    break;
                default:
                    ret = a + b;
                    break;
            }
            System.out.println("Result is: " + ret.toString());

            os.write(ByteBuffer.allocate(byteBufferCapacity).putDouble(ret).array());
            printByteBuffer("Result", byteBuffer);
            os.flush();

            System.out.println("Response is sent");
        }
    }
}
