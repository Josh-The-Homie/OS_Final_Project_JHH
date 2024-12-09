import java.io.*;
import java.net.*;

/**
 * The RockPaperScissorsClient class represents a client that connects to a
 * server for a game of Rock, Paper, Scissors. The client interacts with the user,
 * sending and receiving game data to and from the server, including choices, results,
 * and high scores. The game continues until the user chooses to exit.
 * 
 * This class connects to a server running on the local machine at port 12345.
 * It sends user input (name and game choices) to the server and displays the responses
 * (such as prompts, server choices, results, and high scores).
 * 
 * <p>Example usage:</p>
 * <pre>
 *   java RockPaperScissorsClient
 * </pre>
 */
public class RockPaperScissorsClient {

    /**
     * The main method that runs the client. It connects to the server, handles communication
     * with the user, and processes the game logic. The client reads messages from the server
     * and sends user input (name, choice) back to the server. The game loop continues until
     * the user chooses to exit.
     *
     * @param args the command-line arguments (not used)
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Read and print the welcome message from the server
            System.out.println(in.readLine());

            // Read and send the user's name to the server
            String name = userInput.readLine();
            out.println(name);

            // Main game loop
            while (true) {
                // Prompt the user for their choice
                System.out.println(in.readLine());
                String choice = userInput.readLine();
                out.println(choice);

                // Exit condition
                if (choice.equalsIgnoreCase("exit")) {
                    // Print the server's goodbye message and high scores
                    System.out.println(in.readLine());
                    String highScoreLine;
                    while ((highScoreLine = in.readLine()) != null) {
                        System.out.println(highScoreLine);
                    }
                    break;
                }

                // Print the server's choice, the result, and the current score
                System.out.println(in.readLine()); // Server's choice
                System.out.println(in.readLine()); // Result of the game
                System.out.println(in.readLine()); // Current score
            }
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }
}
