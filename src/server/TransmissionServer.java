package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Common.Score;

public class TransmissionServer extends Thread {
	private int member;// 接続しているメンバーの数

	public static final int PORT = 10000;
	public static final int MAX_BOMB = 3;
	public static final int MAX_PLAYER = Common.Setting.P;

	private Socket[] incoming;// 受付用のソケット
	private InputStreamReader[] isr;// 入力ストリーム用の配列
	private BufferedReader[] in;// バッファリングによりテキスト読み込み用の配列
	private PrintWriter[] out;// 出力ストリーム用の配列
	private ClientProcThread[] myClientProcThread;// スレッド用の配列

	private Direction[] direction;
	private int[] bombs;

	private static TransmissionServer instance;

	// TODO debug
	static int numInstance = 0;

	// constructor
	private TransmissionServer() {
		incoming = new Socket[MAX_PLAYER];
		isr = new InputStreamReader[MAX_PLAYER];
		in = new BufferedReader[MAX_PLAYER];
		out = new PrintWriter[MAX_PLAYER];
		myClientProcThread = new ClientProcThread[MAX_PLAYER];

		member = 0;// 誰も接続していないのでメンバー数は０
		direction = new Direction[MAX_PLAYER]; // ユーザごとのキー入力を保管
		for (int i = 0; i < MAX_PLAYER; i++) // 初期化
			direction[i] = Direction.NONE;
		bombs = new int[MAX_PLAYER];

	}

	public static synchronized TransmissionServer getInstance() {
		if (instance == null) {
			instance = new TransmissionServer();
		}
		return instance;
	}

	// check
	public Direction checkHuman(int playerID) { // IDの人の動きを取得
		Direction res = direction[playerID];
		if (res != Direction.NONE) {
			direction[playerID] = Direction.NONE;
			// TODO debug
			System.out.println("checkHuman:" + playerID + ":" + res.name());
		} else {
			// TODO debug
			// System.out.println("checkHuman running");
		}
		return res;
	}

	public boolean checkBomb(int playerID) {
		if (bombs[playerID] == 0) {
			return false;
		} else {
			bombs[playerID]--;
			return true;
		}
	}

	// announce
	public void announceHuman(PlayerServer human) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			String str;
			if (human.isDeath()) {
				str = "DEAD";
			} else {
				str = "ALIVE";
			}

			out[i].println(
					"PLAYER:" + human.playerID + ":" + human.x + ":" + human.y + ":" + human.dir.name() + ":" + str);
			out[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// フィールドの変化をクライアントに知らせる
	public void announceChangeField(FieldServer field) {
		System.out.println("ACF");
		for (int i = 0; i < MAX_PLAYER; i++) {
			if (field.isExistBomb) { // もしisExistBombがtrueなら
				out[i].println("FIELD:" + field.x + ":" + field.y + ":" + "BOMB" + ":" + field.color.name());
			} else {
				out[i].println(
						"FIELD:" + field.x + ":" + field.y + ":" + field.status.name() + ":" + field.color.name());
			}
			out[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// プレイヤーが揃ったことをクライエントに知らせる
	// ゲームの（残り）時間を告知
	public void announceTime(int time) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			out[i].println("TIME:" + time);
			out[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// ゲームの終了を伝える
	public void announceFinish() {
		for (int i = 0; i < MAX_PLAYER; i++) {
			out[i].println("FINISH");
			out[i].flush();
		}
	}


	// ready
	public boolean isReady() {
		// TODO debug
		// System.out.println("member:"+member+",max_player:"+MAX_PLAYER);
		if (member == MAX_PLAYER) {
			return true;
		} else {
			return false;
		}
	}

	/*******************************追記部分(6/21)***********************/
	// 成績を送る
	public void annouceScore(Score score) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			// debug
			System.out.println("in ts: score null?+"+score);


			// プレイヤー数を送る
			out[i].println("RESULT:PLAYERNUM:"+score.playerNum);
			out[i].flush();

			// 塗られたフィールド数を送る
			for (int j=0; j<score.teamNum; j++)
				out[i].println("RESULT:FIELD:" + j + ":" + score.painted[j]);
			// キル数，デス数を送る
			for (int j=0; j<score.playerNum; j++) {
				out[i].println("RESULT:KILL:" + j + ":" + score.kill[j]);
				out[i].println("RESULT:DEATH:" + j + ":" + score.death[j]);
			}
			out[i].flush();
		}
	}

	// ゲームの開始を伝える（色のペアの番号を送るよ）
	public void announceReady(int colorPairNum) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			out[i].println("READY:COLOR:"+colorPairNum);
			out[i].flush();
		}
	}

	/******************************************************/



	public void run() {
		int n = 0;

		try {
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(PORT);// 10000番ポートを利用する

			while (true) {
				incoming[n] = server.accept(); // 接続要求をを待ち続ける
				System.out.println("Accept client No." + n);

				// 必要な入出力ストリームを作成する
				isr[n] = new InputStreamReader(incoming[n].getInputStream());
				in[n] = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

				myClientProcThread[n] = new ClientProcThread(this, n, incoming[n], isr[n], in[n], out[n]);// 必要なパラメータを渡しスレッドを作成
				myClientProcThread[n].start();// スレッドを開始する

				n++;
				member = n;// メンバーの数を更新する


				// 6/21変更 ゲーム開始の合図はGameClient側で送るようにする
				/*
				if (member == MAX_PLAYER) {
					announceReady();

					break;
				}
				*/

			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: ");
			e.printStackTrace();
		}
	}

	// ClientProcThreadから呼び出す
	void setHuman(int playerID, Direction dir) {
		// TODO debug
		System.out.println("setHuman:" + playerID + ":" + dir.name());

		direction[playerID] = dir;
	}

	void setBomb(int playerID) {
		bombs[playerID]++;
	}

}