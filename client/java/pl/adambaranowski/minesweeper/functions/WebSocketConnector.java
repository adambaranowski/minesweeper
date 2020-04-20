package pl.adambaranowski.minesweeper.functions;

import java.io.*;
import java.net.Socket;

public class WebSocketConnector {

    private static WebSocketConnector INSTANCE;
    private String serverAddress;
    private int socketPort;
    private Socket socket;
    private DataInputStream din;
    private BufferedOutputStream dout;
    private BufferedReader bufferedReader;


    private WebSocketConnector(String serverAddress, int socketPort) throws IOException {
        this.serverAddress = serverAddress;
        this.socketPort = socketPort;
        socket = new Socket(serverAddress, socketPort);
        dout = new BufferedOutputStream(socket.getOutputStream());
        din = new DataInputStream(socket.getInputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(din));

    }


    public String dataTransfer(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            dout.write(message.getBytes("UTF-8"));
            dout.flush();

        } catch (IOException e) {
            return "Failed to send message";
        }
        try {
            char c = (char) bufferedReader.read();
            while (c != '~') {
                stringBuilder.append(c);
                c = (char) bufferedReader.read();
            }

        } catch (Exception e) {
            return "Failed to receive response";
        }
        return stringBuilder.toString();

    }

    public static WebSocketConnector createInstance(String serverAddress, int socketPort) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new WebSocketConnector(serverAddress, socketPort);
        }
        return INSTANCE;
    }

    public static WebSocketConnector getInstance() throws IOException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        return createInstance("172.104.229.108", 1337);
    }

    public void disconnect() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
            INSTANCE = null;
            System.out.println("DISCONNECTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
