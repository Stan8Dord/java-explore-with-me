package explorewithme.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @NotNull @Email
    private String email;
    @NotNull
    private String name;
}
