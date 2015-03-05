package com.bixfordstudios.input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bixfordstudios.camera.Camera;
import com.bixfordstudios.gamestate.State;
import com.bixfordstudios.gamestate.StateManager;
import com.bixfordstudios.main.Main;

public class InputManager {

	private static final float TRANSLATION_SPEED = 0.1f;
	private static final float MOVEMENT_MODIFIER = 0.001f;
	
	private static float speedX = 0;
	private static float speedY = 0;
	private static float speedZ = 0;
	private static float roll = 0;
	
	private static float mouseLastX = 0;
	private static float mouseLastY = 0;
	
	private InputManager()
	{
		throw new AssertionError();
	}
	
	static
	{		
		try {
			Keyboard.create();
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) { e.printStackTrace(); }
	}
	
	/**
	 * This method interprets the keystrokes to their raw action. In Example, the "W" key will add to the speedY in order to keep a sense of a traditional axis-system.
	 * @param camera
	 */
	public static void interpretInput()
	{
		switch(StateManager.getCurrent())
		{
			case PAUSED:
				Mouse.setGrabbed(false);
				updateKeyboard();
				break;
			case RUNNING:
				updateKeyboard();
				
				//Rotate and translate the player's viewingMatrix
				Camera.rotate(getDY() * MOVEMENT_MODIFIER, getDX() * MOVEMENT_MODIFIER, roll * TRANSLATION_SPEED);
				Camera.translate(speedX, speedY, speedZ);
				
				Mouse.setCursorPosition(Main.DISPLAY_WIDTH / 2, Main.DISPLAY_HEIGHT / 2);
				updateMouse();
				break;
			case CONFIGURING: break;
			case ENDED: break;
			case RESET: break;
		}
	}
	
	private static void updateKeyboard()
	{
		switch (StateManager.getCurrent())
		{
			case PAUSED:
				pausedKeyboard();
				break;
			case RUNNING:
				runningKeyboard();
				break;
				
			case CONFIGURING: break;
			case ENDED: break;
			case RESET: break;
		}
	}
	
	private static void pausedKeyboard()
	{
		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				//Key Press
				switch (Keyboard.getEventKey())
				{
					//case (Keyboard.KEY_ESCAPE) : {StateManager.setCurrent(State.RUNNING); break;}
				}
			}
			else
			{
				//Key Release
				switch (Keyboard.getEventKey())
				{
					case (Keyboard.KEY_ESCAPE) :
						StateManager.setCurrent(State.RUNNING); 
						Mouse.setGrabbed(true);
						//updateMouse() before returning to check it's delta's to remove annoying "jumps" when returning to running state
						updateMouse();
						break;
				}
			}
		}
	}
	
	private static void runningKeyboard()
	{
		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				//Key Press
				switch (Keyboard.getEventKey())
				{	
					case (Keyboard.KEY_A):
					case (Keyboard.KEY_LEFT):
						speedX -= TRANSLATION_SPEED;
						break;
					case (Keyboard.KEY_D):
					case (Keyboard.KEY_RIGHT):
						speedX += TRANSLATION_SPEED;
						break;
						
					case (Keyboard.KEY_W):
					case (Keyboard.KEY_UP):
						speedZ += TRANSLATION_SPEED;
						break;
					case (Keyboard.KEY_S):
					case (Keyboard.KEY_DOWN):
						speedZ -= TRANSLATION_SPEED;
						break; 
					
					case (Keyboard.KEY_SPACE):
						speedY += TRANSLATION_SPEED;
						break;
					case (Keyboard.KEY_C):
						speedY -= TRANSLATION_SPEED;
						break;
						
					case (Keyboard.KEY_E):
						roll += TRANSLATION_SPEED;
						break;
					case (Keyboard.KEY_Q):
						roll -= TRANSLATION_SPEED;
						break;
				}
			}
			else
			{
				//Key Release
				switch (Keyboard.getEventKey())
				{
					case (Keyboard.KEY_A):
					case (Keyboard.KEY_LEFT):
					case (Keyboard.KEY_D):
					case (Keyboard.KEY_RIGHT):
						speedX = 0;
						break;
						
					case (Keyboard.KEY_W):
					case (Keyboard.KEY_UP):
					case (Keyboard.KEY_S):
					case (Keyboard.KEY_DOWN):
						speedZ = 0;
						break;

					case (Keyboard.KEY_SPACE):
					case (Keyboard.KEY_C):
						speedY = 0;
						break;
						
					case (Keyboard.KEY_E):
					case (Keyboard.KEY_Q):
						roll = 0;
						break;
						
					case (Keyboard.KEY_ESCAPE):
						StateManager.setCurrent(State.PAUSED);
						break;
				}
			}
		}
	}
	
	private static void updateMouse()
	{
		mouseLastX = Mouse.getX();
		mouseLastY = Mouse.getY();
	}
	private static float getDX() { return Mouse.getX() - mouseLastX; }
	private static float getDY() { return -(Mouse.getY() - mouseLastY); }	
}
