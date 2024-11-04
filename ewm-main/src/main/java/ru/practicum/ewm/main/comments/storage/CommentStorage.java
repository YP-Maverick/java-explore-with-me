package ru.practicum.ewm.main.comments.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.comments.model.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findByEvent_Id(Long eventId); // Поиск комментариев по идентификатору события

    List<Comment> findByParentComment(Comment parentComment); // Поиск дочерних комментариев по родительскому комментарию

    List<Comment> findByEvent_IdAndParentCommentIsNull(Long eventId);
}
