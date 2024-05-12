package com.iremkvkpnr.librarymanagement.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    @DisplayName("Builder should create user with all fields")
    void builderShouldCreateUserWithAllFields() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .phone("1234567890")
                .role(User.Role.PATRON)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getPhone());
        assertEquals(User.Role.PATRON, user.getRole());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty user")
    void noArgsConstructorShouldCreateEmptyUser() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getPhone());
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("PrePersist should set createdAt")
    void prePersistShouldSetCreatedAt() throws Exception {
        User user = new User();
        java.lang.reflect.Method method = User.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(user);
        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @ParameterizedTest
    @EnumSource(User.Role.class)
    @DisplayName("All roles should be valid")
    void allRolesShouldBeValid(User.Role role) {
        User user = new User();
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    @DisplayName("User should handle borrowings relationship")
    void userShouldHandleBorrowingsRelationship() {
        User user = new User();
        List<Borrowing> borrowings = new ArrayList<>();
        Borrowing borrowing = new Borrowing();
        borrowings.add(borrowing);
        
        user.setBorrowings(borrowings);
        assertEquals(1, user.getBorrowings().size());
        assertEquals(borrowing, user.getBorrowings().get(0));
    }

    @Test
    @DisplayName("User should handle equals and hashCode")
    void userShouldHandleEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
                
        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
                
        User user3 = User.builder()
                .id(2L)
                .email("different@example.com")
                .build();
        
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    @DisplayName("User toString should contain all fields")
    void userToStringShouldContainAllFields() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(User.Role.PATRON)
                .build();
                
        String toString = user.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test User"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("role=PATRON"));
    }

    @Test
    @DisplayName("User should handle null borrowings")
    void userShouldHandleNullBorrowings() {
        User user = new User();
        user.setBorrowings(null);
        assertNull(user.getBorrowings());
    }

    @Test
    @DisplayName("User should handle null role")
    void userShouldHandleNullRole() {
        User user = new User();
        user.setRole(null);
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("User should handle null name")
    void userShouldHandleNullName() {
        User user = new User();
        user.setName(null);
        assertNull(user.getName());
    }

    @Test
    @DisplayName("User should handle null email")
    void userShouldHandleNullEmail() {
        User user = new User();
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("User should handle null password")
    void userShouldHandleNullPassword() {
        User user = new User();
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("User should handle null phone")
    void userShouldHandleNullPhone() {
        User user = new User();
        user.setPhone(null);
        assertNull(user.getPhone());
    }

    @Test
    @DisplayName("User should handle empty borrowings list")
    void userShouldHandleEmptyBorrowingsList() {
        User user = new User();
        user.setBorrowings(new ArrayList<>());
        assertNotNull(user.getBorrowings());
        assertTrue(user.getBorrowings().isEmpty());
    }

    @Test
    @DisplayName("User should handle equals with null fields")
    void userShouldHandleEqualsWithNullFields() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        
        user1.setEmail("test@example.com");
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("User should handle toString with null fields")
    void userShouldHandleToStringWithNullFields() {
        User user = new User();
        String toString = user.toString();
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("name=null"));
        assertTrue(toString.contains("email=null"));
        assertTrue(toString.contains("role=null"));
    }

    @Test
    @DisplayName("User should handle null createdAt")
    void userShouldHandleNullCreatedAt() {
        User user = new User();
        user.setCreatedAt(null);
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("User should handle equals with different types")
    void userShouldHandleEqualsWithDifferentTypes() {
        User user = new User();
        assertNotEquals(user, "Not a user");
        assertNotEquals(user, null);
    }

    @Test
    @DisplayName("User should handle equals with same instance")
    void userShouldHandleEqualsWithSameInstance() {
        User user = new User();
        assertEquals(user, user);
    }

    @Test
    @DisplayName("User should handle equals with different IDs")
    void userShouldHandleEqualsWithDifferentIds() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different names")
    void userShouldHandleEqualsWithDifferentNames() {
        User user1 = User.builder().name("Name 1").build();
        User user2 = User.builder().name("Name 2").build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different emails")
    void userShouldHandleEqualsWithDifferentEmails() {
        User user1 = User.builder().email("email1@test.com").build();
        User user2 = User.builder().email("email2@test.com").build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different passwords")
    void userShouldHandleEqualsWithDifferentPasswords() {
        User user1 = User.builder().password("password1").build();
        User user2 = User.builder().password("password2").build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different phones")
    void userShouldHandleEqualsWithDifferentPhones() {
        User user1 = User.builder().phone("1234567890").build();
        User user2 = User.builder().phone("0987654321").build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different roles")
    void userShouldHandleEqualsWithDifferentRoles() {
        User user1 = User.builder().role(User.Role.PATRON).build();
        User user2 = User.builder().role(User.Role.LIBRARIAN).build();
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different borrowings")
    void userShouldHandleEqualsWithDifferentBorrowings() {
        User user1 = new User();
        User user2 = new User();
        List<Borrowing> borrowings1 = new ArrayList<>();
        List<Borrowing> borrowings2 = new ArrayList<>();
        borrowings1.add(new Borrowing());
        user1.setBorrowings(borrowings1);
        user2.setBorrowings(borrowings2);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with different created at")
    void userShouldHandleEqualsWithDifferentCreatedAt() {
        User user1 = new User();
        User user2 = new User();
        user1.setCreatedAt(LocalDateTime.now());
        user2.setCreatedAt(LocalDateTime.now().plusHours(1));
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with all fields different")
    void userShouldHandleEqualsWithAllFieldsDifferent() {
        User user1 = User.builder()
                .id(1L)
                .name("Name 1")
                .email("email1@test.com")
                .password("password1")
                .phone("1234567890")
                .role(User.Role.PATRON)
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Name 2")
                .email("email2@test.com")
                .password("password2")
                .phone("0987654321")
                .role(User.Role.LIBRARIAN)
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null borrowings in both objects")
    void userShouldHandleEqualsWithNullBorrowingsInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setBorrowings(null);
        user2.setBorrowings(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null borrowings in one object")
    void userShouldHandleEqualsWithNullBorrowingsInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setBorrowings(new ArrayList<>());
        user2.setBorrowings(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null role in both objects")
    void userShouldHandleEqualsWithNullRoleInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setRole(null);
        user2.setRole(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null role in one object")
    void userShouldHandleEqualsWithNullRoleInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setRole(User.Role.PATRON);
        user2.setRole(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null name in both objects")
    void userShouldHandleEqualsWithNullNameInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setName(null);
        user2.setName(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null name in one object")
    void userShouldHandleEqualsWithNullNameInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setName("Test Name");
        user2.setName(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null email in both objects")
    void userShouldHandleEqualsWithNullEmailInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setEmail(null);
        user2.setEmail(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null email in one object")
    void userShouldHandleEqualsWithNullEmailInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setEmail("test@example.com");
        user2.setEmail(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null password in both objects")
    void userShouldHandleEqualsWithNullPasswordInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setPassword(null);
        user2.setPassword(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null password in one object")
    void userShouldHandleEqualsWithNullPasswordInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setPassword("password123");
        user2.setPassword(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null phone in both objects")
    void userShouldHandleEqualsWithNullPhoneInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setPhone(null);
        user2.setPhone(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null phone in one object")
    void userShouldHandleEqualsWithNullPhoneInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setPhone("1234567890");
        user2.setPhone(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null createdAt in both objects")
    void userShouldHandleEqualsWithNullCreatedAtInBothObjects() {
        User user1 = new User();
        User user2 = new User();
        user1.setCreatedAt(null);
        user2.setCreatedAt(null);
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("User should handle equals with null createdAt in one object")
    void userShouldHandleEqualsWithNullCreatedAtInOneObject() {
        User user1 = new User();
        User user2 = new User();
        user1.setCreatedAt(LocalDateTime.now());
        user2.setCreatedAt(null);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("User.Role enum valueOf ve toString testleri")
    void userRoleEnumValueOfAndToString() {
        for (User.Role role : User.Role.values()) {
            assertEquals(role, User.Role.valueOf(role.name()));
            assertNotNull(role.toString());
        }
    }

    @Test
    @DisplayName("User equals should return false for subclass instance")
    void userEqualsShouldReturnFalseForSubclass() {
        User user = User.builder().id(1L).build();
        User subclassUser = new User(1L, "Name", "email@example.com", "password", "555", User.Role.PATRON, null, null) {};
        assertNotEquals(user, subclassUser);
    }

    @Test
    @DisplayName("Does modifying the list assigned with setBorrowings affect the entity's internal list?")
    void userSetBorrowingsDefensiveCopyTest() {
        User user = new User();
        List<Borrowing> borrowings = new ArrayList<>();
        user.setBorrowings(borrowings);
        assertEquals(0, user.getBorrowings().size());
        borrowings.add(new Borrowing());
        assertEquals(1, user.getBorrowings().size());
    }

    @Test
    @DisplayName("onCreate should not overwrite createdAt if already set")
    void onCreateShouldNotOverwriteCreatedAtIfAlreadySet() throws Exception {
        User user = new User();
        LocalDateTime initial = LocalDateTime.of(2020, 1, 1, 0, 0);
        user.setCreatedAt(initial);
        java.lang.reflect.Method method = User.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(user);
        assertEquals(initial, user.getCreatedAt());
    }

    @Test
    @DisplayName("onCreate can be called multiple times without error")
    void onCreateCanBeCalledMultipleTimes() throws Exception {
        User user = new User();
        java.lang.reflect.Method method = User.class.getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(user);
        LocalDateTime first = user.getCreatedAt();
        Thread.sleep(10);
        method.invoke(user);
        assertEquals(first, user.getCreatedAt());
    }

    @Test
    @DisplayName("User should handle setting borrowings to the same list reference")
    void userShouldHandleSettingBorrowingsToSameReference() {
        User user = new User();
        List<Borrowing> borrowings = new ArrayList<>();
        user.setBorrowings(borrowings);
        user.setBorrowings(borrowings); // set again with same reference
        assertSame(borrowings, user.getBorrowings());
    }

    @Test
    @DisplayName("User should handle setting phone to the same value")
    void userShouldHandleSettingPhoneToSameValue() {
        User user = new User();
        user.setPhone("555");
        user.setPhone("555");
        assertEquals("555", user.getPhone());
    }

    @Test
    @DisplayName("User.Role valueOf should throw exception for invalid value")
    void userRoleValueOfShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            User.Role.valueOf("INVALID_ROLE");
        });
    }

    @Test
    @DisplayName("User should handle null email and phone together")
    void userShouldHandleNullEmailAndPhoneTogether() {
        User user = new User();
        user.setEmail(null);
        user.setPhone(null);
        assertNull(user.getEmail());
        assertNull(user.getPhone());
    }

    @Test
    @DisplayName("User builder ile aynı referans borrowings set edilirse değişmemeli")
    void userShouldNotChangeBorrowingsIfSameReference() {
        User user = new User();
        List<Borrowing> borrowings = new ArrayList<>();
        user.setBorrowings(borrowings);
        user.setBorrowings(borrowings);
        assertSame(borrowings, user.getBorrowings());
    }

    @Test
    @DisplayName("User builder ile aynı value phone set edilirse değişmemeli")
    void userShouldNotChangePhoneIfSameValue() {
        User user = new User();
        user.setPhone("555");
        user.setPhone("555");
        assertEquals("555", user.getPhone());
    }

    @Test
    @DisplayName("User.Role valueOf null ve boş string için exception fırlatmalı")
    void userRoleValueOfShouldThrowExceptionForNullOrEmpty() {
        assertThrows(NullPointerException.class, () -> User.Role.valueOf(null));
        assertThrows(IllegalArgumentException.class, () -> User.Role.valueOf(""));
    }

    @Test
    @DisplayName("User email ve phone aynı anda null olmalı")
    void userShouldAllowBothEmailAndPhoneNull() {
        User user = new User();
        user.setEmail(null);
        user.setPhone(null);
        assertNull(user.getEmail());
        assertNull(user.getPhone());
    }

    @Test
    @DisplayName("User equals: tüm alanlar null ve farklı kombinasyonlar")
    void userEqualsShouldHandleAllNullAndDifferentCombinations() {
        User u1 = new User();
        User u2 = new User();
        assertEquals(u1, u2);
        u1.setName("Ali");
        assertNotEquals(u1, u2);
        u2.setName("Ali");
        assertEquals(u1, u2);
        u1.setEmail("a@a.com");
        assertNotEquals(u1, u2);
        u2.setEmail("a@a.com");
        assertEquals(u1, u2);
        u1.setPassword("123");
        assertNotEquals(u1, u2);
        u2.setPassword("123");
        assertEquals(u1, u2);
        u1.setPhone("555");
        assertNotEquals(u1, u2);
        u2.setPhone("555");
        assertEquals(u1, u2);
        u1.setRole(User.Role.PATRON);
        assertNotEquals(u1, u2);
        u2.setRole(User.Role.PATRON);
        assertEquals(u1, u2);
    }

    @Test
    @DisplayName("UserDetails metotları true dönmeli ve authorities doğru olmalı")
    void userDetailsMethodsShouldReturnTrueAndAuthorities() {
        User user = User.builder().role(User.Role.LIBRARIAN).build();
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertEquals(1, user.getAuthorities().size());
        assertEquals("ROLE_LIBRARIAN", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("User borrowings listesi referans olarak aynı olmalı (defensive copy yok)")
    void userBorrowingsShouldBeReferenceEqual() {
        User user = new User();
        List<Borrowing> list = new ArrayList<>();
        user.setBorrowings(list);
        list.add(new Borrowing());
        assertEquals(list.size(), user.getBorrowings().size());
    }

    @Test
    @DisplayName("User equals: biri null biri dolu alan kombinasyonları")
    void userEqualsShouldHandleOneNullOneNotNullFields() {
        User u1 = new User();
        User u2 = new User();
        u1.setName("Ali");
        u2.setName(null);
        assertNotEquals(u1, u2);
        u1.setName(null);
        u2.setName("Ali");
        assertNotEquals(u1, u2);
        u1.setName(null);
        u2.setName(null);
        assertEquals(u1, u2);
    }

    @Test
    @DisplayName("User equals: farklı tip ve aynı referans")
    void userEqualsShouldHandleDifferentTypeAndSameReference() {
        User u1 = new User();
        assertNotEquals(u1, "string");
        assertEquals(u1, u1);
    }

    @Test
    @DisplayName("User hashCode: tüm alanlar null ve bazıları null")
    void userHashCodeShouldHandleNullFields() {
        User u1 = new User();
        User u2 = new User();
        assertEquals(u1.hashCode(), u2.hashCode());
        u1.setName("Ali");
        assertNotEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        User user = createSampleUser();
        assertNotEquals(user, null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithDifferentType() {
        User user = createSampleUser();
        assertNotEquals(user, "farkli tip");
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsAreNull() {
        User user1 = new User();
        User user2 = new User();
        assertEquals(user1, user2);
    }

    @Test
    void equals_shouldReturnFalse_whenOnlyOneFieldIsDifferent() {
        User user1 = createSampleUser();
        User user2 = createSampleUser();
        user2.setEmail("farkli@email.com");
        assertNotEquals(user1, user2);
    }

    @Test
    void hashCode_shouldBeEqual_whenAllFieldsAreNull() {
        User user1 = new User();
        User user2 = new User();
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenFieldsAreDifferent() {
        User user1 = createSampleUser();
        User user2 = createSampleUser();
        user2.setName("Farklı Ad");
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void hashCode_shouldBeEqual_whenFieldsAreEqual() {
        User user1 = createSampleUser();
        User user2 = createSampleUser();
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    // Yardımcı metot: Testlerde kullanılmak üzere örnek bir User nesnesi döndürür
    private User createSampleUser() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .phone("1234567890")
                .role(User.Role.PATRON)
                .createdAt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
    }
} 