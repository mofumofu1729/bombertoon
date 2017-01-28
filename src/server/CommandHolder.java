package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Score;

public class CommandHolder extends Thread {
	private int numberOfConectedPlayer;// 接続しているメンバーの数

	public static final int PORT_NUMBER = 20000;
	public static final int MAX_BOMB = 3;
	public static final int MAX_PLAYER = common.Setting.P;

	private Socket[] incomingSocket;// 受付用のソケット
	private InputStreamReader[] recievedCommandReaders;// 入力ストリーム用の配列
	private BufferedReader[] bufferedRecieveCommandReaders;// バッファリングによりテキスト読み込み用の配列
	private PrintWriter[] commandSendWriters;// 出力ストリーム用の配列
	private ClientProcThread[] clientProcThreads;// スレッド用の配列

	// 受信したコマンドを保持しておくためのバッファ
	private Direction[] directionBuffer;
	private int[] bombsBuffer;

	private static CommandHolder selfInstance;
	
	private boolean isBattling = false; // 戦闘中かを示す

	// constructor
	private CommandHolder() {
		incomingSocket = new Socket[MAX_PLAYER];
		recievedCommandReaders = new InputStreamReader[MAX_PLAYER];
		bufferedRecieveCommandReaders = new BufferedReader[MAX_PLAYER];
		commandSendWriters = new PrintWriter[MAX_PLAYER];
		clientProcThreads = new ClientProcThread[MAX_PLAYER];

		numberOfConectedPlayer = 0;// 誰も接続していないのでメンバー数は０
		directionBuffer = new Direction[MAX_PLAYER]; // ユーザごとのキー入力を保管
		for (int i = 0; i < MAX_PLAYER; i++) // 初期化
			directionBuffer[i] = Direction.NONE;
		bombsBuffer = new int[MAX_PLAYER];
	}

	public static CommandHolder createInstance() {
		selfInstance = new CommandHolder();
		return selfInstance;
	}
	
	public static CommandHolder getInstance() {
		if (selfInstance == null)
			createInstance();
		return selfInstance;
	}

	// check
	public Direction checkHuman(int playerID) { // IDの人の動きを取得
		Direction res = directionBuffer[playerID];
		if (res != Direction.NONE) {
			directionBuffer[playerID] = Direction.NONE;
			// TODO debug
			System.out.println("checkHuman:" + playerID + ":" + res.name());
		} else {
			// TODO debug
			// System.out.println("checkHuman running");
		}
		return res;
	}

	public boolean checkBomb(int playerID) {
		if (bombsBuffer[playerID] == 0) {
			return false;
		} else {
			bombsBuffer[playerID]--;
			return true;
		}
	}

	// announce
	public void announceHuman(PlayerServer human) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			String status;
			if (human.isDeath()) {
				status = "DEAD";
			} else {
				status = "ALIVE";
			}

			commandSendWriters[i].println(
					"PLAYER:" + human.playerID + ":" + human.x + ":" + human.y + ":" + human.dir.name() + ":" + status);
			commandSendWriters[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// フィールドの変化をクライアントに知らせる
	public void announceChangeField(FieldServer field) {
		System.out.println("ACF");
		for (int i = 0; i < MAX_PLAYER; i++) {
			if (field.isExistBomb) { // もしisExistBombがtrueなら
				commandSendWriters[i].println("FIELD:" + field.x + ":" + field.y + ":" + "BOMB" + ":" + field.color.name());
			} else {
				commandSendWriters[i].println(
						"FIELD:" + field.x + ":" + field.y + ":" + field.status.name() + ":" + field.color.name());
			}
			commandSendWriters[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// プレイヤーが揃ったことをクライエントに知らせる
	// ゲームの（残り）時間を告知
	public void announceTime(int time) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			commandSendWriters[i].println("TIME:" + time);
			commandSendWriters[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
		}
	}

	// ゲームの終了を伝える
	public void announceFinish() {
		for (int i = 0; i < MAX_PLAYER; i++) {
			commandSendWriters[i].println("FINISH");
			commandSendWriters[i].flush();
		}
	}

	// ready
	public boolean isReady() {
		return isBattling;
	}

	/******************************* 追記部分(6/21) ***********************/
	// 成績を送る
	public void annouceScore(Score score) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			// debug
			System.out.println("in ts: score null?+" + score);

			// プレイヤー数を送る
			commandSendWriters[i].println("RESULT:PLAYERNUM:" + score.playerNum);
			commandSendWriters[i].flush();

			// 塗られたフィールド数を送る
			for (int j = 0; j < score.teamNum; j++)
				commandSendWriters[i].println("RESULT:FIELD:" + j + ":" + score.painted[j]);
			// キル数，デス数を送る
			for (int j = 0; j < score.playerNum; j++) {
				commandSendWriters[i].println("RESULT:KILL:" + j + ":" + score.kill[j]);
				commandSendWriters[i].println("RESULT:DEATH:" + j + ":" + score.death[j]);
			}
			commandSendWriters[i].flush();
		}
	}

	// ゲームの開始を伝える（色の組み合わせも送る）
	public void announceReady(int colorPair) {
		for (int i = 0; i < MAX_PLAYER; i++) {
			commandSendWriters[i].println("READY:COLOR:" + colorPair + ":ID:" + i);
			commandSendWriters[i].flush();
		}
	}


	// ゲームを終了させる
	public void finishGame()  {
		isBattling = false;
		Socket socket;
		try {
			socket = new Socket("127.0.0.1",PORT_NUMBER); // 追い返そうと待機しているサーバに接続
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	
	public void run() {
		int n = 0;

		try {
			ServerSocket server = new ServerSocket(PORT_NUMBER); // サーバ起動
			System.out.println("The server has launched!");
				// 待ち受け
				while (true) {
					incomingSocket[n] = server.accept(); // 接続要求をを待ち続ける
					System.out.println("Accept client No." + n);

					// 必要な入出力ストリームを作成する
					recievedCommandReaders[n] = new InputStreamReader(incomingSocket[n].getInputStream());
					bufferedRecieveCommandReaders[n] = new BufferedReader(recievedCommandReaders[n]);
					commandSendWriters[n] = new PrintWriter(incomingSocket[n].getOutputStream(), true);

					commandSendWriters[n].println("CONNECTED"); // 接続完了の合図
					
					clientProcThreads[n] = new ClientProcThread(this, n, incomingSocket[n], recievedCommandReaders[n], bufferedRecieveCommandReaders[n], commandSendWriters[n]);// 必要なパラメータを渡しスレッドを作成
					clientProcThreads[n].start();// スレッドを開始する
			
					n++;
					numberOfConectedPlayer = n;// メンバーの数を更新する

					if (numberOfConectedPlayer == MAX_PLAYER) { // 人数が揃ったら開始
						isBattling = true;
						break;
					}
				}

				// 戦闘中は接続依頼を追い返す
				while (isBattling) {
					System.out.println("追い返してる");
					Socket s = server.accept();
					(new PrintWriter(s.getOutputStream(), true)).println("BUSY");

					s.close();

					System.out.println("reject a connection"); // TODO debug
				}

				// 戦闘終了処理
				server.close();
				System.out.println("battle end"); // debug

		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: ");
			e.printStackTrace();
		}
	}

	// ClientProcThreadから呼び出す
	void setHuman(int playerID, Direction dir) {
		// TODO debug
		System.out.println("setHuman:" + playerID + ":" + dir.name());

		directionBuffer[playerID] = dir;
	}

	void setBomb(int playerID) {
		bombsBuffer[playerID]++;
	}
	
	

}