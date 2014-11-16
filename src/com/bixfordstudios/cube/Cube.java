package com.bixfordstudios.cube;

import java.util.Arrays;

public class Cube {

	public static final float RENDER_SIZE = 1f;
	public static final int NUMBER_OF_VERTICES = 36;
	public static final int NUMBER_OF_FACES = 6;
	public static final int NUMBER_OF_VERTICES_PER_FACE = NUMBER_OF_VERTICES / NUMBER_OF_FACES;
	public static final int NUMBER_OF_VERTEX_COORDS = 3;
	public static final int NUMBER_OF_TEXTURE_COORDS = 2;
	
	public static final float[][] VBO_CUBE = 
		{
			// Front Face
			{
				// First Triangle
				0, -RENDER_SIZE, RENDER_SIZE, 0, 0,
				RENDER_SIZE, -RENDER_SIZE, RENDER_SIZE, 1, 0,
				RENDER_SIZE, 0, RENDER_SIZE, 1, 1,
				
				// Second Triangle
				RENDER_SIZE, 0, RENDER_SIZE, 1, 1,
				0, 0, RENDER_SIZE, 0, 1,
				0, -RENDER_SIZE, RENDER_SIZE, 0, 0
			},
			// Back Face
			{
				// First Triangle
				RENDER_SIZE, -RENDER_SIZE, 0, 0, 0,
				0, -RENDER_SIZE, 0, 1, 0,
				0, 0, 0, 1, 1,
								
				// Second Triangle
				0, 0, 0, 1, 1,
				RENDER_SIZE, 0, 0, 0, 1,
				RENDER_SIZE, -RENDER_SIZE, 0, 0, 0
			},
			// Top Face
			{
				// First Triangle
				0, 0, RENDER_SIZE, 0, 0,
				RENDER_SIZE, 0, RENDER_SIZE, 1, 0,
				RENDER_SIZE, 0, 0, 1, 1,
				
				// Second Triangle
				RENDER_SIZE, 0, 0, 1, 1,
				0, 0, 0, 0, 1,
				0, 0, RENDER_SIZE, 0, 0
			},
			// Bottom Face
			{
				// First Triangle
				0, -RENDER_SIZE, 0, 0, 0,
				RENDER_SIZE, -RENDER_SIZE, 0, 1, 0,
				RENDER_SIZE, -RENDER_SIZE, RENDER_SIZE, 1, 1,
				
				// Second Triangle
				RENDER_SIZE, -RENDER_SIZE, RENDER_SIZE, 1, 1,
				0, -RENDER_SIZE, RENDER_SIZE, 0, 1,
				0, -RENDER_SIZE, 0, 0, 0
			},
			// Left Face
			{
				// First Triangle
				0, -RENDER_SIZE, 0, 0, 0,
				0, -RENDER_SIZE, RENDER_SIZE, 1, 0,
				0, 0, RENDER_SIZE, 1, 1,
				
				// Second Triangle
				0, 0, RENDER_SIZE, 1, 1,
				0, 0, 0, 0, 1,
				0, -RENDER_SIZE, 0, 0, 0
			},
			// Right Face
			{
				// First Triangle
				RENDER_SIZE, -RENDER_SIZE, RENDER_SIZE, 0, 0,
				RENDER_SIZE, -RENDER_SIZE, 0, 1, 0,
				RENDER_SIZE, 0, 0, 1, 1,
				
				// Second Triangle
				RENDER_SIZE, 0, 0, 1, 1,
				RENDER_SIZE, 0, RENDER_SIZE, 0, 1,
				RENDER_SIZE, -RENDER_SIZE, RENDER_SIZE, 0, 0
			}
		};

	public static float[][] createCubeVerticesByFace(float x, float y, float z)
	{
		float[][] ret = new float[NUMBER_OF_FACES][(NUMBER_OF_TEXTURE_COORDS + NUMBER_OF_VERTEX_COORDS) * NUMBER_OF_VERTICES_PER_FACE];
		
		for(int i = 0; i < ret.length; i++)
		{
			for(int j = 0; j < ret[i].length; j += 5)
			{
				ret[i][j] = VBO_CUBE[i][j] + x;
				ret[i][j + 1] = VBO_CUBE[i][j + 1] + y;
				ret[i][j + 2] = VBO_CUBE[i][j + 2] + z;
				
				ret[i][j + 3] = VBO_CUBE[i][j + 3];
				ret[i][j + 4] = VBO_CUBE[i][j + 4];
			}
		}
		return ret;
	}
	
	public static float[] vboToRender(int x, int y, int z, byte sideFlags) {
		float[][] verticesByFace = createCubeVerticesByFace(x, y, z);
		float[] ret = new float[verticesByFace.length * verticesByFace[0].length];
		int retIndex = 0;
		for(int i = 0; i < verticesByFace.length; i++) {
			for(int j = 0; j < verticesByFace[i].length; j++)
			{
				if((sideFlags & (1 << i)) != (1 << i))
				{
					//System.out.println(Integer.toBinaryString(sideFlags) + " & " + Integer.toBinaryString(1 << i) + " = " + (sideFlags & (1 << i)) + " ?= " + (1 << i));
					ret[retIndex] = verticesByFace[i][j];
					retIndex++;
				}
			}
		}
		return Arrays.copyOfRange(ret, 0, retIndex);
	}
}
