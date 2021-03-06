package service;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static util.CommonUtils.equalsIgnoreCase;
import static util.CommonUtils.isEmpty;

public class UserService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public void save(User user) {
    logger.debug("user : {}", user);
    DataBase.addUser(user);
  }

  public boolean login(String userId, String password) {

    User user = DataBase.findUserById(userId);

    if (isEmpty(user)) {
      return false;
    }

    return equalsIgnoreCase(user.getPassword(), password);
  }

  public Collection<User> getUserList() {
    return DataBase.findAll();
  }
}
