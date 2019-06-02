package net.script.data.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

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
        return csvToBean.parse();
    }
}
