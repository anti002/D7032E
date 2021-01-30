package Main;

import java.util.ArrayList;

public class View
{
    /*
    The standard new round print, differs for judge and regular player.
    */
    public void writeNewRound(boolean judeBool){
        System.out.println("*****************************************************");
        if(judeBool)
        {
            System.out.println("**                 NEW ROUND - JUDGE               **");
        }
        else
        {
            System.out.println("**                    NEW ROUND                    **");
        }
        System.out.println("*****************************************************");
    }

    /*
    Print for the cards in the players hand.
     */
    public void printPlayerCards(ArrayList hand)
    {
        System.out.println("Choose a white card to play");
        for(int i=0; i < hand.size(); i++)
        {
            System.out.println("["+i+"]   " + hand.get(i));
        }
        System.out.println("");
    }

    /*
    Print for a wait message
     */
    public void printWait()
    {
        System.out.println("Waiting for other players\n");
    }

    /*
    Print for a message for invalid input.
     */
    public void printInputError()
    {
        System.out.println("Invalid input");

    }

    /*
    Print for an error message when something goes wrong.
     */
    public void printErrorStart(){
        System.out.println("Something went wrong");
    }

    /*
    Print for the connection message.
     */
    public void printConnection(int onlineClient){
        System.out.println("Connected to Player ID: " + (onlineClient));
    }

    /*
    Print for the played ScenarioCard.
     */
    public void printPlayedScenarioCards(String playedScenarioCards)
    {
        System.out.println(playedScenarioCards);
    }

    /*
    Print for a scenario card (black card since it is cards against humanity).
     */
    public void printScenarioCard(String playedScenarioCard){
        System.out.println("Black card: " + playedScenarioCard + "\n");
    }

    /*
    Print for the cards the judge can pick as a winner for the round.
     */
    public void printJudgesCard()
    {
        System.out.println("Choose which white card wins\n");
    }

    /*
    Print for the winning card.
     */
    public void printWinningCard(String winningPlayerCard)
    {
        System.out.println(winningPlayerCard + "\n");
    }

    /*
    Print for the player that wins.
     */
    public void printWinner(String winnerString){
        System.out.println(winnerString + "\n");
    }

    /*
    Print for when a game is finished.
     */
    public void printFinishedOnlineGame(String finishedGame){
        System.out.println("\n"+finishedGame);
    }

}
