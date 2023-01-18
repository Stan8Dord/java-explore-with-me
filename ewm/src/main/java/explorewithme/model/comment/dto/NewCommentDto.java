package explorewithme.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewCommentDto {
    @NotNull
    private String text;
    private Long authorId;
}
