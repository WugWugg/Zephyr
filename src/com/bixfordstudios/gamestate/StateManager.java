package com.bixfordstudios.gamestate;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class StateManager {
	
	public static ArrayList<ChangeListener> changeListenerList = new ArrayList<ChangeListener>();
	
	private static State currentState;
	private static HashMap<State, EnumSet<State>> stateMap;
	
	// Suppress default constructor for non-instantiability
	private  StateManager()
	{
		throw new AssertionError();
	}
	
	static {		
		stateMap = new HashMap<State, EnumSet<State>>();
		
		stateMap.put(State.RUNNING, 
				EnumSet.of(State.PAUSED, State.ENDED));
		
		stateMap.put(State.PAUSED, 
				EnumSet.of(State.RUNNING, State.RESET, State.CONFIGURING));
		
		stateMap.put(State.ENDED,
				EnumSet.of(State.RESET));
		
		stateMap.put(State.CONFIGURING, 
				EnumSet.of(State.PAUSED));
		
		stateMap.put(State.RESET, 
				EnumSet.of(State.PAUSED, State.RESET));
		
		//Sets Initial State
		currentState = State.PAUSED;
	}
	
	public static State getCurrent()
	{
		return currentState;
	}
	
	public static State setCurrent(State desiredState)
	{
		if(!isReachable(desiredState))
		{
			throw new IllegalArgumentException();
		}
		
		return setAsFinal(desiredState);
	}
	
	private static boolean isReachable(State desiredState)
	{
		return stateMap.get(currentState).contains(desiredState);
	}
	
	private static State setAsFinal(State desiredState)
	{
		currentState = desiredState;
		final ChangeEvent e = new ChangeEvent(currentState);
		
		for (final ChangeListener l : changeListenerList)
		{
			l.stateChanged(e);
		}
		
		return currentState;
	}
}
