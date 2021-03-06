
package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Main.GamePanel;
import server.tileMap.Tile;
import server.tileMap.TileMap;

public abstract class MapObject
{
	public double getXmap()
	{
		return xmap;
	}

	public double getYmap()
	{
		return ymap;
	}


	protected Set<String> keyEvents = new HashSet<String>(){
		private static final long serialVersionUID = -4438428606386159048L;
		
		public String toString() {
			        Iterator<String> i = iterator();
			        if (! i.hasNext())
			            return "";
			
			        StringBuilder sb = new StringBuilder();
			        for (;;) {
			        	String e = i.next();
			            sb.append(e);
			            if (! i.hasNext())
			                return sb.toString();
			            sb.append(",");
			        }
			    }
		
	};
	public Set<String> getKeyEvents()
	{
		return keyEvents;
	}

	public void addKeyEvent(String keyEvent)
	{
		keyEvents.add(keyEvent);
	}
	
	public void setKeyEvents(Set<String> keyEvents)
	{
		this.keyEvents = keyEvents;
	}
	
	public void removeKeyEvent(String keyEvent)
	{
		keyEvents.remove(keyEvent);
	}


	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	
	
	
	
	public void setXmap(double xmap)
	{
		this.xmap = xmap;
	}

	public void setYmap(double ymap)
	{
		this.ymap = ymap;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}


	protected double ymap;

	protected double x;
	protected double y;
	protected double dx;
	protected double dy;

	protected int width;
	protected int height;

	protected int cwidth;
	protected int cheight;

	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;

	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;

	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;

	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;

	public MapObject(TileMap tm)
	{
		tileMap = tm;
		tileSize = tm.getTileSize();
	}

	public boolean intersects(MapObject o)
	{
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();

		return r1.intersects(r2);
	}

	public Rectangle getRectangle()
	{
		return new Rectangle((int) x - cwidth, (int) y - cheight, cwidth, cheight);
	}

	public void checkTileMapCollision()
	{
		currCol = (int) x / tileSize;
		currRow = (int) y / tileSize;

		xdest = x + dx;
		ydest = y + dy;

		xtemp = x;
		ytemp = y;

		calculateCorners(x, ydest);
		if (dy < 0)
		{
			if (topLeft || topRight)
			{
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else
			{
				ytemp += dy;
			}
		}

		if (dy > 0)
		{
			if (bottomLeft || bottomRight)
			{
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else
			{
				ytemp += dy;
			}
		}

		calculateCorners(xdest, y);

		if (dx < 0)
		{
			if (bottomLeft || topLeft)
			{
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else
			{
				xtemp += dx;
			}
		}
		if (dx > 0)
		{
			if (bottomRight || topRight)
			{
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			}
			else
			{
				xtemp += dx;
			}
		}

		if (!falling)
		{
			calculateCorners(x, ydest + 1);
			if (!bottomLeft && !bottomRight)
			{
				falling = true;
			}
		}

	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getCwidth()
	{
		return cwidth;
	}

	public int getCheight()
	{
		return cheight;
	}

	public void setPosition(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public void setVector(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}

	public void setMapPosition()
	{
		xmap = tileMap.getX();
		ymap = tileMap.getY();
	}

	public void setLeft(boolean b)
	{
		left = b;
	}

	public void setRight(boolean b)
	{
		right = b;
	}

	public void setUp(boolean b)
	{
		up = b;
	}

	public void setDown(boolean b)
	{
		down = b;
	}

	public void setJumping(boolean b)
	{
		jumping = b;
	}

	public boolean notOnScreen()
	{
		return x + xmap + width < 0 || x + xmap - width > GamePanel.WIDTH || y + ymap + height < 0
				|| y + ymap - height > GamePanel.HEIGHT;
	}

	public void calculateCorners(double x, double y)
	{
		int leftTile = (int) (x - cwidth / 2) / tileSize;
		int rightTile = (int) (x + cwidth / 2 - 1) / tileSize;
		int topTile = (int) (y - cheight / 2) / tileSize;
		int bottom = (int) (y + cheight / 2 - 1) / tileSize;

		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottom, leftTile);
		int br = tileMap.getType(bottom, rightTile);

		topLeft = tl == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;
	}
	
	public void draw(Graphics2D g)
	{
		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap -width / 2), (int)(y + ymap -height / 2), null);
		
		}
		else
		{			
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2 + width), (int)(y + ymap -height / 2),-width, height, null);
		}
	}

}
