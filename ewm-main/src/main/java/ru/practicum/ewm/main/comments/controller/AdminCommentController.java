package ru.practicum.ewm.main.comments.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comments.dto.CommentDto;
import ru.practicum.ewm.main.comments.service.CommentService;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events/{eventId}/comments")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Обновить комментарий - может только владелец
    @PutMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Updating comment ID: {} for event ID: {}", commentId, eventId);
        return commentService.updateComment(eventId, commentDto);
    }

    // Удалить комментарий - может владелец события или комментария
    @DeleteMapping("/{commentId}")
    public Long deleteComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId) {
        log.info("Deleting comment ID: {} for event ID: {}", commentId, eventId);
        return commentService.deleteComment(eventId, commentId);
    }
}
