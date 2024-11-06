package ru.practicum.ewm.main.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.comments.dto.CommentDto;
import ru.practicum.ewm.main.comments.dto.CreateCommentDto;
import ru.practicum.ewm.main.comments.mapper.CommentMapper;
import ru.practicum.ewm.main.comments.model.Comment;
import ru.practicum.ewm.main.comments.storage.CommentStorage;
import ru.practicum.ewm.main.events.storage.EventStorage;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentStorage commentStorage;
    private final CommentMapper commentMapper;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Override
    public CommentDto createComment(Long eventId, CreateCommentDto commentDto) {
        eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("Forbidden. Non-existent event with id {} is trying to interact with the event.", commentDto.getCommentatorId());
            return new ForbiddenException("You haven't access event.");
        });

        userStorage.findById(commentDto.getCommentatorId()).orElseThrow(() -> {
            log.error("Forbidden. Non-existent user with id {} is trying to interact with the event.", commentDto.getCommentatorId());
            return new ForbiddenException("You haven't access. Please, log in.");
        });

        Comment comment = commentStorage.save(commentMapper.toComment(commentDto, LocalDateTime.now()));
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long eventId, CommentDto commentDto) {

        log.info("Request to update comment with id {}.", commentDto.getCommentId());
        Comment oldComment = commentStorage.findById(commentDto.getCommentId()).orElseThrow(
                () -> new NotFoundException("Comment with id " + commentDto.getCommentId() + " not found.")
        );


        try {
            Comment savedComment = commentStorage.saveAndFlush(commentMapper.toComment(commentDto, oldComment.getTimestamp()));
            return commentMapper.toCommentDto(savedComment);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Attempt to update comment with existing id {}.", commentDto.getCommentId());
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public Long deleteComment(Long eventId, Long commentId) {
        log.info("Request to delete category with id {}.", commentId);

        getCommentById(commentId, false);

        commentStorage.deleteById(commentId);
        return commentId;
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentById(Long id, boolean includeChildren) {
        log.info("Request to find comment with id {}. Include children: {}", id, includeChildren);

        Comment comment = commentStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. Comment with id {} does not exist.", id);
            return new NotFoundException(String.format("Comment with id %d does not exist.", id));
        });

        if (includeChildren) {
            List<Comment> childComments = commentStorage.findByParentComment(comment);
            comment.setChildren(childComments);
            childComments.forEach(this::populateChildComments);
        }

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId, boolean includeChildren) {
        List<Comment> allComments = commentStorage.findAllByEvent_Id(eventId);

        if (includeChildren) {
            Map<Long, Comment> commentMap = allComments.stream()
                    .collect(Collectors.toMap(Comment::getCommentId, comment -> comment));

            allComments.forEach(comment -> {
                Comment parent = commentMap.get(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
                if (parent != null) {
                    parent.getChildren().add(comment);
                }
            });
        }

        return allComments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }



    private void populateChildComments(Comment comment) {
        List<Comment> childComments = commentStorage.findByParentComment(comment);
        comment.setChildren(childComments);
        childComments.forEach(this::populateChildComments);
    }
}
