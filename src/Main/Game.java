package Main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.SplashScreen;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Game
{
	public static void main(String [] args)
	{
		JFrame window = new JFrame("DT");
		
		JButton b = new JButton("Button text");
GamePanel panel = new GamePanel(args);

		panel.add(b, BorderLayout.NORTH);
		b.setEnabled(true);
		b.setVisible(true);
		b.setAlignmentX(200);
		b.setAlignmentY(200);
		

		
		
		window.setContentPane(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
}
