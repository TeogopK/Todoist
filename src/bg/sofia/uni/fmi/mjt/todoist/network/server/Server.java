package bg.sofia.uni.fmi.mjt.todoist.network.server;

import bg.sofia.uni.fmi.mjt.todoist.database.Database;
import bg.sofia.uni.fmi.mjt.todoist.menu.collaboration.CollaborationExecutor;
import bg.sofia.uni.fmi.mjt.todoist.menu.command.Command;
import bg.sofia.uni.fmi.mjt.todoist.menu.executor.literals.CommandExecutorResponses;
import bg.sofia.uni.fmi.mjt.todoist.menu.main.MainMenu;
import bg.sofia.uni.fmi.mjt.todoist.menu.parsers.StringToCommandParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private static final int PORT = 7777;

    private static final String LOG_FILENAME = "log.txt";

    private final PrintStream printStream = new PrintStream((LOG_FILENAME));


    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    private final Map<SocketChannel, String> clientsMap;

    private Database database;

    private MainMenu mainMenu;

    private CollaborationExecutor collaborationExecutor;

    public Server() throws FileNotFoundException {
        database = new Database();
        mainMenu = new MainMenu(database);
        clientsMap = new HashMap<>();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();

                            String clientInput = null;

                            try {
                                clientInput = getClientInput(clientChannel);
                            } catch (IOException e) {
                                e.printStackTrace(printStream);
                                e.printStackTrace();

                                clientsMap.remove(clientChannel);
                                clientChannel.close();
                            }

                            if (clientInput == null) {
                                System.out.println("Disconnected");
                                continue;
                            }
                            System.out.println(clientInput);

                            try {
                                String output;
                                Command command = StringToCommandParser.getCommand(clientInput);


                                if (clientsMap.get(clientChannel) != null) {

                                    CollaborationExecutor collaborationExecutor1 =
                                        new CollaborationExecutor(database.getAccount(clientsMap.get(clientChannel)),
                                            database);
                                    output = collaborationExecutor1.execute(command);

                                } else {
                                    mainMenu.setLoggedInUserNull();
                                    output = mainMenu.loadMainMenu(command);
                                    clientsMap.put(clientChannel, mainMenu.getLoggedInUser());
                                }

                                if (output.equals(CommandExecutorResponses.LOG_OUT.getDescription())) {
                                    mainMenu.setLoggedInUserNull();
                                    clientsMap.put(clientChannel, null);
                                }

                                writeClientOutput(clientChannel, output);

                            } catch (Exception e) {
                                e.printStackTrace(printStream);
                            }

                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                    e.printStackTrace(printStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(printStream);
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientsMap.remove(clientChannel);
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Server server = new Server();

        server.start();
    }
}