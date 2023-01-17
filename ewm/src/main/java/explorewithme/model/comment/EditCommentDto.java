package explorewithme.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class EditCommentDto {
    private Long id;
    @NotNull
    private String text;
}
