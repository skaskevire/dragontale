
package GameState;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import java.util.Arrays;

import javax.swing.JPanel;

public class GameModeChooseState extends MenuState
{

	public GameModeChooseState()
	{
		options = Arrays.asList("Classic Mode", "God Mode", "Multiplayer");
	}

	@Override
	public void draw(Graphics2D g, JPanel panel)
	{
		

		bg.draw(g);
		g.setFont(font);

		for (int i = 0; i < options.size(); i++)
		{
			if (i == currentChoice)
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
		if (currentChoice == 0)
		{
			GameStateManager.getInstance().setState(GameStateManager.LEVEL1STATE);
		}

		if (currentChoice == 1)
		{
			GameStateManager.getInstance().setState(GameStateManager.GODSTATE);
		}
		if (currentChoice == 2)
		{
			GameStateManager.getInstance().setState(GameStateManager.MULTIPLAYER_CLIENT);
		}
	}

	@Override
	public void keyPressed(int k)
	{
		if (k == KeyEvent.VK_ENTER)
		{
			select();
		}
		if (k == KeyEvent.VK_UP)
		{
			currentChoice--;
			if (currentChoice == -1)
			{
				currentChoice = options.size() - 1;
			}
		}
		if (k == KeyEvent.VK_DOWN)
		{
			currentChoice++;
			if (currentChoice == options.size())
			{
				currentChoice = 0;
			}
		}

	}

}
