package com.bixfordstudios.utility;

public class CoordinateInt {

	public int x;
	public int y;
	public int z;
	
	public CoordinateInt()
	{
		this(0, 0, 0);
	}
	
	public CoordinateInt(int x, int y, int z)
	{
		this.x = x;
		this.y = y; 
		this.z = z;
	}
	
	public static float distance(CoordinateInt coord1, CoordinateInt coord2)
	{
		return (float) Math.sqrt((coord2.x - coord1.x) * (coord2.x - coord1.x) + (coord2.y - coord1.y) * (coord2.y - coord1.y) + (coord2.z - coord1.z) * (coord2.z - coord1.z));
	}
	
	public static float distance(CoordinateFloat coord1, CoordinateFloat coord2)
	{
		return (float) Math.sqrt((coord2.x - coord1.x) * (coord2.x - coord1.x) + (coord2.y - coord1.y) * (coord2.y - coord1.y) + (coord2.z - coord1.z) * (coord2.z - coord1.z));
	}
	
	/**
	 * Calculates the six points in all cardinal directions with a distance of 1.
	 * @return Array of coordinates indexed from North, South, East, West, Up, and Down, respectively.
	 */
	public CoordinateInt[] cardinalNeighbors()
	{
		return this.cardinalNeighbors(1);
	}
	
	/**
	 * Calculates the six points in all cardinal directions with a given distance.
	 * @param offset Distance from the point
	 * @return Array of coordinates indexed from North, South, East, West, Up, and Down, respectively.
	 */
	public CoordinateInt[] cardinalNeighbors(float offset)
	{
		CoordinateInt[] ret = new CoordinateInt[6];
		//NORTH
		ret[0] = new CoordinateInt(this.x, this.y, this.z - 1);
		//SOUTH
		ret[1] = new CoordinateInt(this.x, this.y, this.z + 1);
		//EAST
		ret[2] = new CoordinateInt(this.x - 1, this.y, this.z);
		//WEST
		ret[3] = new CoordinateInt(this.x + 1, this.y, this.z);
		//UP
		ret[4] = new CoordinateInt(this.x, this.y + 1, this.z);
		//DOWN
		ret[5] = new CoordinateInt(this.x, this.y - 1, this.z);
		return ret;
	}
	
	public int hashCode()
	{
		return ((this.x * 31 + this.y) * 31 + this.z);
		
	}
	
	public boolean equals(Object ob)
	{
		if(ob==null) return false;
		if(!(ob.getClass() == getClass())) return false;
		
		CoordinateInt other = (CoordinateInt) ob;
		if (other.x == this.x && other.y == this.y && other.z == this.z)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		return this.x +", "+ this.y +", "+ this.z;
	}
}
