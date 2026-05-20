package com.amalitech.notesApi.controller;

import com.amalitech.notesApi.dto.request.NoteRequest;
import com.amalitech.notesApi.models.Note;
import com.amalitech.notesApi.models.Tag;
import com.amalitech.notesApi.service.NoteService;
import com.amalitech.notesApi.service.interfaces.NoteServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class NoteController {
    private final NoteServiceInterface noteService;

    @GetMapping
    public String getNotes(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        Page<Note> notePage = noteService.getNotes(keyword, PageRequest.of(page, size, Sort.by("createdAt").descending()));

        model.addAttribute("notes", notePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notePage.getTotalPages());
        model.addAttribute("totalItems", notePage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "notes/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("noteRequest", new NoteRequest());
        model.addAttribute("categories", noteService.getAllCategories());
        return "notes/create";
    }

    @PostMapping
    public String createNote(@Valid @ModelAttribute NoteRequest request) {
        noteService.createNote(request);
        return "redirect:/";
    }

    @GetMapping("/view/{id}")
    public String viewNote(@PathVariable("id") Long id, Model model) {
        model.addAttribute("note", noteService.getNoteById(id));
        return "notes/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Note note = noteService.getNoteById(id);
        NoteRequest request = new NoteRequest();
        request.setTitle(note.getTitle());
        request.setContent(note.getContent());
        if (note.getCategory() != null) {
            request.setCategoryId(note.getCategory().getId());
        }
        if (note.getTags() != null) {
            request.setTags(note.getTags().stream().map(Tag::getName).collect(Collectors.joining(", ")));
        }

        model.addAttribute("noteRequest", request);
        model.addAttribute("noteId", id);
        model.addAttribute("categories", noteService.getAllCategories());
        return "notes/edit";
    }

    @PostMapping("/update/{id}")
    public String updateNote(@PathVariable("id") Long id, @Valid @ModelAttribute NoteRequest request) {
        noteService.updateNote(id, request);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") Long id) {
        noteService.deleteNote(id);
        return "redirect:/";
    }
}