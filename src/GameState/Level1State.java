package GameState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import java.util.Set;



import Audio.AudioPlayer;
import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Hrum;
import Entity.Enemies.Slugger;
import Main.GamePanel;
import server.tileMap.Background;
import server.tileMap.TileMap;

public class Level1State extends GameState
{	
	private TileMap tileMap;
	private Background bg;
	private Player player;
	private Map<String,Enemy> enemies;
	private ArrayList<Explosion> explosions;
	private HUD hud;
	AudioPlayer bgMusic;
	public Level1State(String playerType)
	{
		init(playerType);
	}

	@Override
	public void init()
	{
		//
	}
	
	private void init(String playerType)
	{
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		if(playerType == "dragon")
		{
			tileMap.loadMap("/Maps/level1-1.map");
		}
		if(playerType == "girl")
		{
			tileMap.loadMap("/Maps/god.map");
		}
		
		tileMap.setPosition(0, 0);	
		tileMap.setTween(0.07);
		

		bg = new Background("/Backgrounds/grassbg1.gif", 0.1);
		
		
		
		
		
		
		
		if(playerType == "dragon")
		{
			 int[] numDragonFrames = { 2, 8, 1, 2, 4, 2, 5 };			 
			 player = new Player(tileMap,"/Sprites/Player/playersprites.gif",numDragonFrames);
		}
		if(playerType == "girl")
		{
			 int[] numGirlFrames = { 8, 8, 1, 2, 4, 2, 5 };
			 player = new Player(tileMap,"/Sprites/Player/playersprites.png",numGirlFrames);
			 player.setGod(true);
		}
		
		player.setPosition(100, 100);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		hud = new HUD(player);
		bgMusic = new AudioPlayer("/Music/level1-1.mp3");
		bgMusic.play();
	}
	
	private void populateEnemies()
	{
		enemies = new HashMap<>();
		Point[] points = new Point[] {
				new Point(860, 200),
				new Point(1625, 200),
				new Point(1680, 200),
				new Point(1800, 200),
				new Point(2800, 200),
				new Point(2850, 200),
				new Point(2900, 200),
				new Point(2950, 200),
				new Point(3000, 200)
		};
		Slugger s;
		for (int i = 0; i < points.length; i++)
		{
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.put(String.valueOf(i), s);
		}
		
		Hrum h = new Hrum(tileMap);
		h.setPosition(3100, 100);
		enemies.put("rd2s", h);		
	}

	@Override
	public void update()
	{
		if(enemies.isEmpty())
		{
			bgMusic.stop();
			new AudioPlayer("/Music/win.mp3").play();
			GameStateManager.getInstance().setState(GameStateManager.MENUSTATE);
		}
		if(player.isDead())
		{
			bgMusic.stop();
			new AudioPlayer("/Music/fail.mp3").play();
			GameStateManager.getInstance().setState(GameStateManager.MENUSTATE);
		}
		player.update();
		tileMap.setPosition(				
				GamePanel.WIDTH / 2 - player.getX(),
				GamePanel.HEIGHT / 2 - player.getY()
				);
		bg.setPosition(tileMap.getX(), tileMap.getY());
		
		
		//attack enemies
		player.checkAttack(enemies);
		
		
		
		for(Entry<String, Enemy> enm : enemies.entrySet())
		{
			enm.getValue().update();
			if(enm.getValue().isDead())
			{
				explosions.add(new Explosion(enm.getValue().getX(), enm.getValue().getY()));
				enemies.remove(enm.getKey());
				
			}
		}
		
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove())
			{
				explosions.remove(i);
				i--;
			}
		}
		
	}

	@Override
	public void draw(Graphics2D g, JPanel panel)
	{
		bg.draw(g);		
		tileMap.draw(g);	
		player.draw(g);
		
		List<Enemy> el = new ArrayList<>(enemies.values());
		
		for (int i = 0; i < el.size(); i++) {
			el.get(i).draw(g);
		}
		
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).setMapPosition(
					(int)tileMap.getX(), (int)tileMap.getY());
			explosions.get(i).draw(g);
		}
		
		hud.draw(g);
		
	}

	@Override
	public void keyPressed(int k)
	{
		if(k == KeyEvent.VK_LEFT)
		{
			player.addKeyEvent("left");
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			player.addKeyEvent("right");
		}
		if(k == KeyEvent.VK_UP)
		{
			player.addKeyEvent("up");
		}
		if(k == KeyEvent.VK_DOWN)
		{
			player.addKeyEvent("down");
		}
		if(k == KeyEvent.VK_W)
		{
			player.addKeyEvent("jumping");
		}
		if(k == KeyEvent.VK_E)
		{
		
			player.addKeyEvent("gliding");
		}
		if(k == KeyEvent.VK_R)
		{
	
			player.addKeyEvent("scratching");
		}
		if(k == KeyEvent.VK_F)
		{
	
			player.addKeyEvent("firing");
		}
		
	}

	@Override
	public void keyReleased(int k)
	{
		if(k == KeyEvent.VK_LEFT)
		{
	
			player.removeKeyEvent("left");
		}
		if(k == KeyEvent.VK_RIGHT)
		{
		
			player.removeKeyEvent("right");
		}
		if(k == KeyEvent.VK_UP)
		{
		
			player.removeKeyEvent("up");
		}
		if(k == KeyEvent.VK_DOWN)
		{
		
			player.removeKeyEvent("down");
		}
		if(k == KeyEvent.VK_W)
		{
	
			player.removeKeyEvent("jumping");
		}
		if(k == KeyEvent.VK_E)
		{

			player.removeKeyEvent("gliding");
		}
		if(k == KeyEvent.VK_R)
		{

			player.removeKeyEvent("scratching");
		}
		if(k == KeyEvent.VK_F)
		{

			player.removeKeyEvent("firing");
		}
		
	}
	
	public String getPlayerCoordinates()
	{
		return player.getX() + "," + player.getXmap() + "," + player.getY() + "," + player.getYmap();
	}
	
	@Override
	public void updatePlayerStates(String data, String playerId)
	{
		
		Set<String> keyEvents = new HashSet<String>();
		
		if(data != null)
		{
			String [] keyEventsArray = data.split(",");
			for(int i = 0; i < keyEventsArray.length; i++)
			{
				keyEvents.add(keyEventsArray[i]);
			}
		}	
		
		player.setKeyEvents(keyEvents);
	}
	
	
	public void addNewPlayer(String pid, String skin)
	{
		
	}

	@Override
	public String getEnemyCoordinates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addOrUpdateEnemies(String data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawToScreen(Graphics graphics, BufferedImage image)
	{
		Graphics g2 = graphics;
		g2.drawImage(image, 0, 0, GamePanel.WIDTH *  GamePanel.SCALE, GamePanel.HEIGHT * GamePanel.SCALE,  null);
		g2.dispose();	
		
	}

	@Override
	public void performCloseOperations()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePlayer(String pid)
	{
		// TODO Auto-generated method stub
		
	}

}
