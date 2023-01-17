package explorewithme.service;

import explorewithme.model.user.NewUserRequest;
import explorewithme.model.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    List<User> getUsers(Long[] users, int from, int size);

    User addNewUser(NewUserRequest newUser, HttpServletRequest request);

    void deleteUser(Long userId);

    void subscribeUser(Long userId, HttpServletRequest request);

    User checkUser(Long userId, HttpServletRequest request);
}
