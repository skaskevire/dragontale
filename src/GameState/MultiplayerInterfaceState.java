
package GameState;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import com.hypefiend.javagamebook.server.GameServer;

import Main.GamePanel;

public class MultiplayerInterfaceState extends MenuState implements MouseListener
{
	private static final Rectangle NESTED_PANEL_BOUNDS = new Rectangle(160, 120, 315, 230);
	private static final Rectangle PLAYER_LABEL_BOUNDS = new Rectangle(30, 140, 120, 20);
	private static final Rectangle PLAYER_INPUT_BOUNDS = new Rectangle(30, 160, 120, 20);

	private static final Rectangle SERVER_LABEL_BOUNDS = new Rectangle(20, 10, 120, 20);
	private static final Rectangle SERVER_INPUT_BOUNDS = new Rectangle(20, 30, 120, 20);

	private static final Rectangle BUTTON_BOUNDS = new Rectangle(20, 60, 120, 20);
	private static final String BUTTON_TEXT_JOIN_GAME = "Join Game";
	
	private static final Rectangle CREATE_GAME_BUTTON_BOUNDS = new Rectangle(170, 60, 120, 20);
	private static final String BUTTON_TEXT_CREATE_GAME = "Create Game";

	private static final String PLAYER_DEFAULT_NAME = "raman";

	private static final String SERVER_DEFAULT_IP = "10.6.94.81";
	private static final String PLAYER_LABEL_TEXT = "Player Name:";
	private static final String SERVER_LABEL_TEXT = "Server IP:";

	private JPanel mainPanel;
	private JPanel nestedPanel;
	private JButton button;
	private JButton createGameButton;
	private JTextField player;
	private JTextField server;
	CheckboxGroup playerSelectGroup = new CheckboxGroup();
	Checkbox girlCheckbox = new Checkbox("",playerSelectGroup, false);
	Checkbox dragonCheckbox = new Checkbox("",playerSelectGroup, true);


	public MultiplayerInterfaceState()
	{
		girlCheckbox.setBounds(170, 150,10, 10);
		dragonCheckbox.setBounds(230, 150,10, 10);

		
		
		player = createInputTextField(PLAYER_DEFAULT_NAME, PLAYER_INPUT_BOUNDS);
		server = createInputTextField(SERVER_DEFAULT_IP, SERVER_INPUT_BOUNDS);

		button = new JButton(BUTTON_TEXT_JOIN_GAME);
		button.setBounds(BUTTON_BOUNDS);
		button.setVisible(true);
		button.addMouseListener(this);
		
		
		createGameButton = new JButton(BUTTON_TEXT_CREATE_GAME);
		createGameButton.setBounds(CREATE_GAME_BUTTON_BOUNDS);
		createGameButton.setVisible(true);
		createGameButton.addMouseListener(this);
	}

	@Override
	public void draw(Graphics2D g, JPanel panel)
	{
		
		
		
		
		
		
		if (nestedPanel == null)
		{
			this.mainPanel = panel;
			nestedPanel = createServerConnectionPanel();
			panel.add(nestedPanel);
			nestedPanel.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		
		int x = arg0.getComponent().getX();
		int y = arg0.getComponent().getY();//170, 160, 120, 20
		
		if(arg0.getComponent().equals(createGameButton))
		{
			player.getText();
			mainPanel.remove(nestedPanel);
			mainPanel.requestFocus();
			BasicConfigurator.configure();
			GameServer gs = new GameServer();
			gs.start();
			try
			{
				Thread.sleep(3000l);
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try
			{
				String skin = "dragon";
				
				if(playerSelectGroup.getSelectedCheckbox().equals(dragonCheckbox))
				{
					skin = "girl";
				}
						
				
				GameStateManager.getInstance().setState(GameStateManager.MULTIPLAYER_CLIENT,
						player.getText(), Inet4Address.getLocalHost().getHostAddress().toString(), skin);
			}
			catch (UnknownHostException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		if(arg0.getComponent().equals(button))
		{
			String skin = "dragon";
			
			if(playerSelectGroup.getSelectedCheckbox().equals(dragonCheckbox))
			{
				skin = "girl";
			}
			
			player.getText();
			mainPanel.remove(nestedPanel);
			mainPanel.requestFocus();
			GameStateManager.getInstance().setState(GameStateManager.MULTIPLAYER_CLIENT,
					player.getText(), server.getText(), skin);
		}
		
		


	}

	private JPanel createServerConnectionPanel()
	{

		JPanel nestedPanel = new JPanel(){
			 protected void paintComponent(Graphics g) {
				    super.paintComponent(g);
				    g.setFont(new Font("default", Font.BOLD, 12));
				    g.drawString("Join game", 10, 100);
				    g.drawString("Create game", 160, 100);
				    g.drawRect(10,10,140,80); 
				    g.drawRect(160,10,140,80);  
				    g.setColor(Color.RED);  
				    
				    
				    
				    try
					{
						BufferedImage girl = ImageIO
								.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.gif"));
						girl = girl.getSubimage(0, 0, 30, 30);
						
						
						BufferedImage dragon = ImageIO
								.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.png"));
						dragon = dragon.getSubimage(0, 0, 30, 30);
					
						g.drawImage(girl,180,150,null);
						g.drawImage(dragon,240,150,null);
					
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				    
					
					
				   // g.fillRect(230,80,10,10);  
				  }
		};
		
		
		
		
		
		

		JLabel playerLabel = createLabel(PLAYER_LABEL_TEXT, PLAYER_LABEL_BOUNDS);
		JLabel serverLabel = createLabel(SERVER_LABEL_TEXT, SERVER_LABEL_BOUNDS);

		nestedPanel.setBounds(NESTED_PANEL_BOUNDS);
		nestedPanel.setBackground(Color.white);
		nestedPanel.add(button);
		nestedPanel.add(createGameButton);
		nestedPanel.add(player);
		nestedPanel.add(server);
		
		
		nestedPanel.add(girlCheckbox);
		nestedPanel.add(dragonCheckbox);
		
	
		nestedPanel.add(playerLabel);
		nestedPanel.add(serverLabel);
		nestedPanel.setPreferredSize(new Dimension((GamePanel.WIDTH * GamePanel.SCALE) / 2,
				(GamePanel.HEIGHT * GamePanel.SCALE) / 2));

		return nestedPanel;
	}

	private JTextField createInputTextField(String defaultValue, Rectangle bounds)
	{
		JTextField textField = new JTextField();
		textField.setBounds(bounds);
		textField.setColumns(20);
		textField.setText(defaultValue);
		return textField;
	}

	private JLabel createLabel(String value, Rectangle bounds)
	{
		JLabel playerLabel = new JLabel(value);
		playerLabel.setBounds(bounds);
		return playerLabel;
	}

	// UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED
	// UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED
	// UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED
	// UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED
	// UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED UNUSED
	@Override
	public void keyPressed(int k)
	{}

	@Override
	public void drawToScreen(Graphics graphics, BufferedImage image)
	{}

	@Override
	public void mousePressed(MouseEvent arg0)
	{}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{}

	@Override
	public void mouseExited(MouseEvent arg0)
	{}
}
