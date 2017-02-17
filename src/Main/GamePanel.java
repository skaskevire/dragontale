package Main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.GameEventDefault;

import GameState.GameStateManager;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
	private static final long serialVersionUID = 4294956057709600805L;
	public static int WIDTH = 320;
	public static int HEIGHT = 240;
	public static final int SCALE = 2;
	
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	private BufferedImage image;
	private Graphics2D g;	
	 /**
     * just use the default GameEvent class
     */
    public GameEvent createGameEvent() {
	return new GameEventDefault();
    }
    
	 public GameEvent createDisconnectEvent(String reason) {
			return new GameEventDefault(GameEventDefault.S_DISCONNECT, reason);
		    }
	
	public GamePanel(String [] args)
	{



		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();	
	}
	
	@Override	
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
		
	}
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		GameStateManager.getInstance().keyPressed(arg0.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		GameStateManager.getInstance().keyReleased(arg0.getKeyCode());		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		
	}
	
	private void init()
	{
		image = new BufferedImage(
				WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
	}

	@Override
	public void run()
	{
		init();
		GameStateManager.getInstance().setState(GameStateManager.MENUSTATE);
		
		long start;
		long elapsed;
		long wait;
		
		while(running)
		{
			
			start = System.nanoTime();		
   
			update();
			draw();
			drawToScreen();	
			
			elapsed = System.nanoTime() - start;
			
			wait = targetTime - elapsed / 1000000;
			if(wait < 0)
			{
				wait = 5;
			}
			try
			{
				Thread.sleep(wait);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private void update()
	{
		GameStateManager.getInstance().update();
	}
	
	private void draw()
	{
		GameStateManager.getInstance().draw(g, this);
	}
	
	private void drawToScreen()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE,  null);
		g2.dispose();
	}
}
