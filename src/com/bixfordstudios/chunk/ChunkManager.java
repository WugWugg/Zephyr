package com.bixfordstudios.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.bixfordstudios.camera.Camera;
import com.bixfordstudios.main.Main;
import com.bixfordstudios.utility.CoordinateFloat;
import com.bixfordstudios.utility.CoordinateInt;

/**
 * 
 * @author Nolan E. Rosen
 *
 */
public class ChunkManager {

	public static final int WORLD_SIZE = 32;
	public static final int WORLD_OFFSET = WORLD_SIZE / 2;
	public static final int ASYNC_NUM_CHUNKS_PER_FRAME = 2;
	
	public static transient int NUM_OF_LOADED_CHUNKS = 0;
	public static transient int NUM_OF_VISIBLE_CHUNKS = 0;
	
	public static Chunk[][][] world = new Chunk[WORLD_SIZE][WORLD_SIZE][WORLD_SIZE];	
	
	public static HashMap<CoordinateInt, Chunk> loadedChunks = new HashMap<CoordinateInt, Chunk>();
	
	public static ArrayList<CoordinateInt> unloadList = new ArrayList<CoordinateInt>();
	public static ArrayList<CoordinateInt> loadList = new ArrayList<CoordinateInt>();
	public static ArrayList<CoordinateInt> setupList = new ArrayList<CoordinateInt>();
	//public static ArrayList<CoordinateInt> flagsList = new ArrayList<CoordinateInt>();
	public static ArrayList<CoordinateInt> visibleList = new ArrayList<CoordinateInt>();
	
	private ChunkManager()
	{
		throw new AssertionError();
	}
	
	/**
	 * Method finds the chunk that the given coordinates is in.
	 * @param coord A {@link CoordinateFloat} given in an absolute coordinates
	 * @return A {@link CoordinateInt} given back in the chunk coordinates
	 */
	private static CoordinateInt containingChunk(CoordinateFloat coord)
	{
		CoordinateInt ret = new CoordinateInt();
		ret.x = (int) Math.floor(coord.x / (Chunk.ABSOLUTE_CHUNK_SIZE));
		ret.y = (int) Math.floor(coord.y / (Chunk.ABSOLUTE_CHUNK_SIZE));
		ret.z = (int) Math.floor(coord.z / (Chunk.ABSOLUTE_CHUNK_SIZE));
		return ret;
	}
	
	/**
	 * Retrieves the chunk from the world data stucture (i.e. array).
	 * @param coord A {@link CoorinateInt} of the chunk given in the chunk coordinate space
	 * @return Returns that {@link Chunk} object
	 */
	private static Chunk getFromWorld(CoordinateInt coord)
	{
		int x = coord.x + WORLD_OFFSET - 1;
		int y = coord.y + WORLD_OFFSET - 1;
		int z = coord.z + WORLD_OFFSET - 1;
		
		if (world[x][y][z] == null) return new Chunk();
		else return world[x][y][z];
	}
	
	/**
	 * Sets a chunk to the world data structure (i.e. array).
	 * @param coord A {@link CoordinateInt} of the chunk given in the chunk coordinate space
	 * @param chunk A {@link Chunk} object to be stored
	 */
	private static void setToWorld(CoordinateInt coord, Chunk chunk)
	{
		int x = coord.x + WORLD_OFFSET - 1;
		int y = coord.y + WORLD_OFFSET - 1;
		int z = coord.z + WORLD_OFFSET - 1;
		world[x][y][z] = chunk;
	}
	
	/**
	 * Forces a given chunk to be loaded in, if not already
	 * @param coord A {@link CoordinateInt} of the chunk given in the chunk coordinate space
	 */
	private static void assertLoad(CoordinateInt coord)
	{
		if (!loadedChunks.containsKey(coord))
		{
			//Not in the list
			loadList.add(coord);
		}
	}
	
	/**
	 * Checks to see if the chunk has all of its neighboring chunks -- those that touch faces, orthogonally.
	 * @param coord A {@link CoordinateInt} of the center chunk given in the chunk coordinate space
	 * @return A boolean: True, all neighbors are already loaded; False, on
	 */
	private static boolean hasAllNeighborsLoaded(CoordinateInt coord)
	{
		for (CoordinateInt index : coord.cardinalNeighbors())
		{
			if (!loadedChunks.containsKey(index)) return false;
		}
		return true;
	}
	/**
	 * Checks to see if the chunk in the chunk coordinate space is less than the distance defined by {@link Camera#VIEW_RADIUS}.
	 * @param coord1 A {@link CoordinateInt} of one chunk given in the chunk coordinate space
	 * @param coord2 A {@link CoordinateInt} of one chunk given in the chunk coordinate space
	 * @return A boolean: True, the given chunks' distance from each other is less than {@link Camera#VIEW_RADIUS}; False, they are not
	 */
	private static boolean isInRange(CoordinateInt coord1, CoordinateInt coord2)
	{
		return CoordinateInt.distance(coord1, coord2) <= Camera.VIEW_RADIUS;
	}
	
	/**
	 * Checks to see if the chunk is in the players viewing frustum
	 * @param coord A {@link CoordinateInt} of one chunk given in the chunk coordinate space
	 * @return A boolean: True, the chunk is in the viewing frustum; False, it is not
	 */
	private static boolean isVisible(CoordinateInt coord)
	{
		float chunkRadius = Chunk.ABSOLUTE_CHUNK_SIZE / 2;
		//Translates from chunk coordinates to world coordinates -- the same as the matrixes are in
		CoordinateFloat chunkCoords = new CoordinateFloat(coord.x, coord.y , coord.z).scale(Chunk.ABSOLUTE_CHUNK_SIZE);
		if (Main.firstPlayer.frustum.cubeInFrustum(chunkCoords.x + chunkRadius, chunkCoords.y + chunkRadius, chunkCoords.z + chunkRadius, chunkRadius)) return true;
		return false;
	}
	
	/**
	 * Loads in all chunks that are neighbors of the given chunk by ensuring that the neighbor chunks aren't out of the viewing range. Also
	 * adds visible chunks to the rendering list.
	 * @param centerScene A {@link CoordinateInt} of the chunk in the chunk coordinate space
	 */
	private static void pollChunks(CoordinateInt centerScene)
	{
		//Run through all loadedChunks sorting into proper lists
		Iterator<Entry<CoordinateInt, Chunk>> chunkIterator = loadedChunks.entrySet().iterator();
		while (chunkIterator.hasNext())
		{
			Entry<CoordinateInt, Chunk> curEntry = chunkIterator.next();
			Chunk curChunk = curEntry.getValue();
			CoordinateInt curCoord = curEntry.getKey();
			
			//Get all chunks that need to be un-loaded
			if (!isInRange(curCoord, centerScene)) unloadList.add(curCoord);
			else
			{	
				//Get all chunks that need to be loaded by checking if the chunk's neighbors
				if (!hasAllNeighborsLoaded(curCoord))
				{
					for (CoordinateInt coord : curCoord.cardinalNeighbors())
					{
						if (isInRange(coord, centerScene) && !loadedChunks.containsKey(coord)) loadList.add(coord);
					}
				}
			
				//Get all chunks that need to be setup (thus, rebuilt)
				if (!curChunk.isSetup) setupList.add(curCoord);
				//Get all chunks that need flags reset (Currently not implemented)
				//Get all chunks that are visible
				else if (isVisible(curCoord)) visibleList.add(curCoord);
			}
		}
	}
	
	/**
	 * Method to iterate through the unloading list and remove them from the loaded chunk list. Limits the number of chunks managed to
	 * the number defined in {@link #ASYNC_NUM_CHUNKS_PER_FRAME}.
	 */
	private static void unload()
	{
		int numChunkUnloaded = 0;
		Iterator<CoordinateInt> itr = unloadList.iterator();
		while (numChunkUnloaded < ASYNC_NUM_CHUNKS_PER_FRAME && itr.hasNext())
		{
			NUM_OF_LOADED_CHUNKS--;
			CoordinateInt key = itr.next();
			setToWorld(key, loadedChunks.get(key));
			loadedChunks.remove(key);
			
			numChunkUnloaded++;
		}
	}
	/**
	 * Method to iterate through the loading list and add them to the loaded chunk list. Limits the number of chunks managed to
	 * the number defined in {@link #ASYNC_NUM_CHUNKS_PER_FRAME}.
	 */
	private static void load()
	{
		int numChunksLoaded = 0;
		Iterator<CoordinateInt> itr = loadList.iterator();
		while (numChunksLoaded < ASYNC_NUM_CHUNKS_PER_FRAME && itr.hasNext())
		{
			NUM_OF_LOADED_CHUNKS++;
			CoordinateInt key = itr.next();
			
			//Check to make sure the chunk isn't already loaded
			if (!(loadedChunks.containsKey(key))) loadedChunks.put(key, getFromWorld(key));
			
			numChunksLoaded++;
		}
	}
	
	/**
	 * Method to iterate through the setup list and ensure that the given chunks are setup. Limits the number of chunks managed to
	 * the number defined in {@link #ASYNC_NUM_CHUNKS_PER_FRAME}.
	 */
	private static void setup()
	{
		int numChunksSetup = 0;
		Iterator<CoordinateInt> itr = setupList.iterator();
		while (numChunksSetup < ASYNC_NUM_CHUNKS_PER_FRAME && itr.hasNext())
		{
			CoordinateInt key = itr.next();
			
			//Check to make sure the chunk isn't already setup
			if (!loadedChunks.get(key).isSetup) loadedChunks.get(key).setupData();
			
			numChunksSetup++;
		}
	}
	
	/**
	 * Method to iterate through the visible list and render all chunks within.
	 */
	public static void renderVisible()
	{
		NUM_OF_VISIBLE_CHUNKS = 0;
		Iterator<CoordinateInt> itr = visibleList.iterator();
		while (itr.hasNext())
		{
			NUM_OF_VISIBLE_CHUNKS++;
			CoordinateInt key = itr.next();
			loadedChunks.get(key).render(key);
		}
	}
	
	/**
	 * Clears all list.<p>
	 * List Cleared:
	 * <ul><li>Unload List</li>
	 * <li>Load List</li>
	 * <li>Setup List</li>
	 * <li>Visible List</li></ul>
	 */
	public static void clearAllList()
	{
		unloadList.clear();
		loadList.clear();
		setupList.clear();
		//flagsList.clear();
		visibleList.clear();
	}
	
	/**
	 * The method called to update the chunk manager's state.
	 * @param centerSceneFloat A {@link CoordinateFloat} for the camera, center scene, or player location in the world coordinate space.
	 */
	public static void update(CoordinateFloat centerSceneFloat)
	{
		
		CoordinateInt centerSceneInt = containingChunk(centerSceneFloat);
		
		//The chunk that the player is in must at least be loaded
		assertLoad(centerSceneInt);
		
		//Populate lists with candid chunks
		pollChunks(centerSceneInt);
		
		unload();
		
		load();
		
		//flag();
		
		setup();
		
		renderVisible();
		
		//Clear all list
		clearAllList();
	}
	
	
	
	
	
}
