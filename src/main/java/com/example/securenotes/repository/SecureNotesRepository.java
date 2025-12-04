package com.example.securenotes.repository;

import com.example.securenotes.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureNotesRepository extends JpaRepository<Notes, Long> {
}
