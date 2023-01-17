package explorewithme.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewCommentDto {
    private String text;
    private Long authorId;
}
