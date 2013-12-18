package all.entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
	private int id;
	private String username;
	private char[] password;
	private boolean isAdmin;

	public User() {
		username = null;
		password = null;
	}

	public User(String username, char[] password, boolean isAdmin) {
		setUsername(username);
		setPassword(password);
		setIsAdmin(isAdmin);
	}
	
	public User(int id, String username, char[] password, boolean isAdmin) {
		setId(id);
		setUsername(username);
		setPassword(password);
		setIsAdmin(isAdmin);
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public char[] getPassword() {
		return password;
	}

	public boolean IsAdmin() {
		return isAdmin;
	}
}
