package server.scenes;

import java.util.Random;

import common.TeamColorsPair;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import common.Setting;
import server.constants.Color;
import server.constants.Direction;
import server.constants.State;
import server.constants.Status;
import server.data.Bomb;
import server.data.FieldServer;
import server.data.PlayerServer;
import server.network.TransmissionServer;


// TODO マッチングと対戦とリザルトがくっついているからそれをバラけさせる
// 内部処理はこいつの管轄
public class BattleServer extends BasicGameState {
    public static final int N_PLAYERS = common.Setting.N_PLAYERS;  // 参加プレイヤー数
    public static final int TIMELIMIT = 20000; // 制限時間
    // フィールドサイズ
    public static final int FIELDHEIGHT = 16;
    public static final int FIELDWIDTH = 16;
    common.Score score = new common.Score(N_PLAYERS);

    private int state;
    private boolean enterFinished = false; // 画面遷移が終わったか
    private boolean isSendReady = false; // 開始の合図を既に送ったか
    private boolean isSent = false;
    int gameTimer; // ゲーム自体の残り時間
    public PlayerServer[] player;
    FieldServer[][] field;
    private TransmissionServer ts;
    int timeLeft;
    int team1Point;
    int team2Point;
    int[] fieldData;

    public BattleServer(int state) {
        this.state = state;
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {


    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
            throws SlickException {
    }

    /**
     * ゲームループ.
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int msecSinceLastUpdate)
            throws SlickException {

        gameTimer -= msecSinceLastUpdate;
        ts.announceTime(gameTimer);

        if (((gameTimer / 1000) <= 0) && (isSent == false)) {
            finishGame();

            sbg.enterState(State.MATCHINGSERVER);

        }

        updatePlayersStatus(msecSinceLastUpdate);

        updateFieldStatus(msecSinceLastUpdate);
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {

        fieldData = Setting.STAGE4TWO; // フィールドのパラメータ
        ts = TransmissionServer.getInstance();
        gameTimer = TIMELIMIT;

        player = new PlayerServer[N_PLAYERS];
        field = new FieldServer[FIELDHEIGHT][FIELDWIDTH];

        // debug
        System.out.println("enter BattleServer");

        initializeStatus(player, field);
        // enterFinished = true;
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        this.enterFinished = false;
        this.isSendReady = false;
        this.isSent = false;
    }

    @Override
    public int getID() {
        return state;
    }

    /**
     * プレイヤーの状態を更新.
     *
     * @param msecSinceLastUpdate 最後の更新からのミリ秒
     */
    private void updatePlayersStatus(int msecSinceLastUpdate) {

        for (int i = 0; i < N_PLAYERS; i++) {
            if (player[i].death != true) {
                // プレイヤーが生きている時

                // 移動
                Direction dir = ts.checkHuman(i);
                if (dir != Direction.NONE) {
                    player[i].move(dir, field);
                }

                // 爆弾の設置
                // TODO 本来はupdateFieldStatusの責務かも
                if (ts.checkBomb(i)) {
                    player[i].putBomb(field);
                }

                // 爆発の当たり判定
                if (field[player[i].y][player[i].x].status == Status.BOMBERING) {
                    player[i].killing(player[i],
                                      player[field[player[i].y][player[i].x].explodeID]);
                    player[i].death = true;
                    player[i].rebornCount = 2000;

                    field[player[i].y][player[i].x].isExistHuman = false;
                }
            } else {
                // プレイヤーが死んでいる時

                player[i].rebornCount -= msecSinceLastUpdate;

                // 一定時間経ったら復活
                if (player[i].rebornCount < 0) {
                    player[i].revive();
                }
            }
        }
    }

    /**
     * フィールドの状態を更新.
     *
     * @param msecSinceLastUpdate 最後の更新からのミリ秒
     */
    private void updateFieldStatus(int msecSinceLastUpdate) {

        for (int y = 0; y < FIELDHEIGHT; y++) {
            for (int x = 0; x < FIELDHEIGHT; x++) {
                updateFiledGridStatus(x, y, msecSinceLastUpdate);

            }

        }
    }

    /**
     * フィールドのマス目の状態を更新.
     * 
     * @param x マス目のx座標
     * @param y マス目のy座標
     * @param msecSinceLastUpdate 前回の更新からのミリ秒
     */
    private void updateFiledGridStatus(int x, int y, int msecSinceLastUpdate) {

        if (field[y][x].isExistBomb) {
            // 爆弾がある場合

            field[y][x].bomb.count -= msecSinceLastUpdate;  // 爆発までの時間を更新

            if (field[y][x].bomb.count < 0) {
                // 爆発処理
                // フィールドの変化はexpolosion内でClientにannounceする

                field[y][x].bomb.bombExplosion(field[y][x].bomb.playerID, x, y, field);
                field[y][x].isExistBomb = false;
                field[y][x].bomb.count = Bomb.bombInitial;
                field[y][x].isFireSource = true;
                player[field[y][x].bomb.playerID].bombCount--;
            }
        } else if (field[y][x].isFireSource) {
            // 爆発中の中心である場合

            field[y][x].bomb.refreshCount -= msecSinceLastUpdate;

            if (field[y][x].bomb.refreshCount < 0) {
                // 爆風を消す処理
                // フィールドの変化はexplosion内でClientにannounceする

                field[y][x].bomb.resetExplosion(field[y][x].bomb.playerID, x, y, field);
                field[y][x].status = Status.NOTHING;
                field[y][x].bomb.refreshCount = Bomb.refreshInitial;
                field[y][x].isFireSource = false;
            }
        }
    }

    /**
     * 試合終了時の処理.
     */
    private void finishGame() {
        int team1 = 0;
        int team2 = 0;

        for (int y = 0; y < FIELDHEIGHT; y++) {
            for (int x = 0; x < FIELDHEIGHT; x++) {
                if (field[y][x].color == Setting.ColorTeam1) {
                    team1++;
                } else if (field[y][x].color == Setting.ColorTeam2) {
                    team2++;
                }
            }
        }
        score.painted[0] = team1;
        score.painted[1] = team2;

        for (int i = 0; i < N_PLAYERS; i++) {
            score.death[i] = player[i].deathTimes;
            score.kill[i] = player[i].killTimes;
        }

        System.out.println(score.painted[0] + "点対");
        System.out.println(score.painted[1] + "点");
        ts.annouceScore(score);
        ts.announceFinish();
        isSent = true;

        ts.finishGame(); // ゲーム終了

        try {
            // 同じポート番号が使えるまで少し時間がかかるので待機
            // TODO 本当はもう少し安全な方法を使うべき
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ゲーム開始時の初期化処理.
     *
     * @param players プレイヤーの状態
     * @param field フィールドの状態
     */
    private void initializeStatus(PlayerServer[] players, FieldServer[][] field) {

        // プレイヤー色の組み合わせを決定
        Random r = new Random();
        int colorPair = r.nextInt(TeamColorsPair.N_COLOR_PAIRS);

        // TODO global変数と同じ理由で，下二行staticフィールドじゃなくてローカル変数使うべきだと思われ
        Setting.ColorTeam1 = common.TeamColorsPair.findTeamColorsPair(colorPair)[0];
        Setting.ColorTeam2 = common.TeamColorsPair.findTeamColorsPair(colorPair)[1];

        initializePlayersStatus(players);

        initializeFieldStatus(field);

        ts.announceReady(colorPair);
    }

    /**
     * プレイヤーの状態の初期化.
     *
     * @param players プレイヤーの状態
     */
    private void initializePlayersStatus(PlayerServer[] players) {
        for (int i = 0; i < N_PLAYERS; i++) {
            switch (i) {
                case 0:
                    players[i] =
                        new PlayerServer(0, 0, Setting.ColorTeam1, 0,
                                Direction.DOWN, ts);
                    break;
                case 1:
                    players[i] =
                        new PlayerServer(FIELDHEIGHT - 1, 0, Setting.ColorTeam2, 1,
                                Direction.DOWN, ts);
                    break;
                case 2:
                    players[i] =
                        new PlayerServer(FIELDHEIGHT - 1, FIELDHEIGHT - 1, Setting.ColorTeam1, 2,
                                Direction.DOWN, ts);
                    break;
                case 3:
                    players[i] =
                        new PlayerServer(0, FIELDHEIGHT - 1, Setting.ColorTeam2, 3,
                                Direction.DOWN, ts);
                    break;
            }

        }
    }

    /**
     * フィールドの状態を初期化.
     *
     * @param field フィールドの状態
     */
    private void initializeFieldStatus(FieldServer[][] field) {
        for (int y = 0; y < FIELDHEIGHT; y++) { // ここで障害物と無敵エリアを設定する
            for (int x = 0; x < FIELDHEIGHT; x++) {
                switch (fieldData[x + y * FIELDWIDTH]) {
                    case 0:
                        field[y][x] =
                            new FieldServer(Status.NOTHING, x, y, ts, Color.Transparent);
                        break;
                    case 1:
                        field[y][x] =
                            new FieldServer(Status.BREAKABLE1, x, y, ts, Color.Transparent);
                        break;
                    case 2:
                        field[y][x] =
                            new FieldServer(Status.UNBREAKABLE, x, y, ts, Color.Transparent);
                        break;
                    case -1:
                        // TODO ちゃんと任意の色への無敵地点とする
                        field[y][x] =
                            new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam1);
                        break;
                    case -2:
                        field[y][x] =
                            new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam2);
                        break;
                    case -3:
                        field[y][x] =
                            new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam1);
                        break;
                    case -4:
                        field[y][x] =
                            new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam2);
                        break;
                }
            }
        }
    }
}
