package server;

//爆破範囲を返すメソッドを含むクラス
//ArrayListを使うために必要
import java.util.ArrayList;

public class Postion {
	int x, y;// 初期位置

	public Postion(int x, int y) {
		this.x = x;
		this.y = y;
	}

	private int up = 3, down = 3, left = 3, right = 3;
	// ここの値を変更すると十字方向を伸ばせる

	public ArrayList<Postion> explosionAreaCount(FieldServer f[][]) {
		ArrayList<Postion> areaList = new ArrayList<Postion>();// 返り値用のリスト

		areaList.add(new Postion(this.x, this.y));// 初期位置の追加

		for (int i = 1; i <= up; i++) {// 上方向
			System.out.println(x + "," + (y - i));
			if (y - i < 0)
				break;
			else if (f[y - i][x].status == Status.BREAKABLE1) {
				areaList.add(new Postion(this.x, this.y - i));
				break;
			} else if (f[y - i][x].status != Status.NOTHING && f[y - i][x].status != Status.BOMBERING) {
				System.out.println(f[this.x][y - i].status + "  故にbreak");
				break;
			}
			areaList.add(new Postion(this.x, this.y - i));
		}

		for (int i = 1; i <= down; i++) {// 下方向
			if (y + i >= BattleServer.FIELDHEIGHT)
				break;
			else if (f[y + i][x].status == Status.BREAKABLE1) {
				areaList.add(new Postion(this.x, this.y + i));
				break;
			} else if (f[y + i][x].status != Status.NOTHING && f[y + i][x].status != Status.BOMBERING) {
				break;
			}
			areaList.add(new Postion(this.x, this.y + i));
		}

		for (int i = 1; i <= left; i++) {// 左方向
			System.out.println((x - i) + "," + y);
			if (x - i < 0)
				break;
			else if (f[y][x - i].status == Status.BREAKABLE1) {
				areaList.add(new Postion(this.x - i, this.y));
				break;
			} else if (f[y][x - i].status != Status.NOTHING && f[y][x - i].status != Status.BOMBERING) {
				System.out.println(f[this.x - i][y].status + "  故にbreak");
				break;
			}
			areaList.add(new Postion(this.x - i, this.y));
		}

		for (int i = 1; i <= down; i++) {// 右方向
			System.out.println((x + i) + "," + y);
			if (x + i >= BattleServer.FIELDHEIGHT)
				break;
			else if (f[y][x + i].status == Status.BREAKABLE1) {
				areaList.add(new Postion(this.x + i, this.y));
				break;
			} else if (f[y][x + i].status != Status.NOTHING && f[y][x + i].status != Status.BOMBERING) {
				System.out.println(f[this.x + i][y].status + "  故にbreak");
				break;
			}
			areaList.add(new Postion(this.x + i, this.y));
		}

		return areaList;
	}
}