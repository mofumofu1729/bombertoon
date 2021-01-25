package common;

public class Score {
    public int teamNum; // チーム数
    public int playerNum; // プレイヤー数

    public int[] painted; // チームごとに塗られたフィールドの数

    public int[] kill; // プレイヤーごとの殺害，被殺害数
    public int[] death;


    // プレイヤー数を引数としてインスタンスを生成
    public Score(int playerNum) {
        this(playerNum, 2); // 普通はチーム数2としてコンストラクタを呼ぶ
    }

    public Score(int playerNum, int teamNum) {
        this.teamNum = teamNum;
        this.playerNum = playerNum;

        painted = new int[teamNum];
        kill = new int[playerNum];
        death = new int[playerNum];
    }
}
