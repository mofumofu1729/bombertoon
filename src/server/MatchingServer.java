package server;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MatchingServer extends BasicGameState {
	boolean enterFinished = false;
	boolean isSendReady = false;
	private int state;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame sbg, int arg2) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ
		if (!(BattleServer.ts.isReady())) {
			return;
		} else if (!enterFinished) {
			// TODO debug
			// System.out.println("返された");
			return;
		} else {
			// 6/21 変更 okmt
			if (isSendReady == false) {
				// setInitialField(player, field);
				sbg.enterState(State.BATTLESERVER, new FadeOutTransition(Color.black, 1000),
						new FadeInTransition(Color.black, 1000));
				System.out.println("バトサに移動");
				// isSendReady = true;

			}
		}
	}

	public MatchingServer(int state) {
		this.state = state;
}

	@Override
	public int getID() {
		return state;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// debug
		System.out.println("enter MachingServer");
		
		enterFinished = true;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ
		enterFinished = false;
	}
}
