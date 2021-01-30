package Main;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeckFactoryTest
{
    DeckFactory deckFactory;
    DeckFactory checkRandom;

    /**
     * Test by checking the first value of the list. This test can still fail since there is a chance that the first card gets shuffled into the first position.
     * @throws Exception
     */
    @Test
    public void deckShufflerTest() throws Exception
    {
        deckFactory = new DeckFactory();
        deckFactory.deckShuffler();
        assertNotEquals(DeckFactory.getScenarioCards().get(0), "Why can't I sleep at night?");
        assertNotEquals(DeckFactory.getPlayerCards().get(0), "Coat hanger abortions.");
    }


    /**
     * Tests by checking the size of the lists.
     * @throws Exception
     */
    @Test
    public void getScenarioCardsTest() throws Exception
    {
        deckFactory = new DeckFactory();
        assertEquals(78, deckFactory.getScenarioCards().size());
    }

    @Test
    public void getPlayerCardsTest() throws Exception
    {
        deckFactory = new DeckFactory();
        assertEquals(460, deckFactory.getPlayerCards().size());
    }

}
