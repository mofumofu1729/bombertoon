package server.scenes;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import server.constants.State;
import server.network.TransmissionServer;

public class MatchingServer extends BasicGameState {
    private boolean enterFinished = false; // MatchingServerへの画面遷移が完了したか
    private boolean isSendReady = false;
    private int state;
    private TransmissionServer ts;

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
            throws SlickException {
    }

    @Override
    public void update(GameContainer arg0, StateBasedGame sbg, int arg2) throws SlickException {
        /*
         * if (!(BattleServer.ts.isReady())) { // まだクライアントの接続が完了していない return; } else if
         * (!enterFinished) { // まだMachingServerへの画面遷移が完了していない return; } else { if (isSendReady ==
         * false) { // setInitialField(player, field); sbg.enterState(State.BATTLESERVER, new
         * FadeOutTransition(Color.black, 1000), new FadeInTransition(Color.black, 1000));
         * System.out.println("バトサに移動"); // isSendReady = true;
         * 
         * } }
         */

        if (ts.isReady()) {
            sbg.enterState(State.BATTLESERVER, new FadeOutTransition(Color.black, 1000),
                    new FadeInTransition(Color.black, 1000));
            System.out.println("バトサに移動");
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

        ts = TransmissionServer.createInstance();
        ts.start();
        enterFinished = true;
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        enterFinished = false;
    }
}
