package explorewithme.model.user;

import explorewithme.model.user.dto.NewUserRequest;
import explorewithme.model.user.dto.UserShortDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest newUser) {
        return new User(
                null,
                newUser.getName(),
                newUser.getEmail(),
                false);
    }
}
