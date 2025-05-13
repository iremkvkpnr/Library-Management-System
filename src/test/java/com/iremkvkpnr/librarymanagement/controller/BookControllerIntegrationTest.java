package com.iremkvkpnr.librarymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iremkvkpnr.librarymanagement.model.dto.request.BookRequest;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublicationDate(LocalDate.now());
        testBook.setGenre(Book.Genre.FICTION);
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(5);
        bookRepository.save(testBook);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void addBook_ShouldReturnCreatedBook() throws Exception {
        BookRequest bookRequest = new BookRequest("New Book", "New Author", "9876543210", "FICTION", 3, LocalDate.now());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("New Author"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getBookDetails_ShouldReturnBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void updateBook_ShouldReturnUpdatedBook() throws Exception {
        BookRequest updateRequest = new BookRequest("Updated Book", "Updated Author", "1234567890", "FICTION", 5, LocalDate.now());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void deleteBook_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", testBook.getId()))
                .andExpect(status().isNoContent());
    }
} 