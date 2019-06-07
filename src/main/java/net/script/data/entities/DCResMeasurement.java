package net.script.data.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;
import net.script.data.annotations.Comment;
import net.script.data.annotations.enums.Author;
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
    @Column("OBJECTID")
    @CsvBindByName(column = "OBJECTID")
    @Comment(
            value = "Auto-generated internal unique ID"
    )
    private Integer id;

    @Column("SSL")
    @CsvBindByName(column = "SSL")
    @Comment(
            value = "Square, suffix, lot ID"
    )
    private String lotID;

    @Column(value = "BATHRM", fuzzyable = true)
    @CsvBindByName(column = "BATHRM")
    @Comment(
            value = "Number of full bathrooms"
    )
    private Integer noBathrooms;

    @Column(value = "HF_BATHRM", fuzzyable = true)
    @CsvBindByName(column = "HF_BATHRM")
    @Comment(
            value = "Number of half bathrooms (no shower or tub)"
    )
    private Integer noHalfBathrooms;

    @Column("HEAD")
    @CsvBindByName(column = "HEAD")
    @Comment(
            value = "Heating code"
    )
    private Integer heatingCode;

    @Column("HEAD_D")
    @CsvBindByName(column = "HEAD_D")
    @Comment(
            value = "Heating description"
    )
    private String heatingDescription;

    @Column("AC")
    @CsvBindByName(column = "AC")
    @Comment(
            value = "Air conditioning (Y/N)"
    )
    private String isAirConditioned;

    @Column(value = "NUM_UNITS", fuzzyable = true)
    @CsvBindByName(column = "NUM_UNITS")
    @Comment(
            value = "Number of units"
    )
    private Integer noUnits;

    @Column(value = "ROOMS", fuzzyable = true)
    @CsvBindByName(column = "ROOMS")
    @Comment(
            value = "Number of rooms"
    )
    private Integer noRooms;

    @Column(value = "BEDRM", fuzzyable = true)
    @CsvBindByName(column = "BEDRM")
    @Comment(
            value = "Number of bedrooms"
    )
    private Integer noBedrooms;

    @Column(value = "AYB", fuzzyable = true)
    @CsvBindByName(column = "AYB")
    @Comment(
            value = "The earliest time the main portion of the building was built." +
                    "It is not affected by subsequent construction."
    )
    private Integer yearBuilt;

    @Column(value = "YR_RMDL", fuzzyable = true)
    @CsvBindByName(column = "YR_RMDL")
    @Comment(
            value = "Last year residence was remodeled"
    )
    private Integer yearRemodelled;

    @Column(value = "STORIES", fuzzyable = true)
    @CsvBindByName(column = "STORIES")
    @Comment(
            value = "Stories"
    )
    private Float stories;

    @Column("SALEDATE")
    @CsvCustomBindByName(column = "SALEDATE", converter = LocalDateConverter.class)
    @Comment(
            value = "Date of most recent sale",
            madeBy = Author.AdrianFijalkowski
    )
    private LocalDate dateOfRecentSale;

    @Column(value = "PRICE", fuzzyable = true)
    @CsvBindByName(column = "PRICE")
    @Comment(
            value = "Price of most recent sale",
            madeBy = Author.AdrianFijalkowski
    )
    private Float recentSalePrice;

    @Column(value = "SALE_NUM", fuzzyable = true)
    @CsvBindByName(column = "SALE_NUM")
    @Comment(
            value = "Sale number since May 2014",
            madeBy = Author.AdrianFijalkowski
    )
    private String saleNumber;

    @Column(value = "GBA", fuzzyable = true)
    @CsvBindByName(column = "GBA")
    @Comment(
            value = "Gross building area in square feet "
    )
    private Integer gbArea;

    @Column(value = "KITCHENS", fuzzyable = true)
    @CsvBindByName(column = "KITCHENS")
    @Comment(
            value = "Number of kitchens "
    )
    private Integer noKitchens;

    @Column(value = "FIREPLACES", fuzzyable = true)
    @CsvBindByName(column = "FIREPLACES")
    @Comment(
            value = "Number of fireplaces "
    )
    private Integer noFireplaces;

    @Column(value = "LANDAREA", fuzzyable = true)
    @CsvBindByName(column = "LANDAREA")
    @Comment(
            value = "Land area of property in square feet"
    )
    private Integer landArea;
}
