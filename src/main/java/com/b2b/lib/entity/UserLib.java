package com.b2b.lib.entity;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLib {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CsvBindByName(column = "First name",required = true)
    private String name;
    @CsvBindByName(column = "Name", required = true)
    private String family;
    @CsvCustomBindByName(column = "Member since", converter = convertUtil.class)
    private LocalDate startMembership;
    @CsvCustomBindByName(column = "Member till", converter = convertUtil.class)
    private LocalDate endMembership;
    @CsvBindByName(column = "Gender")
    private String gender;

    //    public static class LocalDateConverter extends com.opencsv.bean.AbstractBeanField {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//
//        @Override
//        protected Object convert(String s) {
//
//            if (StringUtils.isBlank(s)) {
//                return null;
//            }
//            try {
//                return LocalDate.parse(s, formatter);
//                //     return DateUtils.parseDateStrictly(s, DATE_FORMATS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to parse date: " + s, e);
//            }
//        }
//    }
    public static class convertUtil extends AbstractBeanField {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        @Override
        protected Object convert(String date) {
            if (StringUtils.isBlank(date))
                return null;
            return LocalDate.parse(date, formatter);
        }
    }
}
