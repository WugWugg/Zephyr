package com.bixfordstudios.utility;

/**
 * Structure to hold values for a 3D coordinate system.
 * @author DarkEther
 *
 */
public class CoordinateFloat {
	public float x;
	public float y;
	public float z;
	
	public CoordinateFloat()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public CoordinateFloat(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static float distance(CoordinateFloat coord1, CoordinateFloat coord2)
	{
		return (float) Math.sqrt((coord2.x - coord1.x) * (coord2.x - coord1.x) + (coord2.y - coord1.y) * (coord2.y - coord1.y) + (coord2.z - coord1.z) * (coord2.z - coord1.z));
	}
	
	public CoordinateFloat round()
	{
		this.x =  ((float)((int)(this.x * 100))) / 100;
		this.y =  ((float)((int)(this.y * 100))) / 100;
		this.z =  ((float)((int)(this.z * 100))) / 100;
		return this;
	}
	
	public CoordinateFloat scale(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}
	
	/**
	 * Calculates the six points in all cardinal directions with a distance of 1.
	 * @return Array of coordinates indexed from North, South, East, West, Up, and Down, respectively.
	 */
	public CoordinateFloat[] cardinalNeighbors()
	{
		return cardinalNeighbors(1);
	}
	
	/**
	 * Calculates the six points in all cardinal directions with a given distance.
	 * @param offset Distance from the point
	 * @return Array of coordinates indexed from North, South, East, West, Up, and Down, respectively.
	 */
	public CoordinateFloat[] cardinalNeighbors(float offset)
	{
		CoordinateFloat[] ret = new CoordinateFloat[6];
		//NORTH
		ret[0] = new CoordinateFloat(this.x, this.y, this.z - 1);
		//SOUTH
		ret[1] = new CoordinateFloat(this.x, this.y, this.z + 1);
		//EAST
		ret[2] = new CoordinateFloat(this.x - 1, this.y, this.z);
		//WEST
		ret[3] = new CoordinateFloat(this.x + 1, this.y, this.z);
		//UP
		ret[4] = new CoordinateFloat(this.x, this.y + 1, this.z);
		//DOWN
		ret[5] = new CoordinateFloat(this.x, this.y - 1, this.z);
		return ret;
	}
	
	public String toString()
	{
		return this.x +", "+ this.y +", "+ this.z;
	}
}
