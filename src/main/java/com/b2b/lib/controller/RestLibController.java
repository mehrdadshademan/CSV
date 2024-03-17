package com.b2b.lib.controller;

import com.b2b.lib.dto.BookDto;
import com.b2b.lib.dto.BorrowDto;
import com.b2b.lib.dto.UserDto;
import com.b2b.lib.entity.Book;
import com.b2b.lib.service.LibService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lib")
@RequiredArgsConstructor
public class RestLibController {

    private final LibService libService;

    @GetMapping("/user/borrowed")
    public ResponseEntity<List<UserDto>> uploadDeviceCSV() {
        return new ResponseEntity<>(libService.usersBorrowedBook(), HttpStatus.OK);
    }

    @GetMapping("/user/non-terminated/not-borrowed")
    public ResponseEntity<List<UserDto>> nonTerminateUserNotBorrowedBook() {
        return new ResponseEntity<>(libService.nonTerminateUserNotBorrowedBook(), HttpStatus.OK);
    }

    @GetMapping("/book/availableBooks")
    public ResponseEntity<List<BookDto>> availableBooks() {
        return new ResponseEntity<>(libService.availableBooks(), HttpStatus.OK);
    }

    @GetMapping("/user/borrowed-by-date")
    public ResponseEntity<List<UserDto>> nonTerminateUserNotBorrowedBook(@RequestParam String date) {
        return new ResponseEntity<>(libService.borrowedByDate(date), HttpStatus.OK);
    }

    @PostMapping("/insertFiles")
    public ResponseEntity<String> uploadDeviceCSV(@RequestParam("borrowed") MultipartFile borrowed, @RequestParam("books") MultipartFile book, @RequestParam("user") MultipartFile user) {
        return new ResponseEntity<>(libService.insertCSVFiles(user, book, borrowed), HttpStatus.OK);
    }

    @PostMapping("/book/borrowed-by-range")
    public ResponseEntity<List<Book>> retrievedBorrowedByRangeAndUser(@Valid @RequestBody BorrowDto borrowed) {
        return new ResponseEntity<>(libService.borrowedByRangeAndUser(borrowed.getBorrowedFrom(), borrowed.getBorrowedTo(), borrowed.getUser()), HttpStatus.OK);
    }

}
