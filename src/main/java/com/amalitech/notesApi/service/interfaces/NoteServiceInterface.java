package com.amalitech.notesApi.service.interfaces;

import com.amalitech.notesApi.dto.request.NoteRequest;
import com.amalitech.notesApi.models.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import com.amalitech.notesApi.models.Category;


public interface  NoteServiceInterface {
    Note createNote(NoteRequest note);
    List<Note> getAllNotes();
    Page<Note> getNotes(String keyword, Pageable pageable);
    Note getNoteById(Long id);
    Note updateNote(Long id, NoteRequest note);
    void deleteNote(Long id);
    // In NoteServiceInterface.java

    List<Category> getAllCategories();
}
