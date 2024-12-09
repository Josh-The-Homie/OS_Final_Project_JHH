import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * The {@code RockPaperScissorsServer} class represents a server that handles multiple
 * clients in a game of Rock, Paper, Scissors. The server listens for incoming client connections
 * on port 12345 and starts a new thread for each connected client. The server maintains
 * high scores for each player and determines the winner of each game round.
 *
 * Upon a client's connection, the server will prompt the user to enter their name,
 * followed by their choice for the game. The server randomly selects a choice (rock, paper, or scissors)
 * and compares it with the player's choice to determine the outcome. The client can exit the game,
 * at which point the server will display the high scores for all players.
 *
 */
public class RockPaperScissorsServer {

    /**
     * A map that stores high scores for players. The keys are player names, and the values
     * are the respective scores.
     */
    private static final Map<String, Integer> highScores = new HashMap<>();

    /**
     * A lock object to ensure thread-safe access to the {@link #highScores} map.
     */
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * The main method that starts the server, listens for incoming client connections,
     * and handles each client in a separate thread.
     *
     * @param args the command-line arguments (not used)
     * @throws IOException if an I/O error occurs when starting the server or accepting client connections
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server started. Waiting for clients...");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    /**
     * The {@code ClientHandler} class handles communication with a single client. It is executed
     * in a separate thread for each client. It receives the player's name and game choices, sends
     * the server's responses, and updates the player's score.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        /**
         * Constructs a {@code ClientHandler} with the given socket.
         *
         * @param socket the socket connected to the client
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Runs the client handler logic. This includes reading player input, determining the game result,
         * sending responses, and updating scores. The game continues in a loop until the player exits.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("Welcome to Rock-Paper-Scissors! Enter your name:");
                String playerName = in.readLine();

                // Initialize player score
                lock.lock();
                highScores.putIfAbsent(playerName, 0);
                lock.unlock();

                while (true) {
                    out.println("Enter your choice (rock, paper, scissors) or 'exit' to quit:");
                    String playerChoice = in.readLine();
                    if (playerChoice == null || playerChoice.equalsIgnoreCase("exit")) {
                        // Display high scores on exit
                        out.println("Goodbye! Here are the high scores:");
                        lock.lock();
                        highScores.forEach((name, score) -> out.println(name + ": " + score));
                        lock.unlock();
                        break;
                    }

                    String[] choices = {"rock", "paper", "scissors"};
                    String serverChoice = choices[new Random().nextInt(3)];
                    out.println("Server chose: " + serverChoice);

                    String result = determineWinner(playerChoice.toLowerCase(), serverChoice);
                    out.println(result);

                    if (result.contains("You win")) {
                        lock.lock();
                        highScores.put(playerName, highScores.get(playerName) + 1);
                        lock.unlock();
                    }

                    lock.lock();
                    out.println("Your score: " + highScores.get(playerName));
                    lock.unlock();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Determines the winner of a round of Rock, Paper, Scissors.
         *
         * @param playerChoice the player's choice (rock, paper, or scissors)
         * @param serverChoice the server's choice (rock, paper, or scissors)
         * @return a string indicating the result of the game ("You win", "You lose", or "It's a draw")
         */
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
