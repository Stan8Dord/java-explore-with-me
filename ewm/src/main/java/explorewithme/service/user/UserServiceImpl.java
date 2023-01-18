package explorewithme.service.user;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.ConflictException;
import explorewithme.exceptions.NotFoundUserException;
import explorewithme.model.user.dto.NewUserRequest;
import explorewithme.model.user.User;
import explorewithme.model.user.UserMapper;
import explorewithme.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers(Long[] users, int fromNum, int size) {
        if (users != null) {
            return userRepository.findAllById(Arrays.asList(users));
        } else {
            int from = fromNum >= 0 ? fromNum / size : 0;
            Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
            Page<User> userPage = userRepository.findAll(page);

            return userPage.stream().collect(Collectors.toList());
        }
    }

    @Override
    public User addNewUser(NewUserRequest newUser, HttpServletRequest request) {
        if (newUser.getName() == null)
            throw new BadRequestException(request.getParameterMap().toString());
        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());
        if (emails.contains(newUser.getEmail()))
            throw new ConflictException("Повторяющееся значение email = " + newUser.getEmail() +
                    ". Request path = " + request.getRequestURI());
        else
            return userRepository.save(UserMapper.toUser(newUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void subscribeUser(Long userId, HttpServletRequest request) {
        User user = checkUser(userId, request);
        user.setSubscribed(true);

        userRepository.save(user);
    }

    @Override
    public User checkUser(Long userId, HttpServletRequest request) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(userId, request));
    }
}
