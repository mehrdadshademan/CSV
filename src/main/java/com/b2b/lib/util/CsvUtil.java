package com.b2b.lib.util;

import com.b2b.lib.entity.UserLib;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CsvUtil {
    public static <T> List fetch(MultipartFile file, Class<T> resultClass) throws IOException {

        Reader reader = new InputStreamReader(file.getInputStream());
        return new CsvToBeanBuilder(reader)
                .withType(resultClass)
                .withIgnoreEmptyLine(true)
                .withIgnoreLeadingWhiteSpace(true)
                .withFilter(new CsvToBeanFilter() {
                    @Override
                    public boolean allowLine(String[] strings) {
                        for (String one : strings) {
                            if (one != null && one.length() > 0) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .build()
                .parse();
    }


    public static <T> void write(Class<T> classes, T entity) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<T> s = new ArrayList<>();
        s.add(entity);
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + "example.csv";

        try (Writer writer = new FileWriter("example.csv")) {
            new StatefulBeanToCsvBuilder<T>(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build()
                    .write(entity);
            writer.flush();
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
