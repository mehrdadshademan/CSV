package com.b2b.lib.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CsvBindByName(column = "Borrower")
    private String borrower;
    @CsvBindByName(column = "Book")
    private String book;
    @CsvCustomBindByName(column = "borrowed from", converter = UserLib.convertUtil.class)
    private LocalDate borrowedFrom;
    @CsvCustomBindByName(column = "borrowed to", converter = UserLib.convertUtil.class)
    private LocalDate borrowedTo;
}
