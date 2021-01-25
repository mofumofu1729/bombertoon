package server.scenes;

import java.util.Random;

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
//TODO マッチングと対戦とリザルトがくっついているからそれをバラけさせる
//内部処理はこいつの管轄
public class BattleServer extends BasicGameState {
	public static final int PLAYERNUMBER = common.Setting.P;
	public static final int TIMELIMIT = 20000; // 制限時間
	// フィールドサイズ
	public static final int FIELDHEIGHT = 16;
	public static final int FIELDWIDTH = 16;
	common.Score score = new common.Score(4);

	private int state;
	private boolean enterFinished = false; // 画面遷移が終わったか
	private boolean isSendReady = false; // 開始の合図を既に送ったか
	private boolean isSent = false;
	int gameTimer;// ゲーム自体の残り時間
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
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
	}

	/**************** ゲームループ！ *******************/
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int dlt) throws SlickException {
		// TODO debug
		// System.out.println("test dlt:" + dlt);
		// System.out.println("1,1の状態は" + field[1][1].isExistHuman);
		// System.out.println(player[0].team.toString());
		// System.out.println(player[1].team.toString());

		//ここからがマッチングに必要な部分

		/*
		if (!ts.isReady()) {
			return;
		} else if (!enterFinished) {
			// TODO debug
			// System.out.println("返された");
			return;
		} else {
			// 6/21 変更 okmt
			if (isSendReady == false) {
				setInitialField(player,field);
				isSendReady = true;
			}
		}
*/
		//ここまでがマッチングに必要な部分

		//// 対戦毎に必要な初期設定。ここまで。

		// System.out.println("フィールドのテスト");
		// System.out.println(field[0][0]);
		// .out.println("終わり");

		gameTimer -= dlt;
		ts.announceTime(gameTimer); // ここで残り時間を伝える
		if (gameTimer / 1000 <= 0 && isSent == false) { // ゲーム時間が終わったら // 画面遷移
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

			for (int i = 0; i < 4; i++) {
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
				Thread.sleep(500);  // 同じポート番号が使えるまで少し時間がかかるので待機
									// TODO 本当はもう少し安全な方法を使うべき
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			sbg.enterState(State.MATCHINGSERVER); 

		}
		for (int i = 0; i < PLAYERNUMBER; i++) {
			// 人の動きに関する部分 ここから↓
			if (player[i].death != true) { // 生きていたら動く
				Direction dir;
				if ((dir = ts.checkHuman(i)) != Direction.NONE) {
					// TODO debug
					// System.out.println("BattleServer:move:"+i+":"+dir.name());

					player[i].move(dir, field); // 入力に応じて人が動く

					// TODO debug annouceResultのテスト
					// System.out.println("test score");
					// Common.Score score = new Common.Score(4);
					// System.out.println("is score null?:"+score);
					// ts.annouceScore(score);

				}

				// 人の動きに関する部分 ここまで
				if (ts.checkBomb(i)) { // checkbomb→iさんが爆弾を置いたかをチェック
					player[i].putBomb(field); // putbomb→人の状態を読み取りそれに応じて内部処理的に爆弾を置く
				}

				// 死を司るところ ここから↓
				if (field[player[i].y][player[i].x].status == Status.BOMBERING) { // もし当たり判定のところに人がいたら
					////////////// 修正!!!!!!!!!!!!!!!!!!!!!!!!!!
					player[i].killing(player[i], player[field[player[i].y][player[i].x].explodeID]); // そいつは死ぬ
																										// 第一引数→死者
																										// 第2引数→殺人者のプレイヤー
					////////////// 修正// //!!!!!!!!!!!!!!!!!!!!!!
					System.out.println("殺人の発生");
					// TODO
					// 引数は殺した奴だと思うぞい
					player[i].death = true;
					field[player[i].y][player[i].x].isExistHuman = false; // そこに人はいない
					player[i].rebornCount = 2000;// 2000 カウント後に復活

				}

				// 死を司るところ ここまで↑

				// 復活を司るところ ここから↓

			} else if (player[i].death == true) {
				player[i].rebornCount -= dlt;
				if (player[i].rebornCount < 0) {
					player[i].revive(); // revive() 復活カウントが0になったら復活
				}

			}
			// 死を司るところ ここまで↑

		}

		// フィールドを捜査して爆発をさせるところ ここから↓

		for (int y = 0; y < FIELDHEIGHT; y++) {
			for (int x = 0; x < FIELDHEIGHT; x++) {
				if (field[y][x].isExistBomb == true) {
					field[y][x].bomb.count -= dlt; // 爆弾があったら時間を減らす→爆発へのカウントダウン

					if (field[y][x].bomb.count < 0) { // 爆発
						field[y][x].bomb.bombExplosion(field[y][x].bomb.playerID, x, y, field); // フィールドの変化はexpolosion内でClientにannounceする
						field[y][x].isExistBomb = false;
						field[y][x].bomb.count = Bomb.bombInitial;
						field[y][x].isFireSource = true;
						player[field[y][x].bomb.playerID].bombCount--;
					}
				}

				else if (field[y][x].isFireSource == true) { // 火元だったらカウントを減らし、0になったら周りを鎮火させる!!!!!!!!!!!新しい変数!!!!!!!!!
					field[y][x].bomb.refreshCount -= dlt; // 爆弾があったら時間を減らす→爆発へのカウントダウン

					// TODO debug
					// System.out.println("isFireSourse? Yes!");

					if (field[y][x].bomb.refreshCount < 0) { // 爆風が消える
						field[y][x].bomb.resetExplosion(field[y][x].bomb.playerID, x, y, field);
						// フィールドの変化はexplosion内でClientにannounceする
						field[y][x].status = Status.NOTHING;
						field[y][x].bomb.refreshCount = Bomb.refreshInitial;
						field[y][x].isFireSource = false;
					}
				}

			}

		}

		// 爆発をさせるところ ここまで↑
		// System.out.println("p1の死は" + player[0].deathTimes + "p1の殺しは" +
		// player[0].killTimes);
		// System.out.println("p2の死は" + player[1].deathTimes + "p2の殺しは" +
		// player[1].killTimes);
	}

	public void enter(GameContainer container, StateBasedGame game) throws SlickException {

		fieldData = Setting.STAGE4TWO; // フィールドのパラメータ
		ts = TransmissionServer.getInstance();
		gameTimer = TIMELIMIT;

		player = new PlayerServer[PLAYERNUMBER];
		field = new FieldServer[FIELDHEIGHT][FIELDWIDTH];
		
		// debug
		System.out.println("enter BattleServer");
		
		setInitialField(player,field);
		//enterFinished = true;
	}

	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		this.setInitialValue();
	}

	public void gameResult() {
		for (int player = 0; player < PLAYERNUMBER; player++) {

		}

	}

	private void setInitialValue() {
		this.enterFinished = false; // 画面遷移が終わったか
		this.isSendReady = false; // 開始の合図を既に送ったか
		this.isSent = false;
	}



	private void setInitialField(PlayerServer p[],FieldServer f[][]) {
		// 対戦毎に必要な初期設定。ここから。
		Random r = new Random();
		int colorPair = r.nextInt(4); // プレイヤー色の組み合わせ
		
		// TODO global変数と同じ理由で，下二行staticフィールドじゃなくてローカル変数使うべきだと思われ
		Setting.ColorTeam1 = common.ColorPair.getColorPair(colorPair)[0];
		Setting.ColorTeam2 = common.ColorPair.getColorPair(colorPair)[1];
		
		for (int i = 0; i < PLAYERNUMBER; i++) {
			switch (i) { 
			case 0:
				p[i] = new PlayerServer(0, 0, Setting.ColorTeam1, 0, Direction.DOWN, ts);
				break;
			case 1:
				p[i] = new PlayerServer(FIELDHEIGHT - 1, 0, Setting.ColorTeam2, 1, Direction.DOWN, ts);
				break;
			case 2:
				p[i] = new PlayerServer(FIELDHEIGHT - 1, FIELDHEIGHT - 1, Setting.ColorTeam1, 2, Direction.DOWN,
						ts);
				break;
			case 3:
				p[i] = new PlayerServer(0, FIELDHEIGHT - 1, Setting.ColorTeam2, 3, Direction.DOWN, ts);
				break;
			}

			// player[0] = new PlayerServer(0, 0, Color.Blue, 0,
			// Direction.DOWN,
			// ts); // tsが引数にあるのはannouncehumanで自分の死を伝える必要があるため
			// player[1] = new PlayerServer(FIELDHEIGHT - 1, FIELDHEIGHT
			// - 1,
			// Color.Green, 1, Direction.UP, ts);

			// player[0].initialX = 0;
			// player[0].initialY = 0;

			// player[1].initialX = FIELDWIDTH - 1;
			// player[1].initialY = FIELDHEIGHT - 1;

		}

		for (int y = 0; y < FIELDHEIGHT; y++) {// ここで障害物と無敵エリアを設定する
			for (int x = 0; x < FIELDHEIGHT; x++) {
				switch (fieldData[x + y * FIELDWIDTH]) {
				case 0:
					f[y][x] = new FieldServer(Status.NOTHING, x, y, ts, Color.Transparent);
					break;
				case 1:

					f[y][x] = new FieldServer(Status.BREAKABLE1, x, y, ts, Color.Transparent);
					break;
				case 2:
					f[y][x] = new FieldServer(Status.UNBREAKABLE, x, y, ts, Color.Transparent);
					break;

				case -1:
					f[y][x] = new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam1);// TODO
					// ちゃんと任意の色への無敵地点とする
					break;
				case -2:
					f[y][x] = new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam2);
					break;
				case -3:
					f[y][x] = new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam1);
					break;
				case -4:
					f[y][x] = new FieldServer(Status.MUTEKI, x, y, ts, common.Setting.ColorTeam2);

					break;

				}
			}
		}

		ts.announceReady(colorPair); // クライアントにゲーム開始の合図とプレイヤー色の組み合わせを送る

	}



	@Override
	public int getID() {
		return state;
	}

}
