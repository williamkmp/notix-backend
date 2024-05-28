package com.william.notix.services;

import com.william.notix.entities.Finding;
import com.william.notix.repositories.FindingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindingService {

    private final FindingRepository findingRepository;

    public Optional<Finding> create() {
        //TODO: implement this
        return Optional.empty();
    }

    public Optional<Finding> update(Long id, Finding finding) {
        //TODO: implement this
        return Optional.empty();
    }

    public Optional<Finding> delete(Finding finding) {
        //TODO: implement this
        return Optional.empty();
    }
}
