package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Queue;

import common.Setting;

public class TransmissionClient {
	private PrintWriter out;// 出力用のライター
	private boolean ready = false; // 準備出来たか

	private Queue<PlayerClient> recievedHumanBuffer;
	private Queue<FieldClient> recievedFieldBuffer;

	public static final int MAX_PLAYER = Setting.P;
	private boolean isFinish = false; // 終了
	private int time; // 経過時間か何か
	static String hostName="localhost";
	private common.Score score; // 成績
	private int colorPair;

	public TransmissionClient() {
		this(hostName, server.TransmissionServer.PORT);
	}

	public TransmissionClient(String hostname, int portnumber) {
		recievedHumanBuffer = new ArrayDeque<PlayerClient>();
		recievedFieldBuffer = new ArrayDeque<FieldClient>();

		// サーバに接続する
		Socket socket = null;
		try {

			socket = new Socket(hostname, portnumber);
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			System.err.println("エラーが発生しました: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(this, socket);
		mrt.start();
	}

	// send
	public void sendHuman(Direction dir) {
		out.println(dir.name());
	}

	public void sendBomb() {
		// TODO debug
		// System.out.println("SBBBBBBBBBBBBBBBBBBBBBBB");

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

	public boolean isReady() {
		return ready;
	}
	public void setHostName(String hostName){
		this.hostName=hostName;


	}

	// MesgRecvThreadから呼ぶ
	void setField(FieldClient field) {
		recievedFieldBuffer.offer(field);
	}

	void setHuman(PlayerClient human) {
		recievedHumanBuffer.offer(human);
	}

	void setReady() {
		ready = true;
	}

	void setFinish() {
		isFinish = true;
	}

	void setTime(int time) {
		this.time = time;
	}

	/*******************追記部分(6/21)**********************/
	// 成績を受け取る scoreにまだ何も入ってなかったらnull返すよ！
	public common.Score recieveScore() {
		if (score == null) {
			System.err.println("TransmissionClient: warning: score is null");
		}
		return score;
	}


	// 内部的に使う
	void createScore(int playerNum) {
		score = new common.Score(playerNum);
	}

	common.Score getScore() {
		if (this.score == null) {
			System.err.println("tc: warning, score is null");
		}

		return score;
	}

	void setColorPair(int colorPair) {
		this.colorPair = colorPair;
	}

	// 色のペアを返す
	int recieveColorPair() {
		return this.colorPair;
	}

	/***************************************************/
}
