package explorewithme.service.comment;

import explorewithme.exceptions.BadRequestException;
import explorewithme.exceptions.NotFoundException;
import explorewithme.model.comment.*;
import explorewithme.model.comment.dto.CommentDto;
import explorewithme.model.comment.dto.EditCommentDto;
import explorewithme.model.comment.dto.NewCommentDto;
import explorewithme.model.user.User;
import explorewithme.repository.CommentRepository;
import explorewithme.service.event.EventService;
import explorewithme.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository, EventService eventService, UserService userService) {
        this.commentRepository = commentRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public CommentDto addComment(Long eventId, NewCommentDto dto, HttpServletRequest request) {
        log.info("addComment to event " + eventId);

        if (dto.getText().equals(""))
            throw new BadRequestException("Текст комментария не должен быть пустым!");

        eventService.checkEvent(eventId, request);
        User user = userService.checkUser(dto.getAuthorId(), request);

        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(dto, eventId, user)));
    }

    @Override
    public CommentDto getComment(Long comId, HttpServletRequest request) {
        log.info("getComment: " + comId);

        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий " + comId));

        return CommentMapper.toCommentDto(comment);
    }

    private Comment checkComment(Long comId) {
        return commentRepository.findById(comId).orElseThrow(() -> new NotFoundException(""));
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, int fromNum, int size, HttpServletRequest request) {
        log.info("getEventComments of event " + eventId);

        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "created"));
        Page<Comment> comments = commentRepository.findAll(page);

        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto editComment(Long comId, Long userId, EditCommentDto dto, HttpServletRequest request) {
        log.info("editComment: " + comId);

        User user = userService.checkUser(userId, request);
        Comment comment = checkComment(comId);
        int editedTimes = comment.getVersion();
        String newText = dto.getText();

        if (user.isSubscribed() && editedTimes < 3 && !newText.equals("")
                && Objects.equals(userId, comment.getAuthor().getId())) {
            comment.setText(newText);
            comment.setState(CommentState.EDITED);
            comment.setVersion(editedTimes + 1);
        } else
            throw new BadRequestException(String.format("Редактирование своих комментариев доступно только платным " +
                    "подписчикам (%b) и не более трех раз (%d). Текст %s не должен быть пустым!",
                    user.isSubscribed(), editedTimes, newText));

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void approveComment(Long comId) {
        log.info("approveComment: " + comId);

        Comment comment = checkComment(comId);
        comment.setState(CommentState.APPROVED);

        CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByOwner(Long comId, Long userId, HttpServletRequest request) {
        log.info("deleteCommentByOwner: " + comId);

        Comment comment = checkComment(comId);

        if (!Objects.equals(comment.getAuthor().getId(), userId))
            throw new BadRequestException("Нельзя удалить чужой комментарий " + comId);

        commentRepository.deleteById(comId);
    }

    @Override
    public void deleteCommentByAdmin(Long comId) {
        log.info("deleteCommentByAdmin: " + comId);

        checkComment(comId);

        commentRepository.deleteById(comId);
    }
}
