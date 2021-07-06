package com.codingame.game;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class SolutionOk2
{
    private int indAllP = 0;
    private int [][] allP = new int[92][8];

    private boolean isValid(int[] nowP)
    {
        int[] vis1 = new int[15];
        int[] vism1 = new int[15];
        for (int i = 0; i <= 8 - 1; ++i)
        {
            if (vis1[nowP[i] - i + 7] == 0) vis1[nowP[i] - i + 7] = 1;
            else return false;

            if (vism1[nowP[i] + i] == 0) vism1[nowP[i] + i] = 1;
            else return false;
        }
        return true;
    }

    private void genAll(int[] addP, int[] nowP)
    {
        int lenAdd = addP.length, lenNow = nowP.length;
        if (lenNow == 0)
        {
            if (isValid(addP)) allP[indAllP++] = addP;
            //allP[indAllP++] = addP;
        }
        else
        {
            for (int i = 0; i <= lenNow - 1; ++i)
            {
                int[] tmpAddP = new int[lenAdd + 1];
                for (int j = 0; j <= lenAdd - 1; ++j) tmpAddP[j] = addP[j];
                tmpAddP[lenAdd] = nowP[i];

                int[] tmpNowP = new int[lenNow - 1];
                for (int j = 0; j <= i - 1; ++j) tmpNowP[j] = nowP[j];
                for (int j = i + 1; j <= lenNow - 1; ++j) tmpNowP[j - 1] = nowP[j];

                genAll(tmpAddP, tmpNowP);
            }
        }
    }

    public double Solve(String[] iniPos)
    {
//        Scanner scanner = new Scanner(in);
//        int n = scanner.nextInt();
//        String[] iniPos = new String[n];
//        for (int i = 0; i <= n - 1; ++i)
//            iniPos[i] = scanner.next();

        int n = iniPos.length;
        int[] a = {0,1,2,3,4,5,6,7};
        int[] _null = new int[0];
        genAll(_null, a);

        int lenAllP = allP.length;

        double maxPoints = 0.0;
        int[] ans = new int[0];
        for (int i = 0; i <= lenAllP - 1; ++i)
        {
            int[] nowP = allP[i];
            boolean flg = true;
            for (int j = 0; j <= n - 1; ++j)
            {
                int posX = (iniPos[j].charAt(0) - 'A'), posY = (iniPos[j].charAt(1) - '1');
                if (nowP[posX] != posY)
                {
                    flg = false; break;
                }
            }

            if (flg == true)
            {
                double points = 0.0;
                for (int j = 0; j <= 8 - 1; ++j) points += Math.sqrt(j * j + nowP[j] * nowP[j]);

                if (points > maxPoints)
                {
                    maxPoints = points;
                    ans = nowP;
                }
            }
        }

        List<String> allAns = new ArrayList<String>();
        for (int j = 0; j <= 8 - 1; ++j)
        {
            char firstCh = (char)('A' + j), secondCh = (char)('1' + ans[j]);
            String out2 = "";
            out2 += firstCh; out2 += secondCh;

            boolean flg = false;
            for (int k = 0; k <= n - 1; ++k)
                if (iniPos[k].equals(out2)) flg = true;
            if (flg) continue;

            allAns.add(out2);
        }
        Collections.sort(allAns);

        return maxPoints;
    }
}
