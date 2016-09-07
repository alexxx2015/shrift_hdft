package edu.tum.uc.jvm.sap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import it.unimi.dsi.fastutil.Arrays;

public class MethodLabelSecLevel {

	private static final String TABLE = "method_label_secLevel";
	private static final String COL_CLASS = "class";
	private static final String COL_SIGNATURE = "signature";
	private static final String COL_SOURCE = "source";
	private static final String COL_SINK = "sink";
	private static final String COL_SECLEVEL = "sec_level";
	private static final String COL_DECLASS = "declass";
	private static final String COL_IDLABEL = "id_label";

	public static class MethodLabel {
		public String clazz;
		public String methodSignature;
		public int source;
		public int sink;
		public int declass;
		public String secLevel;
		public int idLabel;
	}

	public static void main(String[] args) {
		try {
			List<MethodLabel> m = getSecLevel("BufferedReader", "read()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<MethodLabel> getSecLevel(String clazz, String methodSignature)
			throws ClassNotFoundException, SQLException {
		List<MethodLabel> _return = new LinkedList<MethodLabel>();
		Connection conn = DatabaseConnection.getConnection();
		try {
			Statement statement = conn.createStatement();
			String s = "SELECT * FROM " + TABLE + " WHERE " + COL_CLASS + " = \"" + clazz + "\" AND " + COL_SIGNATURE
					+ " = \"" + methodSignature+"\"";
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
