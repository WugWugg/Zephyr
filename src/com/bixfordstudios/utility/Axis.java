package com.bixfordstudios.utility;

import org.lwjgl.util.vector.Vector3f;

public class Axis {
	
	public Vector3f up, forward, right;
	
	public Axis()
	{
		this(new Vector3f(), new Vector3f(), new Vector3f());
	}
	
	public Axis(Vector3f up, Vector3f forward, Vector3f right)
	{
		this.up = up;
		this.forward = forward;
		this.right = right;
	}
	
	
}
