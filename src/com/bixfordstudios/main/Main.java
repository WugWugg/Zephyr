package com.bixfordstudios.main;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.bixfordstudios.chunk.Chunk;
import com.bixfordstudios.input.InputManager;
import com.bixfordstudios.player.Player;

public class Main {

	public static final String VERSION_NUMBER = "0.2.1";
	public static final int DISPLAY_WIDTH = 1024;
	public static final int DISPLAY_HEIGHT = 768;
	public static final int DISPLAY_FPS = 60;
	public static Player firstPlayer;
	
	
	public static void main(String[] args) {
		
		//Testing
		Chunk chunk1 = new Chunk();
		chunk1.setupData();
		//
			
		while (!Display.isCloseRequested())
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

			chunk1.render(0, -17, 0);
			
			//Input Check
			InputManager.interpretInput(firstPlayer);
			
			Display.sync(DISPLAY_FPS); 
			Display.update();
		}
		
		Display.destroy(); 
	}
	
	static 
	{
		//Display Initialization Code
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.setTitle("Voxel Engine (v"+ VERSION_NUMBER +") -- Bixford Studios");
			Display.create();
		} catch (LWJGLException e) {e.printStackTrace();}
		
		//OpenGL Initialization Code
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		//Create a new perspective with x degrees for field of view, Width / Height aspect ratio, .001f zNear, 100 zFar
		gluPerspective(90f, DISPLAY_WIDTH / DISPLAY_HEIGHT, 0.001f, 1000f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glFrontFace(GL_CCW);
			
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		//Player Initialization Code
		firstPlayer = new Player();
	}
}
