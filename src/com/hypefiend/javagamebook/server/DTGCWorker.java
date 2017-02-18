package com.hypefiend.javagamebook.server;

import java.util.HashMap;

import com.hypefiend.javagamebook.common.GameEventDefault;
import com.hypefiend.javagamebook.common.Globals;
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

}
