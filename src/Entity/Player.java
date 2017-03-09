
package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import Audio.AudioPlayer;
import server.tileMap.TileMap;

public class Player extends MapObject
{
	private boolean god;
	public boolean isGod()
	{
		return god;
	}

	public void setGod(boolean god)
	{
		this.god = god;
	}

	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	public boolean isDead() {
		return dead;
	}

	private boolean flinching;
	private long flinchTime;


	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;

	private int scratchDamage;
	private int scratchRange;
	private String skin;


	private ArrayList<BufferedImage[]> sprites;

	private static final int IDLE = 0;
	private static final int WALKING = 1; 
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;
	
	//private HashMap<String, AudioPlayer> sfx;

	public Player(TileMap tm, String skinPicturePath, int[] numFrames, String skin)
	{
		this(tm, skinPicturePath, numFrames);
		this.skin = skin;		
	}
	public String getSkin()
	{
		return skin;
	}

	public void setSkin(String skin)
	{
		this.skin = skin;
	}

	public Player(TileMap tm, String skinPicturePath, int[] numFrames)
	{		
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;

		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 1.00;
		fallSpeed = 0.08;
		maxFallSpeed = 4.0;
		jumpStart = -3.8;
		stopJumpSpeed = 0.3;

		facingRight = true;

		health = maxHealth = 5;

		fire = maxFire = 2500;

		fireCost = 200;
		fireBallDamage = 2;

		scratchDamage = 2;
		scratchRange = 40;

		fireBalls = new ArrayList<FireBall>();

		try
		{
			BufferedImage spritesheet = ImageIO
					.read(getClass().getResourceAsStream(skinPicturePath));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 7; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++)
				{
					if (i != 6)
					{
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					}
					else
					{
						bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2,
								height);
					}

				}

				sprites.add(bi);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
		//sfx = new HashMap<String, AudioPlayer>();
		//sfx.put("jump", new AudioPlayer("/SFX/jump.mp3"));
		//sfx.put("scratch", new AudioPlayer("/SFX/scratch.mp3"));

	}

	public int getHealth()
	{
		return health;
	}

	public int getMaxHealth()
	{
		return maxHealth;
	}

	public int getFire()
	{
		return fire;
	}

	public int getMaxFire()
	{
		return maxFire;
	}

	public void update()
	{
		getNextPosition();
		try{
			checkTileMapCollision();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			dead = true;
		}
		
		setPosition(xtemp, ytemp);

		if (currentAction == SCRATCHING)
		{
			if (animation.hasPlayedOnce())
			{
				keyEvents.remove("scratching");
			}
		}

		if (currentAction == FIREBALL)
		{
			if (animation.hasPlayedOnce())
			{
				keyEvents.remove("firing");
			}
		}

		fire += 1;
		if (fire > maxFire)
		{
			fire = maxFire;
		}
		if (keyEvents.contains("firing") && currentAction != FIREBALL)
		{
			if (fire > fireCost)
			{
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);

			}
		}

		for (int i = 0; i < fireBalls.size(); i++)
		{
			fireBalls.get(i).update();
			if (fireBalls.get(i).shouldRemove())
			{
				fireBalls.remove(i);
				i--;
			}
		}
		
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTime) / 1000000;
			if(elapsed > 1000)			
			{
				flinching = false;
			}
		}

		if (keyEvents.contains("scratching"))
		{
			if (currentAction != SCRATCHING)
			{
				//sfx.get("scratch").play();
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;
			}
		}
		else if (keyEvents.contains("firing"))
		{
			if (currentAction != FIREBALL)
			{
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(30);
			}
		}
		else if (dy > 0)
		{
			if (keyEvents.contains("gliding"))
			{
				if (currentAction != GLIDING)
				{
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			}
			else if (currentAction != FALLING)
			{
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if (dy < 0)
		{
			if (currentAction != JUMPING)
			{
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}
		else if (keyEvents.contains("left") || keyEvents.contains("right"))
		{
			if (currentAction != WALKING)
			{
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}
		else
		{
			if (currentAction != IDLE)
			{
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}

		animation.update();

		if (currentAction != SCRATCHING && currentAction != FIREBALL)
		{
			if (keyEvents.contains("right"))
			{
				facingRight = true;
			}
			if (keyEvents.contains("left"))
			{
				facingRight = false;
			}
		}

	}

	private void getNextPosition()
	{
		if (keyEvents.contains("left"))
		{
			dx -= moveSpeed;
			if (dx < -maxSpeed)
			{
				dx = -maxSpeed;
			}
		}
		else if (keyEvents.contains("right"))
		{
			dx += moveSpeed;
			if (dx > maxSpeed)
			{
				dx = maxSpeed;
			}
		}
		else
		{
			if (dx > 0)
			{
				dx -= stopSpeed;
				if (dx < 0)
				{
					dx = 0;
				}
			}
			if (dx < 0)
			{
				dx += stopSpeed;
				if (dx > 0)
				{
					dx = 0;
				}
			}
		}

		if ((currentAction == SCRATCHING || currentAction == FIREBALL) && !(keyEvents.contains("jumping") || falling))
		{
			dx = 0;
		}

		if (keyEvents.contains("jumping") && !falling)
		{
			//sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
		}

		if (falling)
		{
			if (dy > 0 && keyEvents.contains("gliding"))
			{
				dy += fallSpeed * 0.1;
			}
			else
			{
				dy += fallSpeed;
			}

			if (dy > 0)
			{
				keyEvents.remove("jumping");
			}

			if (dy < 0 && !keyEvents.contains("jumping"))
			{
				dy += stopJumpSpeed;
			}

			if (dy > maxFallSpeed)
			{
				dy = maxFallSpeed;
			}
		}
	}

	public void draw(Graphics2D g)
	{
		setMapPosition();

		for (int i = 0; i < fireBalls.size(); i++)
		{
			fireBalls.get(i).draw(g);
		}

		if (flinching)
		{
			long elapsed = (System.nanoTime() - flinchTime) / 1000000;
			if (elapsed / 100 % 2 == 0)
			{
				return;
			}
		}

		super.draw(g);

		// g.drawString("x: " + String.valueOf(x),0, 10);
		// g.drawString("y: " + String.valueOf(y),0, 20);
		// g.drawString("xmap: " + String.valueOf(xmap),0, 30);
		// g.drawString("ymap: " + String.valueOf(ymap),0, 40);
	}

	public void checkAttack(Map<String,Enemy> enemiesMap)
	{		
		
		ArrayList<Enemy> enemies = new ArrayList<>(enemiesMap.values());
		
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			if (keyEvents.contains("scratching"))
			{
				if (facingRight)
				{
					if (e.getX() > x && e.getX() < x + scratchRange && e.getY() > y - height / 2
							&& e.getY() < y + height / 2)
					{
						e.hit(scratchDamage);
					}
				}
				else
				{
					if (e.getX() < x && e.getX() > x - scratchRange && e.getY() > y - height / 2
							&& e.getY() < y + height / 2)
					{
						e.hit(scratchDamage);
					}
				}
			}
			
			//fireballs
			for(int j = 0; j < fireBalls.size(); j++)
			{
				if (fireBalls.get(j).intersects(e) )
				{
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
				}
			}
			
			//check for enemy collision
			
			if(intersects(e))
			{
				hit(e.getDamage());
			}
			
		}
	}
	
	public void hit(int damage)
	{
		if(flinching)
		{
			return;
		}
		
		if(!isGod())
		{
			health -= damage;
		}		
		if(health < 0) health = 0;
		if(health == 0)
		{
			dead = true;
		}
		flinching = true;
		flinchTime = System.nanoTime();
	}

}
