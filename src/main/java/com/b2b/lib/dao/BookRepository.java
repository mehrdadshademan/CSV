package com.b2b.lib.dao;

import com.b2b.lib.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.title IS NOT NULL " +
            "AND NOT EXISTS (SELECT br.book FROM Borrow br WHERE br.book = b.title)")
    List<Book> findAvailableBooks();
}
