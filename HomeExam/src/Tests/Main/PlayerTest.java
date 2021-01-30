package Main;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PlayerTest
{
    Player player;

    /**
     * Tests that a player that wins keeps the scenario card. This test is built in the same way as how a player receives a won scenario.
     */
    @Test
    public void getGreenApples()
    {
        player = new Player(1, new ArrayList(), false, new View());
        assertEquals(player.getWonScenarioCards().size(), 0);
        player.addWonScenarioCard("First won scenario");
        player.addWonScenarioCard("Second won scenario");
        assertEquals(player.getWonScenarioCards().size(), 2);
        assertEquals(player.getWonScenarioCards().get(0), "First won scenario");
        assertEquals(player.getWonScenarioCards().get(1), "Second won scenario");

    }
}