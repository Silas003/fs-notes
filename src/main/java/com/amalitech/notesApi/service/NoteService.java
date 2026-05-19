package com.amalitech.notesApi.service;

import com.amalitech.notesApi.dto.request.NoteRequest;
import com.amalitech.notesApi.exceptions.InvalidNoteException;
import com.amalitech.notesApi.exceptions.NoteCreationException;
import com.amalitech.notesApi.exceptions.NoteNotFoundException;
import com.amalitech.notesApi.models.Category;
import com.amalitech.notesApi.models.Note;
import com.amalitech.notesApi.models.Tag;
import com.amalitech.notesApi.repository.CategoryRepository;
import com.amalitech.notesApi.repository.NoteRepository;
import com.amalitech.notesApi.repository.TagRepository;
import com.amalitech.notesApi.service.interfaces.NoteServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NoteService implements NoteServiceInterface {
    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Note createNote(NoteRequest request) {
        validateNoteRequest(request);

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NoteNotFoundException("Category not found"));
            note.setCategory(category);
        }

        if (request.getTags() != null && !request.getTags().isBlank()) {
            note.setTags(processTags(request.getTags()));
        }

        try {
            return noteRepository.save(note);
        } catch (Exception ex) {
            throw new NoteCreationException("Failed to create note: " + ex.getMessage());
        }
    }

    private void validateNoteRequest(NoteRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidNoteException("Title cannot be empty");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new InvalidNoteException("Content cannot be empty");
        }
    }

    private Set<Tag> processTags(String tagsString) {
        return Arrays.stream(tagsString.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public Page<Note> getNotes(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return noteRepository.searchNotes(keyword, pageable);
        }
        return noteRepository.findAll(pageable);
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    }

    @Override
    @Transactional
    public Note updateNote(Long id, NoteRequest request) {
        validateNoteRequest(request);

        Note existingNote = getNoteById(id);
        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NoteNotFoundException("Category not found"));
            existingNote.setCategory(category);
        } else {
            existingNote.setCategory(null);
        }

        if (request.getTags() != null) {
            existingNote.setTags(processTags(request.getTags()));
        } else {
            existingNote.getTags().clear();
        }

        return noteRepository.save(existingNote);
    }

    @Override
    @Transactional
    public void deleteNote(Long id) {
        Note note = getNoteById(id);
        noteRepository.delete(note);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
