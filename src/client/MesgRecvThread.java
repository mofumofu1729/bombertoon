package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.Color;

//メッセージ受信のためのスレッド
public class MesgRecvThread extends Thread {
	TransmissionClient tc;
	Socket socket;
	PrintWriter out;// 出力用のライター

	public MesgRecvThread(TransmissionClient tc, Socket s) {
		this.tc = tc;
		socket = s;
	}

	// 通信状況を監視し，受信データによって動作する
	public void run() {
		try {
			InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(sisr);
			out = new PrintWriter(socket.getOutputStream(), true);

			messageLoop: while (true) {
				String inputLine = br.readLine();
				if (inputLine != null) {
					int x, y;
					String[] command = inputLine.split(":");
					switch (command[0]) {
					case "FIELD":
						x = Integer.parseInt(command[1]);
						y = Integer.parseInt(command[2]);
						FieldClient field;
						if (command[3].equals("BOMB")) {
							field = new FieldClient(x, y, true, Status.NOTHING, strToColor(command[4]));
						} else {
							field = new FieldClient(x, y, false, strToStatus(command[3]), strToColor(command[4]));
						}

						tc.setField(field);
						break;
					case "PLAYER":
						int id = Integer.parseInt(command[1]);
						x = Integer.parseInt(command[2]);
						y = Integer.parseInt(command[3]);
						boolean isDeath;
						Direction dir = strToDirection(command[4]);
						if (command[5].equals("DEAD")) {
							isDeath = true;
						} else {
							isDeath = false;
						}

						tc.setHuman(new PlayerClient(id, x, y, dir, isDeath));
						break;
					case "READY":
						tc.setColorPair(Integer.parseInt(command[2]));
						tc.setPlayerId(Integer.parseInt(command[4]));
						tc.setReady();
						break;
					case "FINISH": // 終了
						tc.setFinish();
						
						break messageLoop; // メッセージ受信ループを抜ける
					case "TIME": // 時間を送る
						tc.setTime(Integer.parseInt(command[1]));

						// TODO debug
						// System.out.println("TIME:" + command[1]);
						break;
						
						
						// TODO tcからgetScoreするのは結合が強くなるのでまずそう
					case "RESULT": // 結果を送る
						// TODO debug
						System.out.println("recieve result:" + inputLine);

						switch (command[1]) {
						case "PLAYERNUM":
							tc.createScore(Integer.parseInt(command[2]));
							break;
						case "FIELD":
							tc.getScore().painted[Integer.parseInt(command[2])] = Integer.parseInt(command[3]);

							// TODO debug
							System.out.println("recieve field:" + inputLine);

							break;
						case "KILL":
							tc.getScore().kill[Integer.parseInt(command[2])] = Integer.parseInt(command[3]);
							break;
						case "DEATH":
							tc.getScore().death[Integer.parseInt(command[2])] = Integer.parseInt(command[3]);
							break;
						}
						break;
					default:
						System.err.println("MesgRecvThread: unknown command");
						break;
					}

				} else {
					break;
				}
			}
			socket.close();
		} catch (IOException e) {
			System.err.println("エラーが発生しました: " + e);
		}
	}

	private Status strToStatus(String str) {
		if (str.equals(Status.BOMBERING.name())) { // switch文はcase
													// 定数:でなくてはならないのでif
			return Status.BOMBERING;
		} else if (str.equals(Status.BREAKABLE1.name())) {
			return Status.BREAKABLE1;
		} else if (str.equals(Status.BREAKABLE2.name())) {
			return Status.BREAKABLE2;
		} else if (str.equals(Status.BREAKABLE3.name())) {
			return Status.BREAKABLE3;
		} else if (str.equals(Status.MUTEKI.name())) {
			return Status.MUTEKI;
		} else if (str.equals(Status.NOTHING.name())) {
			return Status.NOTHING;
		} else {
			return Status.UNBREAKABLE; // 本当は全てのそれ以外のelseで例外を投げたりしたいが・・・
		}
	}

	private Color strToColor(String str) {
		if (str.equals(Color.Blue.name())) {
			return Color.Blue;
		} else if (str.equals(Color.Green.name())) {
			return Color.Green;
		} else if (str.equals(Color.Red.name())) {
			return Color.Red; // TODO 他の色の分も追加する
		} else if (str.equals(Color.Transparent.name())) {
			return Color.Transparent;
		} else if (str.equals(Color.Purple.name())) {
			return Color.Purple;
		} else if (str.equals(Color.Orange.name())) {
			return Color.Orange;
		} else if (str.equals(Color.Pink.name())) {
			return Color.Pink;
		} else if (str.equals(Color.LimeGreen.name())) {
			return Color.LimeGreen;
		} else if (str.equals(Color.Turquoise.name())) {
			return Color.Turquoise;
		}

		else {
			return null;
		}
	}

	private Direction strToDirection(String str) {
		if (str.equals(Direction.LEFT.name())) { // switch文はcase
													// 定数:でなくてはならないのでif
			return Direction.LEFT;
		} else if (str.equals(Direction.RIGHT.name())) {
			return Direction.RIGHT;
		} else if (str.equals(Direction.UP.name())) {
			return Direction.UP;
		} else if (str.equals(Direction.DOWN.name())) {
			return Direction.DOWN;
		} else if (str.equals(Direction.TURN_LEFT.name())) {
			return Direction.TURN_LEFT;
		} else if (str.equals(Direction.TURN_RIGHT.name())) {
			return Direction.TURN_RIGHT;
		} else if (str.equals(Direction.TURN_UP.name())) {
			return Direction.TURN_UP;
		} else if (str.equals(Direction.TURN_DOWN.name())) {
			return Direction.TURN_DOWN;
		} else {
			return Direction.NONE;
		}

	}
}
