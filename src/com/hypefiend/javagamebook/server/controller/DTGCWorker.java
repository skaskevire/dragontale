package com.hypefiend.javagamebook.server.controller;

import java.util.HashMap;

import com.hypefiend.javagamebook.common.GameEventDefault;

import GameState.GameStateManager;

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
			String coordinatesToUpdate = GameStateManager.getInstance().getPlayerCoodrinates();
			String csToUpdate = GameStateManager.getInstance().getEnemyCoodrinates();
	
			if(players!=null)
			{
				//System.out.println(this.toString());
				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_ENEMY_COORDINATES, csToUpdate), players.values());
				dtgc.sendBroadcastEvent(new GameEventDefault(GameEventDefault.S_UPDATE_PLAYER_COORDINATES, coordinatesToUpdate), players.values());
			  
			}
			
			try
			{
				Thread.sleep(10l);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
