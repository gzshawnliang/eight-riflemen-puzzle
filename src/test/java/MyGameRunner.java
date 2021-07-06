import com.codingame.gameengine.runner.SoloGameRunner;

public class MyGameRunner
{
    public static void main(String[] args) {
        SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
        gameRunner.setAgent(PlayerSolution.class);

        // Sets a test case
        gameRunner.setTestCase("test5.json");
        gameRunner.start();

    }
}
