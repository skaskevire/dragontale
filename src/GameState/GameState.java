package GameState;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public abstract class GameState
{	
	public abstract void init();
	public abstract void update();
	public abstract void draw(java.awt.Graphics2D g, JPanel panel);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	
	public abstract void updatePlayerStates(String data, String playerId);
	public abstract void addNewPlayer(String pid, String skin);
	public abstract String getPlayerCoordinates();
	
	
	public abstract String getEnemyCoordinates();
	public abstract void addOrUpdateEnemies(String data);
	
	
	
	public abstract void drawToScreen(Graphics graphics, BufferedImage image);
}
