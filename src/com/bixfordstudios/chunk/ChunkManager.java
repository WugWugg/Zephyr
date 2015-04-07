package com.bixfordstudios.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.bixfordstudios.camera.Camera;
import com.bixfordstudios.main.Main;
import com.bixfordstudios.utility.CoordinateFloat;
import com.bixfordstudios.utility.CoordinateInt;


public class ChunkManager {

	public static final int WORLD_SIZE = 32;
	public static final int WORLD_OFFSET = WORLD_SIZE / 2;
	public static final int ASYNC_NUM_CHUNKS_PER_FRAME = 2;
	
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
	
	private static CoordinateInt containingChunk(CoordinateFloat coord)
	{
		CoordinateInt ret = new CoordinateInt();
		ret.x = (int) Math.floor(coord.x / (Chunk.ABSOLUTE_CHUNK_SIZE));
		ret.y = (int) Math.floor(coord.y / (Chunk.ABSOLUTE_CHUNK_SIZE));
		ret.z = (int) Math.floor(coord.z / (Chunk.ABSOLUTE_CHUNK_SIZE));
		return ret;
	}
	
	private static Chunk getFromWorld(CoordinateInt coord)
	{
		int x = coord.x + WORLD_OFFSET - 1;
		int y = coord.y + WORLD_OFFSET - 1;
		int z = coord.z + WORLD_OFFSET - 1;
		
		if (world[x][y][z] == null) return new Chunk();
		else return world[x][y][z];
	}
	
	private static void setToWorld(CoordinateInt coord, Chunk chunk)
	{
		int x = coord.x + WORLD_OFFSET - 1;
		int y = coord.y + WORLD_OFFSET - 1;
		int z = coord.z + WORLD_OFFSET - 1;
		world[x][y][z] = chunk;
	}
	
	private static void assertLoad(CoordinateInt coord)
	{
		if (!loadedChunks.containsKey(coord))
		{
			//Not in the list
			loadList.add(coord);
		}
	}
	
	private static boolean hasAllNeighborsLoaded(CoordinateInt coord)
	{
		boolean ret = true;
		for (CoordinateInt index : coord.cardinalNeighbors())
		{
			if (!loadedChunks.containsKey(index)) ret = false;
		}
		return ret;
	}
	
	private static boolean isInRange(CoordinateInt coord1, CoordinateInt coord2)
	{
		return CoordinateInt.distance(coord1, coord2) <= Camera.VIEW_RADIUS;
	}
	
	private static boolean isVisible(CoordinateInt coord)
	{
		float chunkRadius = Chunk.ABSOLUTE_CHUNK_SIZE / 2;
		//Translates from chunk coordinates to world coordinates -- the same as the matrixes are in
		//CURRENT
		//Not translating correctly need's to be scaled or relative?
		CoordinateFloat chunkCoords = new CoordinateFloat(coord.x + chunkRadius, coord.y + chunkRadius, coord.z + chunkRadius).scale(Chunk.ABSOLUTE_CHUNK_SIZE);
		if (Main.firstPlayer.frustum.cubeInFrustum(chunkCoords.x, chunkCoords.y, chunkCoords.z, chunkRadius)) return true;
		return false;
	}
	
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
	
	private static void unload()
	{
		int numChunkUnloaded = 0;
		Iterator<CoordinateInt> itr = unloadList.iterator();
		while (numChunkUnloaded < ASYNC_NUM_CHUNKS_PER_FRAME && itr.hasNext())
		{
			CoordinateInt key = itr.next();
			setToWorld(key, loadedChunks.get(key));
			loadedChunks.remove(key);
			
			numChunkUnloaded++;
		}
	}
	
	private static void load()
	{
		int numChunksLoaded = 0;
		Iterator<CoordinateInt> itr = loadList.iterator();
		while (numChunksLoaded < ASYNC_NUM_CHUNKS_PER_FRAME && itr.hasNext())
		{
			CoordinateInt key = itr.next();
			
			//Check to make sure the chunk isn't already loaded
			if (!(loadedChunks.containsKey(key))) loadedChunks.put(key, getFromWorld(key));
			
			numChunksLoaded++;
		}
	}
	
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
	
	public static void renderVisible()
	{
		Iterator<CoordinateInt> itr = visibleList.iterator();
		while (itr.hasNext())
		{
			CoordinateInt key = itr.next();
			loadedChunks.get(key).render(key);
		}
	}
	
	public static void clearAllList()
	{
		unloadList.clear();
		loadList.clear();
		setupList.clear();
		//flagsList.clear();
		visibleList.clear();
	}
	
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
