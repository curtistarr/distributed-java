package question1;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ConcurrentServer extends Thread {

    private static final String SORT_MESSAGE = "sort";
    private static final String END_MESSAGE = ".";
    private static final int PORT = 7000;

    private StreamSocket socket;

    private ConcurrentServer(StreamSocket socket) {
        System.out.println("New client.");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            List<Integer> ints = new ArrayList<>();
            System.out.println("connection accepted");
            boolean running = true;
            while (running) {
                String message = socket.receiveMessage();
                System.out.println("message received: " + message);

                switch ((message.trim().toLowerCase())) {
                    case END_MESSAGE:
                        System.out.println("Session over.");
                        socket.close();
                        running = false;
                        break;
                    case SORT_MESSAGE:
                        Collections.sort(ints);
                        socket.sendMessage("Numbers sorted: " + ints.toString());
                        break;
                    default:
                        try {
                            int result = Integer.parseInt(message);
                            ints.add(result);
                            socket.sendMessage("Current numbers: " + ints.toString());
                        } catch (NumberFormatException ex) {
                            socket.sendMessage("Not an int or command");
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String args[]) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Conccurent server ready.");
            while (true) {
                System.out.println("Waiting");
                StreamSocket dataSocket = new StreamSocket(server.accept());
                System.out.println("Accepted from " + dataSocket.getInetAddress());
                ConcurrentServer clientHandler = new ConcurrentServer(dataSocket);
                clientHandler.start();
            }
        } catch (IOException ex) {
            System.err.println("Unable to connect on specified port");
        }
    }
}
