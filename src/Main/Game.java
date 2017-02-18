package Main;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Game
{
	public static void main(String [] args)
	{
		JFrame window = new JFrame("DT");
		GamePanel gp = new GamePanel();
		gp.add(new JButton());
		window.setContentPane(new GamePanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
}
