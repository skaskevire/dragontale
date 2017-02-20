package com.hypefiend.javagamebook.server;

import java.util.HashMap;
import java.util.Iterator;

import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.Globals;
import com.hypefiend.javagamebook.common.Player;
import com.hypefiend.javagamebook.common.PlayerDefault;
import com.hypefiend.javagamebook.server.controller.DTGameController;

import GameState.ServerGameStateManager;



public class DTGCWorker implements Runnable
{
	private DTGameController dtgc;
	private HashMap players;
	

	public DTGCWorker(DTGameController dtgc, HashMap players)
	{
		this.dtgc = dtgc;
		this.players = players;
	}
	
	@Override
	public void run()
	{
		while(true)
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
				///players.values().forEach(p -> removePlayerWithClosedChannel((Player) p));
				/*for(Object player : players.values().forEach(p -> );)
				{
					if(!((PlayerDefault) player).getChannel().isOpen())
					{
						ServerGameStateManager.getInstance().removePlayer(((PlayerDefault) player).getPlayerId());
						players.remove(((PlayerDefault) player).getPlayerId());
						dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.SB_PLAYER_QUIT, ((PlayerDefault) player).getPlayerId()), players.values());
					}
				}*/
				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_ENEMY_COORDINATES, csToUpdate), players.values());
				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_PLAYER_COORDINATES, coordinatesToUpdate), players.values());
			  
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
	
	private void removePlayerWithClosedChannel(Player player)
	{
		if(!(player.getChannel().isOpen()))
		{
			ServerGameStateManager.getInstance().removePlayer( player.getPlayerId());
			players.remove(player.getPlayerId());
			dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.SB_PLAYER_QUIT, player.getPlayerId()), players.values());
		}
	}

}
