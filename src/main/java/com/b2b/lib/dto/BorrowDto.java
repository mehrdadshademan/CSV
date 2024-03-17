package com.b2b.lib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BorrowDto {
    @NotNull(message = "The user can not null")
    private UserDto user;
    @NotNull(message = "The borrowedFrom can not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private String borrowedFrom;
    @NotNull(message = "The borrowedTo can not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private String borrowedTo;
}
