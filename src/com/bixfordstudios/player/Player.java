package com.bixfordstudios.player;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class Player {

	public Matrix4f viewingMatrix;
	
	public Player()
	{
		this.viewingMatrix = new Matrix4f();
	}
	
	public void setViewingMatrix(Matrix4f matrix)
	{
		this.viewingMatrix = matrix;
		
		FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
		viewingMatrix.store(matrixData);
		matrixData.flip();
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrix(matrixData);		
	}
	
	public Matrix4f getViewingMatrix()
	{
		return viewingMatrix;
	}
}
