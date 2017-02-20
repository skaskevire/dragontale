package com.hypefiend.javagamebook.server;

import java.util.Iterator;
import java.util.Map;

import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.Globals;
import com.hypefiend.javagamebook.common.Player;
import com.hypefiend.javagamebook.server.controller.DTGameController;

import GameState.ServerGameStateManager;



public class DTGCWorker implements Runnable
{
	private DTGameController dtgc;
	private Map<String, Player> players;
	

	public DTGCWorker(DTGameController dtgc, Map<String, Player> players)
	{
		this.dtgc = dtgc;
		this.players = players;
	}
	
	@Override
	public void run()
	{
		while(true)
		{	try
		{
			String coordinatesToUpdate = ServerGameStateManager.getInstance().getPlayerCoodrinates();
			String csToUpdate = ServerGameStateManager.getInstance().getEnemyCoodrinates();
	
			if(!players.isEmpty())
			{
				Iterator<Player> playersIterator = players.values().iterator();
				while(playersIterator.hasNext())
				{
					Player player = playersIterator.next();
					if(!(player.getChannel().isOpen()))
					{
						ServerGameStateManager.getInstance().removePlayer( player.getPlayerId());
						playersIterator.remove();
						dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.SB_PLAYER_QUIT, player.getPlayerId()), players.values());
					}
				}

				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_ENEMY_COORDINATES, csToUpdate), players.values());
				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_PLAYER_COORDINATES, coordinatesToUpdate), players.values());
			}
			} catch (Exception e)		{
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();e.printStackTrace();e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
			e.printStackTrace();
		}
			
			  
	
			
			try
			{
				Thread.sleep(Globals.CHANNEL_WRITE_SLEEP);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
