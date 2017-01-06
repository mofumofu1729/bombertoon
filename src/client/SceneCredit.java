package client;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class SceneCredit extends BasicGameState {
	private int state;
	Image credit_image;
	// private Music credit;
	SceneCredit (int state) {
		this.state = state;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
			credit_image = new Image("res/client/img/Scene/credit/credit.png");

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
				credit_image.draw();
				arg2.drawString("Press Esc to return Menu",300,550);

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
			if (arg0.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
					arg1.enterState(State.MENU, new FadeOutTransition(Color.black, 1000),
					new FadeInTransition(Color.black, 1000));
			}
	}

	@Override
	public int getID() {
		return state;
	}

}
