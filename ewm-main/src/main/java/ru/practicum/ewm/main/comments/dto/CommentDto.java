package ru.practicum.ewm.main.comments.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.comments.model.Comment;
import ru.practicum.ewm.main.events.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class CommentDto {

    Long commentId;

    Event event;

    Long commentatorId;

    @Column(nullable = false, length = 1500)
    String content;

    LocalDateTime time;

    List<Comment> children = new ArrayList<>();
}
