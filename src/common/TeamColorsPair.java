package common;

import server.constants.Color;

/**
 * チームの色の組を持つクラス.
 */
public class TeamColorsPair {
    public static final int N_COLOR_PAIRS = 4;  // 使用可能な色の組の数

    /**
     * チームの色の組を取得.
     *
     * @param teamNumber チーム番号
     * @return チームの色の組
     */
    public static Color[] findTeamColorsPair(int teamNumber) {
        Color[] teamColorsPair = new Color[2];

        switch (teamNumber) {
            case 0:
                teamColorsPair[0] = Color.Blue;
                teamColorsPair[1] = Color.Orange;
                break;
            case 1:
                teamColorsPair[0] = Color.Pink;
                teamColorsPair[1] = Color.Turquoise;
                break;
            case 2:
                teamColorsPair[0] = Color.Purple;
                teamColorsPair[1] = Color.LimeGreen;
                break;
            case 3:
                teamColorsPair[0] = Color.Red;
                teamColorsPair[1] = Color.Green;
                break;
            default:
                System.err.println("invalid team number: " + teamNumber);
                System.exit(1);
        }

        return teamColorsPair;
    }
}
