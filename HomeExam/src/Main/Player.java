package Main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Player
{
    private int PLAYER_ID;
    private boolean onlineBool;
    private boolean botBool;
    private Socket connection;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private ArrayList<String> hand;
    private ArrayList<String> wonScenarioCards = new ArrayList<String>();
    private View view;

    public Player(int PLAYER_ID, ArrayList<String> hand, boolean botBool, View view)
    {
        this.PLAYER_ID = PLAYER_ID;
        this.hand = hand;
        this.botBool = botBool;
        this.onlineBool = false;
        this.view = view;
    }

    public Player(int PLAYER_ID, boolean botBool, Socket connection, BufferedReader inFromClient, DataOutputStream outToClient, View view)
    {
        this.PLAYER_ID = PLAYER_ID;
        this.botBool = botBool;
        this.onlineBool = true;
        this.connection = connection;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.view = view;
    }

    /**
     * Checks if the player is a bot, online player or a host to determine which play card method to call
     */
    public void play()
    {
        if (botBool)
        {
            playRandomBotCard();
        }
        else if (onlineBool)
        {
            playOnlineCard();
        }
        else
        {
            playCardFromServer();
        }
    }

    /**
     *Checks if the player is a bot, online player or a host to determine which judge method to call
     * @return returns a string of the winning card.
     */
    public PlayedApple judge()
    {
        if (botBool)
        {
            return botJudge();
        }
        else if (onlineBool)
        {
            int playedCardIndex = 0;
            try
            {
                playedCardIndex = Integer.parseInt(inFromClient.readLine());
            }
            catch (Exception e) { }
            return Server.getPlayedCard().get(playedCardIndex);
        }
        else
        {
            return serverJudge();
        }
    }

    /**
     * Reads the user input
     * @return The users input as int.
     * @throws Exception
     */
    private int getServerPlayerInput() throws Exception
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input = bufferedReader.readLine();
        return Integer.parseInt(input);
    }

    /**
     * When a player plays a card this method creates a PlayedApple object and removes the played card from the players hand.
     * @param playerID which player plays the card, needed to create a PlayedApple
     * @param cardString The string of the played card is needed to create a PlayedApple
     * @param cardChoice the index of the card that the player choose so it can be removed from the hand.
     */
    private void playCard(int playerID, String cardString, int cardChoice)
    {
        Server.getPlayedCard().add(new PlayedApple(playerID, cardString));
        hand.remove(cardChoice);
    }

    /**
     * Reads line from client to get the played card as a string and create an object of Played Apple
     */
    private void playOnlineCard(){
        try
        {
            String aPlayedCard = inFromClient.readLine();
            Server.getPlayedCard().add(new PlayedApple(PLAYER_ID, aPlayedCard));
        }
        catch (Exception e) {}
    }

    /**
     * Plays a random card from the bots hand when the bot has to play
     */
    private void playRandomBotCard()
    {
        /** BUG - FIX LATER
         * For some reason I must sleep a random amount of time
         * or the playedApple ArrayList won't get all bot answers
         * (The teacher knows what the bug is, but thought this was fun to do :-)   )
         **/
        Random random = ThreadLocalRandom.current();
        try
        {
            Thread.sleep(random.nextInt(500));
        }
        catch(Exception e){}
        // continue with non-buggy code
        Random randomCard = new Random();
        int cardChoice = randomCard.nextInt(hand.size()-1);
        playCard(PLAYER_ID, hand.get(cardChoice), cardChoice);
    }

    /**
     * Prints out the hand for the host and then checks for the input of the chosen card.
     */
    private void playCardFromServer()
    {
        printHand();
        System.out.println("");
        int cardChoice = 0;
        try
        {
            cardChoice = getServerPlayerInput();
            if (cardChoice >=    hand.size() || cardChoice < 0)
            {
               throw new NumberFormatException();
            }
            playCard(PLAYER_ID, hand.get(cardChoice), cardChoice);
            view.printWait();
        }
        catch (NumberFormatException e)
        {
            view.printInputError();
            play();
        }
        catch (Exception e) {}
    }

    /**
     * Adds a card to a players hand. If it's an online player it sends a string with the card to the clinet instead of just adding it directly.
     * @param scenarioCard the card that should be added to hand.
     */
    public void addCard(String scenarioCard)
    {
        if(botBool || !onlineBool)
        {
            hand.add(scenarioCard);
        }
        else
        {
            try
            {
                outToClient.writeBytes(scenarioCard + "\n");
            }
            catch (Exception e){}
        }
    }

    /**
     * Prints the players hand
     */
    private void printHand()
    {
        view.printPlayerCards(hand);
    }

    /**
     *  Gets the input when the server player is judge, prints all the played cards then
     *  reads input from the terminal.
     *  BUG! Needs input error handling, crashes at wrong input
     * @return the judges choice of the played cards.
     */
    private PlayedApple serverJudge(){
        view.printJudgesCard();
        int cardChoice = 0;
        try {
            cardChoice = getServerPlayerInput();
        }
        catch (NumberFormatException e)
        {
            view.printInputError();
            judge();
        }
        catch (Exception e) {}
        return Server.getPlayedCard().get(cardChoice);
    }

    /**
     * Randomizes the bot judging
     * @return a random card to win the round of all the played ones during the round.
     */
    private PlayedApple botJudge()
    {
        Random random = new Random();
        return Server.getPlayedCard().get(random.nextInt(Server.getPlayedCard().size()-1));
    }

    /**
     * Adds a won scenario card to the player
     * @param wonScenarioCard
     */
    public void addWonScenarioCard(String wonScenarioCard){
        wonScenarioCards.add(wonScenarioCard);
    }


    /**
     * Getters
     */
    public int getPLAYER_ID() {
        return PLAYER_ID;
    }

    public boolean isOnlineBool() {
        return onlineBool;
    }

    public boolean isBotBool() {
        return botBool;
    }

    public ArrayList<String> getHand() {
        return hand;
    }

    public ArrayList<String> getWonScenarioCards() {
        return wonScenarioCards;
    }

    public BufferedReader getInFromClient() {
        return inFromClient;
    }

    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    public Socket getConnection() {
        return connection;
    }

}


