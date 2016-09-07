package edu.tum.uc.jvm.sap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:SecurityKnowledgeBase.db");
		return conn;
	}
}
