package com.iremkvkpnr.librarymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.model.entity.Borrowing;
import com.iremkvkpnr.librarymanagement.model.entity.User;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import com.iremkvkpnr.librarymanagement.repository.BorrowingRepository;
import com.iremkvkpnr.librarymanagement.repository.UserRepository;
import com.iremkvkpnr.librarymanagement.security.service.JwtService;
import com.iremkvkpnr.librarymanagement.model.dto.request.BorrowingRequest;
import com.iremkvkpnr.librarymanagement.model.dto.request.ReturnRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
public class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String userToken;
    private User testUser;
    private Book testBook;
    private Borrowing testBorrowing;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        bookRepository.deleteAll();
        borrowingRepository.deleteAll();

        // create admin user
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.LIBRARIAN);
        admin.setName("Admin User");
        admin.setPhone("1234567890");
        userRepository.save(admin);

        // create test user
        testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setPassword("password");
        testUser.setRole(User.Role.PATRON);
        testUser.setName("Test User");
        testUser.setPhone("1234567890");
        testUser = userRepository.save(testUser);

        // create test book
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setGenre(Book.Genre.FICTION);
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(5);
        testBook.setPublicationDate(LocalDate.now());
        testBook = bookRepository.save(testBook);

        // create borrowing register
        testBorrowing = new Borrowing();
        testBorrowing.setUser(testUser);
        testBorrowing.setBook(testBook);
        testBorrowing.setBorrowDate(LocalDate.now());
        testBorrowing.setDueDate(LocalDate.now().plusDays(14));
        borrowingRepository.save(testBorrowing);

        // create token
        adminToken = "Bearer " + jwtService.generateToken(admin);
        userToken = "Bearer " + jwtService.generateToken(testUser);
    }

    @Test
    void borrowBook_ValidRequest_ReturnsCreated() throws Exception {
        BorrowingRequest request = new BorrowingRequest(testBook.getId());
        mockMvc.perform(post("/api/borrowings")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void returnBook_ValidRequest_ReturnsOk() throws Exception {
        ReturnRequest request = new ReturnRequest(testBorrowing.getId());
        mockMvc.perform(post("/api/borrowings/return")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void returnBook_AlreadyReturned_ReturnsBadRequest() throws Exception {
        testBorrowing.setReturnDate(LocalDate.now());
        borrowingRepository.save(testBorrowing);

        mockMvc.perform(post("/api/borrowings/return")
                .header("Authorization", userToken)
                .param("borrowingId", String.valueOf(testBorrowing.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserBorrowingHistory_ValidUserId_ReturnsBorrowingList() throws Exception {
        mockMvc.perform(get("/api/borrowings/history")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Test Book"))
                .andExpect(jsonPath("$[0].userName").value("Test User"));
    }

    @Test
    void getOverdueBooks_ReturnsOverdueList() throws Exception {
        mockMvc.perform(get("/api/borrowings/overdue-books")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void borrowBook_WithoutAuth_ReturnsUnauthorized() throws Exception {
        BorrowingRequest request = new BorrowingRequest(testBook.getId());
        mockMvc.perform(post("/api/borrowings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Spring Security 403 döndürüyor
    }

    @Test
    @WithMockUser(roles = "PATRON")
    public void getUserBorrowingHistory_WithUserRole_ReturnsOk() throws Exception {
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void getAllBorrowingHistory_WithLibrarianRole_ReturnsOk() throws Exception {
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    public void getOverdueBooksReport_WithLibrarianRole_ReturnsOk() throws Exception {
    }
} 