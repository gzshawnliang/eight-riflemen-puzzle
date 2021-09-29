package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import javafx.util.Pair;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;


public class Referee extends AbstractReferee
{
    @Inject
    private SoloGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private final int totalQueenCount = 8;

    private boolean isValidH(int[] nowP)
    {
        int[] vis = new int[8];
        for (int i = 0; i <= 8 - 1; ++i)
        {
            if (nowP[i] < 0) continue;

            ++vis[nowP[i]];
            if (vis[nowP[i]] == 2) return false;
        }
        return true;
    }

    private boolean isValidD(int[] nowP)
    {
        int[] vis1 = new int[15];
        int[] vism1 = new int[15];
        for (int i = 0; i <= 8 - 1; ++i)
        {
            if (nowP[i] < 0) continue;

            if (vis1[nowP[i] - i + 7] == 0) vis1[nowP[i] - i + 7] = 1;
            else return false;

            if (vism1[nowP[i] + i] == 0) vism1[nowP[i] + i] = 1;
            else return false;
        }
        return true;
    }

    int iniSize;
    double points = 0.0,maxPoints=0.0;
    int[] gameBoard = {-1,-1,-1,-1,-1,-1,-1,-1};
    private Text pointText;

    Line line1,line2,line3,line4;

    Sprite firstQueen;
    List<Sprite> allQueen = new ArrayList<Sprite>();

    @Override
    public void init()
    {
        gameManager.setFrameDuration(1000);
        gameManager.setMaxTurns(8);

        line1=graphicEntityModule.createLine().setVisible(false);
        line2=graphicEntityModule.createLine().setVisible(false);
        line3=graphicEntityModule.createLine().setVisible(false);
        line4=graphicEntityModule.createLine().setVisible(false);

        firstQueen = graphicEntityModule.createSprite()
                .setScale(1.0)
                .setImage(Constants.QUEEN_SPRITE3)
                .setX(-100)
                .setY(-100)
                .setVisible(true)
                .setScale(1.2)
        ;

        //Draw background
        graphicEntityModule.createSprite().setImage(Constants.BACKGROUND_SPRITE);

        List<String> testInput = gameManager.getTestCaseInput();
        iniSize = testInput.size();

        //send data to Player
        ArrayList<String> testInput2=new ArrayList<String>();
        if(testInput.get(0).trim().isEmpty())
        {
            iniSize=0;
            gameManager.getPlayer().sendInputLine(Integer.toString(iniSize));
        }
        else
        {
            gameManager.getPlayer().sendInputLine(Integer.toString(iniSize));

            for (String queenPos : testInput)
            {
                createQueen(queenPos);
                gameManager.getPlayer().sendInputLine(queenPos);

                int posX = (queenPos.charAt(0) - 'A'), posY = (queenPos.charAt(1) - '1');
                gameBoard[posX] = posY;
                points += Math.sqrt(posX * posX + posY * posY);
                testInput2.add(queenPos);
            }
        }
        String[] testInputArr= new String[testInput2.size()];
        testInputArr = testInput2.toArray(testInputArr);
        maxPoints=new SolutionOk2().Solve(testInputArr);

        for (int i =1;i<=8;++i)
        {
            graphicEntityModule.createText(Integer.toString(i))
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(50)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(1450)
                    .setY(930-(i-1)*120)
                    .setZIndex(0)
                    ;
        }
        for (int i =1;i<=8;++i)
        {
            char a=(char)('A'+(i-1));
            graphicEntityModule.createText(""+a)
                    .setStrokeThickness(2) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(50)
                    .setFillColor(0xd1f2eb) // Setting the text color to black
                    .setX(530+(i-1)*115)
                    .setY(0)
                    .setZIndex(0)
                    ;
        }

        pointText = graphicEntityModule.createText("score:  "+String.format("%.4f", points))
                .setStrokeThickness(2) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(50)
                .setFillColor(0xd1f2eb) // Setting the text color to black
                .setX(1510)
                .setY(20);
    }


    @Override
    public void gameTurn(int turn)
    {
        //gameManager.getPlayer().sendInputLine("0");
        gameManager.getPlayer().execute();

        String queenPos = "";
        try
        {
            List<String> outputs = gameManager.getPlayer().getOutputs();
            queenPos = checkOutput(outputs);
            if (queenPos == null || queenPos.isEmpty())
            {
                gameManager.loseGame("Invalid Input.");
                return;
            }
            //System.err.println(queenPos);
            System.err.println("Player Input:" + queenPos);

            int posX = (queenPos.charAt(0) - 'A'), posY = (queenPos.charAt(1) - '1');
            //allPos.add(new point(posX, posY));

            if (posX < 0 || posX > 7 || posY < 0 || posY > 7)
            {
                loseGame(queenPos,"Some riflemen are out of bound.");
                //gameManager.loseGame("Some rifleman are out of bound.");
                return;
            }
            else if (gameBoard[posX] >= 0)
            {
                loseGame(queenPos,"Some riflemen are in the same vertical line.");

                return;
            }
            gameBoard[posX] = posY;
            if (isValidD(gameBoard) == false)
            {
                loseGame(queenPos,"Some riflemen are in the same diagonal line.");
                return;
            }
            else if (isValidH(gameBoard) == false)
            {
                loseGame(queenPos,"Some riflemen are in the same horizontal line.");
                return;
            }
            points += Math.sqrt(posX * posX + posY * posY);

            pointText.setText("score:  "+String.format("%.4f", points));


        } catch (TimeoutException e)
        {
            firstQueen.setVisible(false);
            gameManager.loseGame("Time Limit Exceed!");
            createLoseText("Time Limit Exceed!");
            return;
        }

        createQueen(queenPos);

        if (turn == 8 - iniSize )
        {
            if(points + 0.0001d < maxPoints)
            {
                loseGame(queenPos,"Highest Possible Score:" + String.format("%.4f", maxPoints) + "" );
            }
            else
            {

                firstQueen.setVisible(false);

                for (Sprite sp : allQueen)
                {
                    sp.setVisible(true);
                    sp.setImage(Constants.QUEEN_SPRITE1);
                }

                winGame();
            }
            return;
        }
    }

    private int[] getPos(String pos)
    {
        pos = pos.toUpperCase();
        int x = pos.charAt(0) - 'A';
        int y = 8 - (pos.charAt(1) - '0');
        return new int[]{x, y};
    }

    private void createLine(String pos)
    {
        final int ULX = 480, ULY = 60 - 7, GL = 120;
        double WD = 5;

        int GridX = (pos.charAt(0) - 'A');
        int GridY = (pos.charAt(1) - '1');

        int midX = ULX + GL / 2 + GridX * 120, midY = ULY + GL / 2 + (7 - GridY) * 120;
        line1.setVisible(false);
        line1 = graphicEntityModule.createLine().setX(ULX, Curve.EASE_IN_AND_OUT).setY(midY, Curve.EASE_IN_AND_OUT).setX2(ULX + 8 * GL).setY2(midY).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);

        line2.setVisible(false);
        line2 = graphicEntityModule.createLine().setX(midX, Curve.EASE_IN_AND_OUT).setY(ULY, Curve.EASE_IN_AND_OUT).setX2(midX).setY2(ULY + 8 * GL).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);

        if (midY - midX >= ULY - ULX)
        {
            int dx = midX - ULX, dy = ULY + 8 * GL - midY;
            line3.setVisible(false);
            line3 = graphicEntityModule.createLine().setX(ULX, Curve.EASE_IN_AND_OUT).setY(midY - dx, Curve.EASE_IN_AND_OUT).setX2(midX + dy).setY2(ULY + 8 * GL).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);
        }
        else
        {
            int dx = ULX + 8 * GL - midX, dy = midY - ULY;
            line3.setVisible(false);
            line3 = graphicEntityModule.createLine().setX(midX - dy, Curve.EASE_IN_AND_OUT).setY(ULY, Curve.EASE_IN_AND_OUT).setX2(ULX + 8 * GL).setY2(midY + dx).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);
        }

        if (midX + midY >= ULX + ULY + 8 * GL)
        {
            int dx = ULX + 8 * GL - midX, dy = ULY + 8 * GL - midY;
            line4.setVisible(false);
            line4 = graphicEntityModule.createLine().setX(midX - dy, Curve.EASE_IN_AND_OUT).setY(ULY + 8 * GL, Curve.EASE_IN_AND_OUT).setX2(ULX + 8 * GL).setY2(midY - dx).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);
        }
        else
        {
            int dx = midX - ULX, dy = midY - ULY;
            line4.setVisible(false);
            line4 = graphicEntityModule.createLine().setX(ULX, Curve.EASE_IN_AND_OUT).setY(midY + dx, Curve.EASE_IN_AND_OUT).setX2(midX + dy).setY2(ULY).setLineColor(0xb3ffb3).setLineWidth(WD).setZIndex(0).setAlpha(0.6, Curve.LINEAR).setVisible(true);
        }
    }

    private void winGame()
    {
        graphicEntityModule.createRectangle()
                .setLineWidth(0)
                .setFillColor(0xd4efdf)
                .setWidth(Constants.VIEWER_WIDTH)
                .setHeight(95)
                .setAlpha(0.25)
                .setX(0)
                .setY(Constants.VIEWER_HEIGHT/2-50);

        graphicEntityModule.createText("You win!")
                .setStrokeThickness(2) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(75)
                .setFillColor(0xd1f2eb) // Setting the text color to black
                .setX(Constants.VIEWER_WIDTH/2-150,Curve.EASE_IN_AND_OUT)
                .setY(Constants.VIEWER_HEIGHT/2-50,Curve.EASE_IN_AND_OUT)
                .setZIndex(10000);

        gameManager.winGame("You win!");
    }

    private void loseGame(String pos,String loseText)
    {
        createQueenBad(pos,loseText);
        gameManager.loseGame(loseText);
    }

    //根据国际象棋代数记谱法（Algebraic notation）的位置放置皇后
    private Sprite createQueen(String pos)
    {
        int[] currPos = getPos(pos);
        int x = currPos[0];
        int y = currPos[1];

        x = Constants.INIT_X + x * Constants.CELL_OFFSET;
        y = Constants.INIT_Y + y * Constants.CELL_OFFSET - 50+20;


        firstQueen.setX(x, Curve.EASE_IN_AND_OUT).setY(y - 40, Curve.EASE_IN_AND_OUT).setVisible(true);
        firstQueen.setVisible(false);

        Random random = new Random();
        int Y=0;
        if(random.nextBoolean())
            Y=Constants.VIEWER_WIDTH;       //random.nextInt(1900)

        firstQueen = graphicEntityModule.createSprite()
                .setScale(1.0)
                .setImage(Constants.QUEEN_SPRITE3)
                .setX(Y)
                .setY(random.nextInt(500))
                .setScale(1.2);


        Sprite newQueenSprite = graphicEntityModule.createSprite()
                .setImage(Constants.QUEEN_SPRITE)
                .setX(x)
                .setY(y)
                .setScale(1.2)
                .setZIndex(y+100);

        allQueen.add(newQueenSprite);

        createLine(pos);
        return newQueenSprite;
    }

    private void createLoseText(String loseText)
    {
        Text loseMsg = graphicEntityModule.createText("You lose!")
                .setStrokeThickness(2) // Adding an outline
                .setStrokeColor(0xffffff) // a white outline
                .setFontSize(75)
                .setFillColor(0xd1f2eb) // Setting the text color to black
                .setX(Constants.VIEWER_WIDTH/2-("You lose!".length()*30)/2,Curve.EASE_IN_AND_OUT)
                .setY(Constants.VIEWER_HEIGHT/2-50,Curve.EASE_IN_AND_OUT)
                .setZIndex(10000);

        if(!loseText.equals(null) && !loseText.isEmpty())
            graphicEntityModule.createText(loseText)
                    .setStrokeThickness(1) // Adding an outline
                    .setStrokeColor(0xffffff) // a white outline
                    .setFontSize(36)
                    .setFillColor(0xd1f2eb)
                    .setX(Constants.VIEWER_WIDTH /2-(loseText.length()*15)/2 ,Curve.EASE_IN_AND_OUT)
                    .setY(loseMsg.getY()+72,Curve.EASE_IN_AND_OUT)
                    .setZIndex(100000);

    }

    private Sprite createQueenBad(String pos,String loseText)
    {
        firstQueen.setVisible(false);
        int[] currPos = getPos(pos);
        int x = currPos[0];
        int y = currPos[1];

        x = Constants.INIT_X + x * Constants.CELL_OFFSET;
        y = Constants.INIT_Y + y * Constants.CELL_OFFSET - 50+20;

        firstQueen.setX(x, Curve.EASE_IN_AND_OUT).setY(y - 50, Curve.EASE_IN_AND_OUT).setVisible(true);
        firstQueen.setVisible(false);

        Sprite newQueenSprite = graphicEntityModule.createSprite()
                .setImage(Constants.QUEEN_SPRITE_BAD)
                .setScale(1.2)
                .setX(x)
                .setY(y)
                .setZIndex(3);

        graphicEntityModule.createSprite()
                .setImage(Constants.QUEEN_ERROR)
                .setScale(0.6)
                .setX(x+10+20)
                .setY(y+50+80-20)
                .setAlpha(0.8)
                .setZIndex(150);

        createLine(pos);

        graphicEntityModule.createRectangle()
                .setLineWidth(0)
                .setFillColor(0xd4efdf)
                .setWidth(Constants.VIEWER_WIDTH)
                .setHeight(115)
                .setAlpha(0.25)
                .setX(0)
                .setY(Constants.VIEWER_HEIGHT/2-50);

        createLoseText(loseText);

        return newQueenSprite;

    }

    private String checkOutput(List<String> outputs)
    {
        if (outputs.size() == 1)
        {
            //A-H,1-8
            String output = outputs.get(0).toUpperCase();
            int c = output.charAt(0) - 'A';
            int n = output.charAt(1) - '0';

            if (c >= 0 && c <= 7 && n >= 1 && n <= 8)
            {
                return output;
            }
        }
        return null;
    }
}

