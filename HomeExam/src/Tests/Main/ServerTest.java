package Main;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerTest
{
    Server server;
    int firstJudge;
    int secondJudge;

    @Before
    public void setServer() throws Exception
    {
        server = new Server(0, new View(), true);
        firstJudge = server.setJudge();
        secondJudge = server.setJudge();
    }

    /**
     * The tests had to be running in the same method since it will be on the same port which will crash the program.
     */
    @Test
    public void serverTests() throws Exception
    {
        setJudge();
        getWinCondition();
        playerHandSize();
    }

    /**
     * There's a chance that the same random value can be received from the judge. Thus the test can fail.
     */
    public void setJudge() throws Exception
    {
        assertNotEquals(firstJudge, secondJudge);
    }

    public void getWinCondition() throws Exception
    {
        assertEquals(8, server.getPointsToWin());
    }

    public void playerHandSize() throws Exception
    {
        for(int i = 0; i < server.getPlayers().size(); i++)
        {
            assertEquals(server.getCardsInHand(), server.getPlayers().get(i).getHand().size());
        }
    }
}