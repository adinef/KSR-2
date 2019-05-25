package net.script.data.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import net.script.data.entities.DCResMeasurement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CsvReader {
    public static List<DCResMeasurement> readData(String fileName) throws FileNotFoundException {
        FileReader fileReader = new FileReader(fileName);

        CsvToBean<DCResMeasurement> csvToBean = new CsvToBeanBuilder<DCResMeasurement>(fileReader)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withType(DCResMeasurement.class)
                .build();

        List<DCResMeasurement> parsed = csvToBean.parse();
        return parsed;
    }
}
