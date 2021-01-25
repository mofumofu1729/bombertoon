package server.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import server.constants.Direction;

//スレッド部（各クライアントに応じて）
public class ClientProcThread extends Thread {
    private int number;//自分の番号
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private TransmissionServer ts;

    public ClientProcThread(TransmissionServer ts, int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
    	this.ts = ts;
    	number = n;
    	incoming = i;
    	myIsr = isr;
    	myIn = in;
    	myOut = out;
    }

    public void run() {
    	try {

    		while (true) {//無限ループで，ソケットへの入力を監視する
    			// System.out.println("runnnnnnnnnnnn");
    			String str = myIn.readLine();
    			System.out.println("Received from client No."+number+", command: "+str);
    			switch (str) {
    			case "RIGHT":
    				ts.setHuman(number, Direction.RIGHT);
    				break;
    			case "LEFT":
    				ts.setHuman(number, Direction.LEFT);
    				break;
    			case "UP":
    				ts.setHuman(number, Direction.UP);
    				break;
    			case "DOWN":
    				ts.setHuman(number, Direction.DOWN);
    				break;
    			case "TURN_RIGHT":
    				ts.setHuman(number, Direction.TURN_RIGHT);
    				break;
    			case "TURN_LEFT":
    				ts.setHuman(number, Direction.TURN_LEFT);
    				break;
    			case "TURN_UP":
    				ts.setHuman(number, Direction.TURN_UP);
    				break;
    			case "TURN_DOWN":
    				ts.setHuman(number, Direction.TURN_DOWN);
    				break;
    			case "BOMB":
    				// System.out.println("cptttttttttttttttttttttttttttttttt");
    				ts.setBomb(number);
    			default:
    				;
    			}

    		}
    	} catch (Exception e) {
    		;
    	}
    }
}
