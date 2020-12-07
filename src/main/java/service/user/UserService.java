package service.user;

import db.DataBase;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public UserService() {
    // Dummy Data
    DataBase.addUser(new User("a", "a", "a", "a@a.a"));
  }

  public void saveUser(HttpRequest httpRequest) {

    User user =
        new User(
            httpRequest.getParameter("userId"),
            httpRequest.getParameter("password"),
            httpRequest.getParameter("name"),
            httpRequest.getParameter("email"));

    DataBase.addUser(user);
  }

  public User login(HttpRequest httpRequest) {

    Map<String, String> requestParam = httpRequest.getParams();

    String userId = requestParam.get("userId");
    String password = requestParam.get("password");

    User user = DataBase.findUserById(userId);

    if (password.equals(user.getPassword())) {
      return user;
    }

    return null;
  }

  public List<User> findAll() {
    return new ArrayList<>(DataBase.findAll());
  }
}
