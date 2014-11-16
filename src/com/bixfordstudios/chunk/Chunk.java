package com.bixfordstudios.chunk;

import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBBufferObject.glBufferDataARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import com.bixfordstudios.cube.Cube;
import com.bixfordstudios.cube.CubeType;

public class Chunk {

	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_CAPACITY = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
	public static final int FLOAT_BUFFER_CAPACITY = CHUNK_CAPACITY * Cube.NUMBER_OF_VERTICES * (Cube.NUMBER_OF_VERTEX_COORDS + Cube.NUMBER_OF_TEXTURE_COORDS) * Float.BYTES;
	
	//Texture atlas-ing will remove need for textureIndices
	private static int[] textureIndices = new int[CubeType.values().length - 1];
	private static FloatBuffer chunkVboData = BufferUtils.createFloatBuffer(Chunk.FLOAT_BUFFER_CAPACITY);
	private static int[][][] managedCubes = null;
	int VBOID = 0;
	
	/**
	 * Each cube is stored as a byte. Giving 127 (0 not included) possible texture IDs.<br>
	 * First Bit - Active
	 * Remaining Bits - Texture ID
	 */
	
	public Chunk()
	{
		//Random terrain generator
		managedCubes = new int[CHUNK_SIZE][][];
		for(int x = 0; x < CHUNK_SIZE; x++)
		{
			managedCubes[x] = new int[CHUNK_SIZE][];
			for(int y = 0; y < CHUNK_SIZE; y++)
			{
				managedCubes[x][y] = new int[CHUNK_SIZE];
				for(int z = 0; z < CHUNK_SIZE; z++)
				{
					managedCubes[x][y][z] = 0b10000000;
				}
			}
		}
	}
	
	public void update(int x, int y, int z, CubeType type)
	{
		managedCubes[x][y][z] = getState(x, y, z) ? (byte) type.ordinal() | 0b10000000 : (byte) type.ordinal();
	}
	
	public void update(int x, int y, int z, boolean status)
	{
		managedCubes[x][y][z] = status ? managedCubes[x][y][z] | (1 << 7) : managedCubes[x][y][z] & ~(1 << 7); 
	}
	
	private void test(FloatBuffer a)
	{
		System.out.println("");
		for (int i = 0; i < 25; i++)
		{
			System.out.print(a.get(i) + " ");
		}
		System.out.println("\n- - - - -");
	}
	
	public void render(int x, int y, int z)
	{		
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
			glTranslatef(x, y, z);
			
			//test(chunkVboData);
			glBindBufferARB(GL_ARRAY_BUFFER_ARB, VBOID);
			
			glBufferDataARB(GL_ARRAY_BUFFER_ARB, chunkVboData, GL_STATIC_DRAW_ARB);
			
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			
			glVertexPointer(Cube.NUMBER_OF_VERTEX_COORDS, GL_FLOAT, (Cube.NUMBER_OF_VERTEX_COORDS + Cube.NUMBER_OF_TEXTURE_COORDS) * Float.BYTES, 0);
			glTexCoordPointer(Cube.NUMBER_OF_TEXTURE_COORDS, GL_FLOAT, (Cube.NUMBER_OF_VERTEX_COORDS + Cube.NUMBER_OF_TEXTURE_COORDS) * Float.BYTES, Cube.NUMBER_OF_VERTEX_COORDS * Float.BYTES);
			
			for (int i = 0; i < textureIndices.length - 1; i++)
			{
				if (textureIndices[i + 1] > textureIndices[i])
				{
					glBindTexture(GL_TEXTURE_2D, CubeType.values()[i].textureID);
					glDrawArrays(GL_TRIANGLES, textureIndices[i], (textureIndices[i + 1] - textureIndices[i]));
				}
			}
			
			glDisableClientState(GL_VERTEX_ARRAY);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			
		glMatrixMode(GL_MODELVIEW_MATRIX);
		glPopMatrix();
	}
			
	public void setupData()
	{
		chunkVboData.clear();
		glDeleteBuffers(VBOID);
		
		FloatBuffer[] sortedCubes = new FloatBuffer[CubeType.values().length - 1];
		for (int i = 0; i < sortedCubes.length-1; i++)
		{
			sortedCubes[i] = BufferUtils.createFloatBuffer(FLOAT_BUFFER_CAPACITY);
		}
		
		
		for(int x = 0; x < CHUNK_SIZE; x++)
		{
			for(int y = 0; y < CHUNK_SIZE; y++)
			{
				for(int z = 0; z < CHUNK_SIZE; z++)
				{
					sortedCubes[getCubeTypeOrdinal(x, y, z)].put(checkCubeConditions(x, y, z));
				}
			}
		}

		for (int i = 0; i < CubeType.values().length - 1; i++)
		{
			textureIndices[i] = chunkVboData.position() / (Cube.NUMBER_OF_TEXTURE_COORDS + Cube.NUMBER_OF_VERTEX_COORDS);
			try {	
				for (int j = 0; j < sortedCubes[i].position(); j++)
				{
					chunkVboData.put(sortedCubes[i].get(j));
					System.out.print(j +" ");
				}
			} catch (NullPointerException e) {}
		}
		
		chunkVboData.flip();
		VBOID = glGenBuffers();
		textureIndices[textureIndices.length - 1] = chunkVboData.position() / (Cube.NUMBER_OF_TEXTURE_COORDS + Cube.NUMBER_OF_VERTEX_COORDS);
	}
	
	private float[] checkCubeConditions(int x, int y, int z)
	{
		float[] ret = new float[] {   };
		if (getState(x, y, z))
		{
			/** 
			 * Contains a string of bits that flag whether that side has another block on it<br>
			 * 2^0 - Front Side
			 * 2^1 - Back Side
			 * 2^2 - Top Side
			 * 2^3 - Bottom Side
			 * 2^4 - Left Side
			 * 2^5 - Right Side
			 */
			byte hasNeighbor = 0;
			
			if (x > 0) 
			{
				if (getState(x - 1, y, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b10000);
			}
			if (x < CHUNK_SIZE - 1)	
			{
				if (getState(x + 1, y, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b100000);
			}
			if (y > 0) 
			{
				if (getState(x, y - 1, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b1000);
				} 
			if (y < CHUNK_SIZE - 1)
			{
				if (getState(x, y + 1, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b100);
				} 
			if (z > 0) 
			{
				if (getState(x, y, z - 1) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b10);
			}
			if (z < CHUNK_SIZE - 1)	
			{
				if (getState(x, y , z + 1) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b1);
			}
			
			ret = Cube.vboToRender(x, y, z, hasNeighbor);
		}
		
		return ret;
	}
	
	//Cube Specific Below
	
	private boolean getState(int x, int y, int z)
	{
		return (((byte) managedCubes[x][y][z] >> 7) & 1) == 1;
	}
	
	private byte getCubeTypeOrdinal(int x, int y, int z)
	{
		return (byte) (managedCubes[x][y][z] & ~(1 << 7));
	}
	
	private CubeType getCubeType(int x, int y, int z)
	{
		return CubeType.values()[getCubeTypeOrdinal(x, y , z)];
	}
}
