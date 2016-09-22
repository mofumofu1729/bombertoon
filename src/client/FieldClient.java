package client;

import server.Color;

public class FieldClient {
	int x;
	int y;
	boolean isExistBomb;
	int bombCount = 2800;
	Status status;
	Color color;

	FieldClient(int x, int y, boolean isExistBomb, Status status, Color color) {
		this.x = x;
		this.y = y;
		this.isExistBomb = isExistBomb;
		this.status = status;
		this.color = color;
	}
}
