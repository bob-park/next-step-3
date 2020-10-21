package service.user;

import db.DataBase;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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

    Map<String, String> requestParam = httpRequest.getParams();

    User user =
        new User(
            requestParam.get("userId"),
            requestParam.get("password"),
            requestParam.get("name"),
            requestParam.get("email"));

    DataBase.addUser(user);
  }

  public boolean login(HttpRequest httpRequest) {

    Map<String, String> requestParam = httpRequest.getParams();

    String userId = requestParam.get("userId");
    String password = requestParam.get("password");

    User user = DataBase.findUserById(userId);

    if (user == null) {
      return false;
    }

    return password.equals(user.getPassword());
  }

  public List<User> findAll() {
    return new ArrayList<>(DataBase.findAll());
  }
}
