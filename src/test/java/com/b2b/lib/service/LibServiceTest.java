package com.b2b.lib.service;

import com.b2b.lib.dao.BookRepository;
import com.b2b.lib.dao.BorrowRepository;
import com.b2b.lib.dao.UserRepository;
import com.b2b.lib.dto.BookDto;
import com.b2b.lib.dto.UserDto;
import com.b2b.lib.entity.Book;
import com.b2b.lib.entity.Borrow;
import com.b2b.lib.entity.UserLib;
import com.b2b.lib.exception.InputDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LibServiceTest {
    @Autowired
    LibService libService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BorrowRepository borrowRepository;

    @Test
    void should_Success_When_InsertRightFiles() throws IOException {
        InputStream userFileInputStream = getClass().getClassLoader().getResourceAsStream("user.csv");
        InputStream bookFileInputStream = getClass().getClassLoader().getResourceAsStream("books.csv");
        InputStream borrowedFileInputStream = getClass().getClassLoader().getResourceAsStream("borrowed.csv");
        MockMultipartFile userFile = new MockMultipartFile("user.csv", "user.csv", "text/csv", userFileInputStream);
        MockMultipartFile bookFile = new MockMultipartFile("books.csv", "books.csv", "text/csv", bookFileInputStream);
        MockMultipartFile borrowedFile = new MockMultipartFile("borrowed.csv", "borrowed.csv", "text/csv", borrowedFileInputStream);
        String result = libService.insertCSVFiles(userFile, bookFile, borrowedFile);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Insert Of Files are Success", result);
        Assertions.assertNotEquals(0, userRepository.findAll().size());
    }

    @Test
    void should_InputDataException_When_CsvFilesAreNotCsv() throws IOException {
        InputStream userFileInputStream = getClass().getClassLoader().getResourceAsStream("user.csv");
        InputStream bookFileInputStream = getClass().getClassLoader().getResourceAsStream("books.csv");
        InputStream borrowedFileInputStream = getClass().getClassLoader().getResourceAsStream("borrowed.csv");
        MockMultipartFile userFile = new MockMultipartFile("user.cs", userFileInputStream);
        MockMultipartFile bookFile = new MockMultipartFile("book.csv", bookFileInputStream);
        MockMultipartFile borrowedFile = new MockMultipartFile("borrowed.csv", "borrowed.csv", "text/csv", borrowedFileInputStream);
        Assertions.assertThrows(InputDataException.class, () -> libService.insertCSVFiles(userFile, bookFile, borrowedFile));
    }

    @Test
    void should_InputDataException_When_CsvUserFilesIsWrongName() throws IOException {
        InputStream userFileInputStream = getClass().getClassLoader().getResourceAsStream("wrongNameUser.csv");
        MockMultipartFile userFile = new MockMultipartFile("user.csv", "user.csv", "text/csv", userFileInputStream);
        Assertions.assertThrows(InputDataException.class, () -> libService.insertCSVFiles(userFile, userFile, userFile));
    }

    @Test
    void should_InputDataException_When_CsvUserFilesIsWrongDate() throws IOException {
        InputStream userFileInputStream = getClass().getClassLoader().getResourceAsStream("wrongDateUser.csv");
        MockMultipartFile userFile = new MockMultipartFile("user.csv", "user.csv", "text/csv", userFileInputStream);
        Assertions.assertThrows(InputDataException.class, () -> libService.insertCSVFiles(userFile, userFile, userFile));
    }

    @Test
    void should_Success_When_BookAreAvailable() {
        borrowRepository.deleteAll();
        bookRepository.deleteAll();
        List<Book> bookList = new ArrayList<>();
        Book bookAvailable = Book.builder().title("Java").author("Test").publisher("Oracle").build();
        Book bookBorrowed = Book.builder().title("DB").author("Test-DB").publisher("Oracle").build();
        bookList.add(bookAvailable);
        bookList.add(bookBorrowed);
        bookRepository.saveAll(bookList);
        Borrow borrow = Borrow.builder().borrower("Alex").book("DB").build();
        borrowRepository.save(borrow);
        List<BookDto> books = libService.availableBooks();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(bookAvailable.getTitle(), books.getFirst().getTitle());
        Assertions.assertEquals(bookAvailable.getAuthor(), books.getFirst().getAuthor());
    }

    @Test
    void should_Success_When_UserBorrowBook() {
        List<UserLib> users = new ArrayList<>();

        UserLib userLibBorrowed = UserLib.builder().name("Mehrdad").family("Shademan").build();
        UserLib user = UserLib.builder().name("first name").family("user name").build();
        users.add(user);
        users.add(userLibBorrowed);
        userRepository.deleteAll();
        userRepository.saveAll(users);
        Borrow borrow = Borrow.builder().borrower("Shademan,Mehrdad").book("Micro services").build();
        borrowRepository.deleteAll();
        borrowRepository.save(borrow);
        List<UserDto> usersBorrowedBook = libService.usersBorrowedBook();
        Assertions.assertNotNull(usersBorrowedBook);
        Assertions.assertEquals(1, usersBorrowedBook.size());
        Assertions.assertEquals(userLibBorrowed.getName(), usersBorrowedBook.getFirst().getName());
        Assertions.assertEquals(userLibBorrowed.getFamily(), usersBorrowedBook.getFirst().getFamily());
    }

    @Test
    void should_Success_When_nonTerminateUserBorrowBook() {
        List<UserLib> users = new ArrayList<>();
        userRepository.deleteAll();
        borrowRepository.deleteAll();

        UserLib userNotBorrowedNonTerminate = UserLib.builder()
                .name("Mehrdad")
                .family("Shademan").build();

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        UserLib userTerminateBorrowed = UserLib.builder()
                .name("Test name")
                .family("Test Family name")
                .endMembership(LocalDate.parse("01/01/2010", FORMATTER)).build();

        UserLib userNotBorrowBook = UserLib.builder()
                .name("Test Not Borrow First name")
                .family("Test Not Borrow Family")
                .endMembership(LocalDate.parse("01/01/2010", FORMATTER)).build();

        users.add(userNotBorrowedNonTerminate);
        users.add(userNotBorrowBook);
        users.add(userTerminateBorrowed);
        userRepository.saveAll(users);

        Borrow borrow = Borrow.builder().borrower("Test Family name,Test name").book("Micro services").build();
        borrowRepository.save(borrow);

        List<UserDto> nonTerminateUserNotBorrowedBook = libService.nonTerminateUserNotBorrowedBook();

        Assertions.assertNotNull(nonTerminateUserNotBorrowedBook);
        Assertions.assertEquals(1, nonTerminateUserNotBorrowedBook.size());
        Assertions.assertEquals(nonTerminateUserNotBorrowedBook.getFirst().getName(), userNotBorrowedNonTerminate.getName());
        Assertions.assertEquals(nonTerminateUserNotBorrowedBook.getFirst().getFamily(), userNotBorrowedNonTerminate.getFamily());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void should_ThrowException_When_FindBookAreBorrowWithInvalidDate(String date) {
        Assertions.assertThrows(InputDataException.class, () -> libService.borrowedByDate(date));
    }

    @Test
    void should_ThrowException_When_GivenUserOrDateIsNotValid() {
        UserDto userDto = new UserDto();
        userDto.setFamily("Test Family name");
        userDto.setName("Test name");
        Assertions.assertThrows(InputDataException.class, () -> libService.borrowedByRangeAndUser("", "01/01/2020", userDto));
    }

    @Test
    void should_Success_When_GivenUserOrDateIsValid() {
        UserDto userDto = new UserDto();
        userDto.setFamily("Test Family name");
        userDto.setName("Test name");
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        List<Borrow> borrowList = new ArrayList();
        Borrow borrow = Borrow.builder()
                .borrower("Test Family name,Test name")
                .book("Micro services")
                .borrowedFrom(LocalDate.parse("01/01/2010", FORMATTER))
                .borrowedTo(LocalDate.parse("12/01/2010", FORMATTER))
                .build();
        borrowList.add(borrow);
        Borrow secondBorrow = Borrow.builder()
                .borrower("Test Family name,Test name")
                .book("Micro services")
                .borrowedFrom(LocalDate.parse("01/01/2019", FORMATTER))
                .borrowedTo(LocalDate.parse("01/01/2020", FORMATTER))
                .build();
        borrowList.add(secondBorrow);
        borrowRepository.saveAll(borrowList);
        Book book = Book.builder().title("Micro services").publisher("IBM").build();
        bookRepository.save(book);

        List<Book> books = libService.borrowedByRangeAndUser("01/01/2019", "01/01/2020", userDto);
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(books.getFirst().getAuthor(), book.getAuthor());
        Assertions.assertEquals(books.getFirst().getTitle(), book.getTitle());
    }
}

