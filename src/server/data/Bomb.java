package server.data;

import java.util.ArrayList;
import server.BattleServer;
import server.FieldServer;
import server.constants.Color;

public class Bomb {
    public int playerID;
	private Color color;
	public int explosionCount;
	private int x, y;
    public int count = 3000; // 爆発までのカウント
    public int refreshCount = 2000; // 爆風が消えるまでのカウント
	public static final int bombInitial = 3000;
	public static final int refreshInitial = 2000;
	boolean isFireSource;
	Postion p;
	ArrayList<Postion> pList;
	Postion tempP;

    public Bomb(int id, Color c, int x, int y) {

		playerID = id;
		color = c;
		this.x = x;
		this.y = y;
		refreshCount = refreshInitial;
		count = bombInitial;

	}

	/*
	 * private void countdown(int explosionCount) {
	 *
	 * int explosionArea = 5; // 爆発の範囲
	 *
	 * while (explosionCount > 0) { // カウントを1秒ずつ減らしていく操作// }
	 *
	 * // カウントが0になったら、周囲のマスを引数にして、explosion()を呼ぶ。 for (int i = -explosionArea /
	 * 2; i <= explosionArea / 2; i++) { this.explosion(field[x + i][y]);
	 * this.explosion(field[x][y + i]); } }
	 */

	public void bombExplosion(int playerID, int x, int y, FieldServer fs[][]) { // TODO
		p = new Postion(x, y);
		// 爆発の範囲の変更/追加
		// 指定範囲をfieldexplosionで爆発させて変化させる。
		pList = p.explosionAreaCount(fs);
		while (pList.isEmpty() == false) {// 要素があるかぎり爆発させ続ける
			tempP = pList.remove(0);
			// fs[y][x].fieldExplosion(playerID);

			if ((tempP.y) < BattleServer.FIELDHEIGHT) // フィールドからはみ出してないかチェック
				// fs[y + 1][x].fieldExplosion(playerID);
				if ((tempP.y) >= 0)
					// fs[y - 1][x].fieldExplosion(playerID);
					if ((tempP.x) < BattleServer.FIELDWIDTH)
						// fs[y][x + 1].fieldExplosion(playerID);
						if ((tempP.x) >= 0) {
						fs[tempP.y][tempP.x].fieldExplosion(playerID);
						}
			// fs[y][x - 1].fieldExplosion(playerID);
		}
	}

	public void resetExplosion(int playerID, int x, int y, FieldServer fs[][]) { // TODO
		// 爆発の範囲の変更/追加
		p = new Postion(x, y);

		// 指定範囲のfieldexplosionで爆発させて変化させる
		pList = p.explosionAreaCount(fs);
		while (pList.isEmpty() == false) {// 要素があるかぎり爆発させ続ける
			tempP = pList.remove(0);
			// fs[y][x].fieldExplosion(playerID);

			if ((tempP.y) < BattleServer.FIELDHEIGHT) // フィールドからはみ出してないかチェック
				// fs[y + 1][x].fieldExplosion(playerID);
				if ((tempP.y) >= 0)
					// fs[y - 1][x].fieldExplosion(playerID);
					if ((tempP.x) < BattleServer.FIELDWIDTH)
						// fs[y][x + 1].fieldExplosion(playerID);
						if ((tempP.x) >= 0) {
						fs[tempP.y][tempP.x].fieldresetExplosion(playerID);
						}

			// fs[y][x - 1].fieldExplosion(playerID);
		}
	}

}