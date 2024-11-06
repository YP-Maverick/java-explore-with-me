package ru.practicum.ewm.main.comments.service;

import ru.practicum.ewm.main.comments.dto.CommentDto;
import ru.practicum.ewm.main.comments.dto.CreateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long eventId, CreateCommentDto creationDto);

    CommentDto updateComment(Long eventId, CommentDto categoryDto);

    Long deleteComment(Long eventId, Long commentId);

    CommentDto getCommentById(Long id, boolean includeChildren);

    List<CommentDto> getCommentsByEvent(Long eventId, boolean includeChildren);
}
