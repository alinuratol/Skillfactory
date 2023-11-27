import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SimpleChatClient {

    public static void main(String[] args) {
        SimpleChatClient client = new SimpleChatClient();
        client.startClient("localhost", 5000);
    }

    public void startClient(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server");

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            Thread inputThread = new Thread(new InputHandler(socket));
            inputThread.start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                writer.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class InputHandler implements Runnable {
        private Socket socket;
        private Scanner input;

        public InputHandler(Socket socket) {
            this.socket = socket;
            try {
                this.input = new Scanner(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (input.hasNextLine()) {
                        String message = input.nextLine();
                        System.out.println("Received message: " + message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
