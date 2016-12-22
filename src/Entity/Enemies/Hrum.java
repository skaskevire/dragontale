package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

public class Hrum extends Enemy
{
	private BufferedImage[] sprites;
	public Hrum(TileMap tm) {
		super(tm);
		
		moveSpeed = 0.3;
		maxSpeed = 0.3;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;
		
		width = 40;
		height = 50;
		cwidth = 20;
		cheight = 40;
		
		health = maxHealth = 100;
		
		damage = 2;		
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream("/Sprites/Enemies/hrum.png"));
			sprites = new BufferedImage[5];
			for(int i = 0; i < sprites.length; i++)
			{
				sprites[i] = spritesheet.getSubimage(
						i*width,
						0,
						width,
						height
						);
			}
			

		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		
		right = true;
		facingRight = true;
	}
	
	private void getNextPosition()
	{
		if(left)
		{
			dx -= moveSpeed;
			if(dx < -maxSpeed)
			{
				dx = -maxSpeed;
			}
		}
		else if(right)
		{
			dx += moveSpeed;
			if(dx > maxSpeed)
			{
				dx = maxSpeed;
			}
		}
		
		if(falling)
		{
			dy += fallSpeed;
		}
	}
	
	public void update()
	{
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(flinching)
		{
			long elapsed = 
					(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 400)
			{
				flinching = false;
			}
		}
		
		if(right && dx == 0)
		{
			right = false;
			left = true;
			facingRight = false;
		}
		else if(left && dx == 0)
		{
			right = true;
			left = false;
			facingRight = true;
		}
		
		animation.update();
	}
	
	public void draw(Graphics2D g)
	{
	//	if(notOnScreen())
	//	{
	//		return;
	//	}
		setMapPosition();
		super.draw(g);
	}

}
