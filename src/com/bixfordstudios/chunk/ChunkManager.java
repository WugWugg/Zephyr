package com.bixfordstudios.chunk;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Matrix4f;

public class ChunkManager {

	//Hopefully can remove this limitation and make the world be endless
	public static final int WORLD_SIZE = 32;
	public static final int ASYNC_NUM_CHUNKS_PER_FRAME = 5;
	
	private static ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
	private static ArrayList<Chunk> chunkLoadList = new ArrayList<Chunk>();
	private static ArrayList<Chunk> chunkRenderList = new ArrayList<Chunk>();
	private static ArrayList<Chunk> chunkUnloadList = new ArrayList<Chunk>();
	private static ArrayList<Chunk> chunkVisibilityList = new ArrayList<Chunk>();
	private static ArrayList<Chunk> chunkSetupList = new ArrayList<Chunk>();
	
	private ChunkManager()
	{
		throw new AssertionError();
	}
	
	public static void update(float dt, Matrix4f playerView)
	{
		/*
		   UpdateAsyncChunker();

		    UpdateLoadList();
		
		    UpdateSetupList();
		
		    UpdateRebuildList();
		
		    UpdateFlagsList();
		
		    UpdateUnloadList();
		
		    UpdateVisibilityList(cameraPosition);
			
		    if(m_cameraPosition != cameraPosition || m_cameraView != cameraView)
		    {
		        UpdateRenderList();
		    }
		
		    m_cameraPosition = cameraPosition;
		    m_cameraView = cameraView;
		 */
	}
	
	public static void UpdateLoadList()
	{
		int numChunksLoaded = 0;
		
		 for(Chunk chunk : chunkList)
		 {
			 
		 }
	}
}
