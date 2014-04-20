package pdsd.dao;

import pdsd.beans.User;

public interface UserDao {

	public User getUser(String login, String password);

	public User registerUser(String name, String login, String password);

	public boolean isUserRegistered(String login);

}
