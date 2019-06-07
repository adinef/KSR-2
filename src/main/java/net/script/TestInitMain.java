package net.script;

import lombok.extern.slf4j.Slf4j;
import net.script.data.csv.CsvReader;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.fuzzy.functions.TriangleFunction;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.qualifier.Qualifier;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

@Configuration
@Profile("init")
@Slf4j

public class TestInitMain {

    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    public static void main(String[] args) throws FileNotFoundException {
        LogManager.getLogManager().reset();
        log.info("Beginning data load up.");
        PrintStream original = System.out;
        System.setOut(new PrintStream(new File("NUL")));
        List<?> dcResMeasurements = CsvReader.readData("./src/main/resources/data/raw_residential_data.csv", DCResMeasurement.class);
        System.setOut(original);
        System.out.println("Finished data load up.");
        log.info("Fuzzy set creation start");
        FuzzySet fz = FuzzySet.with(dcResMeasurements).from(new Qualifier("many bathrooms","BATHRM", new TriangleFunction(5D,2D),new Range(3D,4D)));
        assert FuzzySet.intersect(fz, fz).size() == fz.size();
        log.info("Created fuzzySet");
        log.info(String.valueOf(fz.size()));

        for (Object entry : fz.entrySet()) {
            System.out.println(String.valueOf(((Map.Entry<DCResMeasurement,Double>)entry).getKey().getNoBathrooms()) + " u= " + String.valueOf(((Map.Entry<DCResMeasurement,Double>)entry).getValue()));
        }

    }


}
