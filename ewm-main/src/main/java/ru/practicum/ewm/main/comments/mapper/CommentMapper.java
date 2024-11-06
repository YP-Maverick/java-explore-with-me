package ru.practicum.ewm.main.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.comments.dto.CommentDto;
import ru.practicum.ewm.main.comments.dto.CreateCommentDto;
import ru.practicum.ewm.main.comments.model.Comment;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "timestamp", source = "time")  // Используем time в качестве timestamp
    Comment toComment(CreateCommentDto createCommentDto, LocalDateTime time);

    @Mapping(target = "timestamp", source = "time")  // Используем time в качестве timestamp
    Comment toComment(CommentDto commentDto, LocalDateTime time);
}