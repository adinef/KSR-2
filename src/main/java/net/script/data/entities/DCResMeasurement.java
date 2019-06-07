package net.script.data.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;
import net.script.data.csv.LocalDateConverter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "data_points")
public class DCResMeasurement {
    @Id
    @Column(value = "OBJECTID", tooltip = "Auto-generated internal unique ID")
    @CsvBindByName(column = "OBJECTID")
    private Integer id;

    @Column(value = "SSL", tooltip = "Square, suffix, lot ID")
    @CsvBindByName(column = "SSL")
    private String lotID;

    @Column(value = "BATHRM", tooltip = "Number of full bathrooms", fuzzable = true)
    @CsvBindByName(column = "BATHRM")
    private Integer noBathrooms;

    @Column(value = "HF_BATHRM", tooltip = "Number of half bathrooms (no shower or tub)", fuzzable = true)
    @CsvBindByName(column = "HF_BATHRM")
    private Integer noHalfBathrooms;

    @Column(value = "HEAD", tooltip = "Heating code")
    @CsvBindByName(column = "HEAD")
    private Integer heatingCode;

    @Column(value = "HEAD_D", tooltip = "Heating description")
    @CsvBindByName(column = "HEAD_D")
    private String heatingDescription;

    @Column(value = "AC", tooltip = "Air conditioning (Y/N)")
    @CsvBindByName(column = "AC")
    private String isAirConditioned;

    @Column(value = "NUM_UNITS", tooltip = "Number of units", fuzzable = true)
    @CsvBindByName(column = "NUM_UNITS")
    private Integer noUnits;

    @Column(value = "ROOMS", tooltip = "Number of rooms", fuzzable = true)
    @CsvBindByName(column = "ROOMS")
    private Integer noRooms;

    @Column(value = "BEDRM", tooltip = "Number of bedrooms", fuzzable = true)
    @CsvBindByName(column = "BEDRM")
    private Integer noBedrooms;

    @Column(value = "AYB",
            tooltip = "The earliest time the main portion of the building was built." +
            "It is not affected by subsequent construction.",
            fuzzable = true)
    @CsvBindByName(column = "AYB")
    private Integer yearBuilt;

    @Column(value = "YR_RMDL", tooltip = "Last year residence was remodeled", fuzzable = true)
    @CsvBindByName(column = "YR_RMDL")
    private Integer yearRemodelled;

    @Column(value = "STORIES", tooltip = "Stories", fuzzable = true)
    @CsvBindByName(column = "STORIES")
    private Float stories;

    @Column(value = "SALEDATE", tooltip = "Date of most recent sale")
    @CsvCustomBindByName(column = "SALEDATE", converter = LocalDateConverter.class)
    private LocalDate dateOfRecentSale;

    @Column(value = "PRICE", tooltip = "Price of most recent sale", fuzzable = true)
    @CsvBindByName(column = "PRICE")
    private Float recentSalePrice;

    @Column(value = "SALE_NUM", tooltip = "Sale number since May 2014", fuzzable = true)
    @CsvBindByName(column = "SALE_NUM")
    private String saleNumber;

    @Column(value = "GBA", tooltip = "Gross building area in square feet", fuzzable = true)
    @CsvBindByName(column = "GBA")
    private Integer gbArea;

    @Column(value = "KITCHENS", tooltip = "Number of kitchens", fuzzable = true)
    @CsvBindByName(column = "KITCHENS")
    private Integer noKitchens;

    @Column(value = "FIREPLACES", tooltip = "Number of fireplaces ", fuzzable = true)
    @CsvBindByName(column = "FIREPLACES")
    private Integer noFireplaces;

    @Column(value = "LANDAREA", tooltip = "Land area of property in square feet", fuzzable = true)
    @CsvBindByName(column = "LANDAREA")
    private Integer landArea;
}
