package sap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import test.TestIntf;

public class UserAuth implements TestIntf {
	private String username;
	private String password;

	public UserAuth(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserAuth() {
		super();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
		UserAuth user = new UserAuth();
		String logMessage;
		final Logger logger = Logger.getLogger("User");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("User name : ");
		System.out.println("Password : ");

		// input
		try {
			user.setUsername(reader.readLine());
			user.setPassword(reader.readLine());

			// copy password
			String xx = user.getPassword();

			char[] array = new char[xx.length()];
			int i = 0;
			for (char c : xx.toCharArray()) {
				array[i] = c;
				i++;
			}

			System.out.println("new " + array.toString());

			// hash
			MessageDigest hash = MessageDigest.getInstance("MD5");

			byte[] bytes_password = user.getPassword().getBytes("UTF-8");

			byte[] hash_password = hash.digest(bytes_password);

			user.setPassword(hash_password.toString());

			// log
			logMessage = "user name = " + user.getUsername() + ", " + "password = " + user.getPassword() + " " + xx;
			logger.log(Level.INFO, logMessage);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void runtest() {
		// TODO Auto-generated method stub
		try {
			main(new String[] {});
		} catch (Exception e) {

		}
	}
}
