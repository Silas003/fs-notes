package com.amalitech.notesApi.repository;

import com.amalitech.notesApi.models.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @EntityGraph(attributePaths = {"category", "tags"})
    Optional<Note> findById(Long id);

    @EntityGraph(attributePaths = {"category", "tags"})
    @Query(value = "SELECT n FROM Note n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT COUNT(n) FROM Note n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Note> searchNotes(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Note> findAll(Pageable pageable);
}
