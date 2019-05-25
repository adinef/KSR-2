package net.script;

import lombok.extern.slf4j.Slf4j;
import net.script.data.csv.CsvReader;
import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.DCResMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.List;

@Configuration
@Profile("init")
@Slf4j
public class InitMain {

    //BETTER SET IT ON START UP
    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }

    private final DCResMeasurementRepository repository;

    @Autowired
    public InitMain(DCResMeasurementRepository repository) throws FileNotFoundException {
        this.repository = repository;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {

        log.info("Beginning data load up.");
        List<DCResMeasurement> dcResMeasurements =
                CsvReader.readData("./src/main/resources/data/raw_residential_data.csv");
        log.info("Finished data load up.");
        log.info("Saving to db.");
        repository.saveAll(dcResMeasurements);
        log.info("Finished db save.");
    }
}
