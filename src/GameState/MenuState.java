package GameState;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;

import TileMap.Background;

public class MenuState extends GameState
{
	private Background bg;
	
	private int currentChoice = 0;
	private String [] options = 
		{
			"Start",
			"Help",
			"Quit"
		};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;
	
	public MenuState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
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
	public void draw(Graphics2D g)
	{
		bg.draw(g);
		
		g.setColor(titleColor);
		g.drawString("Dragon Tale", 80, 70);
		g.setFont(font);
		
		
		for (int i = 0; i < options.length; i++)
		{
			if( i == currentChoice)
			{
				g.setColor(Color.BLACK);
			}
			else
			{
				g.setColor(Color.RED);
			}
			g.drawString(options[i], 145, 140 + i * 15);
		}
		
	}
	
	
	private void select()
	{
		if(currentChoice == 0)
		{
			gsm.setState(GameStateManager.LEVEL1STATE);
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
				currentChoice = options.length - 1;
			}
		}
		if(k == KeyEvent.VK_DOWN)
		{
			currentChoice++;
			if(currentChoice == options.length)
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

}
