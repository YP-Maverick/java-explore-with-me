package ru.practicum.ewm.stats.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ValidationException;
import ru.practicum.ewm.stats.service.controller.StatsServiceController;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = StatsServiceController.class)
public class StatsServiceExceptionHandler {

    private void log(Throwable e) {
        log.error("Исключение {}: {}", e, e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidate(Exception e) {
        log(e);
        return Map.of("error", e.getClass().getSimpleName(),
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExc(final Exception e) {
        log(e);
        return Map.of("error", "Unexpected error",
                "errorMessage", e.getMessage());
    }
}
