package com.b2b.lib.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UserDto {
    @NotNull(message = "The user   name can not null")
    @NotBlank(message = "The user name is blank")
    private String family;
    @NotNull(message = "The user first name can not null")
    @NotBlank(message = "The user first name is blank")
    private String name;
    private LocalDate startMembership;
    private LocalDate endMembership;
    private String gender;
}
