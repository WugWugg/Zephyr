package com.bixfordstudios.camera;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * General class to handle view frustum 
 * @author DarkEther
 *
 */
public class Frustum {
	
	public float[][] frustum = new float[6][4];
	
	/**
	 * Runs the {@link #update()} function
	 */
	public Frustum()
	{
		update();
	}
	
	/**
	 * Redefines the bounds of the frustum using the current projection matrix and modelview matrix
	 */
	public void update()
	{
		float[] projection = new float[16];
		FloatBuffer projectionBuf = BufferUtils.createFloatBuffer(16);
		//Get the current PROJECTION MATRIX from OpenGL
		glGetFloat(GL_PROJECTION_MATRIX, projectionBuf);
		projectionBuf.get(projection);
		
		float[] model = new float[16];
		FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
		//Get the current MODELVIEW MATRIX from OpenGL
		glGetFloat(GL_MODELVIEW_MATRIX, modelBuf);
		modelBuf.get(model);
		
		float[] clipSpace = new float[16];
		
		float t;
		
		//Combine the projection and modelview to get the clip space
		clipSpace[ 0] = model[ 0] * projection[ 0] + model[ 1] * projection[ 4] + model[ 2] * projection[ 8] + model[ 3] * projection[12];
		clipSpace[ 1] = model[ 0] * projection[ 1] + model[ 1] * projection[ 5] + model[ 2] * projection[ 9] + model[ 3] * projection[13];
		clipSpace[ 2] = model[ 0] * projection[ 2] + model[ 1] * projection[ 6] + model[ 2] * projection[10] + model[ 3] * projection[14];
		clipSpace[ 3] = model[ 0] * projection[ 3] + model[ 1] * projection[ 7] + model[ 2] * projection[11] + model[ 3] * projection[15];

		clipSpace[ 4] = model[ 4] * projection[ 0] + model[ 5] * projection[ 4] + model[ 6] * projection[ 8] + model[ 7] * projection[12];
		clipSpace[ 5] = model[ 4] * projection[ 1] + model[ 5] * projection[ 5] + model[ 6] * projection[ 9] + model[ 7] * projection[13];
	    clipSpace[ 6] = model[ 4] * projection[ 2] + model[ 5] * projection[ 6] + model[ 6] * projection[10] + model[ 7] * projection[14];
	    clipSpace[ 7] = model[ 4] * projection[ 3] + model[ 5] * projection[ 7] + model[ 6] * projection[11] + model[ 7] * projection[15];

	    clipSpace[ 8] = model[ 8] * projection[ 0] + model[ 9] * projection[ 4] + model[10] * projection[ 8] + model[11] * projection[12];
	    clipSpace[ 9] = model[ 8] * projection[ 1] + model[ 9] * projection[ 5] + model[10] * projection[ 9] + model[11] * projection[13];
	    clipSpace[10] = model[ 8] * projection[ 2] + model[ 9] * projection[ 6] + model[10] * projection[10] + model[11] * projection[14];
	    clipSpace[11] = model[ 8] * projection[ 3] + model[ 9] * projection[ 7] + model[10] * projection[11] + model[11] * projection[15];

	    clipSpace[12] = model[12] * projection[ 0] + model[13] * projection[ 4] + model[14] * projection[ 8] + model[15] * projection[12];
	    clipSpace[13] = model[12] * projection[ 1] + model[13] * projection[ 5] + model[14] * projection[ 9] + model[15] * projection[13];
	    clipSpace[14] = model[12] * projection[ 2] + model[13] * projection[ 6] + model[14] * projection[10] + model[15] * projection[14];
	    clipSpace[15] = model[12] * projection[ 3] + model[13] * projection[ 7] + model[14] * projection[11] + model[15] * projection[15];
		
		
	    //Extract the numbers for the RIGHT plane
	    frustum[0][0] = clipSpace[ 3] - clipSpace[ 0];
	    frustum[0][1] = clipSpace[ 7] - clipSpace[ 4];
	    frustum[0][2] = clipSpace[11] - clipSpace[ 8];
	    frustum[0][3] = clipSpace[15] - clipSpace[12];
	    
	    //Normalize
	    t = (float) Math.sqrt( frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2] );
	    frustum[0][0] /= t;
	    frustum[0][1] /= t;
	    frustum[0][2] /= t;
	    frustum[0][3] /= t;
	    
	    /* Extract the numbers for the LEFT plane */
	    frustum[1][0] = clipSpace[ 3] + clipSpace[ 0];
	    frustum[1][1] = clipSpace[ 7] + clipSpace[ 4];
	    frustum[1][2] = clipSpace[11] + clipSpace[ 8];
	    frustum[1][3] = clipSpace[15] + clipSpace[12];

	    /* Normalize the result */
	    t = (float) Math.sqrt( frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2] );
	    frustum[1][0] /= t;
	    frustum[1][1] /= t;
	    frustum[1][2] /= t;
	    frustum[1][3] /= t;

	    /* Extract the BOTTOM plane */
	    frustum[2][0] = clipSpace[ 3] + clipSpace[ 1];
	    frustum[2][1] = clipSpace[ 7] + clipSpace[ 5];
	    frustum[2][2] = clipSpace[11] + clipSpace[ 9];
	    frustum[2][3] = clipSpace[15] + clipSpace[13];

	    /* Normalize the result */
	    t = (float) Math.sqrt( frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2] );
	    frustum[2][0] /= t;
	    frustum[2][1] /= t;
	    frustum[2][2] /= t;
	    frustum[2][3] /= t;

	    /* Extract the TOP plane */
	    frustum[3][0] = clipSpace[ 3] - clipSpace[ 1];
	    frustum[3][1] = clipSpace[ 7] - clipSpace[ 5];
	    frustum[3][2] = clipSpace[11] - clipSpace[ 9];
	    frustum[3][3] = clipSpace[15] - clipSpace[13];

	    /* Normalize the result */
	    t = (float) Math.sqrt( frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2] );
	    frustum[3][0] /= t;
	    frustum[3][1] /= t;
	    frustum[3][2] /= t;
	    frustum[3][3] /= t;

	    /* Extract the FAR plane */
	    frustum[4][0] = clipSpace[ 3] - clipSpace[ 2];
	    frustum[4][1] = clipSpace[ 7] - clipSpace[ 6];
	    frustum[4][2] = clipSpace[11] - clipSpace[10];
	    frustum[4][3] = clipSpace[15] - clipSpace[14];

	    /* Normalize the result */
	    t = (float) Math.sqrt( frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2] );
	    frustum[4][0] /= t;
	    frustum[4][1] /= t;
	    frustum[4][2] /= t;
	    frustum[4][3] /= t;

	    /* Extract the NEAR plane */
	    frustum[5][0] = clipSpace[ 3] + clipSpace[ 2];
	    frustum[5][1] = clipSpace[ 7] + clipSpace[ 6];
	    frustum[5][2] = clipSpace[11] + clipSpace[10];
	    frustum[5][3] = clipSpace[15] + clipSpace[14];

	    /* Normalize the result */
	    t = (float) Math.sqrt( frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2] );
	    frustum[5][0] /= t;
	    frustum[5][1] /= t;
	    frustum[5][2] /= t;
	    frustum[5][3] /= t;
	}
	
	/**
	 * The function simply loops through all six planes, calculating the distance of the point from each plane. If it's behind any one of them we can exit immediately.
	 * @param x A coordinate
	 * @param y A coordinate
	 * @param z A coordinate
	 * @return True if the point is in the frustum; false if not.
	 */
	public boolean pointInFrustum( float x, float y, float z )
	{
		for( int p = 0; p < 6; p++ )
	      if( frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= 0 )
	         return false;
	   return true;
	}
	
	/**
	 * Given the center coordinates of the cube and the size, which is actually half of the cube's length (think of it like a sphere's radius), test for each plane we test each corner of the cube.
	 * @param x The center coordinate
	 * @param y The center coordinate
	 * @param z The center coordinate
	 * @param size The cube's size -- half it's length
	 * @return True if the cube is in the frustum; false if not.
	 */
	public boolean cubeInFrustum( float x, float y, float z, float size )
	{
	   for(int p = 0; p < 6; p++ )
	   {
	      if( frustum[p][0] * (x - size) + frustum[p][1] * (y - size) + frustum[p][2] * (z - size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x + size) + frustum[p][1] * (y - size) + frustum[p][2] * (z - size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x - size) + frustum[p][1] * (y + size) + frustum[p][2] * (z - size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x + size) + frustum[p][1] * (y + size) + frustum[p][2] * (z - size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x - size) + frustum[p][1] * (y - size) + frustum[p][2] * (z + size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x + size) + frustum[p][1] * (y - size) + frustum[p][2] * (z + size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x - size) + frustum[p][1] * (y + size) + frustum[p][2] * (z + size) + frustum[p][3] > 0 ) continue;
	      if( frustum[p][0] * (x + size) + frustum[p][1] * (y + size) + frustum[p][2] * (z + size) + frustum[p][3] > 0 ) continue;
	      return false;
	   }
	   return true;
	}
}
