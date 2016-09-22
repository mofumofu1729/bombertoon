package client;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Menu extends BasicGameState {
	private final int MODENUMBERS = 4;
	private int state;
	// private Music bgm;



	private Image background;
	private int decide; // 選択される状態
	private int nextState = State.MATCHING;
	private int dsum;

	private Image oneb;
	private Image onew;
	private Image twob;
	private Image twow;
	private Image conb;
	private Image conw;
	private Image creb;
	private Image crew;
	private Image mbar;

	private Music menu;
	private Sound select;
	private Sound decision;

	Menu(int state) {
		this.state = state;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		// bgm = new Music("res/takonaguri.ogg");

		decide = 0;
		dsum = 0;

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		arg2.drawImage(background, 0, 0);
		//arg2.drawString("Menu", 100, 100);
		oneb.draw(484, 115);
		twob.draw(493, 165);
		conb.draw(488, 215);
		creb.draw(493, 265);
		switch (decide) {
		case 0:
			mbar.draw(458, 95);
			onew.draw(537, 122);
			break;
		case 1:
			mbar.draw(458, 145);
			twow.draw(533, 172);
			break;
		case 2:
			mbar.draw(458, 193);
			conw.draw(528, 222);
			break;
		case 3:
			mbar.draw(458, 238);
			crew.draw(533, 265);
			break;
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		dsum += arg2;
		if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
			menu.stop();
			decision.play();

			// TODO debug
			System.out.println("menuからマッチングへ");

			sbg.enterState(nextState, new FadeOutTransition(Color.black, 1000),
					new FadeInTransition(Color.black, 1000));
		}
		if (gc.getInput().isKeyDown(Input.KEY_UP) && (decide > 0) && (dsum >= 200)) {
			decide--;
			System.out.println(decide);
			select.play();
			dsum = 0;
		} else if (gc.getInput().isKeyDown(Input.KEY_DOWN) && (decide < MODENUMBERS - 1) && (dsum >= 200)) {
			decide++;
			select.play();
			System.out.println(decide);
			dsum = 0;
		}
		switch (decide) {
		/*
		 * case 0: nextState = State.MATCHING; System.out.println(nextState);
		 * break; case 1: nextState = State.MATCHING;
		 * System.out.println(nextState);
		 *
		 * break;
		 */
		case 0:
		case 1:
			nextState = State.MATCHING;

			break;
		case 2:
			nextState = State.CONFIG;

			break;
		case 3:
			nextState = State.CREDIT;

			break;
		}

	}

	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		background = new Image("Scene\\menu\\menu.png");
		oneb = new Image("Scene\\menu\\1on1_0.png");
		onew = new Image("Scene\\menu\\1on1_2.png");
		twob = new Image("Scene\\menu\\2on2_1.png");
		twow = new Image("Scene\\menu\\2on2_2.png");
		conb = new Image("Scene\\menu\\config_0.png");
		conw = new Image("Scene\\menu\\config_1.png");
		creb = new Image("Scene\\menu\\credit_0.png");
		crew = new Image("Scene\\menu\\credit_1.png");
		mbar = new Image("Scene\\menu\\menu_bar.png");
		menu = new Music("BGM/Menu.ogg");
		select = new Sound("SE\\decide.ogg");
		decision = new Sound("SE\\menu_decision.ogg");
		menu.play();
		nextState = State.MATCHING;

	}

	@Override
	public int getID() {
		return state;
	}

}
