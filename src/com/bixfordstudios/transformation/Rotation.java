package com.bixfordstudios.transformation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class Rotation {

	public static Matrix4f rotate(Quaternion appliedRotation)
	{
		Matrix4f matrix = new Matrix4f();
		Quaternion temp = new Quaternion();
		
		appliedRotation.normalise(appliedRotation);
		
		Quaternion xAxis = new Quaternion(matrix.m00, matrix.m01, matrix.m02, 1f);
		Quaternion yAxis = new Quaternion(matrix.m10, matrix.m11, matrix.m12, 1f);
		Quaternion zAxis = new Quaternion(matrix.m20, matrix.m21, matrix.m22, 1f);
		Quaternion position = new Quaternion(matrix.m30, matrix.m31, matrix.m32, 0);
		
		xAxis = Quaternion.mul(appliedRotation, Quaternion.mul(xAxis, appliedRotation.negate(temp), temp), xAxis);
		yAxis = Quaternion.mul(appliedRotation, Quaternion.mul(yAxis, appliedRotation.negate(temp), temp), yAxis);
		zAxis = Quaternion.mul(appliedRotation, Quaternion.mul(zAxis, appliedRotation.negate(temp), temp), zAxis);
		position = Quaternion.mul(appliedRotation, Quaternion.mul(position, appliedRotation.negate(temp), temp), position);
				
		matrix.m00 = xAxis.x;
		matrix.m01 = xAxis.y;
		matrix.m02 = xAxis.z;
		
		matrix.m10 = yAxis.x;
		matrix.m11 = yAxis.y;
		matrix.m12 = yAxis.z;
		
		matrix.m20 = zAxis.x;
		matrix.m21 = zAxis.y;
		matrix.m22 = zAxis.z;
		
		matrix.m30 = position.x;
		matrix.m31 = position.y;
		matrix.m32 = position.z;
		
		return matrix;
	}
	
	public static Matrix4f rotate(Quaternion appliedRotation, Matrix4f matrix)
	{
		Quaternion temp = new Quaternion();
		
		appliedRotation.normalise(appliedRotation);
		
		Quaternion xAxis = new Quaternion(matrix.m00, matrix.m01, matrix.m02, 1f);
		Quaternion yAxis = new Quaternion(matrix.m10, matrix.m11, matrix.m12, 1f);
		Quaternion zAxis = new Quaternion(matrix.m20, matrix.m21, matrix.m22, 1f);
		Quaternion position = new Quaternion(matrix.m30, matrix.m31, matrix.m32, 0);
		
		xAxis = Quaternion.mul(appliedRotation, Quaternion.mul(xAxis, appliedRotation.negate(temp), temp), xAxis);
		yAxis = Quaternion.mul(appliedRotation, Quaternion.mul(yAxis, appliedRotation.negate(temp), temp), yAxis);
		zAxis = Quaternion.mul(appliedRotation, Quaternion.mul(zAxis, appliedRotation.negate(temp), temp), zAxis);
		position = Quaternion.mul(appliedRotation, Quaternion.mul(position, appliedRotation.negate(temp), temp), position);

		
		matrix.m00 = xAxis.x;
		matrix.m01 = xAxis.y;
		matrix.m02 = xAxis.z;
		
		matrix.m10 = yAxis.x;
		matrix.m11 = yAxis.y;
		matrix.m12 = yAxis.z;
		
		matrix.m20 = zAxis.x;
		matrix.m21 = zAxis.y;
		matrix.m22 = zAxis.z;
		
		matrix.m30 = position.x;
		matrix.m31 = position.y;
		matrix.m32 = position.z;
		
		return matrix;
	}
	
	public static Vector3f rotateToIdentity(Matrix4f originalMatrix)
	{
		Matrix4f matrix = new Matrix4f(originalMatrix);		
		matrix.invert();		
		return new Vector3f(matrix.m30, matrix.m31, matrix.m32);
	}
	
	public static Quaternion rotateTo(Vector3f initial, Vector3f end)
	{
		Quaternion ret = new Quaternion();
		
		Vector3f quaternionXYZ = new Vector3f();
		Vector3f.cross(initial, end, quaternionXYZ);
		ret.x = quaternionXYZ.x;
		ret.y = quaternionXYZ.y;
		ret.z = quaternionXYZ.z;
		ret.w = (float) Math.sqrt((initial.length() * initial.length()) * (end.length() * end.length()) + Vector3f.dot(initial, end));
		ret.normalise(ret);
		
		return ret;
	}
}
