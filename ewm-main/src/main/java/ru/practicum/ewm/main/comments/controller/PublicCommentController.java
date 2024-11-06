package ru.practicum.ewm.main.comments.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comments.dto.CommentDto;
import ru.practicum.ewm.main.comments.dto.CreateCommentDto;
import ru.practicum.ewm.main.comments.service.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/comments")
public class PublicCommentController {

    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping({"/{eventId}", "/{eventId}/"})
    public CommentDto createComment(
            @PathVariable Long eventId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        log.info("Creating comment for event ID: {}", eventId);
        return commentService.createComment(eventId, createCommentDto);
    }

    @GetMapping({"/{eventId}", "/{eventId}/"})
    public List<CommentDto> getCommentsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "false") Boolean includeChildren
    ) {
        log.info("Fetching comments for event ID: {}", eventId);
        return commentService.getCommentsByEvent(eventId, includeChildren);
    }

    @GetMapping({"/{eventId}/{commentId}", "/{eventId}/{commentId}"})
    public CommentDto getCommentById(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "false") Boolean includeChildren
    ) {
        log.info("Fetching comment ID: {} for event ID: {}", commentId, eventId);
        return commentService.getCommentById(commentId, includeChildren);
    }
}

