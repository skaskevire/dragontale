package GameState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JPanel;

import com.hypefiend.javagamebook.client.NIOEventReader;
import com.hypefiend.javagamebook.common.EventQueue;
import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.Globals;
import com.hypefiend.javagamebook.common.NIOUtils;

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

public class MultiplayerClientState extends GameState
{	
	private TileMap tileMap;
	private Background bg;
	//private Player player;
	private Map<String, Player> players = new HashMap<>();
	private Map<String, Enemy> enemies;
	private ArrayList<Explosion> explosions;
	private HUD hud;
	AudioPlayer bgMusic;
	
	
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////	
/** address of server */
protected InetAddress serverAddress;
/** connection to server */
protected SocketChannel channel;
/** queue for incoming events */
protected EventQueue inQueue;
/** queue for outoging events */
protected EventQueue outQueue;    
/** reference to NIOEventReader that reads events from the server */
protected NIOEventReader netReader;
/** buffer for outgoing events */
protected ByteBuffer writeBuffer;
/** id of our player */
protected String playerId;
/** id of our current opponent */
protected String opponentId;
/** are we playing right now? */
protected boolean inGame = false;	
////////////////////////////////////////
////////////////////////////////////////
////////////////////////////////////////

public GameEvent createGameEvent() {
	return new GameEventDefault();
   }
   
	 public GameEvent createDisconnectEvent(String reason) {
			return new GameEventDefault(GameEventDefault.S_DISCONNECT, reason);
		    }

	
	
	public MultiplayerClientState(String playerType, String playerName, String address, String playerSkin)
	{

		
		
		

		
		
		inQueue = new EventQueue("GameClient-in");
		outQueue = new EventQueue("GameClient-out");
		writeBuffer = ByteBuffer.allocate(Globals.MAX_EVENT_SIZE );

		try {
		    serverAddress = InetAddress.getByName(address);
		}
		catch (UnknownHostException uhe) {
	//	    log.error("unknown host: " + args[0]);
		    System.exit(1);
		}
		this.playerId = playerName + new Random().nextInt(93);

		// connect to the server
		if (!connect()) 
		    System.exit(1);
		
		// start our net reader
		netReader = new NIOEventReader(this, channel, inQueue);
		netReader.start();
		
		
		init(playerType);
		
		
		
		
		
		
		
		
		MultiplayerClientStateWorker mcsw = new MultiplayerClientStateWorker(this);
		Thread[] threads = new Thread[10];
		
		for(int i = 0; i < 10; i++)
		{
			threads[i] = new Thread(mcsw);
			threads[i].setDaemon(true);
			threads[i].start();
		}
		
		
		login(playerSkin);
		try
		{
			Thread.sleep(1000L);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	 protected void login(String playerSkin) {
			GameEvent e = new GameEventDefault(GameEventDefault.C_LOGIN);
			e.setGameName("RPS");
			e.setPlayerId(playerId);
			e.setMessage(playerSkin);
			writeEvent(e);
	}
    /**
     * connect to the server
     */
    protected boolean connect() {
	//log.info("connect()");
	try {
	    // open the socket channel
	    channel = SocketChannel.open(new InetSocketAddress(serverAddress, Globals.PORT));
	    channel.configureBlocking(false);

 	    // we don't like Nagle's algorithm
	    channel.socket().setTcpNoDelay(true);
	    return true;
	}
	catch (ConnectException ce) {
	  //  log.error("Connect Exception: " + ce.getMessage());
	    return false;
	}
	catch (Exception e) {
	 //   log.error("Exception while connecting", e);
	    return false;
	}
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
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		bgMusic = new AudioPlayer("/Music/level1-1.mp3");
		bgMusic.play();
	}
	
	private void populateEnemies()
	{
		enemies = new HashMap<>();
	}

	@Override
	public void update()
	{
		//processIncomingEvents();
		//outQueue.enQueue(new GameEventDefault(GameEventDefault.S_UPDATE_PLAYER_COORDINATES));
		//outQueue.enQueue(new GameEventDefault(GameEventDefault.S_UPDATE_ENEMY_COORDINATES));
		
		if(players.get(playerId) != null)
		{
			outQueue.enQueue(new GameEventDefault(GameEventDefault.S_MOVE_PLAYER, 
					players.get(playerId).getKeyEvents().toString()				
			));
		}

		//writeOutgoingEvents();

		if(players.get(playerId) != null){
			
			if(players.get(playerId).isDead())
			{
				bgMusic.stop();
				new AudioPlayer("/Music/fail.mp3").play();
				GameStateManager.getInstance().setState(GameStateManager.MENUSTATE);
			}
			players.get(playerId).update();
			tileMap.setPosition(				
					GamePanel.WIDTH / 2 - players.get(playerId).getX(),
					GamePanel.HEIGHT / 2 - players.get(playerId).getY()
					);
			bg.setPosition(tileMap.getX(), tileMap.getY());
		
			
			//attack enemies
			players.get(playerId).checkAttack(enemies);
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
	 
	 /** 
	     * send an event to the server 
	     */
	    protected void writeEvent(GameEvent ge) {
		// set the gamename and player id
		
	    	
	    	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	    	ge.setGameName("RPS");
	    	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		ge.setPlayerId(playerId);

		NIOUtils.prepBuffer(ge, writeBuffer);
		NIOUtils.channelWrite(channel, writeBuffer);
	    }
	
	
	

	    @Override
		public void draw(Graphics2D g, JPanel panel)
		{
			bg.draw(g);		
			tileMap.draw(g);
			for(Entry<String, Player> player : players.entrySet())
			{
				player.getValue().draw(g);
			}
			
			
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
			players.get(playerId).addKeyEvent("left");
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			players.get(playerId).addKeyEvent("right");
		}
		if(k == KeyEvent.VK_UP)
		{
			players.get(playerId).addKeyEvent("up");
		}
		if(k == KeyEvent.VK_DOWN)
		{
			players.get(playerId).addKeyEvent("down");
		}
		if(k == KeyEvent.VK_W)
		{
			players.get(playerId).addKeyEvent("jumping");
		}
		if(k == KeyEvent.VK_E)
		{
			players.get(playerId).addKeyEvent("gliding");
		}
		if(k == KeyEvent.VK_R)
		{
			players.get(playerId).addKeyEvent("scratching");
		}
		if(k == KeyEvent.VK_F)
		{
			players.get(playerId).addKeyEvent("firing");
		}
		
	}

	@Override
	public void keyReleased(int k)
	{
		if(k == KeyEvent.VK_LEFT)
		{
			players.get(playerId).removeKeyEvent("left");
		}
		if(k == KeyEvent.VK_RIGHT)
		{
			players.get(playerId).removeKeyEvent("right");
		}
		if(k == KeyEvent.VK_UP)
		{
			players.get(playerId).removeKeyEvent("up");
		}
		if(k == KeyEvent.VK_DOWN)
		{
			players.get(playerId).removeKeyEvent("down");
		}
		if(k == KeyEvent.VK_W)
		{
			players.get(playerId).removeKeyEvent("jumping");
		}
		if(k == KeyEvent.VK_E)
		{
			players.get(playerId).removeKeyEvent("gliding");
		}
		if(k == KeyEvent.VK_R)
		{
			players.get(playerId).removeKeyEvent("scratching");
		}
		if(k == KeyEvent.VK_F)
		{
			players.get(playerId).removeKeyEvent("firing");
		}
		
	}
	

	public void updatePlayerCoordinates(String data)
	{	
		if(data != null)
		{
			String [] es = data.split(":");
			for (int i = 0; i < es.length ; i++)
			{
				String [] playerFields = es[i].split(",");
				
					
					
					if(players.get(playerFields[0]) == null)
					{		
						Player player;

						int[] numDragonFrames = { 2, 8, 1, 2, 4, 2, 5 };

							player = new Player(tileMap,"/Sprites/Player/playersprites.gif",numDragonFrames);

						if(playerFields[5].equals("girl"))
						{
							int[] numGirlFrames = { 8, 8, 1, 2, 4, 2, 5 };
							 
							player = new Player(tileMap,"/Sprites/Player/playersprites.png",numGirlFrames);
						}
						player.setX(Double.valueOf(playerFields[1]));
						player.setY(Double.valueOf(playerFields[2]));
						player.setXmap(Double.valueOf(playerFields[3]));				
						player.setYmap(Double.valueOf(playerFields[4]));
						if(playerFields[0].equals(playerId))
						{
							hud = new HUD(player);
						}
						players.put(playerFields[0], player);
					}
					else
					{
						players.get(playerFields[0]).setX(Double.valueOf(playerFields[1]));
						players.get(playerFields[0]).setY(Double.valueOf(playerFields[2]));
						players.get(playerFields[0]).setXmap(Double.valueOf(playerFields[3]));				
						players.get(playerFields[0]).setYmap(Double.valueOf(playerFields[4]));
					}
					
					
			}
		}
		
	}
	
	
	public String getPlayerCoordinates()
	{
		return null;
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
		String [] es = data.split(":");
		for (int i = 0; i < es.length ; i++)
		{
			String [] enemy = es[i].split(",");
			
			try
			{
				if(enemies.get(enemy[1]) == null)
				{
					 
					Enemy enemyObj = (Enemy) Class.forName(enemy[0]).getConstructor(TileMap.class).newInstance(tileMap);
					enemyObj.setX(Double.valueOf(enemy[2]));
					enemyObj.setXmap(Double.valueOf(enemy[3]));
					enemyObj.setY(Double.valueOf(enemy[4]));
					enemyObj.setYmap(Double.valueOf(enemy[5]));
					enemies.put(enemy[1], enemyObj);
					
					
					/*if(enemy[0].equals("Slugger"))
					{
						Slugger slugger = new Slugger(tileMap);
						
						//enemyObj = (Enemy) Class.forName(enemy[0]).getConstructor(TileMap.class).newInstance(tileMap);
						slugger.setX(Double.valueOf(enemy[2]));
						slugger.setXmap(Double.valueOf(enemy[3]));
						slugger.setY(Double.valueOf(enemy[4]));
						slugger.setYmap(Double.valueOf(enemy[5]));
						enemies.put(enemy[1], slugger);
					}
					if(enemy[0].equals("Hrum"))
					{
						Hrum hrum = new Hrum(tileMap);
						
						//enemyObj = (Enemy) Class.forName(enemy[0]).getConstructor(TileMap.class).newInstance(tileMap);
						slugger.setX(Double.valueOf(enemy[2]));
						slugger.setXmap(Double.valueOf(enemy[3]));
						slugger.setY(Double.valueOf(enemy[4]));
						slugger.setYmap(Double.valueOf(enemy[5]));
						enemies.put(enemy[1], slugger);
					}*/
						
				} else
				{
					Enemy enemyObj = enemies.get(enemy[1]);
					enemyObj.setX(Double.valueOf(enemy[2]));
					enemyObj.setXmap(Double.valueOf(enemy[3]));
					enemyObj.setY(Double.valueOf(enemy[4]));
					enemyObj.setYmap(Double.valueOf(enemy[5]));
				}
			

				
				
			}
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void drawToScreen(Graphics graphics, BufferedImage image)
	{
		Graphics g2 = graphics;
		g2.drawImage(image, 0, 0, GamePanel.WIDTH *  GamePanel.SCALE, GamePanel.HEIGHT * GamePanel.SCALE,  null);
		g2.dispose();	
		
	}

}