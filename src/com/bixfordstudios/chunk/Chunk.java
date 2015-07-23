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
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.bixfordstudios.cube.Cube;
import com.bixfordstudios.cube.CubeType;
import com.bixfordstudios.utility.CoordinateInt;
/**
 * 
 * @author Nolan Rosen
 *
 */
public class Chunk {

	public static final int NUM_CUBES_PER_SIDE = 16;
	public static final int NUM_CUBES_PER_CHUNK = NUM_CUBES_PER_SIDE * NUM_CUBES_PER_SIDE * NUM_CUBES_PER_SIDE;
	
	public static final float ABSOLUTE_CHUNK_SIZE = NUM_CUBES_PER_SIDE * Cube.RENDER_SIZE;
	
	public static final int FLOAT_BUFFER_CAPACITY = NUM_CUBES_PER_CHUNK * Cube.NUMBER_OF_VERTICES * (Cube.NUMBER_OF_VERTEX_COORDS + Cube.NUMBER_OF_TEXTURE_COORDS) * Float.BYTES;
	
	private static final int[][][] INIT_MANAGED_CUBES = new int[NUM_CUBES_PER_SIDE][NUM_CUBES_PER_SIDE][NUM_CUBES_PER_SIDE];
	static {
		for(int x = 0; x < NUM_CUBES_PER_SIDE; x++)
		{
			for(int y = 0; y < NUM_CUBES_PER_SIDE; y++)
			{
				for(int z = 0; z < NUM_CUBES_PER_SIDE; z++)
				{
					INIT_MANAGED_CUBES[x][y][z] = 0b10000000;
				}
			}
		}
	}
	
	/**
	 * Indicates if the current data in this chunk has be loaded into the chunkVboData. Changes to false when chunk is updated. 
	 */
	public boolean isSetup = false;
	
	//Texture atlas-ing will remove need for textureIndices
	private int[] textureIndices = new int[CubeType.values().length - 1];
	
	private FloatBuffer chunkVboData = BufferUtils.createFloatBuffer(Chunk.FLOAT_BUFFER_CAPACITY);
	private int VBOID = 0;
	
	/**
	 * Each cube is stored as a byte (8 bits). Giving 127 (0 not included) possible texture IDs.<br>
	 * First Bit - Active
	 * Remaining Bits - Texture ID
	 */
	private int[][][] managedCubes = INIT_MANAGED_CUBES;
	
	
	/**
	 * Constructor
	 */
	public Chunk()
	{
		
	}

	/**
	 * Changes the cube at location x, y, and z to the given cube-type.
	 * @param x X-Coordinate of the cube
	 * @param y Y-Coordinate of the cube
	 * @param z Z-Coordinate of the cube
	 * @param type Cube-type to change the cube to
	 */
	public void update(int x, int y, int z, CubeType type)
	{
		managedCubes[x][y][z] = isActive(x, y, z) ? (byte) type.ordinal() | 0b10000000 : (byte) type.ordinal();
		isSetup = false;
	}
	
	/**
	 * Changes the cube at location x, y, and z to the given state.
	 * @param x X-Coordinate of the cube
	 * @param y Y-Coordinate of the cube
	 * @param z Z-Coordinate of the cube
	 * @param type State to change the cube to: true, active; false, inactive. 
	 */
	public void update(int x, int y, int z, boolean status)
	{
		managedCubes[x][y][z] = status ? managedCubes[x][y][z] | (1 << 7) : managedCubes[x][y][z] & ~(1 << 7);
		isSetup = false;
	}
	
	/**
	 * Draws the chunk from data stored in the chunk's reserved buffer
	 * <p>
	 * @param x X-coordinate that corresponds to the world space location of the chunk's lower-back-left vertex
	 * @param y Y-coordinate that corresponds to the world space location of the chunk's lower-back-left vertex
	 * @param z Z-coordinate that corresponds to the world space location of the chunk's lower-back-left vertex
	 */
	public void render (CoordinateInt coord)
	{		
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
			glTranslatef(coord.x * Chunk.ABSOLUTE_CHUNK_SIZE, coord.y * Chunk.ABSOLUTE_CHUNK_SIZE, coord.z * Chunk.ABSOLUTE_CHUNK_SIZE);
			 
			glBindBuffer(GL_ARRAY_BUFFER, VBOID);
			
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
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
		glMatrixMode(GL_MODELVIEW_MATRIX);
		glPopMatrix();
	}
		
	/**
	 * Method clears the saved data points in the chunk's reserved VBO-Buffer. Then, populates an update version of the chunk's data.
	 * <p>
	 * Method clears the saved data in the reserved VBO-Buffer. Then generate an array of FloatBuffers to hold all possible data for the separate
	 * cube types. Iterating through the cubes it places the corresponding texture types in to an FloatBuffer in the array; furthermore, the index of
	 * this FloatBuffer is determined through base-10 integer represented in the cube's byte descripter's last 7 digits. Next, it indexes the chunk's
	 * array of textureIndices by the number of cubes placed into that FloatBuffer -- given by the position (length) of that FloatBuffer divided by the
	 * number of vertices a single cube adds to the data. Finally, it copies the data in the FloatBuffers to the chunk's chunkVboData. These last two steps
	 * allow for the rendering of the data that allows for the rendering engine to know when to switch textures while maintaining a single data structure
	 *, a FloatBuffer, after "x" number of cubes where "x" is a value in textureIndices.
	 *<p>
	 * The last step in this method is to flip() the chunk's chunkVboData and assign it to a Buffer. The chunk's new buffer handle is generated by glGenBuffers()
	 * and that buffer is loaded with the chunk's chunkVboData. The last or terminating index in the textureIndices array is added as the number of cube's in the
	 * chunk's chunkVboData FloatBuffer. Then, the chunk's boolean flag isSetup is changed to true.
	 */
	public Chunk setupData()
	{
		chunkVboData.clear();
		glDeleteBuffers(VBOID);
		
		FloatBuffer[] sortedCubes = new FloatBuffer[CubeType.values().length - 1];
		for (int i = 0; i < sortedCubes.length-1; i++)
		{
			sortedCubes[i] = BufferUtils.createFloatBuffer(FLOAT_BUFFER_CAPACITY);
		}
		
		
		for(int x = 0; x < NUM_CUBES_PER_SIDE; x++)
		{
			for(int y = 0; y < NUM_CUBES_PER_SIDE; y++)
			{
				for(int z = 0; z < NUM_CUBES_PER_SIDE; z++)
				{
					sortedCubes[getCubeTypeOrdinal(x, y, z)].put(getCubeData(x, y, z));
				}
			}
		}

		for (int i = 0; i < CubeType.values().length - 1; i++)
		{
			textureIndices[i] = chunkVboData.position() / (Cube.NUMBER_OF_TEXTURE_COORDS + Cube.NUMBER_OF_VERTEX_COORDS);
			try {
				chunkVboData.put((FloatBuffer) sortedCubes[i].flip());
			} catch (NullPointerException e) {} //No values in one of the textures
		}
		
		chunkVboData.flip();
		VBOID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, VBOID);
		glBufferData(GL_ARRAY_BUFFER, chunkVboData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		textureIndices[textureIndices.length - 1] = chunkVboData.position() / (Cube.NUMBER_OF_TEXTURE_COORDS + Cube.NUMBER_OF_VERTEX_COORDS);
		this.isSetup = true;
		return this;
	}

	public void deconstruct()
	{
		glDeleteBuffers(VBOID);
	}
	
	//Cube Specific Below
	
	/**
	 * Method to get the vertex data of a cube excluding the faces that are not visible.
	 * @param x X-Coordinate of the cube in question
	 * @param y Y-Coordinate of the cube in question
	 * @param z Z-Coordinate of the cube in question
	 * @return float[] of all viewable vertex points of the cube
	 */
	private float[] getCubeData(int x, int y, int z)
	{
		
		float[] ret = new float[] {   };
		if (isActive(x, y, z))
		{
			/**
			 * Contains a string of bits that flag whether that side has another block on it
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
				if (isActive(x - 1, y, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b10000);
			}
			if (x < NUM_CUBES_PER_SIDE - 1)	
			{
				if (isActive(x + 1, y, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b100000);
			}
			if (y > 0) 
			{
				if (isActive(x, y - 1, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b1000);
				} 
			if (y < NUM_CUBES_PER_SIDE - 1)
			{
				if (isActive(x, y + 1, z) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b100);
				} 
			if (z > 0) 
			{
				if (isActive(x, y, z - 1) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b10);
			}
			if (z < NUM_CUBES_PER_SIDE - 1)	
			{
				if (isActive(x, y , z + 1) && !getCubeType(x, y, z).transparent)
					hasNeighbor = (byte)(hasNeighbor | 0b1);
			}
			
			ret = Cube.vboToRender(x, y, z, hasNeighbor);
		}
		
		return ret;
	}
	
	/**
	 * Reads the first bit of the cube's byte descriptor to determine state of cube in question.
	 * @param x X-Coordinate of the cube in question
	 * @param y Y-Coordinate of the cube in question
	 * @param z Z-Coordinate of the cube in question
	 * @return A boolean that declares the state of the cube: active or non-active.
	 */
	private boolean isActive(int x, int y, int z)
	{
		return (((byte) managedCubes[x][y][z] >> 7) & 1) == 1;
	}
	
	/**
	 * Gets the cube's texture handle -- the index of the cube's texture in the {@link CubeType}
	 * @param x X-Coordinate of the cube in question
	 * @param y Y-Coordinate of the cube in question
	 * @param z Z-Coordinate of the cube in question
	 * @return The texture index in the {@link CubeType}
	 */
	private byte getCubeTypeOrdinal(int x, int y, int z)
	{
		return (byte) (managedCubes[x][y][z] & ~(1 << 7));
	}
	
	/**
	 * Gets the cube's texture name.
	 * @param x X-Coordinate of the cube in question
	 * @param y Y-Coordinate of the cube in question
	 * @param z Z-Coordinate of the cube in question
	 * @return The texture name in the enum {@link CubeType}
	 */
	private CubeType getCubeType(int x, int y, int z)
	{
		//Arguably useless in lieu of getCubeTypeOrdinal...
		return CubeType.values()[getCubeTypeOrdinal(x, y , z)];
	}
}
