package server;

import java.io.File;
import java.io.IOException;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ServerStarter extends StateBasedGame {
	public final static int FPS = 60;

	public ServerStarter(String name) throws IOException {
		super(name);
		this.addState(new BattleServer(State.BATTLESERVER));

	}

	public void initStatesList(GameContainer gc) throws SlickException {
	//	this.getState(State.BATTLESERVER).init(gc, this);
		this.enterState(State.BATTLESERVER);
	}

	public static void main(String[] args) throws SlickException, IOException {
		// この行はちょっと怪しいので後で直す
		/*
		System.setProperty("org.lwjgl.librarypath",
				new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName())
						.getAbsolutePath());
		*/

		AppGameContainer app = new AppGameContainer(new ServerStarter("server"));
		app.setDisplayMode(800, 600, false);
		app.setTargetFrameRate(FPS);
		app.start();
	}

}
