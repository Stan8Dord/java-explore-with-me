package explorewithme.model.user;

public class UserMapper {
    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest newUser) {
        return new User(newUser.getName(), newUser.getEmail());
    }
}
