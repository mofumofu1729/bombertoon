package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Queue;
import client.constants.Direction;
import client.data.FieldClient;
import client.data.PlayerClient;
import common.Setting;

public class TransmissionClient {
    public static String hostName = "localhost"; // TODO Config.update()から参照されている staticでやるのはあまり良くない
    public static final int MAX_PLAYER = Setting.N_PLAYRES; // プレイヤーの人数

    private PrintWriter out; // 出力用のライター

    private boolean ready = false; // サーバー側でゲームを開始する準備ができたか
    private boolean isConnected = false; // サーバと接続が確立されたか
    private boolean isFinish = false; // ゲームが終わったか

    private Queue<PlayerClient> recievedHumanBuffer;
    private Queue<FieldClient> recievedFieldBuffer;

    private int time; // 経過時間か何か
    private int portnumber;
    private Socket socket = null;

    private common.Score score; // 成績
    private int colorPair; // 配色
    private int playerId = -1; // プレイヤー番号


    public TransmissionClient() {
        this(hostName, server.network.TransmissionServer.PORT);
    }

    public TransmissionClient(String hostname, int portnumber) {
        recievedHumanBuffer = new ArrayDeque<PlayerClient>();
        recievedFieldBuffer = new ArrayDeque<FieldClient>();

        TransmissionClient.hostName = hostname;
        this.portnumber = portnumber;
    }

    // サーバーに接続
    /*
     * TODO 例外処理が適当すぎる．例外もきちんと対処出来るようにする
     */
    public boolean openConection() throws ConnectException {
        if (isConnected)
            return true;
        try {
            socket = new Socket(TransmissionClient.hostName, this.portnumber);
        } catch (UnknownHostException e) {
            System.err.println("ホストの IP アドレスが判定できません: " + e);
        } catch (ConnectException e) { // サーバーが立っていない
            throw e;
        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e);
        }

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String responce = null; // サーバーからの応答

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((responce = br.readLine()) == null)
                System.out.println("waiting for responce"); // TODO debug
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(responce); // TODO debug

        if (responce.equals("BUSY")) { // 戦闘中だったら
            try {
                socket.close(); // TODO 毎回作って閉じるのは無駄だな・・・
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else if (responce.equals("CONNECTED")) { // 戦闘中ではなかったら
            // メッセージ受信用のスレッド作成
            MesgRecvThread mrt = new MesgRecvThread(this, socket);
            mrt.start();
            isConnected = true;
            return true;
        } else {
            System.err.println("unknown responce message");
            System.exit(-1);
            return true; // この行は実行されない
        }
    }

    // サーバとの接続を切断
    public void closeConnection() {
        try {
            socket.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send
    public void sendHuman(Direction dir) {
        out.println(dir.name());
    }

    public void sendBomb() {
        out.println("BOMB");
    }

    // receive
    public FieldClient recieveField() {
        if (recievedFieldBuffer.isEmpty()) {
            return null;
        } else {
            return recievedFieldBuffer.remove();
        }
    }

    public PlayerClient recievedHuman() {
        if (recievedHumanBuffer.isEmpty()) {
            return null;
        } else {
            return recievedHumanBuffer.remove();
        }
    }

    // （残り，経過）時間を取得
    public int receiveTime() {
        return time;
    }

    // 終了かを取得
    public boolean recieveFinish() {
        return isFinish;
    }

    public int recieveColorPair() {
        return this.colorPair;
    }

    public int recievePlayerId() {
        return playerId;
    }

    public int recieveTeamNumber() {
        return playerId % 2;
    }

    public boolean isReady() {
        return ready;
    }


    /*
     * TODO アクセス修飾子をつける
     */
    // MesgRecvThreadから呼ぶ
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setField(FieldClient field) {
        recievedFieldBuffer.offer(field);
    }

    public void setHuman(PlayerClient human) {
        recievedHumanBuffer.offer(human);
    }

    public void setReady() {
        ready = true;
    }

    public void setFinish() {
        isFinish = true;
        closeConnection(); // ここで接続を切る
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    // 成績を受け取る scoreにまだ何も入ってなかったらnull返すよ！
    // TODO nullはなるべく返さないようにしたい．nullオブジェクトとか使うか
    public common.Score recieveScore() {
        if (score == null) {
            System.err.println("TransmissionClient: warning: score is null");
        }
        return score;
    }

    public void createScore(int playerNum) {
        score = new common.Score(playerNum);
    }

    public common.Score getScore() {
        if (this.score == null) {
            System.err.println("tc: warning, score is null");
        }

        return score;
    }

    public void setColorPair(int colorPair) {
        this.colorPair = colorPair;
    }
}
