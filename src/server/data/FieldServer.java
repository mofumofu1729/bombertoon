package server.data;

import common.Setting;
import server.constants.Color;
import server.constants.Status;
import server.network.TransmissionServer;

// サーバー上のフィールド
public class FieldServer {
    public Color color;
    public Status status;
    public Bomb bomb;
    public boolean isExistBomb;
    public boolean isExistHuman;
    public boolean isFireSource; // 火元かどうかを判別→爆発を消す際に使用
    public int explodeID; // 爆発させた奴のID
    public int x, y;

    int points; // フィールド上のマスの得点。まだ実装してない。
    TransmissionServer ts;


    public FieldServer(Status status, int x, int y, TransmissionServer ts, Color color) {// コンストラクタ：状態を格納
        // TODO
        this.status = status;
        this.x = x;
        this.y = y;
        this.ts = ts;
        this.color = color;
        isFireSource = false;
        isExistBomb = false;
    }

    public void changeStatus(Status nextStatus) {// 状態変更
        status = nextStatus;
    }

    public Status getStatus() {// 状態取得
        return status;
    }

    public void fieldExplosion(int playerID) {// 爆破メソッド
        switch (status) {
            case BREAKABLE1:
                status = Status.NOTHING;
                break;
            case BREAKABLE2:
                status = Status.BREAKABLE1;
                break;
            case BREAKABLE3:
                status = Status.BREAKABLE2;
                break;
            case MUTEKI:
                break;
            case UNBREAKABLE:
                break;
            default:
                status = Status.BOMBERING;
                break;
        }
        if (isExistBomb) { // 追加!!!!!!!!!!!!!!!!!!!!!!
            status = Status.BOMBERING;
        }
        explodeID = playerID;
        ts.announceChangeField(this);

    }

    // !!!!!!!!!!!!!!!!!!!ボムのexplosionと名前がかぶっている
    public void fieldresetExplosion(int playerID) {
        if (status == Status.BOMBERING) {

        }
        if (!(this.status == status.MUTEKI) && (this.status == Status.BOMBERING)) { // TODO
            status = Status.NOTHING;
            // ここを書き換えれば障害物を塗る、塗らないを変更できる
            switch (playerID) {
                case 0:
                    this.color = Setting.ColorTeam1;
                    break;
                case 1:
                    this.color = Setting.ColorTeam2;
                    System.out.println("KTKRRRRRRRRRRRRRRR");
                    break;
                case 2:
                    this.color = Setting.ColorTeam1;
                    break;
                case 3:
                    this.color = Setting.ColorTeam2;
                    break;

            }
        }
        ts.announceChangeField(this);
    }
}
