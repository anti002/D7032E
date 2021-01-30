package Main;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;



public class DeckFactory
{
    private static ArrayList<String> playerCards;
    private static ArrayList<String> scenarioCards;


    /**
     * Creates and shuffles the two decks needed for the game.
     * @throws Exception
     */
    public DeckFactory() throws Exception
    {
        playerCards = new ArrayList<String>(Files.readAllLines(Paths.get("C:\\Users\\Anton\\Desktop\\HomeExam\\src\\Main\\Cards\\","playerCards.txt"), StandardCharsets.ISO_8859_1));
        scenarioCards = new ArrayList<String>(Files.readAllLines(Paths.get("C:\\Users\\Anton\\Desktop\\HomeExam\\src\\Main\\Cards\\","scenarioCards.txt"), StandardCharsets.ISO_8859_1));
    }

    public void deckShuffler()
    {
        Collections.shuffle(playerCards);
        Collections.shuffle(scenarioCards);
    }

    /*
     *Getters
     */
    public static String getTopPlayerCard()
    {
        return playerCards.remove(0);
    }

    public static ArrayList<String> getPlayerCards()
    {
        return playerCards;
    }

    public static String getTopScenarioCard()
    {
        return scenarioCards.remove(0);
    }

    public static ArrayList<String> getScenarioCards()
    {
        return scenarioCards;
    }

}
