package Main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Server
{
    private static ArrayList<PlayedApple> playedCard = new ArrayList<PlayedApple>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private View view;
    private Random random;
    private DeckFactory deckFactory = new DeckFactory();

    private static final int CARDS_IN_HAND = 7;
    private static final int MINIMAL_PLAYER_AMOUNT = 3;
    private static int POINTS_TO_WIN;


    public Server(int numberOfOnlinePlayers, View view, boolean testBool) throws Exception
    {
        this.view = view;
        deckFactory.deckShuffler();
        addOnlinePlayers(numberOfOnlinePlayers, view);

        if(numberOfOnlinePlayers < MINIMAL_PLAYER_AMOUNT)
        {
            addBots(numberOfOnlinePlayers, view);
        }

        addServerPlayer(view);

        POINTS_TO_WIN = setWinScore(players.size());

        int judge = setJudge();

        boolean gameOver = false;
        if(!testBool)
        {
            startGameLoop(gameOver, judge, numberOfOnlinePlayers);
        }
    }

    /**
     * Starts the game by running the game loop
     * @param finished boolean that checks if the game is still running or not
     * @param judge has info for which player is the judge
     * @param numberOfOnlinePlayers the number of online players
     * @throws Exception
     */
    private void startGameLoop(boolean finished, int judge, int numberOfOnlinePlayers) throws Exception{
        while(!finished) {
            view.writeNewRound((judge==players.size()-1)?true:false);
            String playedScenarioCard = DeckFactory.getTopScenarioCard();
            view.printScenarioCard(playedScenarioCard);
            for(int i=0; i<numberOfOnlinePlayers; i++) {
                Player reciever = players.get(i);
                sendToClient(reciever, ((judge==i)?"JUDGE":"NOTJUDGE")+"\n");
                sendToClient(reciever,"Black card: " + playedScenarioCard + "\n");
            }

            playCards(judge);
            Collections.shuffle(playedCard);

            //sends the list of played cards to the online players
            sendPlayedCards(playedScenarioCard, numberOfOnlinePlayers);

            //judge judges
            PlayedApple winningCard = players.get(judge).judge();
            players.get(winningCard.getPLAYER_ID()).addWonScenarioCard(playedScenarioCard);

            //sends the winner information to the online players
            sendWinnerInfo(winningCard, numberOfOnlinePlayers);

            playedCard.clear();
            endGameRound(judge);
            finished = checkFinishGame(numberOfOnlinePlayers);

            //sets the judge to the next player in the list.
            judge = ((judge==(players.size()-1))?0:(judge+1));
        }
    }

    /**
     * writes to online clients.
     * @param player which player to send to.
     * @param textToSend
     * @throws Exception
     */
    public void sendToClient(Player player, String textToSend) throws Exception
    {
        player.getOutToClient().writeBytes(textToSend);
    }

    /**
     * Messages all the clients about which player won the round.
     * @param winningPlayerCard the player card that won the round.
     * @param numberOfOnlinePlayers
     * @throws Exception
     */
    public void sendWinnerInfo(PlayedApple winningPlayerCard, int numberOfOnlinePlayers) throws Exception
    {
        String winnerString = ((players.get(winningPlayerCard.getPLAYER_ID()).isBotBool()?"Bot":"Player") + " ID" + winningPlayerCard.getPLAYER_ID()+ " won with: " + winningPlayerCard.getScenarioCard());
        view.printWinner(winnerString);

        for(int i=0; i<numberOfOnlinePlayers; i++)
        {
            sendToClient(players.get(i),winnerString+"\n");
        }
    }

    /**
     * Check if the game is over. If it is, it creates a gameWInnerString that is printed for the host and the clients.
     * @param numberOfOnlinePlayers
     * @return returns true if the game is finished else false
     * @throws Exception
     */
    public boolean checkFinishGame(int numberOfOnlinePlayers) throws Exception{
        int gameWinner = 0;
        boolean finished = false;
        for(int i=0; i<players.size(); i++) {
            if (players.get(i).getWonScenarioCards().size() >= POINTS_TO_WIN) {
                gameWinner = i;
                System.out.println(players.get(i).getWonScenarioCards().size());
                finished = true;
            }
        }
        if (finished) {
            String gameWinnerString = "FINISHED: " + ((players.get(gameWinner).isBotBool() ? "Bot" : "Player") + " ID" + gameWinner +
                    " won the game");
            for (int i = 0; i < numberOfOnlinePlayers; i++) {
                sendToClient(players.get(i), (gameWinnerString + "\n"));
            }
            view.printWinningCard(gameWinnerString);
        }
        return finished;
    }

    /**
     * Sends the played cards to the online clients.
     * @param playedScenarioCard The scenario card for the round.
     * @param numberOfOnlinePlayers
     * @throws Exception
     */
    public void sendPlayedCards(String playedScenarioCard, int numberOfOnlinePlayers) throws Exception{
        String playedCardsString = createStringOfPlayedCards(playedScenarioCard);
        for(int i = 0; i < numberOfOnlinePlayers; i++) {
            sendToClient(players.get(i), (playedCardsString +"\n"));
        }
        playedCardsString = playedCardsString.replaceAll("#", "\n");
        view.printPlayedScenarioCards(playedCardsString);
    }

    /**
     * Gives each player except for the judge, a new card at the end of the round.
     * @param judge
     */
    public void endGameRound(int judge){
        for(int i=0; i<players.size(); i++) {
            if (i != judge) {
                players.get(i).addCard(DeckFactory.getTopPlayerCard());
            }
        }
    }

    /**
     * adds the server player
     * @param view
     */
    public void addServerPlayer(View view) {
        players.add(new Player(players.size(), new ArrayList<String>(), false, view));
        for (int j = 0; j < CARDS_IN_HAND; j++) {
            players.get(players.size() - 1).getHand().add(DeckFactory.getTopPlayerCard());
        }
    }

    /**
     * Adds the online players to the player list and gives them the amount of cards that CARDS_IN_HAND is set to
     * @param numberOfOnlinePlayers How many players that should be added to the list
     * @param view prints a connection message to the online players
     * @throws Exception
     */
    public void addOnlinePlayers(int numberOfOnlinePlayers, View view) throws Exception
    {
        ServerSocket aSocket = new ServerSocket(2048);
        for(int onlineClient=0; onlineClient<numberOfOnlinePlayers; onlineClient++)
        {
            Socket connectionSocket = aSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            String handString = "";

            for(int i = 0; i < CARDS_IN_HAND; i++)
            {
                handString = ((handString.compareTo("")==0)?"":(handString+";")) + DeckFactory.getTopPlayerCard();
            }
            outToClient.writeBytes(handString+"\n");
            players.add(new Player(onlineClient, false, connectionSocket, inFromClient, outToClient, view));
            view.printConnection(onlineClient);
        }
    }

    /**
     * Adds the necessary amount of bots if the player count is less than four
     * @param numberOfOnlinePlayers
     * @param view
     */
    public void addBots(int numberOfOnlinePlayers, View view) {
        for (int i = numberOfOnlinePlayers; i < MINIMAL_PLAYER_AMOUNT; i++) {
            players.add(new Player(i, new ArrayList<String>(), true, view));
            for (int j = 0; j < CARDS_IN_HAND; j++) {
                players.get(i).getHand().add(DeckFactory.getTopPlayerCard());
            }
        }
    }

    /**
     * Creates a threadpool that allows players to play at  same time.
     * @param judge the player index that is the current judge
     * @throws Exception
     */
    public void playCards(int judge) throws Exception {
        ExecutorService threadpool = Executors.newFixedThreadPool(players.size() - 1);

        for (int i = 0; i < players.size(); i++) {
            if (i != judge) {
                Player currentPlayer = players.get(i);

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        currentPlayer.play();
                    }
                };
                threadpool.execute(task);
            }
        }
        threadpool.shutdown();

        while(!threadpool.isTerminated()) {
            Thread.sleep(100);
        }
    }

    /**
     * Randomly picks the first judge
     * @return
     */
    public int setJudge()
    {
        random = ThreadLocalRandom.current();
        return random.nextInt(players.size());
    }

    /**
     * Creates a string of the played cards for the round starting with the scenario card
     * @param playedScenarioCard the rounds scenario card
     * @return
     */
    public String createStringOfPlayedCards(String playedScenarioCard)
    {
        String playedCardsString = playedScenarioCard;
        for(int j=0; j<players.size()-1; j++)
        {
            playedCardsString = playedCardsString + "#\t["+j+"] "+ playedCard.get(j).getScenarioCard();
        }
        return playedCardsString;
    }

    /**
     * Sets the score needed to win based on the number of players
     * @param numberOfPlayers number of players in the game
     * @return
     */
    public int setWinScore(int numberOfPlayers)
    {
        int winScore = 8;
        if (numberOfPlayers == 5)
        {
            winScore = 7;
        }
        else if (numberOfPlayers == 6)
        {
            winScore = 6;
        }
        else if (numberOfPlayers == 7)
        {
            winScore = 5;
        }
        else if (numberOfPlayers >= 8)
        {
            winScore = 4;
        }
        return winScore;
    }

    /**
     *Getters
     */
    public static int getCardsInHand() {
        return CARDS_IN_HAND;
    }

    public static int getPointsToWin(){
        return POINTS_TO_WIN;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public static ArrayList<PlayedApple> getPlayedCard() {
        return playedCard;
    }
}
