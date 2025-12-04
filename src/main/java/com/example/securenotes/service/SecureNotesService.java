package com.example.securenotes.service;

import com.example.securenotes.dto.NotesDto;
import com.example.securenotes.model.Notes;
import com.example.securenotes.repository.SecureNotesRepository;

import com.example.securenotes.util.EncryptionUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SecureNotesService {

    private final SecureNotesRepository secureNoteRepository;

    private final EncryptionUtil encryptionUtil;

    public SecureNotesService(SecureNotesRepository secureNoteRepository, EncryptionUtil encryptionUtil) {
        this.secureNoteRepository = secureNoteRepository;
        this.encryptionUtil = encryptionUtil;
    }

    private NotesDto toDto(Notes note) {
        NotesDto dto = new NotesDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        try {
            dto.setContent(encryptionUtil.decrypt(note.getContent()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to decrypt note content");
        }
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }

    public NotesDto createNote(String title, String content) throws Exception {

        Notes note = new Notes();
        note.setTitle(title);
        note.setContent(encryptionUtil.encrypt(content));
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        secureNoteRepository.save(note);

        return toDto(note);
    }

    public NotesDto getNoteById(Long id) {
        Notes note = secureNoteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id));

        return toDto(note);
    }

    public List<NotesDto> getAllNotes() {
        List<Notes> note = secureNoteRepository.findAll();
        return note.stream().map(this::toDto).collect(Collectors.toList());
    }

    public NotesDto updateNote(Long id, NotesDto req) throws Exception {
        Notes note = secureNoteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id));
        note.setTitle(req.getTitle());
        note.setContent(encryptionUtil.encrypt(req.getContent()));
        note.setUpdatedAt(Instant.now());
        return toDto(secureNoteRepository.save(note));
    }

    public void deleteNote(Long id) {
        if (!secureNoteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + id);
        }
        secureNoteRepository.deleteById(id);
    }
}
