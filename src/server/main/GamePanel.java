package server.main;

import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.GameEventDefault;

import GameState.GameStateManager;
import GameState.ServerGameStateManager;

public class GamePanel implements Runnable
{
	public static final int SCALE = 2;
	
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	
	 /**
     * just use the default GameEvent class
     */
    public GameEvent createGameEvent() {
	return new GameEventDefault();
    }
    
	 public GameEvent createDisconnectEvent(String reason) {
			return new GameEventDefault(GameEventDefault.S_DISCONNECT, reason);
		    }
	
	
	
   
	
	private void init()
	{		
		running = true;
	}

	@Override
	public void run()
	{
		init();
		ServerGameStateManager.getInstance().setState(GameStateManager.MULTIPLAYER_SERVER);
		long start;
		long elapsed;
		long wait;
		
		while(running)
		{			
			start = System.nanoTime();
		    
			update();
			
			
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
		ServerGameStateManager.getInstance().update();
	}
}
