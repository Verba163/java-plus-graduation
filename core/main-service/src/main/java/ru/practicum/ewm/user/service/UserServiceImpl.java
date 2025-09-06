package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.params.UserQueryParams;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        if (userRepository.existsByEmail(newUserRequestDto.getEmail())) {
            throw new DataIntegrityViolationException(String.format("Email must be unique: %s", newUserRequestDto.getEmail()));
        }

        User user = UserMapper.toUserEntity(newUserRequestDto);
        log.debug("Received POST request to create user: {}", newUserRequestDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers(UserQueryParams params) {

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Page<User> userPage;

        if (params.getIds() != null && !params.getIds().isEmpty()) {
            userPage = userRepository.findAllByIdIn(params.getIds(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
        userRepository.deleteById(userId);
    }
}
