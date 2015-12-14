package com.vip.sdk.base.utils;

/**
 * utilities of number
 * @author Yin Yong
 * @version 1.0
 */
public class NumberUtils {

	/**
	 * no instance needed
	 */
	private NumberUtils() { /*no instance*/ }
	
	public static final byte BYTE_ZERO = (byte) 0;
	public static final short SHORT_ZERO = (short) 0;
	public static final int INT_ZERO = 0;
	public static final long LONG_ZERO = 0L;
	public static final float FLOAT_ZERO = 0.0F;
	public static final double DOUBLE_ZERO = 0.0D;
	
	public static final String PLUS_SIGN = "+";
	public static final String MINUS_SIGN = "-";
	
	/**
	 * 从String转为byte。
	 * <p>
	 * 与{@link java.lang.Byte#parseByte(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为{@link #BYTE_ZERO}。
	 * </p>
	 * @param string 源字符串
	 * @return 转为byte后的数值，默认为{@link #BYTE_ZERO}
	 */
	public static byte getByte(String string) {
		return getByte(string, BYTE_ZERO);
	}
	
	/**
	 * 从String转为byte，可能发生转换的错误，此时使用defVal作为代替。
	 * <p>
	 * 与{@link java.lang.Byte#parseByte(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为defVal。
	 * </p>
	 * @param string 源字符串
	 * @param defVal 转换错误后作为替代的值
	 * @return 转为byte后的数值，默认为defVal
	 */
	public static byte getByte(String string, byte defVal) {
		try {
			return Byte.parseByte(string);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 从String转为short。
	 * <p>
	 * 与{@link java.lang.Short#parseShort(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为{@link #SHORT_ZERO}。
	 * </p>
	 * @param string 源字符串
	 * @return 转为short后的数值，默认为{@link #SHORT_ZERO}
	 */
	public static short getShort(String string) {
		return getShort(string, SHORT_ZERO);
	}
	
	/**
	 * 从String转为short，可能发生转换的错误，此时使用defVal作为代替。
	 * <p>
	 * 与{@link java.lang.Short#parseShort(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为defVal。
	 * </p>
	 * @param string 源字符串
	 * @param defVal 转换错误后作为替代的值
	 * @return 转为short后的数值，默认为defVal
	 */
	public static short getShort(String string, short defVal) {
		try {
			return Short.parseShort(string);
		} catch (Exception e) {
			return defVal;
		}
	}
	
	/**
	 * 从String转为int。
	 * <p>
	 * 与{@link java.lang.Integer#parseInt(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为{@link #INT_ZERO}。
	 * </p>
	 * @param string 源字符串
	 * @return 转为int后的数值，默认为{@link #INT_ZERO}
	 */
	public static int getInt(String string) {
		return getInt(string, INT_ZERO);
	}
	
	/**
	 * 从String转为int，可能发生转换的错误，此时使用defVal作为代替。
	 * <p>
	 * 与{@link java.lang.Integer#parseInt(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为defVal。
	 * </p>
	 * @param string 源字符串
	 * @param defVal 转换错误后作为替代的值
	 * @return 转为int后的数值，默认为defVal
	 */
	public static int getInt(String string, int defVal) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 从String转为long。
	 * <p>
	 * 与{@link java.lang.Long#parseLong(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为{@link #LONG_ZERO}。
	 * </p>
	 * @param string 源字符串
	 * @return 转为long后的数值，默认为{@link #LONG_ZERO}
	 */
	public static long getLong(String string) {
		return getLong(string, LONG_ZERO);
	}
	
	/**
	 * 从String转为long，可能发生转换的错误，此时使用defVal作为代替。
	 * <p>
	 * 与{@link java.lang.Long#parseLong(String)}不同的是，
	 * 在转换错误时，我们不让异常抛出，而是使用默认值代替，默认为defVal
	 * </p>
	 * @param string 源字符串
	 * @param defVal 转换错误后作为替代的值
	 * @return 转为long后的数值，默认为defVal
	 */
	public static long getLong(String string, long defVal) {
		try {
			return Long.parseLong(string);
		} catch (Exception e) {
			return defVal;
		}
	}

	public static float getFloat(String string) {
		return getFloat(string, FLOAT_ZERO);
	}
	
	public static float getFloat(String string, float defVal) {
		try {
			return Float.parseFloat(string);
		} catch (Exception e) {
			return defVal;
		}
	}

	public static double getDouble(String string) {
		return getDouble(string, DOUBLE_ZERO);
	}
	
	public static double getDouble(String string, double defVal) {
		try {
			return Double.parseDouble(string);
		} catch (Exception e) {
			return defVal;
		}
	}
	
	/**
	 * 格式化数值，保留小数点后几位。
	 * @param d 原数值
	 * @param scale 定义保留小数点后的几位
	 * @return 经过格式化后的字符串
	 */
	public static String format(double d, int scale) {
		if (scale <= 0) {
			return String.valueOf((int) d);
		}
		return String.format("%." + scale + "f", d);
	}
	
	/**
	 * 根据数值的正负性质，返回其符号。
	 * <p>
	 * if d == 0 return "";
	 * </p>
	 * <p>
	 * if d > 0 return (ignorePositive ? "" : PLUS_SIGN);
	 * </p>
	 * <p>
	 * if d < 0 return MINUS_SIGN
	 * </p>
	 * @param d 数值
	 * @param ignorePositive 如果是正数，是否需要返回符号，如果为false，则为空字符串。
	 * @return
	 */
	public static String getNumberSign(double d, boolean ignorePositive) {
		if (d == DOUBLE_ZERO) {
			return "";
		}
		return d > 0 ? (ignorePositive ? "" : PLUS_SIGN) : MINUS_SIGN;
	}
	
	/**
	 * 格式化数值，根据单位进行换算，将数值转化到最接近的单位，并返回带单位的字符串。
	 * <p>
	 * 如果参数中的单位阶数不够，则截止到最大单位。
	 * </p>
	 * @param i 原数值
	 * @param incrementBetweenUnits 相邻单位之间的变化数值，比如长度中以10为换算，则incrementBetweenUnits为10
	 * @param units 字符串单位阶数数组
	 * @return 经过格式化后的字符串
	 */
	public static String formatByUnit(int i, int incrementBetweenUnits, String...units) {
		if (incrementBetweenUnits <= 0) {
			throw new IllegalArgumentException("NumberUtil.formatByUnit incrementBetweenUnits is negative!");
		}
		if (null == units || units.length == 0) {
			throw new IllegalArgumentException("NumberUtil.formatByUnit 'units' is of a length of at least 1!");
		}
		int unitIndex = 0;
		int nearestVal = Math.abs(i);
		int tmp = 0;
		while ((tmp = nearestVal / incrementBetweenUnits) > 0) {
			// 如果参数中的单位阶数不够，则截止到最大单位
			if (unitIndex >= units.length - 1) {
				break;
			}
			unitIndex ++;
			nearestVal = tmp;
		}
		return getNumberSign(i, true) + nearestVal + units[Math.min(units.length, unitIndex)];
	}
	
	/**
	 * 格式化数值，根据单位进行换算，将数值转化到最接近的单位，并返回带单位的字符串。
	 * <p>
	 * 如果参数中的单位阶数不够，则截止到最大单位。
	 * </p>
	 * @param d 原数值
	 * @param incrementBetweenUnits 相邻单位之间的变化数值，比如长度中以10为换算，则incrementBetweenUnits为10
	 * @param scale 最终返回的字符串中，数值的小数点后位数
	 * @param units 字符串单位阶数数组
	 * @return 经过格式化后的字符串
	 */
	public static String formatByUnit(double d, double incrementBetweenUnits, int scale, String...units) {
		return formatByUnit(d, incrementBetweenUnits, incrementBetweenUnits, scale, units);
	}
	
	/**
	 * 格式化数值，根据单位进行换算，将数值转化到最接近的单位，并返回带单位的字符串。
	 * <p>
	 * 如果参数中的单位阶数不够，则截止到最大单位。
	 * </p>
	 * @param d 原数值
	 * @param incrementBetweenUnits 相邻单位之间的变化数值，比如长度中以10为换算，则incrementBetweenUnits为10
	 * @param offset 达到某一值时，我们认为可以使用下一个单位。比如，在文件大小的显示上，incrementBetweenUnits为1024，但是在某些情况下达到900时，我们可能就要它使用下一阶单位，比如0.91M
	 * @param scale 最终返回的字符串中，数值的小数点后位数
	 * @param units 字符串单位阶数数组
	 * @return 经过格式化后的字符串
	 */
	public static String formatByUnit(double d, double incrementBetweenUnits,
			double offset, int scale, String...units) {
		if (incrementBetweenUnits <= 0) {
			throw new IllegalArgumentException("NumberUtil.formatByUnit incrementBetweenUnits is negative!");
		}
		if (null == units || units.length == 0) {
			throw new IllegalArgumentException("NumberUtil.formatByUnit 'units' is of a length of at least 1!");
		}
		int unitIndex = 0;
		double nearestVal = Math.abs(d);
		// |		0		|			 	|
		// |	__________check__________	|
		// |		1		|	  check		|--->return?
		// |		2		|	  check		|--->return?
		// |		3		|	  check		|--->return?
		// |		4		|	  check		|--->return?
		// |	   ...		|	   ....		|
		if (unitIndex < units.length - 1) {
			double tmp = 0;
			while ((int)(tmp = nearestVal / incrementBetweenUnits) > 0) {
				unitIndex ++;
				nearestVal = tmp;
				if (unitIndex >= units.length - 1) {
					break;
				}
			}
		}
		// 1024 * 1024 * 900, if offset is 800, then here nearestVal == 900,
		// 900 > 800, we think it can be up to the next unit
		if (nearestVal > offset) {
			if (unitIndex < units.length - 1) {
				nearestVal = nearestVal / incrementBetweenUnits;
				unitIndex ++;
			}
		}
		return getNumberSign(d, true) + format(nearestVal, scale) + units[Math.min(units.length, unitIndex)];
	}
}
