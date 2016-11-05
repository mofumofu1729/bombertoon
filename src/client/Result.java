package client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import common.Score;
import common.Setting;

public class Result extends BasicGameState {
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
	private Music bgm;

	public Result(int state) {
		this.state = state;
	}

	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		re = new Image("res/client/img/Scene/results/BackGround.png");
		uf = new UnicodeFont("res/client/font/SHOWG.TTF", 90, false, false);
		uf2 = new UnicodeFont("res/client/font/SHOWG.TTF", 60, false, false);
		uf3 = new UnicodeFont("res/client/font/SHOWG.TTF", 32, false, false);
		bgm = new Music("res/client/sound/BGM/result.ogg");
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
re.draw(0, 0);
		uf.addAsciiGlyphs();
		uf.getEffects().add(new ColorEffect());
		uf.loadGlyphs();

		arg2.setFont(uf);
		// arg2.drawString("Shit example", 100, 100);

		arg2.drawString(Integer.toString(score.painted[0]), 165, 110);
		arg2.drawString(Integer.toString(score.painted[1]), 545, 110);
		team1.draw(70, 290);
		team2.draw(430, 290);
		bar1.draw(38, 360);
		bar1.draw(38, 440);
		bar2.draw(430, 360);
		bar2.draw(430, 440);
		kill.draw(0, -15);
		death.draw(0, -10);
		
		uf2.addAsciiGlyphs();
		uf2.getEffects().add(new ColorEffect());
		uf2.loadGlyphs();
		arg2.setFont(uf2);

		arg2.drawString("1", 70, 390); // 385 425
		arg2.drawString("3", 70, 470);
		arg2.drawString("2", 460, 385);
		arg2.drawString("4", 460, 465);

		uf3.addAsciiGlyphs();
		uf3.getEffects().add(new ColorEffect());
		uf3.loadGlyphs();
		arg2.setFont(uf3);

		arg2.drawString(Integer.toString(score.kill[0]), 330, 390); // 385 425
		arg2.drawString(Integer.toString(score.death[0]), 330, 425);
		arg2.drawString(Integer.toString(score.kill[2]), 330, 470); // 385 425
		arg2.drawString(Integer.toString(score.death[2]), 330, 505);

		arg2.drawString(Integer.toString(score.kill[1]), 720, 390);
		arg2.drawString(Integer.toString(score.death[1]), 720, 425);
		arg2.drawString(Integer.toString(score.kill[3]), 720, 470);
		arg2.drawString(Integer.toString(score.death[3]), 720, 505);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO 自動生成さtc.getScore().れたメソッド・スタブ
		if (arg0.getInput().isKeyPressed(Input.KEY_ENTER)) {
			arg1.enterState(State.TOP);
		}

	}

	@Override
	public int getID() {
		return state;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		tc = ClientStarter.getTransmissionClient();
		score = ClientStarter.getTransmissionClient().getScore();
		
		
		int winTeam = -1;
		if (score.painted[0] > score.painted[1]) {
			team1 = new Image("res/client/img/Scene/results/WIN.png"); // TODO
			team2 = new Image("res/client/img/Scene/results/LOSE.png");
			winTeam = 0;
		} else {
			team1 = new Image("res/client/img/Scene/results/LOSE.png");
			team2 = new Image("res/client/img/Scene/results/WIN.png");
			winTeam = 1;
		}
		
		
		if (tc.recieveTeamNumber() == winTeam) {
			bgm = new Music("res/client/sound/BGM/resultBGM_win.ogg");
		} else {
			bgm = new Music("res/client/sound/BGM/resultBGM_lose.ogg");
		}
				
		// TODO かな？ファイルは何時読み込むべき？
		bar1 = new Image("res/client/img/Scene/results/bar/" + Setting.ColorTeam1.toString().toLowerCase() + "_bar.png");
		bar2 = new Image("res/client/img/Scene/results/bar/" + Setting.ColorTeam2.toString().toLowerCase() + "_bar.png");
		kill = new Image("res/client/img/Scene/results/kill.png");
		death = new Image("res/client/img/Scene/results/Death_2.png");

		bgm.loop();
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		bgm.stop();
	}
}
