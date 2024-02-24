package com.william.notix.services;

import com.william.notix.entities.File;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.UserRepository;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private String FILE_BASE_PATH = "http://localhost:5050/file/";
    private final UserRepository userRepository;

    /**
     * get user image url
     *
     * @param userId {@link Long} user id
     * @return {@link Optional}<{@link String}> image url, empty if not found
     * @throws UserNotFoundException if user not found
     */
    public Optional<String> getUserImageUrl(@NonNull Long userId)
        throws UserNotFoundException {
        User user = userRepository
            .findById(userId)
            .orElseThrow(UserNotFoundException::new);
        File userImage = user.getImage();
        if (userImage == null) {
            return Optional.empty();
        }
        return Optional.of(FILE_BASE_PATH + userImage.getId().toString());
    }
}
