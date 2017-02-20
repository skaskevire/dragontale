
package GameState;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;


public class GameStateManager
{
	//private ArrayList<MultiplayerClientState> s = new ArrayList<MultiplayerClientState>();


	private static GameStateManager instance;

	private GameState[] gameStates;
	private int currentState;

	public static final int NUMGAMESTATES = 7;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int GAMEMODECHOOSESTATE = 3;
	public static final int GODSTATE = 2;
	public static final int MULTIPLAYER_CLIENT = 4;
	public static final int MULTIPLAYER_SERVER = 5;
	public static final int MULTIPLAYER_INTERFACE_STATE = 6;

	public static GameStateManager getInstance()
	{
		if (instance == null)
		{
			synchronized (GameStateManager.class)
			{
				if (instance == null)
				{
					instance = new GameStateManager();
				}
			}
		}
		return instance;
	}

	private GameStateManager()
	{
		gameStates = new GameState[NUMGAMESTATES];
		currentState = MENUSTATE;
		loadState(currentState);
	}

	private void loadState(int state, Object ... agrs)
	{
		if (state == MENUSTATE)
		{
			gameStates[state] = new MenuState();
		}
		if (state == LEVEL1STATE)
		{
			gameStates[state] = new Level1State("dragon");
		}
		if (state == GAMEMODECHOOSESTATE)
		{
			gameStates[state] = new GameModeChooseState();
		}
		if (state == GODSTATE)
		{
			gameStates[state] = new Level1State("girl");
		}
		if (state == MULTIPLAYER_CLIENT)
		{
			//for (int i = 0; i < 10; i++)
			/*{
				MultiplayerClientState mst1 = new MultiplayerClientState("dragon",(String) agrs[0],(String) agrs[1],(String) agrs[2]);
				s.add(mst1);
			}*/

			gameStates[state] = new MultiplayerClientState("dragon",(String) agrs[0],(String) agrs[1],(String) agrs[2]);
		}
		if (state == MULTIPLAYER_SERVER)
		{
			gameStates[state] = new MultiplayerServerState("dragon");
		}
		if (state == MULTIPLAYER_INTERFACE_STATE)
		{
			gameStates[state] = new MultiplayerInterfaceState();
		}
	}	

	private void unloadState(int state)
	{
		gameStates[state].performCloseOperations();
		gameStates[state] = null;
	}

	public synchronized void setState(int state, Object ... agrs)
	{
		unloadState(currentState);
		currentState = state;
		loadState(currentState, agrs);
	}

	public void update()
	{
		try
		{
			gameStates[currentState].update();
		}
		catch (Exception e)
		{}

	}

	public void addPlayerGameStates(String message, String playerId)
	{
		try
		{
			gameStates[currentState].updatePlayerStates(message, playerId);
		}
		catch (Exception e)
		{}

	}

	public String getPlayerCoodrinates()
	{
		try
		{
			return gameStates[currentState].getPlayerCoordinates();
		}
		catch (Exception e)
		{}
		return null;

	}

	public String getEnemyCoodrinates()
	{
		try
		{
			return gameStates[currentState].getEnemyCoordinates();
		}
		catch (Exception e)
		{}
		return null;

	}

	public void draw(java.awt.Graphics2D g, JPanel panel)
	{
		try
		{
			gameStates[currentState].draw(g, panel);
		}
		catch (Exception e)
		{}
	}

	public void keyPressed(int k)
	{
		gameStates[currentState].keyPressed(k);
	}
	
	public synchronized int getCurrentState()
	{
		return currentState;
	}

	public void keyReleased(int k)
	{
		gameStates[currentState].keyReleased(k);
	}
	
	public synchronized void drawToScreen(Graphics g2, BufferedImage image){
		gameStates[currentState].drawToScreen(g2, image);
	}

}
