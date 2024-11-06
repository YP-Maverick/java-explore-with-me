package ru.practicum.ewm.main.comments.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.comments.model.Comment;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId")
    List<Comment> findAllByEvent_Id(@Param("eventId") Long eventId);

    List<Comment> findByParentComment(Comment parentComment);
}
