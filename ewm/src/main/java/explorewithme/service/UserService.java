package explorewithme.service;

import explorewithme.model.user.NewUserRequest;
import explorewithme.model.user.User;

import java.util.List;

public interface UserService {
    List<User> getUsers(Long[] users, int from, int size);

    User addNewUser(NewUserRequest newUser);

    void deleteUser(long userId);
}
