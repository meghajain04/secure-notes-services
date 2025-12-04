package com.example.securenotes;

import com.example.securenotes.dto.NotesDto;
import com.example.securenotes.model.Notes;
import com.example.securenotes.repository.SecureNotesRepository;
import com.example.securenotes.util.EncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecureNoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${app.token}")
    private String token;

    @Autowired
    private ObjectMapper objectMapper;
    private NotesDto createdNote;

    @Autowired
    private EncryptionUtil cryptoService;

    @Autowired
    private SecureNotesRepository notesRepository;

    /**
     * Create Dto for all API
     * @throws Exception
     */
    @BeforeEach
    public void setup() throws Exception {
        NotesDto request = new NotesDto();
        request.setTitle("Test Note");
        request.setContent("Test content");

        String response = mockMvc.perform(post("/notes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        createdNote = objectMapper.readValue(response, NotesDto.class);
    }

    /**
     * Create success
     * @throws Exception
     */
    @Test
    public void testCreateNote_success() throws Exception {
        NotesDto request = new NotesDto();
        request.setTitle("New Note");
        request.setContent("New content");

        mockMvc.perform(post("/notes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    /**
     * Create for Unauthorized with no Token
     * @throws Exception
     */
    @Test
    void testCreateNote_unauthorized() throws Exception {
        NotesDto request = new NotesDto();
        request.setTitle("Test");
        request.setContent("Test content");

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Create with empty title
     * @throws Exception
     */
    @Test
    void testCreateNote_emptyTitle_shouldReturnBadRequest() throws Exception {
        NotesDto request = new NotesDto();
        request.setTitle("");  // Empty title
        request.setContent("Some content");

        mockMvc.perform(post("/notes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Create with null content
     * @throws Exception
     */
    @Test
    void testCreateNote_emptyContent_shouldReturnBadRequest() throws Exception {
        NotesDto request = new NotesDto();
        request.setTitle("test title ");  // Empty title
        request.setContent(null);

        mockMvc.perform(post("/notes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Get Notes with Id
     * @throws Exception
     */
    @Test
    void testGetNoteById_success() throws Exception {
        mockMvc.perform(get("/notes/" + createdNote.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdNote.getId()))
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(status().isOk());
    }

    /**
     * Get Notes with no Id found
     * @throws Exception
     */
    @Test
    void testGetNoteById_notFound() throws Exception {
        mockMvc.perform(get("/notes/6")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    /**
     * Get All Notes
     * @throws Exception
     */
    @Test
    void testGetAllNotes_success() throws Exception {
        mockMvc.perform(get("/notes")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Update API Success
     * @throws Exception
     */
    @Test
    void testUpdateNote_success() throws Exception {
        NotesDto updateRequest = new NotesDto();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");

        mockMvc.perform(put("/notes/" + createdNote.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    /**
     * Update API for Id not found
     * @throws Exception
     */
    @Test
    void testUpdateNote_notFound() throws Exception {
        NotesDto updateRequest = new NotesDto();
        updateRequest.setTitle("Does Not Exist");
        updateRequest.setContent("Does Not Exist");

        mockMvc.perform(put("/notes/8")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    /**
     * Delete API success
     * @throws Exception
     */
    @Test
    void testDeleteNote_success() throws Exception {
        mockMvc.perform(delete("/notes/" + createdNote.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    /**
     * Delete API for Unauthorized
     * @throws Exception
     */
    @Test
    void testDeleteNote_unauthorized() throws Exception {
        mockMvc.perform(delete("/notes/" + createdNote.getId())
                        .header("Authorization","1234"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * DB store encrypted values
     * @throws Exception
     */
    @Test
    void testContentIsEncryptedInDatabase() throws Exception {
        // Create a note
        NotesDto request = new NotesDto();
        request.setTitle("Encrypt Test");
        request.setContent("Secret message");

        String response = mockMvc.perform(post("/notes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        NotesDto created = objectMapper.readValue(response, NotesDto.class);

        Notes noteInDb = notesRepository.findById(created.getId()).orElseThrow();

        System.out.println("Stored DB content: " + noteInDb.getContent());

        assertNotEquals("Secret message", noteInDb.getContent());
        assertEquals("Secret message", cryptoService.decrypt(noteInDb.getContent()));
    }

}
