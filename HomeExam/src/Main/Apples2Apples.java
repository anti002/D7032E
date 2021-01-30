package Main;


public class Apples2Apples
{

    /**
     * starts the games and takes the given argument when starting,
     * If you put in a single number, it will host a game with that many slots
     * An ip adress as argument will join the game at that ip
     * and no argument at all will start a game with bots
     * @param argv
     */
    public static void main(String argv[])
    {
        View view = new View();
        if(argv.length == 0)
        {
            try
            {
                Server server = new Server(0, view, false);
            }
            catch (Exception e)
            {
                e.printStackTrace(System.out);
            }
        }
        else
        {
            try
            {
                int amountOfOnlineClients = Integer.parseInt(argv[0]);
                Server server = new Server(amountOfOnlineClients, view, false);
            }
            catch(NumberFormatException e)
            {
                try
                {
                    OnlineClient OnlineClient = new OnlineClient(argv[0], view);
                }
                catch (Exception err)
                {
                    System.out.println(err.getMessage());
                }
            }
            catch(Exception e)
            {
                e.printStackTrace(System.out);
                view.printErrorStart();
            }
        }
    }
}