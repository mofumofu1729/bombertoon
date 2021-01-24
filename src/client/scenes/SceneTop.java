package client.scenes;

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

import client.State;

public class SceneTop extends BasicGameState {
	int state;
	Music bgm;
	Image back,button,logo;
	Sound entered;
	private int timer1=1000,timer2=250;


	public SceneTop(int state) {
		this.state = state;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		entered = new Sound("res/client/sound/SE/title_decision.ogg");
		bgm = new Music("res/client/sound/BGM/Bombertoon.ogg");
		back = new Image("res/client/img/Scene/title/background.png");
		button=new Image("res/client/img/Scene/title/button.png");
		logo=new Image("res/client/img/Scene/title/logo2.png");
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		//arg2.drawString("title push space to menu", 100, 100);
		back.draw(0, 0);
		logo.draw(0, 0);
		if(timer1>0)
		button.draw(0, 0);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		if(timer1>=0)
		timer1-=arg2;
		else if(timer1<0)
		timer2-=arg2;
		if (timer2<0){
			timer1=1000;
			timer2=250;}

		if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
			bgm.stop();
			entered.play();
			sbg.enterState(State.MENU, new FadeOutTransition(Color.black, 1000),
					new FadeInTransition(Color.black, 1000));
		}
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		bgm.loop();
	}

	@Override
	public int getID() {
		return state;
	}

}
