package service;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public void save(User user) {
    logger.debug("user : {}", user);
    DataBase.addUser(user);
  }

  public boolean login(String userId, String password) {

    User user = DataBase.findUserById(userId);

    if (user == null) {
      return false;
    }

    return user.getPassword().equals(password);
  }
}
