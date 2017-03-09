package com.hypefiend.javagamebook.server.controller;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hypefiend.javagamebook.common.EventQueue;
import com.hypefiend.javagamebook.common.GameConfig;
import com.hypefiend.javagamebook.common.GameEvent;
import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.Player;
import com.hypefiend.javagamebook.common.PlayerDefault;
import com.hypefiend.javagamebook.games.rps.RPSGame;
import com.hypefiend.javagamebook.server.DTGCWorker;

import GameState.ServerGameStateManager;
import server.main.GamePanel;

public class DTGameController extends GameController 
{
	 /** list of connected players */
    public static final Map players = new HashMap<>();

    /** list of games */
    protected HashMap games;
    
	@Override
	protected void initController(GameConfig gc)
	{
		Thread t = new Thread(new GamePanel());
		t.start();
		
		
		
		log.info("initController");
		
	//	players = new HashMap();
		//	clients = new HashMap();
		games = new HashMap();
		
	}
	
	 /**
     * @param numWorkers number of worker threads to spawn
     */
    public void initWrap(int numWorkers) {
    	
    	Thread t = new Thread(new DTGCWorker(this, players));
    	t.start();
	// setup the log4j Logger
	shortname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
	log = Logger.getLogger(shortname);
	log.info("initWrap - " + shortname);

	eventQueue = new EventQueue(shortname + "-in");

	// spawn worker threads
	workers = new Thread[numWorkers];
	for (int i=0; i<numWorkers; i++) {
	    workers[i] = new Thread(this, shortname + "-" + (i+1));
	    workers[i].setDaemon(true);
	    workers[i].setPriority(Thread.NORM_PRIORITY + 1);
	    workers[i].start();
	}
    }

	@Override
	public String getGameName()
	{
		return "RPS";
	}

	@Override
	public Player createPlayer()
	{
		PlayerDefault p = new PlayerDefault();
		p.setSessionId(gameServer.nextSessionId());
		return p;
	}

	 /** 
     * factory method to create GameEvents
     */
    public GameEvent createGameEvent() {
	return new GameEventDefault();
    }
    


	@Override
	protected void processEvent(GameEvent event)
	{
		switch (event.getType()) {
		case GameEventDefault.S_MOVE_PLAYER:
			ServerGameStateManager.getInstance().addPlayerGameStates(event.getMessage(), event.getPlayerId());
			
			break;
		case GameEventDefault.S_UPDATE_PLAYER_COORDINATES:
			String coordinatesToUpdate = ServerGameStateManager.getInstance().getPlayerCoodrinates();
			
			Player p = gameServer.getPlayerById(event.getPlayerId());
			sendEvent(new GameEventDefault(GameEventDefault.S_UPDATE_PLAYER_COORDINATES, coordinatesToUpdate), p);

		    break;
		    
		    
		    
		case GameEventDefault.S_UPDATE_ENEMY_COORDINATES:
			
			String csToUpdate = ServerGameStateManager.getInstance().getEnemyCoodrinates();
			
			Player pl = gameServer.getPlayerById(event.getPlayerId());
			sendEvent(new GameEventDefault(GameEventDefault.S_UPDATE_ENEMY_COORDINATES, csToUpdate), pl);

		    break;
		    
		case GameEventDefault.S_GAME_STATE_CHANGE:
			ServerGameStateManager.getInstance().setState(
							Integer.valueOf(event.getMessage()));
		    login(event);
		    break;
		case GameEventDefault.C_LOGIN:
		    login(event);
		    break;
		case GameEventDefault.C_LOGOUT:
		    logout(event);
		    break;
		case GameEventDefault.C_JOIN_GAME:
		   // join(event);
		    break;
		case GameEventDefault.C_QUIT_GAME:
		    quit(event);
		    break;
		case GameEventDefault.C_CHAT_MSG:
		    chat(event);
		    break;
		case GameEventDefault.C_GET_PLAYERS:
		    getPlayers(event);
		    break;
		}
		
	}
	
	
    /** 
     * handle chat events
     */
    protected void chat(GameEvent e) {
	e.setType(GameEventDefault.SB_CHAT_MSG);
	sendBroadcastEvent(e, players.values());
	log.info("chat, player " + e.getPlayerId() + " says " + e.getMessage());
    }
    
	 protected void login(GameEvent e) {
		 String pid = e.getPlayerId();

			
			Player p = gameServer.getPlayerById(pid);
			if (p == null) {
			    log.error("got login event for null player");
			    return;
			}
			
			if (p.loggedIn())
			    log.warn("got login event for already logged in player: " + pid);
			
			p.setLoggedIn(true);
			
			// send ACK to player
			GameEventDefault la = new GameEventDefault(GameEventDefault.S_LOGIN_ACK_OK);
			sendEvent(la, p);

			// tell everyone this player is here
			GameEventDefault sbl = new GameEventDefault(GameEventDefault.SB_LOGIN, p.getPlayerId());
			sendBroadcastEvent(sbl, players.values());

			// add to our list
			players.put(pid, p);			
			
			ServerGameStateManager.getInstance().addNewPlayer(pid, e.getMessage());		
			
			
			// send player list
			getPlayers(e);

			log.info("login, player: " + pid + ", players online: " + players.size());
			
			
	 
	 }
	 
	 /**
	     * handle logout events
	     */
	    protected void logout(GameEvent e) {
		String pid = e.getPlayerId();
		Player p = (Player) players.get(pid);
		
		// if in game, kill it first
		if (p.inGame()) {
		    quit(e);
		}

		// remove the player
		players.remove(pid);

		// send them a disconnect
		GameEventDefault dis = new GameEventDefault(GameEventDefault.S_DISCONNECT, "logged out");
		sendEvent(dis, p);

		// tell everyone else
		GameEventDefault sbl = new GameEventDefault(GameEventDefault.SB_LOGOUT, p.getPlayerId());
		sendBroadcastEvent(sbl, players.values());

		log.info("logout, player: " + pid + ", players online: " + players.size());
	    }
	    
	    /**
	     * handle quit events
	     */
	    protected void quit(GameEvent e) {
		String p1_id = e.getPlayerId();
		Player player = (Player) players.get(p1_id);
		RPSGame g = (RPSGame) games.get("" + player.getGameId());

		if (g == null) {
		    GameEventDefault jf = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_FAIL);
		    jf.setMessage("you are not in a game");
		    sendEvent(jf, player);
		    return;
		}

		Player p1 = g.getPlayer1();
		Player p2 = g.getPlayer2();
		p1.setInGame(false);
		p2.setInGame(false);
		p1.setGameId(g.getGameId());
		p2.setGameId(g.getGameId());
		games.remove("" + g.getGameId());

		// return the ack, and final game stats
		String msg1 = "GameOver, player " + player.getPlayerId() + " has quit.\n";
		String msg2 = "GameOver\n";

		String msgt = "Final tallies\n" + 
		    p1.getPlayerId() + " wins: " + g.getP1Wins() + "\n" +
		    p2.getPlayerId() + " wins: " + g.getP2Wins() + "\n" +
		    "ties: " + g.getTies() + "\n";
		GameEventDefault qe = new GameEventDefault(GameEventDefault.SB_PLAYER_QUIT, msg1 + msgt);
		sendEvent(qe, p1);
		qe = new GameEventDefault(GameEventDefault.SB_PLAYER_QUIT, msg2 + msgt);
		sendEvent(qe, p2);
	    }
	 
	 
	 
	 
	  /**
	     * handle get_player events
	     */
	    protected void getPlayers(GameEvent e) {
		String pid = e.getPlayerId();
		Player p = (Player) players.get(pid);

		StringBuffer sb = new StringBuffer();
		sb.append("players online:\n");
		Iterator i = players.values().iterator();
		while(i.hasNext()) {
		    Player p2 = (Player) i.next();
		    sb.append(p2.getPlayerId());
		    sb.append("\n");
		}

		GameEventDefault pl = new GameEventDefault(GameEventDefault.S_GET_PLAYERS, sb.toString());
		sendEvent(pl, p);
	    }
	
	    /** 
	     * initiate a game w/another player 
	     * synchronized so we don't have concurrency problems with multiple
	     * players starting games with the same opponent
	     */
	    protected synchronized void join(GameEvent e) {
		String p1_id = e.getPlayerId();
		String p2_id = e.getMessage();

		Player p1 = (Player) players.get(p1_id);

		if (p2_id.equals(p1_id)) {
		    GameEventDefault jf = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_FAIL);
		    jf.setMessage("sorry, can't play against yourself");
		    sendEvent(jf, p1);
		    return;
		}
			
		Player p2 = (Player) players.get(p2_id);

		if ((p1 == null) || (p2==null)) {
		    GameEventDefault jf = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_FAIL);
		    jf.setMessage("unknown player id");
		    sendEvent(jf, p1);
		    return;
		}
		if (p1.inGame()) {
		    GameEventDefault jf = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_FAIL);
		    jf.setMessage("sorry, you are already in a game");
		    sendEvent(jf, p1);
		    return;
		}
		if (p2.inGame()) {
		    GameEventDefault jf = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_FAIL);
		    jf.setMessage("sorry, that player is already in a game");
		    sendEvent(jf, p1);
		    return;
		}

		// create new game
		RPSGame g = new RPSGame(p1, p2);
		games.put("" + g.getGameId(), g);
		p1.setGameId(g.getGameId());
		p2.setGameId(g.getGameId());

		// let them know
		GameEventDefault jok = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_OK);
		jok.setMessage("Game started: " + p1.getPlayerId() + " vs. " + p2.getPlayerId());
		sendEvent(jok, p1);
		
		jok = new GameEventDefault(GameEventDefault.S_JOIN_GAME_ACK_OK);
		jok.setMessage("Game started: " + p1.getPlayerId() + " vs. " + p2.getPlayerId());
		sendEvent(jok, p2);

		log.info("Game started: " + p1.getPlayerId() + " vs. " + p2.getPlayerId());
	    }

}
