package com.example.securenotes.controller;

import com.example.securenotes.dto.NotesDto;
import com.example.securenotes.service.SecureNotesService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class SecureNotesController {

    @Value("${app.token}")
    private String TOKEN;
    private final SecureNotesService secureNoteService;

    @Autowired
    public SecureNotesController(SecureNotesService secureNoteService) {
        this.secureNoteService = secureNoteService;
    }

    private void authenticate(HttpServletRequest request) {
        String req = request.getHeader("Authorization");
        if (req == null || !req.equals(TOKEN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request");
        }
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public NotesDto createNote(@Valid @RequestBody NotesDto reqNote,
                               HttpServletRequest request) throws Exception {
        authenticate(request);
        String title = reqNote.getTitle();
        String content = reqNote.getContent();
        NotesDto noteDto = secureNoteService.createNote(title, content);
        return noteDto;
    }

    @GetMapping("/{id}")
    public NotesDto getNoteById(@PathVariable Long id, HttpServletRequest request) {
        authenticate(request);
        return secureNoteService.getNoteById(id);
    }

    @GetMapping()
    public List<NotesDto> getAllNotes(HttpServletRequest request) {
        authenticate(request);
        return secureNoteService.getAllNotes();
    }

    @PutMapping("/{id}")
    public NotesDto updateNote(@PathVariable Long id, @RequestBody NotesDto req,
                               HttpServletRequest http) throws Exception {
        authenticate(http);
        return secureNoteService.updateNote(id, req);
    }


    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id, HttpServletRequest request) {
        authenticate(request);
        secureNoteService.deleteNote(id);
    }

}
