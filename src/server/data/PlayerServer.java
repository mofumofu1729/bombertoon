package server.data;

import server.BattleServer;
import server.Bomb;
import server.FieldServer;
import server.TransmissionServer;
import server.constants.Color;
import server.constants.Direction;
import server.constants.Status;

public class PlayerServer {
	/*
	 * 作成日 6/1 作成者 道券裕二 // ただしのちに更に色々変更あり
	 */
    public int killTimes;// キル数
    public int deathTimes;// デス数
    public int bombCount;// 置いたボムの数
    public int x, y;// 座標
	int initialX, initialY; // 復活座標

    public boolean death;// 死亡状態
    public int rebornCount;// 復活までの
    public Direction dir;// 向き
	Color team;// 所属のチーム
    public int playerID;// プレーヤーの識別用
	TransmissionServer ts;

    public PlayerServer(int x, int y, Color color, int playerID, Direction dir,
            TransmissionServer ts) {
		// Todo:コンストラクタの引数を減らせないか
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.team = color;
		this.playerID = playerID;
		this.ts = ts;
		initialX = x;
		initialY = y;
		killTimes = 0;
		deathTimes = 0;
		bombCount = 0; // 0から始まり増えていく

		// TODO debug
		System.out.println("PlayerServer#PlayerServer:playerID=" + playerID);
	}

	public void putBomb(FieldServer[][] field) {
		if (bombCount <= 3) {
			// TODO debug
			System.out.println("putBomb");
			int xTmp = x, yTmp = y;

			if (dir == Direction.UP) {
				yTmp--;
			} else if (dir == Direction.DOWN) {
				yTmp++;
			} else if (dir == Direction.RIGHT) {
				xTmp++;
			} else if (dir == Direction.LEFT) {
				xTmp--;
			} else {
			}

			if (xTmp >= 0 && xTmp < BattleServer.FIELDWIDTH && yTmp >= 0 && yTmp < BattleServer.FIELDHEIGHT
					&& !(field[yTmp][xTmp].isExistBomb) && !(field[yTmp][xTmp].isExistHuman)
					&& (field[yTmp][xTmp].status) == Status.NOTHING) {
                field[yTmp][xTmp].bomb = new Bomb(playerID, team, x, y);
				field[yTmp][xTmp].isExistBomb = true;
				bombCount++;
				ts.announceChangeField(field[yTmp][xTmp]);
			}
		}
	}

	public void move(Direction move_dir, FieldServer[][] field) {

		this.dir = move_dir;
		int xTmp = x, yTmp = y;

		field[y][x].isExistHuman = false;

		if (move_dir == Direction.UP) {
			yTmp--;
		} else if (move_dir == Direction.DOWN) {
			yTmp++;
		} else if (move_dir == Direction.RIGHT) {
			xTmp++;
		} else if (move_dir == Direction.LEFT) {
			xTmp--;
		} else if (move_dir == Direction.TURN_UP) {
			this.dir = Direction.UP;
		} else if (move_dir == Direction.TURN_DOWN) {
			this.dir = Direction.DOWN;
		} else if (move_dir == Direction.TURN_RIGHT) {
			this.dir = Direction.RIGHT;
		} else if (move_dir == Direction.TURN_LEFT) {
			this.dir = Direction.LEFT;
		}
		// 動けるかどうか判定して，移動

		if (yTmp >= 0 && yTmp <= (BattleServer.FIELDHEIGHT - 1) && xTmp <= (BattleServer.FIELDWIDTH - 1) && xTmp >= 0
				&& !(field[yTmp][xTmp].isExistBomb) // 先に添字範囲判定していないと配列の添字範囲超える
				&& !(field[yTmp][xTmp].isExistHuman)
				&& (field[yTmp][xTmp].status == Status.NOTHING
						|| ((field[yTmp][xTmp].status == Status.MUTEKI) && (field[yTmp][xTmp].color == this.team))
						|| field[yTmp][xTmp].status == Status.BOMBERING)) {
			// System.out.println("行こうとしているところの色は"+field[yTmp][xTmp].color.toString());
			x = xTmp;
			y = yTmp;
		}
		field[y][x].isExistHuman = true;

		ts.announceHuman(this);
	}

	public void killing(PlayerServer deadman, PlayerServer killer) {// playerID=殺害した人で、自分自身を死んだ処理
		// TODO ::自爆した場合のデス数キル数の処理
		this.death = true;// 死亡
		System.out.println("死んだよ");

		deadman.deathTimes++;// 死亡回数のカウント
		if (killer.playerID != deadman.playerID) { // ここがヘンだよ
			killer.killCount();
			System.out.println("殺したよ");
		}
		//
		ts.announceHuman(this);
	}

	public void killCount() {
		killTimes++;
	}

	public boolean isDeath() {
		return death;
	}

	public int getKill() {
		return this.killTimes;
	}

	public int getDeath() {
		return this.deathTimes;
	}

	public void revive() {
		switch (this.playerID) { // TODO:マシなコードに書き直す
		case 0:
			this.x = 0;
			this.y = 0;

			break;
		case 1:
			this.x = 15;
			this.y = 0;
			break;
		case 2:
			this.x = 15;
			this.y = 15;
			break;
		case 3:
			this.x = 0;
			this.y = 15;
			break;
		default:
			break;
		}
		this.death = false;
		ts.announceHuman(this);

	}
}
