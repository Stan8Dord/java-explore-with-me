package explorewithme.service.comment;

import explorewithme.model.comment.dto.CommentDto;
import explorewithme.model.comment.dto.EditCommentDto;
import explorewithme.model.comment.dto.NewCommentDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommentService {
    CommentDto addComment(Long eventId, NewCommentDto dto, HttpServletRequest request);

    CommentDto getComment(Long comId, HttpServletRequest request);

    List<CommentDto> getEventComments(Long eventId, int fromNum, int size, HttpServletRequest request);

    CommentDto editComment(Long comId, Long userId, EditCommentDto dto, HttpServletRequest request);

    void deleteCommentByOwner(Long comId, Long userId, HttpServletRequest request);

    void approveComment(Long comId);

    void deleteCommentByAdmin(Long comId);
}
