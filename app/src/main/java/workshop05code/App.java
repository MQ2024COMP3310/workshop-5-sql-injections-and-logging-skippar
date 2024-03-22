package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            
            logger.log(Level.INFO, "The wordle database is created and connected.");
        } else {

            logger.log(Level.INFO, "The wordle database was unable to connect.");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {

            logger.log(Level.INFO, "The wordletable structures created");
        } else {

            logger.log(Level.INFO, "Unable to create the wordletable strcutures");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.INFO, "valid word: {0}", line);
                // input validation for adding words 
                if(line.matches("[a-z]{4}")) { //only add valid ones and ignore if not
                    wordleDatabaseConnection.addValidWord(i, line);
                } else { //log invalid word in data.txt 
                    logger.log(Level.SEVERE, "invalid word: {0}, could not be added", line);
                }
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to laod data.txt file", e);
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();



            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");

                if (wordleDatabaseConnection.isValidWord(guess) && guess.matches("[a-z]{4}")) { 
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    // check and tell user if word was valid or not 
                    if(!guess.matches("[a-z]{4}")) {
                        System.out.println("This guess was NOT valid. Enter a 4 letter word!");
                        logger.log(Level.INFO, "Invalid guess: {0}", guess);
                    } else {
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}