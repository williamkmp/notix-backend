package com.william.notix.repositories;

import com.william.notix.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionReplyRepository extends JpaRepository<Reply, Long> {}
