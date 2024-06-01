package com.william.notix.services;

import com.william.notix.repositories.FindingLogReposiory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindingLogService {

    private final FindingLogReposiory logReposiory;
}
