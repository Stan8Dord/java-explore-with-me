package explorewithme.model.comment;

import explorewithme.model.user.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(NewCommentDto newComment, Long eventId, User user) {
        return new Comment(
                null,
                newComment.getText(),
                CommentState.NEW,
                0,
                user,
                eventId,
                LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getVersion());
    }
}
