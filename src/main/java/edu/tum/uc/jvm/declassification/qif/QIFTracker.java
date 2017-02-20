package edu.tum.uc.jvm.declassification.qif;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.tum.uc.jvm.utility.UnsafeUtil;
import edu.tum.uc.jvm.utility.Utility;
import groovyjarjarasm.asm.Opcodes;

public class QIFTracker {
	/*
	 * Stores for each source how much of its data still exist within the
	 * process
	 */
	private static class Qif {
		private String id;
		private double oriQty = 0.0;
		private double actQty = 0.0;

		Qif(double qty, String id) {
			this.oriQty = this.actQty = qty;
			this.id = id;
		}

		public double getOriQty() {
			return this.oriQty;
		}

		public double getActQty() {
			return this.actQty;
		}

		public String getId() {
			return this.id;
		}

		public void setOritQty(double qty) {
			this.oriQty = qty;
		}

		public void setActQty(double qty) {
			this.actQty = qty;
		}
	}

	/*
	 * Stores all executed sources
	 */
	private static Map<String, Qif> qif = new HashMap<String, Qif>();

	/*
	 * represents the byte-size of each primitive data type
	 */
	public static enum SIZE {
		BYTE(1), BOOLEAN(1), CHARACTER(2), DOUBLE(8), FLOAT(4), INTEGER(4), LONG(8), SHORT(2);

		private int size;

		SIZE(int size) {
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}
	}

	/*
	 * Invoked on an executed source, computes the size of read data and stores
	 * it into an internal hash table
	 */
	public static void addQty(Object o, String sourceId) {
		long size = getByteSize(o);
		Qif q = null;
		if (qif.containsKey(sourceId))
			q = qif.get(sourceId);
		else
			q = new Qif((double) size, sourceId);
		qif.put(sourceId, q);
	}

	/*
	 * Quantity estimation for arithmetic operations
	 */
	public static void decArithQty(Object o1, Object o2, String sourceId, int opcode) {
		Qif actualAmount = qif.get(sourceId);
		if (actualAmount == null)
			return;
		
		double diff = 0;
		String o1str, o2str;

		if (opcode == Opcodes.IAND || opcode == Opcodes.LAND || opcode == Opcodes.IOR || opcode == Opcodes.LOR
				|| opcode == Opcodes.IXOR || opcode == Opcodes.LXOR) {
			StringBuilder o1strb = null, o2strb = null, restrb = null;
			if (opcode == Opcodes.IAND || opcode == Opcodes.IOR || opcode == Opcodes.IXOR) {
				// compute the actual result between both operands
				int o1val = (int) o1, o2val = (int) o2, res = 0;
				if (opcode == Opcodes.IAND)
					res = o1val & o2val;
				else if (opcode == Opcodes.IOR)
					res = o1val | o2val;
				else if (opcode == Opcodes.IXOR)
					res = o1val ^ o2val;
				o1strb = new StringBuilder(Integer.toBinaryString(o1val));
				o2strb = new StringBuilder(Integer.toBinaryString(o2val));
				restrb = new StringBuilder(Integer.toBinaryString(res));
			} else if (opcode == Opcodes.LAND || opcode == Opcodes.LOR || opcode == Opcodes.LXOR) {
				long o1val = (long) o1, o2val = (long) o2, res = 0;
				if (opcode == Opcodes.LAND)
					res = o1val & o2val;
				else if (opcode == Opcodes.LOR)
					res = o1val | o2val;
				else if (opcode == Opcodes.LXOR)
					res = o1val ^ o2val;
				o1strb = new StringBuilder(Long.toBinaryString(o1val));
				o2strb = new StringBuilder(Long.toBinaryString(o2val));
				restrb = new StringBuilder(Long.toBinaryString(res));
			}

			// generate the reverse binary string
			o1str = o1strb.reverse().toString();
			o2str = o2strb.reverse().toString();
			String restr = restrb.reverse().toString();

			// compute how many positions of the result match within each of the
			// parameter
			int[] matches = new int[] { 0, 0 };
			for (int i = 0; i < restr.length(); i++) {
				if (o1str.length() > i && restr.charAt(i) == o1str.charAt(i)) {
					matches[0]++;
				}
				if (o2str.length() > i && restr.charAt(i) == o2str.charAt(i)) {
					matches[1]++;
				}
			}

			double o1cut = (double) matches[0] / o1str.length();
			double o2cut = (double) matches[1] / o2str.length();
			// System.out.println("MATCHES with "+restr+": " +o1str+" -> "+
			// matches[0] + " , " + o2str + " -> "+ matches[1]);
			// System.out.println("DEC: " + o1cut + " , " + o2cut);
			// diff = 1 - (double) count / (getByteSize(o2) * 8);
			diff = 1 - Math.max(o1cut, o2cut);
		}
		// Bit-Op: compute the difference as the ratio between the number of
		// shifted bits and the number of total possible shifts which is 2^5 as
		// java only considers the last five bits of a word for the number of
		// shift positions.
		else if (opcode == Opcodes.ISHL || opcode == Opcodes.ISHR || opcode == Opcodes.IUSHR) {
			diff = ((Integer) o2).intValue() / Math.pow(2, 5);
		} 
		else if (opcode == Opcodes.LSHL || opcode == Opcodes.LSHR || opcode == Opcodes.LUSHR) {
			diff = ((Long) o2).intValue() / Math.pow(2, 6);
		} 
//		The else branch handles all the remainig arithmetic commands
//		rethink how to handle TREM command
		else {
			// Compute the difference between two numeric values as the number
			// of flipped bits between the result and each operand
			if (o1 instanceof Integer && o2 instanceof Integer) {
				o1str = Integer.toBinaryString((Integer) o1);
				o2str = Integer.toBinaryString((Integer) o2);
				int distance = Utility.levenshteinDistance(o1str, o2str);
				int maxLength = Math.max(o1str.length(), o2str.length());// Integer.toBinaryString(Integer.MAX_VALUE).length()+1;
				diff = (double) distance / maxLength;
			} else if (o1 instanceof Long && o2 instanceof Long) {
				o1str = Long.toBinaryString((Long) o1);
				o2str = Long.toBinaryString((Long) o2);
				int distance = Utility.levenshteinDistance(o1str, o2str);
				int maxLength = Math.max(o1str.length(), o2str.length());
				diff = (double) distance / maxLength;
			} else if (o1 instanceof Float && o2 instanceof Float) {
				o1str = Long.toBinaryString(Float.floatToRawIntBits((Float) o1));
				o2str = Long.toBinaryString(Float.floatToRawIntBits((Float) o2));
				int distance = Utility.levenshteinDistance(o1str, o2str);
				int maxLength = Math.max(o1str.length(), o2str.length());
				diff = (double) distance / maxLength;
			} else if (o1 instanceof Double && o2 instanceof Double) {
				o1str = Long.toBinaryString(Double.doubleToRawLongBits((Double) o1));
				o2str = Long.toBinaryString(Double.doubleToRawLongBits((Double) o2));
				int distance = Utility.levenshteinDistance(o1str, o2str);
				int maxLength = Math.max(o1str.length(), o2str.length());
				diff = (double) distance / maxLength;
			}
		}

		// System.out.println("... Reduced: " + diff + " , " +
		// actualAmount.getActQty() + " , " + aQif + " , " +
		// Mnemonic.OPCODE[opcode]);
		double aQif = actualAmount.getActQty() * (1 - diff);
		actualAmount.setActQty(aQif);
	}

	/*
	 * Quantity estimation for primitive typed inversion commands Quantity is
	 * reduced to the domain of the target data type, but only if the actual
	 * value exceed the max value of the target type
	 */
	public static void decConvQty(Object o, String sourceid, int opcode) {
		double qty = 0;
		int i;
		float f;
		long l;
		double d;
		switch (opcode) {
		case Opcodes.I2B:
			i = ((Integer) o).intValue();
			if (i > Byte.MAX_VALUE)
				qty = (i - Byte.MAX_VALUE) / (double) Integer.MAX_VALUE;
			break;
		case Opcodes.I2C:
			i = ((Integer) 0).intValue();
			if (i > Character.MAX_VALUE)
				qty = (i - Character.MAX_VALUE) / (double) Integer.MAX_VALUE;
			break;
		case Opcodes.I2S:
			i = ((Integer) 0).intValue();
			if (i > Short.MAX_VALUE)
				qty = (i - Short.MAX_VALUE) / (double) Integer.MAX_VALUE;
			break;
		case Opcodes.F2I:
			f = ((Float) o).floatValue();
			if (f > Integer.MAX_VALUE)
				qty = (f - Integer.MAX_VALUE) / (double) Float.MAX_VALUE;
			break;
		case Opcodes.F2L:
			f = ((Float) o).floatValue();
			if (f > Long.MAX_VALUE)
				qty = (f - Long.MAX_VALUE) / (double) Float.MAX_VALUE;
			break;
		case Opcodes.L2I:
			l = ((Long) o).longValue();
			if (l > Integer.MAX_VALUE)
				qty = (l - Integer.MAX_VALUE) / (double) Long.MAX_VALUE;
			break;
		case Opcodes.L2F:// TODO: I think float is larger than long.
			l = ((Long) o).longValue();
			if (l > Float.MAX_VALUE)
				qty = (l - Float.MAX_VALUE) / (double) Long.MAX_VALUE;
			break;
		case Opcodes.D2I:
			d = ((Double) o).doubleValue();
			if (d > Integer.MAX_VALUE) {
				qty = (d - (double) Integer.MAX_VALUE) / Double.MAX_VALUE;
			}
			break;
		case Opcodes.D2F:
			d = ((Double) o).doubleValue();
			if (d > Float.MAX_VALUE)
				qty = (d - Float.MAX_VALUE) / Double.MAX_VALUE;
			break;
		case Opcodes.D2L:
			d = ((Double) o).doubleValue();
			if (d > Long.MAX_VALUE)
				qty = (d - Long.MAX_VALUE) / Double.MAX_VALUE;
			break;
		}
		if (qty > 0) {
			Qif actualAmount = qif.get(sourceid);
			if (actualAmount != null) {
				double aQif = actualAmount.getActQty() * (1 - qty);
				actualAmount.setActQty(aQif);
			}
		}
	}

	/*
	 * Quantity estimation of StringBuilder.append and StringBuilder.replace
	 */
	public static void decStringQty(String o1, String o2, String sourceId) {
		if (!o1.contains(o2) && !o2.contains(o1)) {
			int d = Utility.levenshteinDistance(o1.trim(), o2.trim());
			Qif actualAmount = qif.get(sourceId);
			if (actualAmount != null) {
				int length = Math.max(o1.trim().length(), o2.trim().length());
				double aQif = actualAmount.getActQty() * (1 - ((double) d / length));
				// System.out.println("-- "+sourceId+" reduced from "+
				// actualAmount.getActQty()+" to "+aQif);
				actualAmount.setActQty(aQif);
			}
		}
	}

	// Check if a specific sink was reached
	public static void check(String sinkId, String... sources) {
		for (String s : sources)
			System.out.println("-- Reached sink " + sinkId + " with qty " + qif.get(s).getActQty() + " from " + s
					+ "; Original-Qty " + qif.get(s).getOriQty());
	}

	/*
	 * Returns the size of the parameter o in bytes
	 */
	public static long getByteSize(Object o) {
		long size = 0;
		if (o instanceof Byte)
			size = SIZE.BYTE.getSize();
		else if (o instanceof Boolean)
			size = SIZE.BOOLEAN.getSize();
		else if (o instanceof Character)
			size = SIZE.CHARACTER.getSize();
		else if (o instanceof Short)
			size = SIZE.SHORT.getSize();
		else if (o instanceof Double)
			size = SIZE.DOUBLE.getSize();
		else if (o instanceof Float)
			size = SIZE.FLOAT.getSize();
		else if (o instanceof Integer)
			size = SIZE.INTEGER.getSize();
		else if (o instanceof Long)
			size = SIZE.LONG.getSize();
		else if (o instanceof String)
			size = ((String) o).length();
		else if (o.getClass().isArray()) {
			size = (long) getNumElements(o);
		} else {
			size = UnsafeUtil.sizeOf(o);
		}

		return size;
	}

	/*
	 * Computes the number of elements in a multi-dimensional array
	 */
	public static int getNumElements(Object o) {
		int _return = 1;
		if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			_return *= length;
			if (length > 0) {
				o = Array.get(o, 0);
				length = getNumElements(o);
				_return = (length > 0) ? _return *= length : _return;
			}
		}

		if (_return == 1) {
			// Compute the size of an atom array element, and multiply it with
			// the array dimension
			long arrAtomSize = getByteSize(o);
			if (arrAtomSize != 0)
				_return *= arrAtomSize;
		}

		return _return;
	}

	// >>>Cutter for string operations
	/*
	 * Quantity estimation for String.substr and String.subsequence
	 */
	public static void decSubSequQty(String shortenStr, String origStr, String sourceId) {
		Qif actualAmount = qif.get(sourceId);
		double amount = (double) shortenStr.length() / origStr.length();
		double aQif = actualAmount.getActQty() * amount;
		actualAmount.setActQty(aQif);
	}

	/*
	 * Quantity estimation for String.split
	 */
	public static void decSplitQty(String[] splitStr, String sourceId) {
		Qif actualAmount = qif.get(sourceId);
		int totalLength = Arrays.toString(splitStr).length() - 2;
		double amount = -1;
		for (String s : splitStr) {
			s = s.trim();
			amount = Math.max(amount, (double) s.length() / totalLength);
		}
		double aQif = actualAmount.getActQty() * (amount);
		actualAmount.setActQty(aQif);
	}
}
