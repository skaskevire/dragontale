package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import java.util.Arrays;

public class GameModeChooseState extends MenuState
{

	public GameModeChooseState(GameStateManager gsm)
	{
		super(gsm);
		options = Arrays.asList("Classic Mode", "God Mode");
	}

	@Override
	public void draw(Graphics2D g)
	{
		bg.draw(g);
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
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		
		if(currentChoice == 1)
		{
			gsm.setState(GameStateManager.GODSTATE);
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

}
