package client;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class SceneConfig extends BasicGameState {
	private int state;
	Image conPicture;
	Image register;
	Image registerPushed;
	TextField hostNameBox;
	TextField portNumber;
	UnicodeFont uf;
	boolean pushed=false;
	private Sound decision;
	int timer;
	SceneConfig(int state) {
		this.state = state;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ
		registerPushed=new Image("res/client/img/Scene/conf/register_2.png");
		conPicture = new Image("res/client/img/Scene/conf/config.png");
		register = new Image("res/client/img/Scene/conf/register_1.png");
		uf = new UnicodeFont("res/client/font/SHOWG.TTF", 32, false, false);

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		uf.addAsciiGlyphs();
		uf.getEffects().add(new ColorEffect());
		uf.loadGlyphs();

		conPicture.draw();
		register.draw();
		if(pushed){
			registerPushed.draw();
		}
		hostNameBox.render(arg0, arg2);
		portNumber.render(arg0, arg2);

		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ
		if (arg0.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON) && (arg0.getInput().getAbsoluteMouseX() > 456)
				&& (arg0.getInput().getAbsoluteMouseX() < 660) && (arg0.getInput().getAbsoluteMouseY() > 425)
				&& (arg0.getInput().getAbsoluteMouseY() < 508)
				&& ((hostNameBox.getText().equals("") == false) || (portNumber.getText().equals("") == false))) {
			if (hostNameBox.getText().equals("") == false) {
				TransmissionClient.hostName = this.hostNameBox.getText();
				hostNameBox.setText("");
			}
			pushed=true;
			decision.play();
			portNumber.setText("");
		}
		if (arg0.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			arg1.enterState(State.MENU, new FadeOutTransition(Color.black, 1000),
					new FadeInTransition(Color.black, 1000));

		}
		if(pushed==true){
			timer-=arg2;
		}
		if(timer<0){
			timer=200;
			pushed=false;
		}
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		decision = new Sound("res/client/sound/SE/menu_decision.ogg");
		hostNameBox = new TextField(container, uf, 189, 207, 470, 38);
		hostNameBox.setBackgroundColor(Color.white);
		hostNameBox.setBorderColor(Color.transparent);
		hostNameBox.setTextColor(Color.black);
		hostNameBox.setAcceptingInput(true);
		hostNameBox.setFocus(true);
		portNumber = new TextField(container, uf, 189, 337, 470, 38);
		portNumber.setBackgroundColor(Color.white);
		portNumber.setBorderColor(Color.transparent);
		portNumber.setTextColor(Color.black);
		portNumber.setAcceptingInput(true);
		portNumber.setFocus(false);

	}
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public int getID() {
		return state;
	}

}
