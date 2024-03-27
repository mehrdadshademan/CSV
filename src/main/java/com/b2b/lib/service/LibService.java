package com.b2b.lib.service;

import com.b2b.lib.repository.BookRepository;
import com.b2b.lib.repository.BorrowRepository;
import com.b2b.lib.repository.UserRepository;
import com.b2b.lib.dto.BookDto;
import com.b2b.lib.dto.UserDto;
import com.b2b.lib.entity.Book;
import com.b2b.lib.entity.Borrow;
import com.b2b.lib.entity.UserLib;
import com.b2b.lib.exception.InputDataException;
import com.b2b.lib.mapper.BookMapper;
import com.b2b.lib.mapper.UserMapper;
import com.b2b.lib.util.CsvUtil;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibService {
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    /**
     * a) returns all users who have actually borrowed at least one book
     *
     * @return
     */
    public List<UserDto> usersBorrowedBook() {
        List<UserLib> userLibList = userRepository.findAll();
        List<Borrow> borrowList = borrowRepository.findAll();
        List<UserLib> borrowedUsers = userLibList.stream()
                .filter(userDto -> borrowList.stream()
                        .anyMatch
                                (borrow -> (userDto.getFamily() + "," + userDto.getName()).equalsIgnoreCase(borrow.getBorrower())))
                .collect(Collectors.toList());
        return userMapper.toDto(borrowedUsers);
    }

    /**
     * b) returns all non-terminated users who have not currently borrowed anything
     *
     * @return
     */
    public List<UserDto> nonTerminateUserNotBorrowedBook() {
        List<UserLib> userLibList = userRepository.findAll();
        List<Borrow> borrowList = borrowRepository.findAll();
        List<UserLib> borrowedUsers = userLibList.stream()
                .filter(userDto ->
                        userDto.getEndMembership() == null &&
                                borrowList.stream()
                                        .noneMatch
                                                (borrow -> (userDto.getFamily() + "," + userDto.getName()).equalsIgnoreCase(borrow.getBorrower())))
                .collect(Collectors.toList());
        return userMapper.toDto(borrowedUsers);
    }


    /**
     * c) returns all users who have borrowed a book on a given date
     *
     * @param date
     * @return
     */
    public List<UserDto> borrowedByDate(String date) {
        try {
            if (StringUtils.isBlank(date)) {
                throw new InputDataException("Date is null or empty");
            }
            LocalDate targetDate = LocalDate.parse(date, FORMATTER);
            List<UserLib> userLibList = userRepository.findAll();
            List<Borrow> borrowList = borrowRepository.findAll();
            List<UserLib> borrowedUsers = userLibList.stream().filter(usr ->
                    borrowList.stream().filter(br ->
                                    !targetDate.isBefore(br.getBorrowedFrom())
                                            && !targetDate.isAfter(br.getBorrowedTo()))
                            .anyMatch(borrow -> isUserBorrowedOnDate(usr, borrow, targetDate))).toList();
            return userMapper.toDto(borrowedUsers);
        } catch (Exception ex) {
            log.error("Can not find BorrowBook in date range:{}", date);
            throw new InputDataException("Can not find BorrowBook in date range:" + date);
        }
    }

    /**
     * e) returns all available (not borrowed) books
     *
     * @return
     */
    public List<BookDto> availableBooks() {
        List<Book> availableBooks = bookRepository.findAvailableBooks();
        return bookMapper.toDto(availableBooks);
    }

    private boolean isUserBorrowedOnDate(UserLib user, Borrow borrow, LocalDate targetDate) {
        return (user.getFamily() + "," + user.getName()).equalsIgnoreCase(borrow.getBorrower()) &&
                !targetDate.isBefore(borrow.getBorrowedFrom()) &&
                !targetDate.isAfter(borrow.getBorrowedTo());
    }

    private void parsCsvUser(MultipartFile user) {
        try (Reader reader = new BufferedReader(new InputStreamReader(user.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            log.trace("CSV user file is parsed, csv:{}", csvParser);
            for (CSVRecord csvRecord : csvParser) {
                parseAndValidateCSVUserLine(csvRecord);
            }
        } catch (IOException e) {
            log.error("Can not mapping csv to User dto, csv file:{} ", user);
            throw new InputDataException(e.getMessage());
        }
    }

    private void parsCsvBorrowed(MultipartFile borrowed) {
        try (Reader reader = new BufferedReader(new InputStreamReader(borrowed.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            log.trace("CSV file is parsed, csv:{}", csvParser);
            for (CSVRecord csvRecord : csvParser) {
                parseAndValidateCSVBorrowLine(csvRecord);
            }
        } catch (Exception e) {
            log.error("Can not mapping csv to Borrowed dto, csv file:{} ", borrowed);
            throw new InputDataException(e.getMessage());
        }
    }

    private void parsCsvBook(MultipartFile book) {
        try (Reader reader = new BufferedReader(new InputStreamReader(book.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
            log.trace("CSV file is parsed, csv:{}", csvParser);
            for (CSVRecord csvRecord : csvParser) {
                parseAndValidateCSVBookLine(csvRecord);
            }
        } catch (IOException e) {
            log.error("Can not mapping csv to Book dto, csv file:{} ", book);
            throw new InputDataException(e.getMessage());
        }
    }

    /**
     * write to CSV file
     */
    public  void writeCSV() {
        UserLib build = userRepository.findAll().getFirst();
        try {
            CsvUtil.write(UserLib.class , build) ;
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        }
    }
    public String insertCSVFiles(MultipartFile user, MultipartFile book, MultipartFile borrowed) throws IOException {

        //this is generic way

        csvValidation(user, book, borrowed);
        List userLibList = CsvUtil.fetch(user, UserLib.class);
        userRepository.saveAll(userLibList);
        List bookList = CsvUtil.fetch(book, Book.class);
        bookRepository.saveAll(bookList);
        List borrowList = CsvUtil.fetch(borrowed, Borrow.class);
        borrowRepository.saveAll(borrowList);

        //-------------- this is one way ,
        //        parsCsvUser(user);
        //        parsCsvBook(book);
        //        parsCsvBorrowed(borrowed);


        log.trace("The CSV files imported");
        return "Insert Of Files are Success";
    }



    private void userValidation(UserLib user) {
        if (StringUtils.isBlank(user.getFamily()) || StringUtils.isBlank(user.getName())) {
            throw new InputDataException("The name and firstName is blank for UserCSV ");
        }
        if (user.getStartMembership() != null && user.getStartMembership().isAfter(LocalDate.now())) {
            throw new InputDataException("The membership Date is not valid for user " + user);
        }
    }

    private boolean isCsvFile(MultipartFile file) {
        log.trace("check validation CSV file format...");
        return Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".csv");
    }

    private void checkCSVFile(MultipartFile file) {
        log.trace("check validation CSV file name...");
        if (file == null || file.getOriginalFilename() == null)
            throw new InputDataException("Invalid file. Please upload a CSV file.");
    }

    private void csvValidation(MultipartFile... files) {
        log.trace("validation CSV file...");
        for (MultipartFile file : files) {
            checkCSVFile(file);
            if (!isCsvFile(file)) {
                throw new InputDataException("Invalid file format. Please upload a CSV file.");
            }
        }
    }

    private void borrowedBookValidation(Borrow borrow) {
        if (StringUtils.isBlank(borrow.getBorrower()) || StringUtils.isBlank(borrow.getBook())) {
            throw new InputDataException("The Borrow CSV is not include borrower or book name");
        }
    }

    private void parseAndValidateCSVBookLine(CSVRecord csvRecord) {
        Book book = Book.builder()
                .author(csvRecord.get("Author"))
                .title(csvRecord.get("Title"))
                .genre(csvRecord.get("Genre"))
                .publisher(csvRecord.get("Publisher")).build();
        bookValidation(book);
        bookRepository.save(book);
    }

    private void bookValidation(Book book) {
        if (StringUtils.isBlank(book.getAuthor()) || StringUtils.isBlank(book.getTitle())) {
            throw new InputDataException("The Book CSV is not include Title or Author");
        }
    }

    private void parseAndValidateCSVBorrowLine(CSVRecord csvRecord) {
        Borrow.BorrowBuilder borrower = Borrow.builder().book(csvRecord.get("Book"))
                .borrower(csvRecord.get("Borrower"));

        if (!StringUtils.isBlank(csvRecord.get("borrowed from"))) {
            borrower.borrowedFrom(LocalDate.parse(csvRecord.get("borrowed from"), FORMATTER));
        }
        if (!StringUtils.isBlank(csvRecord.get("borrowed to"))) {
            borrower.borrowedTo(LocalDate.parse(csvRecord.get("borrowed to"), FORMATTER));
        }
        Borrow borrowedBook = borrower.build();
        borrowedBookValidation(borrowedBook);
        borrowRepository.save(borrowedBook);
    }

    private void parseAndValidateCSVUserLine(CSVRecord csvRecord) {
        UserLib.UserLibBuilder userLibBuilder = UserLib.builder().name(csvRecord.get("First name"))
                .family(csvRecord.get("Name"))
                .gender(csvRecord.get("Gender"));
        if (!StringUtils.isBlank(csvRecord.get("Member since"))) {
            userLibBuilder.startMembership(LocalDate.parse(csvRecord.get("Member since"), FORMATTER));
        }
        if (!StringUtils.isBlank(csvRecord.get("Member till"))) {
            userLibBuilder.endMembership(LocalDate.parse(csvRecord.get("Member till"), FORMATTER));
        }
        UserLib user = userLibBuilder.build();
        userValidation(user);
        userRepository.save(user);
    }

    /**
     * d) returns all books borrowed by a given user in a given date range
     */
    public List<Book> borrowedByRangeAndUser(String fromDate, String toDate, @Valid UserDto user) {

        if (!isValidUserAndRange(fromDate, toDate, user)) {
            throw new InputDataException("The Input are not valid");
        }
        String borrower = user.getFamily() + "," + user.getName();
        return borrowRepository.findBooksBorrowedByUserInDateRange(borrower
                , LocalDate.parse(fromDate, FORMATTER)
                , LocalDate.parse(toDate, FORMATTER));
    }

    private boolean isValidUserAndRange(String fromDate, String toDate, @Valid UserDto user) {
        return !StringUtils.isBlank(fromDate)
                && !StringUtils.isBlank(toDate)
                && user != null
                && !StringUtils.isBlank(user.getFamily())
                && !StringUtils.isBlank(user.getName());
    }

}
