package com.b2b.lib.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BookDto {
    @NotNull(message = "The book title can not null")
    @NotBlank(message = "The book title is blank")
    private String title;
    @NotNull(message = "The book author can not null")
    @NotBlank(message = "The book author is blank")
    private String author;
    private String genre;
    private String publisher;

}
