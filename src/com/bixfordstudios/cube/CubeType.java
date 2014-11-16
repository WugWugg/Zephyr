package com.bixfordstudios.cube;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

//The textureID's of the types start at index 1 because binding to an index of 0 does nothing

public enum CubeType {

	DEFAULT("res/window.png", false),
	STONE("res/stone.png", false),
	GREEN("res/green.png", false),
	RED("res/red.png", false),
	BLACK("res/black.png", false),
	BLUE("res/blue.png", false),
	GREY("res/grey.png", false),
	BROWN("res/brown.png", false),
	PURPLE("res/purple.png", false),
	WHITE("res/white.png", false),
	TRANSPARENT("res/transparent.png", true);
	
	public final String location;
	public final int textureID;
	public final boolean transparent;
	public final static int FIRST_INDEX = 1;
	
	CubeType(String location, boolean transparent)
	{
		this.location = location;
		this.transparent = transparent;
		
		Texture texture = null;
		try
		{
			texture = TextureLoader.getTexture("PNG", new FileInputStream(new File(location)));
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		this.textureID = texture.getTextureID();
	}
}
