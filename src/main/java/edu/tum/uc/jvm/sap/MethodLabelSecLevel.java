package edu.tum.uc.jvm.sap;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import org.objectweb.asm.Type;

public class MethodLabelSecLevel {

	private static final String TABLE = "method_label_secLevel";
	private static final String COL_CLASS = "class";
	private static final String COL_SIGNATURE = "signature";
	private static final String COL_SOURCE = "source";
	private static final String COL_SINK = "sink";
	private static final String COL_SECLEVEL = "sec_level";
	private static final String COL_DECLASS = "declass";
	private static final String COL_IDLABEL = "id_label";
	private static final String COL_IDTEXT = "text";
	private static final String COL_RETURN = "bc_return";

	public static class MethodLabel {
		public String clazz;
		public String methodSignature;
		public int source;
		public int sink;
		public int declass;
		public String secLevel;
		public int idLabel;
		public String idText;
		public String returnParam;
	}

	public static void main(String[] args) {
		String s = "boolean x o";
		for (int i = 0; i < s.length(); i++) {
			System.out.println(s.charAt(i));
		}
	}

	public static List<MethodLabel> getSecLevel(String clazz, String methodSignature)
			throws ClassNotFoundException, SQLException {
		return getSecLevel(clazz, null, methodSignature);
	}

	private static <T extends Object> T[] concatTwoArrays(T[] a, T[] b) {
		T[] _return = (T[]) new Object[a.length + b.length];
		System.arraycopy(a, 0, _return, 0, a.length);
		System.arraycopy(b, 0, _return, a.length, b.length);
		return _return;
	}

	public static List<MethodLabel> getSecLevel(String clazz, String methodName, String methodSignature)
			throws ClassNotFoundException, SQLException {
		List<MethodLabel> _return = new LinkedList<MethodLabel>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			// extract class
			String[] clazzCmp = clazz.split("/");
			clazz = clazzCmp[clazzCmp.length - 1];

			// extract method parameters
			Type newType = Type.getType(methodSignature);
			Type[] argType = newType.getArgumentTypes();
			Type retType = newType.getReturnType();

			Statement statement = conn.createStatement();
			String s = "SELECT * FROM " + TABLE + " WHERE " + COL_CLASS + " = \"" + clazz + "\" AND " + COL_SIGNATURE
					+ " LIKE \"" + methodName + "%\"";
			ResultSet res = statement.executeQuery(s);
			while (res.next()) {
				MethodLabel m = new MethodLabel();
				m.clazz = res.getString(COL_CLASS);
				m.methodSignature = res.getString(COL_SIGNATURE);
				m.source = res.getInt(COL_SOURCE);
				m.sink = res.getInt(COL_SINK);
				m.secLevel = res.getString(COL_SECLEVEL);
				m.declass = res.getInt(COL_DECLASS);
				m.idLabel = res.getInt(COL_IDLABEL);
				m.idText = res.getString(COL_IDTEXT);
				m.returnParam = res.getString(COL_RETURN);

				boolean add = true;
				int start = m.methodSignature.indexOf("(") + 1;
				int end = m.methodSignature.indexOf(")");
				String[] substr = new String[0];
				if (!"".equals(m.methodSignature.substring(start, end).trim())) {
					substr = m.methodSignature.substring(start, end).split(",");
				}
				if (argType.length != substr.length) {
					add = false;
					continue;
				}
				// check if parameters match
				for (int i = 0; i < argType.length; i++) {
					String[] argT = argType[i].getClassName().split("\\.");
					String argClazzType = argT[argT.length - 1].trim();// .replace(";",
																		// "");

					String substrT = substr[i].trim();
					if (!substrT.toLowerCase().startsWith(argClazzType.toLowerCase())) {
						add = false;
						break;
					}
				}
				// check if return value match
				String[] retT = retType.getClassName().split("\\.");
				if (retT.length > 0) {
					String retClassType = retT[retT.length - 1].trim();
					String retSubStr = m.returnParam.trim();
					if (!retSubStr.toLowerCase().endsWith(retClassType.toLowerCase())) {
						add = false;
					}
				}
				if (add)
					_return.add(m);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

		return _return;
	}

}
