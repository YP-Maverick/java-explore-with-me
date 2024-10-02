package ru.practicum.ewm.main.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.user.dto.CreateUserDto;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.service.UserService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsersByIds(
            @RequestParam(required = false) List<Long> ids,
           @RequestParam(defaultValue = "0")
           @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
           int from,
           @RequestParam(defaultValue = "10")
           @Positive(message = "Parameter 'size' should be positive")
           int size
    ) {
        int page = from / size;
        return userService.getUsers(ids, PageRequest.of(page, size));
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping()
    public UserDto createUser(
            @Valid @RequestBody CreateUserDto createUserDto
    ) {
        return userService.createUser(createUserDto);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping({"/{userId}", "/{userId}/"})
    public Long deleteUser(
            @PathVariable
           @Positive(message = "User's id should be positive")
           Long userId
    ) {
        return userService.deleteUser(userId);
    }


}
