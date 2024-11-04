package ru.practicum.ewm.main.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.main.categories.controller.AdminCategoryController;
import ru.practicum.ewm.main.categories.controller.PublicCategoryController;
import ru.practicum.ewm.main.comments.controller.AdminCommentController;
import ru.practicum.ewm.main.comments.controller.PublicCommentController;
import ru.practicum.ewm.main.events.controller.AdminEventController;
import ru.practicum.ewm.main.events.controller.PrivateEventController;
import ru.practicum.ewm.main.events.controller.PublicEventController;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.NotValidException;
import ru.practicum.ewm.main.compilation.controller.AdminCompilationController;
import ru.practicum.ewm.main.request.controller.PrivateRequestController;
import ru.practicum.ewm.main.compilation.controller.PublicCompilationController;
import ru.practicum.ewm.main.user.controller.AdminUserController;
import ru.practicum.ewm.stats.dto.Formatter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        AdminUserController.class,
        AdminCategoryController.class,
        PublicCategoryController.class,
        PrivateEventController.class,
        AdminEventController.class,
        PublicEventController.class,
        PrivateRequestController.class,
        PublicCompilationController.class,
        AdminCompilationController.class,
        AdminCommentController.class,
        PublicCommentController.class
})
public class EwmExceptionHandler {

    private void log(Throwable e) {
        log.error("Исключение {}: {}", e, e.getMessage());
    }

    private Map<String, String> createMap(String status, String reason, String message) {
        return Map.of("status", status,
                "reason", reason,
                "message", message,
                "timestamp", LocalDateTime.now().format(Formatter.getFormatter()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final MethodArgumentNotValidException e) {
        log(e);
        List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return createMap("BAD_REQUEST", "Incorrectly made request", details.getFirst());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            NotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final Exception e) {
        log(e);
        return createMap("BAD_REQUEST", "Incorrectly made request", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log(e);
        return createMap("NOT_FOUND", "The required object was not found", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        log(e);
        return createMap("CONFLICT", "Integrity constraint has been violated", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAccess(final ForbiddenException e) {
        log(e);
        return createMap("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExc(final Exception e) {
        log(e);
        return createMap("INTERNAL_SERVER_ERROR", "Unexpected error", e.getMessage());
    }
}
