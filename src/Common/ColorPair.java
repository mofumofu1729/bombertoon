package Common;

import server.Color;

public class ColorPair {
	static public Color[] getColorPair (int getNumber){
		Color [] cl =new Color[2];
		switch (getNumber){
		case 0:
		cl[0]=Color.Blue;
		cl[1]=Color.Orange;
		break;
		case 1:
			cl[0]=Color.Pink;
			cl[1]=Color.Turquoise;
			break;
		case 2:
			cl[0]=Color.Purple;
			cl[1]=Color.LimeGreen;
			break;
		case 3:
			cl[0]=Color.Red;
			cl[1]=Color.Green;
			break;

		}
		return cl;
	}
}
