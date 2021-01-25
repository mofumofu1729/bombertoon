package server;

import java.io.IOException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import server.constants.State;
import server.scenes.BattleServer;
import server.scenes.MatchingServer;

public class ServerStarter extends StateBasedGame {
    public final static int FPS = 60;

    public ServerStarter(String name) throws IOException {
        super(name);
        this.addState(new MatchingServer(State.MATCHINGSERVER)); // 一番最初に登録された状態が呼ばれる
        this.addState(new BattleServer(State.BATTLESERVER));
    }

    public void initStatesList(GameContainer gc) throws SlickException {
        this.getState(State.MATCHINGSERVER).init(gc, this);
        this.getState(State.BATTLESERVER).init(gc, this);
    }

    public static void main(String[] args) throws SlickException, IOException {
        AppGameContainer app = new AppGameContainer(new ServerStarter("server"));
        app.setDisplayMode(800, 600, false);
        app.setTargetFrameRate(FPS);
        app.start();
    }

}
