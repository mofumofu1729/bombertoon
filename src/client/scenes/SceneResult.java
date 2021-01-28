package client.scenes;

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
import client.ClientStarter;
import client.constants.State;
import client.network.TransmissionClient;
import common.Score;
import common.Setting;

public class SceneResult extends BasicGameState {
    private static final int KILL_COUNT_DISPLAY_POSITIONS[][] =
            {{330, 390}, {720, 390}, {330, 470}, {720, 470}};
    private static final int DEATH_COUNT_DISPLAY_POSITIONS[][] =
            {{330, 425}, {720, 425}, {330, 505}, {720, 505}};

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

    public SceneResult(int state) {
        this.state = state;
    }

    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        re = new Image("res/client/img/Scene/results/BackGround.png");
        uf = new UnicodeFont("res/client/font/SHOWG.TTF", 90, false, false);
        uf2 = new UnicodeFont("res/client/font/SHOWG.TTF", 60, false, false);
        uf3 = new UnicodeFont("res/client/font/SHOWG.TTF", 32, false, false);
        bgm = new Music("res/client/sound/BGM/result.ogg");
    }

    /**
     * 試合結果の画面を描画.
     */
    @Override
    public void render(GameContainer container, StateBasedGame game,
            Graphics graphics) throws SlickException {
        // TODO 現状は四人対戦を前提とした画面配置

        re.draw(0, 0);
        uf.addAsciiGlyphs();
        uf.getEffects().add(new ColorEffect());
        uf.loadGlyphs();

        graphics.setFont(uf);

        graphics.drawString(Integer.toString(score.painted[0]), 165, 110);
        graphics.drawString(Integer.toString(score.painted[1]), 545, 110);
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
        graphics.setFont(uf2);

        graphics.drawString("1", 70, 390); // 385 425
        graphics.drawString("3", 70, 470);
        graphics.drawString("2", 460, 385);
        graphics.drawString("4", 460, 465);

        uf3.addAsciiGlyphs();
        uf3.getEffects().add(new ColorEffect());
        uf3.loadGlyphs();
        graphics.setFont(uf3);

        // 各プレイヤーのキル数・デス数の表示
        for (int i = 0; i < Setting.N_PLAYERS; i++) {
            graphics.drawString(
                    Integer.toString(score.kill[i]),
                    KILL_COUNT_DISPLAY_POSITIONS[i][0],
                    KILL_COUNT_DISPLAY_POSITIONS[i][1]);
            graphics.drawString(
                    Integer.toString(score.kill[i]),
                    DEATH_COUNT_DISPLAY_POSITIONS[i][0],
                    DEATH_COUNT_DISPLAY_POSITIONS[i][1]);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        // TODO 恐らく試合画面でのキー入力のバッファが残っているため
        // 試合終了後すぐにTOP画面に遷移するバグがあり
        if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
            game.enterState(State.TOP);
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
        bar1 = new Image("res/client/img/Scene/results/bar/"
                + Setting.ColorTeam1.toString().toLowerCase() + "_bar.png");
        bar2 = new Image("res/client/img/Scene/results/bar/"
                + Setting.ColorTeam2.toString().toLowerCase() + "_bar.png");
        kill = new Image("res/client/img/Scene/results/kill.png");
        death = new Image("res/client/img/Scene/results/Death_2.png");

        bgm.loop();
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        bgm.stop();
    }
}
