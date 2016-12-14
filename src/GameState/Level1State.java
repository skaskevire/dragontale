package GameState;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Audio.AudioPlayer;
import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Enemies.Slugger;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class Level1State extends GameState
{	
	private TileMap tileMap;
	private Background bg;
	private Player player;
	private ArrayList<Enemy> enemies;
	private ArrayList<Explosion> explosions;
	private HUD hud;
	AudioPlayer bgMusic;
	public Level1State(GameStateManager gsm)
	{
		this.gsm = gsm;
		init();
	}

	@Override
	public void init()
	{
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);	
		tileMap.setTween(0.07);
		

		bg = new Background("/Backgrounds/grassbg1.gif", 0.1);
		
		
		player = new Player(tileMap);
		
		player.setPosition(100, 100);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		hud = new HUD(player);
		bgMusic = new AudioPlayer("/Music/level1-1.mp3");
		bgMusic.play();
	}
	
	private void populateEnemies()
	{
		enemies = new ArrayList<Enemy>();
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
			enemies.add(s);
		}
		
		
		
	}

	@Override
	public void update()
	{
		if(enemies.isEmpty())
		{
			bgMusic.stop();
			new AudioPlayer("/Music/win.mp3").play();
			gsm.setState(GameStateManager.MENUSTATE);
		}
		if(player.isDead())
		{
			bgMusic.stop();
			new AudioPlayer("/Music/fail.mp3").play();
			
			gsm.setState(GameStateManager.MENUSTATE);
		}
		player.update();
		tileMap.setPosition(				
				GamePanel.WIDTH / 2 - player.getX(),
				GamePanel.HEIGHT / 2 - player.getY()
				);
		bg.setPosition(tileMap.getX(), tileMap.getY());
		
		
		//attack enemies
		player.checkAttack(enemies);
		
		for (int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			enemies.get(i).update();
			if(e.isDead())
			{
				enemies.remove(i);
				i--;
				explosions.add(new Explosion(e.getX(), e.getY()));
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
	public void draw(Graphics2D g)
	{
		bg.draw(g);		
		tileMap.draw(g);	
		player.draw(g);
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
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
			player.setLeft(true);
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			player.setRight(true);
		}
		if(k == KeyEvent.VK_UP)
		{
			player.setUp(true);
		}
		if(k == KeyEvent.VK_DOWN)
		{
			player.setDown(true);
		}
		if(k == KeyEvent.VK_W)
		{
			player.setJumping(true);
		}
		if(k == KeyEvent.VK_E)
		{
			player.setGliding(true);
		}
		if(k == KeyEvent.VK_R)
		{
			player.setScratching();
		}
		if(k == KeyEvent.VK_F)
		{
			player.setFiring();
		}
		
	}

	@Override
	public void keyReleased(int k)
	{
		if(k == KeyEvent.VK_LEFT)
		{
			player.setLeft(false);
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			player.setRight(false);
		}
		if(k == KeyEvent.VK_UP)
		{
			player.setUp(false);
		}
		if(k == KeyEvent.VK_DOWN)
		{
			player.setDown(false);
		}
		if(k == KeyEvent.VK_W)
		{
			player.setJumping(false);
		}
		if(k == KeyEvent.VK_E)
		{
			player.setGliding(false);
		}
		if(k == KeyEvent.VK_R)
		{
			player.setScratching();
		}
		if(k == KeyEvent.VK_F)
		{
			player.setFiring();
		}
		
	}

}
