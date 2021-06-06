package service;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public void save(User user) {
    logger.debug("save user.");
  }
}
