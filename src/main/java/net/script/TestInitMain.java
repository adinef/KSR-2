package net.script;

import lombok.extern.slf4j.Slf4j;
import net.script.data.csv.CsvReader;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.fuzzy.functions.RectangularFunction;
import net.script.logic.fuzzy.functions.TriangleFunction;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.view.Summary;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
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


        Qualifier bathrooms = new Qualifier("dużo łazienek", "BATHRM", new TriangleFunction(3.0, 5.0, 7.0), new Range(3D, 4D));
        Qualifier landarea = new Qualifier("ogromną działkę", "LANDAREA", new TriangleFunction(30000D, 500000D, 970000D), new Range(0D, 0D));

        Quantifier duzoQuant = new Quantifier("dużo", new TriangleFunction(0.49, 0.75D, 1.01D));
        Quantifier maloQuant = new Quantifier("mało", new TriangleFunction(0.00,0.25D, 0.48D));
        //Quantifier pojebanyQuant = new Quantifier("chuj mnie to obchodzi ile", new RectangularFunction(0D, 1D));

        Summarizer bedroomsSum = new Summarizer("wchuj sypialni", "BEDRM", new TriangleFunction(10D,50D, 90D), new Range(0D, 0D));

        List<Qualifier> qualifiers = new ArrayList<>();
        qualifiers.add(bathrooms);
        qualifiers.add(landarea);
        List<Quantifier> quantifiers = new ArrayList<>();
        quantifiers.add(maloQuant);
        quantifiers.add(duzoQuant);
        List<Summarizer> summarizers = new ArrayList<>();
        summarizers.add(bedroomsSum);

        Summary summary = createSummary(dcResMeasurements, quantifiers, qualifiers, summarizers);
        System.out.println(summary.getContent());
    }

    private static Summary createSummary(List<?> dcResMeasurements, List<Quantifier> quantifiers, List<Qualifier> qualifiers, List<Summarizer> summarizers) {
        List<FuzzySet> f = new ArrayList<>();
        for (Qualifier q : qualifiers) {
            f.add(FuzzySet.with(dcResMeasurements).from(q));
        }
        FuzzySet qualifiersSet = FuzzySet.intersect(f.get(0),f.get(1));
        FuzzySet summarizersSet = FuzzySet.with(dcResMeasurements).from(summarizers.get(0));
        FuzzySet wyrazenie = FuzzySet.intersect(qualifiersSet,summarizersSet);
        double finalSizeNormalized = wyrazenie.size() * 1.0/qualifiersSet.size();
        assert wyrazenie.size() > 0;
        double max = 0;
        String name = "";
        for(Quantifier q : quantifiers) {
            if (q.calculate(finalSizeNormalized) > max) {
                max = q.calculate(wyrazenie.size());
                name = q.getName();
            }
        }
        Summary s = new Summary();
        String podsumowanie = name + " budynków mających ";
        for(Qualifier q : qualifiers) {
            podsumowanie += q.getName() + ", ";
        }
        podsumowanie += "ma " + summarizers.get(0).getName();
        s.setContent(podsumowanie);
        return s;
    }

        /*FuzzySet bathrooms = FuzzySet.with(dcResMeasurements)
                .from(new Qualifier("dużo łazienek","BATHRM", new TriangleFunction(5D,2D),new Range(3D,4D)));

        FuzzySet landarea = FuzzySet.with(dcResMeasurements)
                .from(new Qualifier("ogromną działkę", "LANDAREA",new TriangleFunction(500000D,470000D),new Range(0D,0D)));
        Double aDouble = FuzzySet.sumWithCardinality(fz, 1);
        System.out.println("sup size: " + fz.support().size());
        System.out.println(aDouble);
        log.info("Created fuzzySet");
        log.info(String.valueOf(fz.size()));

        for (Object entry : fz.entrySet()) {
            System.out.println(String.valueOf(((Map.Entry<DCResMeasurement,Double>)entry).getKey().getNoBathrooms()) + " u= " + String.valueOf(((Map.Entry<DCResMeasurement,Double>)entry).getValue()));
        }*/


}
