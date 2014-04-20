package pdsd.beans;

public class User {

	private Integer userId;

	private String username;

	private String password;

	private String login;

	public User(Integer userId, String username, String password, String login) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.login = login;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
