package Main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class OnlineClient
{
    private Socket aSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private ArrayList<String> hand;
    private View view;

    public OnlineClient(String ipAddress, View view) throws Exception
    {
        this.view = view;
        aSocket = new Socket(ipAddress, 2048);
        outToServer = new DataOutputStream(aSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
        getPlayerCardsFromServer();
        gameLoop();
    }


    /**
     * First it reads from the server to check if a player is the judge or not
     * Then it checks if that same string starts with "FINISHED", because that means that the game is over.
     * Then it calls for the prints to print a new round.
     * Then prints out the scenario card and the player cards (the hand) and let the player and judge play.
     * Then add a card to the players hand since the player just got rid of one. The judge do not get an extra card
     * @throws Exception
     */
    private void gameLoop() throws Exception
    {
        while(true) {
            String judgeString = readFromServer();
            boolean judgeBool = checkJudge(judgeString);
            if(judgeString.startsWith("FINISHED")) {
                view.printFinishedOnlineGame(judgeString);
                break;
            }
            view.writeNewRound(judgeBool);
            view.printScenarioCard(readFromServer());

            if(!judgeBool) {
                playScenarioCard(outToServer, hand);
            } else {
                judge(outToServer);
            }

            printRoundInfo();
            if(!judgeBool) {
                hand.add(readFromServer());
            }
        }
    }

    private String readFromServer() throws Exception{
        return inFromServer.readLine();
    }

    /**
     * Prints out all the played card for the completed turn and prints out the winning card the player that played it
     * @throws Exception
     */
    private void printRoundInfo() throws Exception{
        String playedCard = (readFromServer()).replaceAll("#", "\n");
        view.printPlayedScenarioCards(playedCard);
        String winningScenarioCard = readFromServer();
        view.printWinningCard(winningScenarioCard);
    }

    /**
     * Prints the cards that was played for the judge and then judge picks which one is the winner of the round and sends the input to the server
     *  BUG! Needs input error handling, crashes at wrong input
     * @param outToServer
     * @throws Exception
     */
    private void judge(DataOutputStream outToServer) throws Exception{
        view.printJudgesCard();
        outToServer.writeBytes(getInput()+"\n");
    }

    /**
     * Check if the player is a judge or not for the round
     * @param judgeString the string given from the server is either "JUDGE" or "NOTJUDGE"
     * @return checks if the player is judge or not based on the server output
     * @throws Exception
     */
    private boolean checkJudge(String judgeString) throws Exception{
        return (judgeString.compareTo("JUDGE")==0);
    }

    /**
     * Retrieves the players hand from the server and makes an ArrayList and sets it as the players hand
     * to that list.
     * @throws Exception
     */
    private void getPlayerCardsFromServer() throws Exception {
        String[] cardString = (readFromServer()).split(";");
        hand = new ArrayList<String>(Arrays.asList(cardString));
    }

    /**
     * Prints out the players hand and lets the player pick a card, then the input is sent to the server and the picked card gets removed from the hand.
     * @param outToServer The players DataOutPutSteam
     * @param hand  The players hand
     * @throws Exception
     */
    private void playScenarioCard(DataOutputStream outToServer, ArrayList hand) throws Exception{
        view.printPlayerCards(hand);
        try
        {
            int cardChoice = getInput();
            if (cardChoice >= hand.size() || cardChoice < 0)
            {
                throw new NumberFormatException();
            }
            outToServer.writeBytes(hand.get(cardChoice)+"\n");
            hand.remove(cardChoice);
            view.printWait();
        }
        catch (NumberFormatException e)
        {
            view.printInputError();
            playScenarioCard(outToServer, hand);
        }
    }

    /**
     * Getter
     * @return input as int
     * @throws Exception
     */
    private int getInput() throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        return Integer.parseInt(bufferedReader.readLine());
    }
}
