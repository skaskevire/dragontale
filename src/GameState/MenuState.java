package GameState;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import Main.GamePanel;
import server.tileMap.Background;


public class MenuState extends GameState
{
	protected Background bg;
	
	protected int currentChoice = 0;
	protected List<String> options;
	
	protected Color titleColor;
	protected Font titleFont;
	protected Font font;
	
	public MenuState()
	{	
		options = Arrays.asList("Start", "Help", "Quit");
		
		try
		{
			bg = new Background("/Backgrounds/menubg.gif", 1);
			bg.setVector(-0.1, 0);
			
			titleColor = new Color(128, 0, 0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			font = new Font("Arial", Font.PLAIN, 12);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update()
	{
		bg.update();
		
	}

	@Override
	public void draw(Graphics2D g, JPanel panel)
	{
		bg.draw(g);
		
		g.setColor(titleColor);
		g.drawString("Dragon Tale", 80, 70);
		g.setFont(font);
		
		
		for (int i = 0; i < options.size(); i++)
		{
			if( i == currentChoice)
			{
				g.setColor(Color.BLACK);
			}
			else
			{
				g.setColor(Color.RED);
			}
			g.drawString(options.get(i), 145, 140 + i * 15);
		}
		
	}
	
	
	private void select()
	{
		if(currentChoice == 0)
		{
			GameStateManager.getInstance().setState(GameStateManager.GAMEMODECHOOSESTATE);
		}
		
		if(currentChoice == 1)
		{
			try {
				openWebpage(new URL("https://github.com/skaskevire/dragontale").toURI());
			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if(currentChoice == 2)
		{
			Frame frame = Frame.getFrames()[0];
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	 private void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	@Override
	public void keyPressed(int k)
	{
		if(k == KeyEvent.VK_ENTER)
		{
			select();
		}
		if(k == KeyEvent.VK_UP)
		{
			currentChoice--;
			if(currentChoice == -1)
			{
				currentChoice = options.size() - 1;
			}
		}
		if(k == KeyEvent.VK_DOWN)
		{
			currentChoice++;
			if(currentChoice == options.size())
			{
				currentChoice = 0;
			}
		}
		
	}

	@Override
	public void keyReleased(int k)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePlayerStates(String data, String playerId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addNewPlayer(String pid, String skin)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPlayerCoordinates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnemyCoordinates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addOrUpdateEnemies(String data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawToScreen(Graphics graphics, BufferedImage image)
	{
		Graphics g2 = graphics;
		g2.drawImage(image, 0, 0, GamePanel.WIDTH *  GamePanel.SCALE, GamePanel.HEIGHT * GamePanel.SCALE,  null);
		g2.dispose();		
	}

	@Override
	public void performCloseOperations()
	{
		// TODO Auto-generated method stub
		
	}

}
