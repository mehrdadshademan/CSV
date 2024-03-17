package com.b2b.lib.dao;

import com.b2b.lib.entity.Book;
import com.b2b.lib.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    @Query("SELECT DISTINCT bk FROM Borrow b " +
            "INNER JOIN Book bk ON b.book = bk.title " +
            "WHERE b.borrower = :borrower " +
            "AND b.borrowedFrom >= :startDate AND b.borrowedTo <= :endDate")
    List<Book> findBooksBorrowedByUserInDateRange(String borrower, LocalDate startDate, LocalDate endDate);

}
