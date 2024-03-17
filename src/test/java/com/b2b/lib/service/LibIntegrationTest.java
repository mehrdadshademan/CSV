package com.b2b.lib.service;

import com.b2b.lib.dto.BookDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LibIntegrationTest {

    @Autowired
    LibService libService;

    @Test
    void should_Success_When_BookAreAvailable() throws IOException {
        InputStream userFileInputStream = getClass().getClassLoader().getResourceAsStream("user.csv");
        InputStream bookFileInputStream = getClass().getClassLoader().getResourceAsStream("books.csv");
        InputStream borrowedFileInputStream = getClass().getClassLoader().getResourceAsStream("borrowed.csv");
        MockMultipartFile userFile = new MockMultipartFile("user.csv", "user.csv", "text/csv", userFileInputStream);
        MockMultipartFile bookFile = new MockMultipartFile("books.csv", "books.csv", "text/csv", bookFileInputStream);
        MockMultipartFile borrowedFile = new MockMultipartFile("borrowed.csv", "borrowed.csv", "text/csv", borrowedFileInputStream);
        libService.insertCSVFiles(userFile, bookFile, borrowedFile);
        List<BookDto> books = libService.availableBooks();
        Assertions.assertNotNull(books);
        Assertions.assertNotEquals(0, books.size());
    }


}
