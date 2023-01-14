package explorewithme.service;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.ConflictException;
import explorewithme.model.user.NewUserRequest;
import explorewithme.model.user.User;
import explorewithme.model.user.UserMapper;
import explorewithme.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
    public User addNewUser(NewUserRequest newUser) {
        if (newUser.getName() == null)
            throw new BadRequestException("Некорректный запрос");
        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());
        if (emails.contains(newUser.getEmail()))
            throw new ConflictException("Повторяющееся значение email = " + newUser.getEmail());
        else
            return userRepository.save(UserMapper.toUser(newUser));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
