package client;

import server.Color;

public class FieldClient {
	public int x;
	public int y;
	public boolean isExistBomb;
	public int bombCount = 2800;
	public Status status;
	public Color color;


	public FieldClient(int x, int y, boolean isExistBomb, Status status, Color color) {
		this.x = x;
		this.y = y;
		this.isExistBomb = isExistBomb;
		this.status = status;
		this.color = color;
	}
}
