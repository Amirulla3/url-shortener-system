package com.shorter_url.shorter_service.repository;

import com.shorter_url.shorter_service.Entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortUrl);

    @Modifying
    @Query("""
    UPDATE Link l
    SET l.clicks = l.clicks + 1
    WHERE l.shortCode = :shortCode
    """)
    void incrementClicks(@Param("shortCode") String shortCode);

}
