package common;

import server.constants.Color;

/**
 * チームの色の組を持つクラス.
 */
public class ColorPair {
    public static final int N_COLOR_PAIRS = 4;  // 使用可能な色の組の数

    /**
     * チームの色の組を取得.
     *
     * @param teamNumber チーム番号
     * @return チームの色の組
     */
    public static Color[] getColorPair(int teamNumber) {
        Color[] teamColorPair = new Color[2];

        switch (teamNumber) {
            case 0:
                teamColorPair[0] = Color.Blue;
                teamColorPair[1] = Color.Orange;
                break;
            case 1:
                teamColorPair[0] = Color.Pink;
                teamColorPair[1] = Color.Turquoise;
                break;
            case 2:
                teamColorPair[0] = Color.Purple;
                teamColorPair[1] = Color.LimeGreen;
                break;
            case 3:
                teamColorPair[0] = Color.Red;
                teamColorPair[1] = Color.Green;
                break;
            default:
                System.err.println("invalid team number: " + teamNumber);
                System.exit(1);
        }

        return teamColorPair;
    }
}
