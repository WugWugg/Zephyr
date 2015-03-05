package com.bixfordstudios.math;

import org.lwjgl.util.vector.Quaternion;

public class QMath {
	
	public static Quaternion inverse(Quaternion quaternion)
	{
		Quaternion conjugate = new Quaternion(); quaternion.negate(conjugate);
		Float norm = (float) Math.sqrt((quaternion.w * quaternion.w) + (quaternion.x * quaternion.x) + (quaternion.y * quaternion.y) + (quaternion.z * quaternion.z));
		return QMath.divide(conjugate, (norm * norm));
	}
	
	public static Quaternion divide(Quaternion quaternion, Float scalar)
	{
		return new Quaternion((quaternion.x / scalar), (quaternion.y / scalar), (quaternion.z / scalar), (quaternion.w / scalar));
	}
	
	public static Quaternion divide(Quaternion divisor, Quaternion dividend)
	{
		Float dividendNorm = (dividend.w * dividend.w) + (dividend.x * dividend.x) + (dividend.y * dividend.y) + (dividend.z * dividend.z);
		return QMath.divide(new Quaternion((dividend.w * divisor.z), -(dividend.x * divisor.y), (dividend.y * divisor.x), -(dividend.z * divisor.w)), dividendNorm);
	}
}
