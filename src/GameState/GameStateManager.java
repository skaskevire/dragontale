package GameState;


public class GameStateManager
{
	private GameState[] gameStates;
	private int currentState;
	
	public static final int NUMGAMESTATES = 4;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int GAMEMODECHOOSESTATE = 3;
	public static final int GODSTATE = 2;
	
	public GameStateManager()
	{
		gameStates = new GameState[NUMGAMESTATES];
		
		
		currentState = MENUSTATE;
		
		loadState(currentState);
	}
	
	private void loadState(int state)
	{
		if(state == MENUSTATE)
		{
			gameStates[state] = new MenuState(this);
		}
		if(state == LEVEL1STATE)
		{
			gameStates[state] = new Level1State(this, "dragon");
		}
		if(state == GAMEMODECHOOSESTATE)
		{
			gameStates[state] = new GameModeChooseState(this);
		}
		if(state == GODSTATE)
		{
			gameStates[state] = new Level1State(this, "girl");
		}
	}
	
	private void unloadState(int state)
	{
		gameStates[state] = null;
	}
	
	public void setState(int state)
	{
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
		
		
		//gameStates[currentState].init();
	}
	
	public void update()
	{
		try
		{
			gameStates[currentState].update();
		} catch (Exception e){}
		
	}
	
	public void draw(java.awt.Graphics2D g)
	{
		try
		{
		gameStates[currentState].draw(g);
		} catch (Exception e){}
	}
	
	public void keyPressed(int k)
	{
		gameStates[currentState].keyPressed(k);
	}
	
	public void keyReleased(int k)
	{
		gameStates[currentState].keyReleased(k);
	}
	
}
