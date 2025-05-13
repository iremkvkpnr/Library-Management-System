package com.iremkvkpnr.librarymanagement.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookEntityTest {

    @Test
    @DisplayName("Builder should create book with all fields")
    void builderShouldCreateBookWithAllFields() {
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .publicationDate(LocalDate.now())
                .genre(Book.Genre.FICTION)
                .availableCopies(5)
                .totalCopies(5)
                .build();

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(LocalDate.now(), book.getPublicationDate());
        assertEquals(Book.Genre.FICTION, book.getGenre());
        assertEquals(5, book.getAvailableCopies());
        assertEquals(5, book.getTotalCopies());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty book")
    void noArgsConstructorShouldCreateEmptyBook() {
        Book book = new Book();
        assertNotNull(book);
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getIsbn());
        assertNull(book.getGenre());
        assertEquals(0, book.getAvailableCopies());
        assertEquals(0, book.getTotalCopies());
    }

    @Test
    @DisplayName("PrePersist should set createdAt")
    void prePersistShouldSetCreatedAt() throws Exception {
        Book book = new Book();
        java.lang.reflect.Method method = Book.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(book);
        assertNotNull(book.getCreatedAt());
        assertTrue(book.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @ParameterizedTest
    @EnumSource(Book.Genre.class)
    @DisplayName("All genres should be valid")
    void allGenresShouldBeValid(Book.Genre genre) {
        Book book = new Book();
        book.setGenre(genre);
        assertEquals(genre, book.getGenre());
    }

    @Test
    @DisplayName("Book should handle borrowings relationship")
    void bookShouldHandleBorrowingsRelationship() {
        Book book = new Book();
        List<Borrowing> borrowings = new ArrayList<>();
        Borrowing borrowing = new Borrowing();
        borrowings.add(borrowing);
        
        book.setBorrowings(borrowings);
        assertEquals(1, book.getBorrowings().size());
        assertEquals(borrowing, book.getBorrowings().get(0));
    }

    @Test
    @DisplayName("Book should handle available copies correctly")
    void bookShouldHandleAvailableCopies() {
        Book book = new Book();
        book.setTotalCopies(10);
        book.setAvailableCopies(5);
        
        assertEquals(10, book.getTotalCopies());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    @DisplayName("Book should handle equals and hashCode")
    void bookShouldHandleEqualsAndHashCode() {
        Book book1 = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("1234567890")
                .build();
                
        Book book2 = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("1234567890")
                .build();
                
        Book book3 = Book.builder()
                .id(2L)
                .title("Different Book")
                .isbn("0987654321")
                .build();
        
        assertEquals(book1, book2);
        assertNotEquals(book1, book3);
        assertEquals(book1.hashCode(), book2.hashCode());
        assertNotEquals(book1.hashCode(), book3.hashCode());
    }

    @Test
    @DisplayName("Book toString should contain all fields")
    void bookToStringShouldContainAllFields() {
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .build();
                
        String toString = book.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title=Test Book"));
        assertTrue(toString.contains("author=Test Author"));
        assertTrue(toString.contains("isbn=1234567890"));
    }

    @Test
    @DisplayName("Book should handle null borrowings")
    void bookShouldHandleNullBorrowings() {
        Book book = new Book();
        book.setBorrowings(null);
        assertNull(book.getBorrowings());
    }

    @Test
    @DisplayName("Book should handle null genre")
    void bookShouldHandleNullGenre() {
        Book book = new Book();
        book.setGenre(null);
        assertNull(book.getGenre());
    }

    @Test
    @DisplayName("Book should handle null publication date")
    void bookShouldHandleNullPublicationDate() {
        Book book = new Book();
        book.setPublicationDate(null);
        assertNull(book.getPublicationDate());
    }

    @Test
    @DisplayName("Book should handle negative copies")
    void bookShouldHandleNegativeCopies() {
        Book book = new Book();
        book.setTotalCopies(-1);
        book.setAvailableCopies(-2);
        assertEquals(-1, book.getTotalCopies());
        assertEquals(-2, book.getAvailableCopies());
    }

    @Test
    @DisplayName("Book should handle null title")
    void bookShouldHandleNullTitle() {
        Book book = new Book();
        book.setTitle(null);
        assertNull(book.getTitle());
    }

    @Test
    @DisplayName("Book should handle null author")
    void bookShouldHandleNullAuthor() {
        Book book = new Book();
        book.setAuthor(null);
        assertNull(book.getAuthor());
    }

    @Test
    @DisplayName("Book should handle null ISBN")
    void bookShouldHandleNullISBN() {
        Book book = new Book();
        book.setIsbn(null);
        assertNull(book.getIsbn());
    }

    @Test
    @DisplayName("Book should handle empty borrowings list")
    void bookShouldHandleEmptyBorrowingsList() {
        Book book = new Book();
        book.setBorrowings(new ArrayList<>());
        assertNotNull(book.getBorrowings());
        assertTrue(book.getBorrowings().isEmpty());
    }

    @Test
    @DisplayName("Book should handle equals with null fields")
    void bookShouldHandleEqualsWithNullFields() {
        Book book1 = new Book();
        Book book2 = new Book();
        
        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
        
        book1.setTitle("Test");
        assertNotEquals(book1, book2);
        assertNotEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    @DisplayName("Book should handle toString with null fields")
    void bookShouldHandleToStringWithNullFields() {
        Book book = new Book();
        String toString = book.toString();
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("title=null"));
        assertTrue(toString.contains("author=null"));
        assertTrue(toString.contains("isbn=null"));
    }

    @Test
    @DisplayName("Book should handle equals with different types")
    void bookShouldHandleEqualsWithDifferentTypes() {
        Book book = new Book();
        assertNotEquals(book, "Not a book");
        assertNotEquals(book, null);
    }

    @Test
    @DisplayName("Book should handle equals with same instance")
    void bookShouldHandleEqualsWithSameInstance() {
        Book book = new Book();
        assertEquals(book, book);
    }

    @Test
    @DisplayName("Book should handle equals with different IDs")
    void bookShouldHandleEqualsWithDifferentIds() {
        Book book1 = Book.builder().id(1L).build();
        Book book2 = Book.builder().id(2L).build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different titles")
    void bookShouldHandleEqualsWithDifferentTitles() {
        Book book1 = Book.builder().title("Title 1").build();
        Book book2 = Book.builder().title("Title 2").build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different authors")
    void bookShouldHandleEqualsWithDifferentAuthors() {
        Book book1 = Book.builder().author("Author 1").build();
        Book book2 = Book.builder().author("Author 2").build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different ISBNs")
    void bookShouldHandleEqualsWithDifferentISBNs() {
        Book book1 = Book.builder().isbn("ISBN1").build();
        Book book2 = Book.builder().isbn("ISBN2").build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different genres")
    void bookShouldHandleEqualsWithDifferentGenres() {
        Book book1 = Book.builder().genre(Book.Genre.FICTION).build();
        Book book2 = Book.builder().genre(Book.Genre.NON_FICTION).build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different publication dates")
    void bookShouldHandleEqualsWithDifferentPublicationDates() {
        Book book1 = Book.builder().publicationDate(LocalDate.now()).build();
        Book book2 = Book.builder().publicationDate(LocalDate.now().plusDays(1)).build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different copies")
    void bookShouldHandleEqualsWithDifferentCopies() {
        Book book1 = Book.builder().totalCopies(5).availableCopies(3).build();
        Book book2 = Book.builder().totalCopies(5).availableCopies(4).build();
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different borrowings")
    void bookShouldHandleEqualsWithDifferentBorrowings() {
        Book book1 = new Book();
        Book book2 = new Book();
        List<Borrowing> borrowings1 = new ArrayList<>();
        List<Borrowing> borrowings2 = new ArrayList<>();
        borrowings1.add(new Borrowing());
        book1.setBorrowings(borrowings1);
        book2.setBorrowings(borrowings2);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with different created at")
    void bookShouldHandleEqualsWithDifferentCreatedAt() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setCreatedAt(LocalDateTime.now());
        book2.setCreatedAt(LocalDateTime.now().plusHours(1));
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with all fields different")
    void bookShouldHandleEqualsWithAllFieldsDifferent() {
        Book book1 = Book.builder()
                .id(1L)
                .title("Title 1")
                .author("Author 1")
                .isbn("ISBN1")
                .genre(Book.Genre.FICTION)
                .publicationDate(LocalDate.now())
                .totalCopies(5)
                .availableCopies(3)
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("Title 2")
                .author("Author 2")
                .isbn("ISBN2")
                .genre(Book.Genre.NON_FICTION)
                .publicationDate(LocalDate.now().plusDays(1))
                .totalCopies(10)
                .availableCopies(8)
                .build();

        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null borrowings in both objects")
    void bookShouldHandleEqualsWithNullBorrowingsInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setBorrowings(null);
        book2.setBorrowings(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null borrowings in one object")
    void bookShouldHandleEqualsWithNullBorrowingsInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setBorrowings(new ArrayList<>());
        book2.setBorrowings(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null genre in both objects")
    void bookShouldHandleEqualsWithNullGenreInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setGenre(null);
        book2.setGenre(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null genre in one object")
    void bookShouldHandleEqualsWithNullGenreInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setGenre(Book.Genre.FICTION);
        book2.setGenre(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null publication date in both objects")
    void bookShouldHandleEqualsWithNullPublicationDateInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setPublicationDate(null);
        book2.setPublicationDate(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null publication date in one object")
    void bookShouldHandleEqualsWithNullPublicationDateInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setPublicationDate(LocalDate.now());
        book2.setPublicationDate(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null title in both objects")
    void bookShouldHandleEqualsWithNullTitleInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setTitle(null);
        book2.setTitle(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null title in one object")
    void bookShouldHandleEqualsWithNullTitleInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setTitle("Test Title");
        book2.setTitle(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null author in both objects")
    void bookShouldHandleEqualsWithNullAuthorInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setAuthor(null);
        book2.setAuthor(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null author in one object")
    void bookShouldHandleEqualsWithNullAuthorInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setAuthor("Test Author");
        book2.setAuthor(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null ISBN in both objects")
    void bookShouldHandleEqualsWithNullISBNInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setIsbn(null);
        book2.setIsbn(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null ISBN in one object")
    void bookShouldHandleEqualsWithNullISBNInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setIsbn("1234567890");
        book2.setIsbn(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null createdAt in both objects")
    void bookShouldHandleEqualsWithNullCreatedAtInBothObjects() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setCreatedAt(null);
        book2.setCreatedAt(null);
        assertEquals(book1, book2);
    }

    @Test
    @DisplayName("Book should handle equals with null createdAt in one object")
    void bookShouldHandleEqualsWithNullCreatedAtInOneObject() {
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setCreatedAt(LocalDateTime.now());
        book2.setCreatedAt(null);
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book.Genre enum valueOf ve toString testleri")
    void bookGenreEnumValueOfAndToString() {
        for (Book.Genre genre : Book.Genre.values()) {
            assertEquals(genre, Book.Genre.valueOf(genre.name()));
            assertNotNull(genre.toString());
        }
    }

    @Test
    @DisplayName("Book equals should return false for subclass instance")
    void bookEqualsShouldReturnFalseForSubclass() {
        Book book = Book.builder().id(1L).build();
        Book subclassBook = new Book(1L, "Title", "Author", "ISBN", null, Book.Genre.FICTION, 1, 1, null, null) {};
        assertNotEquals(book, subclassBook);
    }

    @Test
    @DisplayName("Does modifying the list assigned with setBorrowings affect the entity's internal list?")
    void bookSetBorrowingsDefensiveCopyTest() {
        Book book = new Book();
        List<Borrowing> borrowings = new ArrayList<>();
        book.setBorrowings(borrowings);
        assertEquals(0, book.getBorrowings().size());
        borrowings.add(new Borrowing());
        assertEquals(1, book.getBorrowings().size());
    }

    @Test
    @DisplayName("onCreate should not overwrite createdAt if already set")
    void onCreateShouldNotOverwriteCreatedAtIfAlreadySet() throws Exception {
        Book book = new Book();
        LocalDateTime initial = LocalDateTime.of(2020, 1, 1, 0, 0);
        book.setCreatedAt(initial);
        java.lang.reflect.Method method = Book.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(book);
        assertEquals(initial, book.getCreatedAt());
    }

    @Test
    @DisplayName("onCreate can be called multiple times without error")
    void onCreateCanBeCalledMultipleTimes() throws Exception {
        Book book = new Book();
        java.lang.reflect.Method method = Book.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(book);
        LocalDateTime first = book.getCreatedAt();
        Thread.sleep(10);
        method.invoke(book);
        assertEquals(first, book.getCreatedAt());
    }

    @Test
    @DisplayName("Book should handle setting borrowings to the same list reference")
    void bookShouldHandleSettingBorrowingsToSameReference() {
        Book book = new Book();
        List<Borrowing> borrowings = new ArrayList<>();
        book.setBorrowings(borrowings);
        book.setBorrowings(borrowings); // set again with same reference
        assertSame(borrowings, book.getBorrowings());
    }

    @Test
    @DisplayName("Book should handle setting availableCopies to the same value")
    void bookShouldHandleSettingAvailableCopiesToSameValue() {
        Book book = new Book();
        book.setAvailableCopies(5);
        book.setAvailableCopies(5);
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    @DisplayName("Book.Genre fromString should throw exception for invalid value")
    void bookGenreFromStringShouldThrowExceptionForInvalidValue() {
        assertThrows(com.iremkvkpnr.librarymanagement.model.exception.BookValidationException.class, () -> {
            Book.Genre.fromString("INVALID_GENRE");
        });
    }

    @Test
    @DisplayName("Book should handle null publicationDate and genre together")
    void bookShouldHandleNullPublicationDateAndGenreTogether() {
        Book book = new Book();
        book.setPublicationDate(null);
        book.setGenre(null);
        assertNull(book.getPublicationDate());
        assertNull(book.getGenre());
    }

    @Test
    @DisplayName("Book builder ile aynı referans borrowings set edilirse değişmemeli")
    void bookShouldNotChangeBorrowingsIfSameReference() {
        Book book = new Book();
        List<Borrowing> borrowings = new ArrayList<>();
        book.setBorrowings(borrowings);
        book.setBorrowings(borrowings);
        assertSame(borrowings, book.getBorrowings());
    }

    @Test
    @DisplayName("Book builder ile aynı value availableCopies set edilirse değişmemeli")
    void bookShouldNotChangeAvailableCopiesIfSameValue() {
        Book book = new Book();
        book.setAvailableCopies(5);
        book.setAvailableCopies(5);
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    @DisplayName("Book.Genre valueOf null ve boş string için exception fırlatmalı")
    void bookGenreValueOfShouldThrowExceptionForNullOrEmpty() {
        assertThrows(NullPointerException.class, () -> Book.Genre.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> Book.Genre.valueOf(""));
    }

    @Test
    @DisplayName("Book publicationDate ve genre aynı anda null olmalı")
    void bookShouldAllowBothPublicationDateAndGenreNull() {
        Book book = new Book();
        book.setPublicationDate(null);
        book.setGenre(null);
        assertNull(book.getPublicationDate());
        assertNull(book.getGenre());
    }

    @Test
    @DisplayName("Book equals: tüm alanlar null ve farklı kombinasyonlar")
    void bookEqualsShouldHandleAllNullAndDifferentCombinations() {
        Book b1 = new Book();
        Book b2 = new Book();
        assertEquals(b1, b2);
        b1.setTitle("Kitap");
        assertNotEquals(b1, b2);
        b2.setTitle("Kitap");
        assertEquals(b1, b2);
        b1.setAuthor("Yazar");
        assertNotEquals(b1, b2);
        b2.setAuthor("Yazar");
        assertEquals(b1, b2);
        b1.setIsbn("123");
        assertNotEquals(b1, b2);
        b2.setIsbn("123");
        assertEquals(b1, b2);
        b1.setGenre(Book.Genre.FICTION);
        assertNotEquals(b1, b2);
        b2.setGenre(Book.Genre.FICTION);
        assertEquals(b1, b2);
        b1.setPublicationDate(java.time.LocalDate.now());
        assertNotEquals(b1, b2);
        b2.setPublicationDate(java.time.LocalDate.now());
        assertEquals(b1, b2);
        b1.setAvailableCopies(5);
        assertNotEquals(b1, b2);
        b2.setAvailableCopies(5);
        assertEquals(b1, b2);
        b1.setTotalCopies(10);
        assertNotEquals(b1, b2);
        b2.setTotalCopies(10);
        assertEquals(b1, b2);
    }

    @Test
    @DisplayName("Book borrowings listesi referans olarak aynı olmalı (defensive copy yok)")
    void bookBorrowingsShouldBeReferenceEqual() {
        Book book = new Book();
        List<Borrowing> list = new ArrayList<>();
        book.setBorrowings(list);
        list.add(new Borrowing());
        assertEquals(list.size(), book.getBorrowings().size());
    }

    @Test
    @DisplayName("Book equals: biri null biri dolu alan kombinasyonları")
    void bookEqualsShouldHandleOneNullOneNotNullFields() {
        Book b1 = new Book();
        Book b2 = new Book();
        b1.setTitle("Kitap");
        b2.setTitle(null);
        assertNotEquals(b1, b2);
        b1.setTitle(null);
        b2.setTitle("Kitap");
        assertNotEquals(b1, b2);
        b1.setTitle(null);
        b2.setTitle(null);
        assertEquals(b1, b2);
    }

    @Test
    @DisplayName("Book equals: farklı tip ve aynı referans")
    void bookEqualsShouldHandleDifferentTypeAndSameReference() {
        Book b1 = new Book();
        assertNotEquals(b1, "string");
        assertEquals(b1, b1);
    }

    @Test
    @DisplayName("Book hashCode: tüm alanlar null ve bazıları null")
    void bookHashCodeShouldHandleNullFields() {
        Book b1 = new Book();
        Book b2 = new Book();
        assertEquals(b1.hashCode(), b2.hashCode());
        b1.setTitle("Kitap");
        assertNotEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        Book book = createSampleBook();
        assertNotEquals(book, null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithDifferentType() {
        Book book = createSampleBook();
        assertNotEquals(book, "farkli tip");
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsAreNull() {
        Book book1 = new Book();
        Book book2 = new Book();
        assertEquals(book1, book2);
    }

    @Test
    void equals_shouldReturnFalse_whenOnlyOneFieldIsDifferent() {
        Book book1 = createSampleBook();
        Book book2 = createSampleBook();
        book2.setTitle("Farklı Başlık");
        assertNotEquals(book1, book2);
    }

    @Test
    void hashCode_shouldBeEqual_whenAllFieldsAreNull() {
        Book book1 = new Book();
        Book book2 = new Book();
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenFieldsAreDifferent() {
        Book book1 = createSampleBook();
        Book book2 = createSampleBook();
        book2.setIsbn("farkli-isbn");
        assertNotEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void hashCode_shouldBeEqual_whenFieldsAreEqual() {
        Book book1 = createSampleBook();
        Book book2 = createSampleBook();
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    // Yardımcı metot: Testlerde kullanılmak üzere örnek bir Book nesnesi döndürür
    private Book createSampleBook() {
        return Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .genre(Book.Genre.FICTION)
                .availableCopies(5)
                .totalCopies(5)
                .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
    }
} 