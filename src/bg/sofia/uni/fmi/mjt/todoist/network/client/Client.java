package bg.sofia.uni.fmi.mjt.todoist.network.client;

import bg.sofia.uni.fmi.mjt.todoist.menu.main.literals.MainMenuResponses;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;

    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private static final String COMMAND_STARTER = "> ";

    private static final String CONNECTED_TO_SERVER_MESSAGE = "Connected to the server.";

    public static void startClient() {
        try (SocketChannel socketChannel = SocketChannel.open(); Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println(CONNECTED_TO_SERVER_MESSAGE);

            boolean mustStop = false;

            while (!mustStop) {
                System.out.print(COMMAND_STARTER);
                String message = scanner.nextLine();

                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8);

                if (reply.equals(MainMenuResponses.QUITING.getDescription())) {
                    mustStop = true;
                }

                System.out.println(reply);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    public static void main(String[] args) {
        startClient();
    }
}
