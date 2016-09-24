package client;

import java.io.File;
import java.io.IOException;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ClientStarter extends StateBasedGame {
	public final static int FPS = 60;
	private static TransmissionClient tc; // 一つのクライアントで一つの送信機

	public ClientStarter(String name) throws IOException {
		super(name);
		this.addState(new GameClient(State.GAMECLIENT));
		this.addState(new Top(State.TOP));
		this.addState(new Result(State.RESULT));

		this.addState(new Menu(State.MENU));
		this.addState(new Config(State.CONFIG));
		this.addState(new Credit(State.CREDIT));
		this.addState(new Matching(State.MATCHING));
		this.addState(new Rating(State.RATING));
	}

	public void initStatesList(GameContainer gc) throws SlickException {
		this.getState(State.GAMECLIENT).init(gc, this);
		this.getState(State.TOP).init(gc, this);
		this.getState(State.RESULT).init(gc, this);

		this.getState(State.MENU).init(gc, this);
		this.getState(State.CONFIG).init(gc, this);
		this.getState(State.CREDIT).init(gc, this);
		this.getState(State.MATCHING).init(gc, this);
		this.getState(State.RATING).init(gc, this);
		this.getState(State.CONFIG).init(gc, this);

		// this.enterState(State.GAMECLIENT);

		this.enterState(State.TOP);

	}

	public static void main(String[] args) throws SlickException, IOException {
		// TODO ちょっとこの行は怪しいので後で直す
		/* System.setProperty("org.lwjgl.librarypath",
				new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName())
						.getAbsolutePath()); */

		AppGameContainer app = new AppGameContainer(new ClientStarter("client"));
		app.setDisplayMode(800, 600, false);
		app.setTargetFrameRate(FPS);
		app.start();
	}

	// TransmissionClinet

	public static void createTransmissionClient() {
		ClientStarter.tc = new TransmissionClient();
	}

	public static void createTransmissionClient(String hostname, int portNumber) {
		ClientStarter.tc = new TransmissionClient(hostname, portNumber);
	}

	public static TransmissionClient getTransmissionClient() {
		if (tc == null) {
			System.err.println("client-server tc null");
		}

		return ClientStarter.tc;
	}

}
