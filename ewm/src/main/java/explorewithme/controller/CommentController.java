package explorewithme.controller;

import explorewithme.model.comment.CommentDto;
import explorewithme.model.comment.EditCommentDto;
import explorewithme.model.comment.NewCommentDto;
import explorewithme.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/event/{eventId}")
    public CommentDto addComment(@PathVariable("eventId") Long eventId,
                                 @RequestBody NewCommentDto dto, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return commentService.addComment(eventId, dto, request);
    }

    @GetMapping("/{comId}")
    public CommentDto getComment(@PathVariable("comId") Long comId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return commentService.getComment(comId, request);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getEventComments(@PathVariable("eventId") Long eventId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return commentService.getEventComments(eventId, from, size, request);
    }

    @PatchMapping("/{comId}")
    @Validated
    public CommentDto editComment(@PathVariable("comId") Long comId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody EditCommentDto dto, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        return commentService.editComment(comId, userId, dto, request);
    }

    @DeleteMapping("/{comId}")
    public void deleteComment(@PathVariable("comId") Long comId,
                              @RequestHeader("X-Sharer-User-Id") Long userId, HttpServletRequest request) {
        log.info(request.getMethod() + ": " + request.getRequestURI());

        commentService.deleteCommentByOwner(comId, userId, request);
    }
}
