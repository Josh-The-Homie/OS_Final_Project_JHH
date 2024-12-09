import java.io.*;
import java.net.*;
import java.util.*;

public class RockPaperScissorsServerNoMutex {
    private static final Map<String, Integer> highScores = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server started. Waiting for clients...");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("Welcome to Rock-Paper-Scissors! Enter your name:");
                String playerName = in.readLine();

                // Initialize player score without synchronization
                highScores.putIfAbsent(playerName, 0);

                while (true) {
                    out.println("Enter your choice (rock, paper, scissors) or 'exit' to quit:");
                    String playerChoice = in.readLine();
                    if (playerChoice == null || playerChoice.equalsIgnoreCase("exit")) {
                        // Display high scores on exit
                        out.println("Goodbye! Here are the high scores:");
                        highScores.forEach((name, score) -> out.println(name + ": " + score));
                        break;
                    }

                    String[] choices = {"rock", "paper", "scissors"};
                    String serverChoice = choices[new Random().nextInt(3)];
                    out.println("Server chose: " + serverChoice);

                    String result = determineWinner(playerChoice.toLowerCase(), serverChoice);
                    out.println(result);

                    if (result.contains("You win")) {
                        // Update player score without synchronization
                        highScores.put(playerName, highScores.get(playerName) + 1);
                    }

                    out.println("Your score: " + highScores.get(playerName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String determineWinner(String playerChoice, String serverChoice) {
            if (playerChoice.equals(serverChoice)) {
                return "It's a draw!";
            } else if ((playerChoice.equals("rock") && serverChoice.equals("scissors")) ||
                       (playerChoice.equals("paper") && serverChoice.equals("rock")) ||
                       (playerChoice.equals("scissors") && serverChoice.equals("paper"))) {
                return "You win!";
            } else {
                return "You lose!";
            }
        }
    }
}
