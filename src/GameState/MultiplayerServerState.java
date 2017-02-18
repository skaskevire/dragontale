package GameState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import java.util.Set;


import Entity.Enemy;
import Entity.Explosion;
import Entity.Player;
import Entity.Enemies.Hrum;
import Entity.Enemies.Slugger;
import server.tileMap.TileMap;

public class MultiplayerServerState extends GameState
{	
	private TileMap tileMap;
	private Map<String, Enemy> enemies;
	private Map<String, Player> players = new HashMap<>();
	private ArrayList<Explosion> explosions;

	public MultiplayerServerState(String playerType)
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

		tileMap.loadMap("/Maps/level1-1.map");
		
		tileMap.setPosition(0, 0);	
		tileMap.setTween(0.07);
		

		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
	}
	
	private void populateEnemies()
	{
		enemies = new HashMap<>();
		Point[] points = new Point[] {
				new Point(860, 200),
				new Point(1625, 200),
				new Point(1680, 200)
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
		enemies.put("r2d2",h);		
	}

	@Override
	public void update()
	{		
		for(Entry<String, Player> player : players.entrySet())
		{
			if(player.getValue().isDead())
			{
				players.remove(player.getKey());
				continue;
			} 
			player.getValue().update();
			player.getValue().checkAttack(enemies);
		}
		
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
	}

	@Override
	public void keyPressed(int k)
	{		
	}

	@Override
	public void keyReleased(int k)
	{		
	}

	public String getPlayerCoordinates()
	{	
		StringBuilder sb = new StringBuilder();
		for(Entry<String,Player> player : players.entrySet())
		{			
			sb.append(player.getKey())
			.append(",")
			.append(player.getValue().getX())
			.append(",")
			.append(player.getValue().getY())
			.append(",")
			.append(player.getValue().getXmap())
			.append(",")
			.append(player.getValue().getYmap())
			.append(",")
			.append(player.getValue().getSkin())
			.append(":");
		}
		
		return sb.toString();
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
		players.get(playerId).setKeyEvents(keyEvents);
		//player.setKeyEvents(keyEvents);
	}
	
	@Override
	public String getEnemyCoordinates()
	{
		StringBuilder sb = new StringBuilder();
		for(Entry<String,Enemy> enemy : enemies.entrySet())
		{			
			sb.append(enemy.getValue().getClass().getName())
			.append(",")
			.append(enemy.getKey())
			.append(",")
			.append(enemy.getValue().getX())
			.append(",")
			.append(enemy.getValue().getXmap())
			.append(",")
			.append(enemy.getValue().getY())
			.append(",")
			.append(enemy.getValue().getYmap())
			.append(":");
		}
		
		return sb.toString();
		
	}
	
	
	public void addNewPlayer(String pid, String skin)
	{		
		if(players.get(pid) == null)
		{
			
			 Player player = null;
			 if(skin.equals("girl"))
			 {
				 int[] numDragonFrames = { 2, 8, 1, 2, 4, 2, 5 };
				 player = new Player(tileMap,"/Sprites/Player/playersprites.png",numDragonFrames, skin);
			 }
			 if(skin.equals("dragon"))
			 {
				 int[] numGirlFrames = { 8, 8, 1, 2, 4, 2, 5 };
				 player = new Player(tileMap,"/Sprites/Player/playersprites.gif",numGirlFrames, skin);
			 }
			 
			 
			
			 player.setPosition(100, 100);
			 players.put(pid, player);
		}		
	}

	@Override
	public void addOrUpdateEnemies(String data)
	{		
	}

	@Override
	public void drawToScreen(Graphics graphics, BufferedImage image)
	{
		// TODO Auto-generated method stub
		
	}

}
