package ru.practicum.ewm.main.comments.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateCommentDto {

    Long eventId;

    Long commentatorId;

    @Column(nullable = false, length = 1500)
    String content;
}
