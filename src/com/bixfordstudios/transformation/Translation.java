package com.bixfordstudios.transformation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Translation {

	public static Matrix4f translate(Vector3f appliedTranslation)
	{
		Matrix4f matrix = new Matrix4f();
		
		matrix.m30 += appliedTranslation.x;
		matrix.m31 += appliedTranslation.y;
		matrix.m32 += appliedTranslation.z;
		
		return matrix;
	}
	
	public static Matrix4f translate(Vector3f appliedTranslation, Matrix4f matrix)
	{		
		matrix.m30 += appliedTranslation.x;
		matrix.m31 += appliedTranslation.y;
		matrix.m32 += appliedTranslation.z;
		
		return matrix;
	}
}
