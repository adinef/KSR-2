package net.script.data.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReader {
    public static <T> List<T> readData(String fileName, Class<T> tClass) throws FileNotFoundException {
        FileReader fileReader = new FileReader(fileName);
        HeaderColumnNameMappingStrategy<T> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
        mappingStrategy.setType(tClass);
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(fileReader)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withMappingStrategy(mappingStrategy)
                .build();

        List<T> parsed = csvToBean.parse();
        List<T> filered = parsed.stream().filter(CsvReader::areAllFieldFilled).collect(Collectors.toList());

        return filered;
    }

    public static boolean areAllFieldFilled(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object val;
            try {
                val = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
            if (Number.class.isAssignableFrom(field.getType())) {
                if (val == null) {
                    return false;
                }
            }
        }
        return true;
    }

}
