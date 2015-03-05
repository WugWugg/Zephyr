package com.bixfordstudios.camera;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bixfordstudios.transformation.Rotation;
import com.bixfordstudios.transformation.Translation;
import com.bixfordstudios.utility.Axis;
import com.bixfordstudios.utility.CoordinateFloat;

public class Camera {

	/**
	 * viewRadius is the measure of how many chunks around the camera should be loaded
	 */
	public static float VIEW_RADIUS = 1f;
	public static float RADIANS_TO_DEGREES_SCALAR = (float) (180 / Math.PI);
	
	/**
	 * Varible locations as followed:<br>
	 * 	m00	m10	m20	m30<br>
	 * 	m01	m11	m21	m31<br>
	 * 	m02	m12	m22 m32<br>
	 * 	m03	m13 m23	m33<br><br>
	 * The axis are column based and are as follows: x-axis (m01, m02, m03), y-axis (m11, m12, m13), and z-axis (m21, m22, m23).<br>
	 */
	private static Matrix4f viewingMatrix = new Matrix4f();
	public static CoordinateFloat position = new CoordinateFloat(0, 0, 0);
	
	private Camera()
	{
		throw new AssertionError();
	}
	
	public static void translate(float x, float y, float z)
	{	
		setViewingMatrix(Translation.translate(new Vector3f(-x, -y ,z), viewingMatrix));
		
		Vector3f positionV = Rotation.rotateToIdentity(viewingMatrix);
		position.x = positionV.x;
		position.y = positionV.y;
		position.z = positionV.z;
		position.round();
	}
	
	public static  void rotate(float pitch, float yaw, float roll)
	{
		setViewingMatrix(Rotation.rotate(new Quaternion(pitch, yaw, roll, 1), viewingMatrix));
	}
	
	public static Axis getAxis()
	{
		Vector3f up = new Vector3f(viewingMatrix.m10, viewingMatrix.m11, viewingMatrix.m12);
		Vector3f forward = new Vector3f(viewingMatrix.m20, viewingMatrix.m21, viewingMatrix.m22);
		Vector3f right = new Vector3f(viewingMatrix.m00, viewingMatrix.m01, viewingMatrix.m02);
		return new Axis(up, forward, right);
	}
	
	/**
	 * Returns a float array of pitch, yaw, and roll, measured in radians, of the player object <br>
	 * Pitch: <br>
	 * The pitch is measured from the positive Y-axis.<br>
	 * Yaw:<br>
	 * The yaw is measured from the positive Z-axis.<br>
	 * Roll:<br>
	 * The roll is measured from the positive Y-Axis.<br>
	 * @return float[] {pitch, yaw, roll}
	 */
	public static float[] getZAxisAngles()
	{
		//Pitch, Yaw, Roll
		float[] ret = new float[3];
		Vector3f playerVectorZ = new Vector3f(viewingMatrix.m20, viewingMatrix.m21, viewingMatrix.m22);
		Vector3f playerVectorY = new Vector3f(viewingMatrix.m10, viewingMatrix.m11, viewingMatrix.m12);
		
		Vector3f vectorX = new Vector3f(1, 0, 0);
		Vector3f vectorY = new Vector3f(0, 1, 0);
		//Pitch
		float pitch = viewingMatrix.m12 > 1 || viewingMatrix.m12 < -1 ? 0 : (float) (Math.acos(viewingMatrix.m12));
		ret[0] = (float) (Vector3f.dot(vectorX, Vector3f.cross(vectorY, playerVectorZ, new Vector3f())) > 0 ? Math.PI - pitch : Math.PI + pitch);
		
		//Yaw
		float yaw = viewingMatrix.m22 > 1 || viewingMatrix.m22 < -1 ? 0 : (float) (Math.acos(viewingMatrix.m22));
		ret[1] = (float) (Vector3f.dot(vectorX, playerVectorZ) > 0 ? (2 * Math.PI) - yaw : yaw);
		
		//Roll
		float roll = viewingMatrix.m00 > 1 || viewingMatrix.m00 < -1 ? 0 : (float) (Math.acos(viewingMatrix.m00));
		ret[2] = (float) (Vector3f.dot(vectorX, playerVectorY) > 0 ? (2 * Math.PI) - roll : roll);
		
		return ret;
	}
	
	public static float[] getYAxisAngles()
	{
		float[] ret = getZAxisAngles();
		ret[0] -= (Math.PI/2);
		return ret;
	}
	
	public static float[] getXAxisAngles()
	{
		float[] ret = getZAxisAngles();
		ret[1] += (Math.PI/2);
		return ret;
	}
	
	public static void setViewingMatrix(Matrix4f matrix)
	{
		viewingMatrix = matrix;
		
		FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
		viewingMatrix.store(matrixData);
		matrixData.flip();
		glMatrixMode(GL_MODELVIEW); 
		glLoadMatrix(matrixData);		
	}
}
