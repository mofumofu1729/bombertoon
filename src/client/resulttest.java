package client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import common.Score;

public class resulttest extends BasicGameState {

	private int state;
	Image re;
	UnicodeFont uf;
	UnicodeFont uf2;
	UnicodeFont uf3;
	TransmissionClient tc;
	Score score;
	Image bar1;
	Image bar2;
	Image team1;
	Image team2;
	Image kill;
	Image death;

	public resulttest(int state) {
		this.state = state;
	}

	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ
		re = new Image("Scene\\results\\BackGround.png");
		uf = new UnicodeFont("SHOWG.TTF", 100, false, false);
		uf2 = new UnicodeFont("SHOWG.TTF", 60, false, false);
		uf3 = new UnicodeFont("SHOWG.TTF", 32, false, false);

	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		// TODO 自動生成されたメソッド・スタブ

		re.draw(0, 0);
		uf.addAsciiGlyphs();
		uf.getEffects().add(new ColorEffect());
		uf.loadGlyphs();

		arg2.setFont(uf);
		// arg2.drawString("Shit example", 100, 100);

		team1.draw(70, 290);
		team2.draw(430, 290);
		bar1.draw(38, 360);
		bar1.draw(38, 440);
		bar2.draw(430, 360);
		bar2.draw(430, 440);
		kill.draw(0, -18);
		death.draw(0, -10);
		arg2.drawString("31", 545, 110);
		arg2.drawString("26", 165, 110);
		uf2.addAsciiGlyphs();
		uf2.getEffects().add(new ColorEffect());
		uf2.loadGlyphs();
		arg2.setFont(uf2);
		arg2.drawString("1", 70, 390);  //385 425
		arg2.drawString("2", 70, 470);
		arg2.drawString("3", 460, 385);
		arg2.drawString("4", 460, 465);

		uf3.addAsciiGlyphs();
		uf3.getEffects().add(new ColorEffect());
		uf3.loadGlyphs();
		arg2.setFont(uf3);
		arg2.drawString("1", 330, 385);  //385 425
		arg2.drawString("2", 330, 420);
		arg2.drawString("3", 720, 385);
		arg2.drawString("4", 720, 420);
		arg2.drawString("1", 330, 465);  //385 425
		arg2.drawString("2", 330, 500);
		arg2.drawString("3", 720, 465);
		arg2.drawString("4", 720, 500);




	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO 自動生成さtc.getScore().れたメソッド・スタブ

	}

	@Override
	public int getID() {
		return state;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		bar1 = new Image("Scene\\results\\bar\\red_bar.png");
		bar2 = new Image("Scene\\results\\bar\\blue_bar.png");
		// tc = ClientStarter.getTransmissionClient();
		team1 = new Image("Scene\\results\\WIN.png");
		team2 = new Image("Scene\\results\\LOSE.png");
		kill = new Image("Scene\\results\\kill.png");
		death = new Image("Scene\\results\\Death_2.png");

	}

}
