package client.data;

import client.constants.Direction;

public class PlayerClient {
    public int playerID;
    public int x;
    public int y;
    public Direction dir; // 向き
    public boolean isDeath;


    public PlayerClient(int playerID, int x, int y, Direction dir, boolean isDeath) {
        this.playerID = playerID;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.isDeath = isDeath;
    }

    public String toString() {
        return ("playerID:" + playerID + ",x:" + x + ",y:" + ",dir:" + dir.name() + ",isDeath:"
                + isDeath);
    }
}
