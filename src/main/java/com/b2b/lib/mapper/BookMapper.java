package com.b2b.lib.mapper;

import com.b2b.lib.dto.BookDto;
import com.b2b.lib.entity.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    List<BookDto> toDto(List<Book> books);
}
