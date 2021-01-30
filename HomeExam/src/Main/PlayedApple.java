package Main;

class PlayedApple
{
    private int PLAYER_ID;
    private String scenarioCard;

    public PlayedApple(int PLAYER_ID, String scenarioCard)
    {
        this.PLAYER_ID = PLAYER_ID;
        this.scenarioCard = scenarioCard;
    }

    /**
     *Getters
     */
    public int getPLAYER_ID()
    {
        return PLAYER_ID;
    }

    public String getScenarioCard()
    {
        return scenarioCard;
    }

}