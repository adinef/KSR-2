package net.script.data.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;
import net.script.data.annotations.Comment;
import net.script.data.annotations.enums.Author;

import javax.persistence.*;

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
            value = "Auto-generated internal unique ID",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer id;

    @Column("SSL")
    @CsvBindByName(column = "SSL")
    @Comment(
            value = "Square, suffix, lot ID",
            madeBy = Author.AdrianFijalkowski
    )
    private String lotID;

    @Column("BATHRM")
    @CsvBindByName(column = "BATHRM")
    @Comment(
            value = "Number of full bathrooms",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noBathrooms;

    @Column("HF_BATHRM")
    @CsvBindByName(column = "HF_BATHRM")
    @Comment(
            value = "Number of half bathrooms (no shower or tub)",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noHalfBathrooms;

    @Column("HEAD")
    @CsvBindByName(column = "HEAD")
    @Comment(
            value = "Heating code",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer heatingCode;

    @Column("HEAD_D")
    @CsvBindByName(column = "HEAD_D")
    @Comment(
            value = "Heating description",
            madeBy = Author.AdrianFijalkowski
    )
    private String heatingDescription;

    @Column("AC")
    @CsvBindByName(column = "AC")
    @Comment(
            value = "Air conditioning (Y/N)",
            madeBy = Author.AdrianFijalkowski
    )
    private String isAirConditioned;

    @Column("NUM_UNITS")
    @CsvBindByName(column = "NUM_UNITS")
    @Comment(
            value = "Number of units",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noUnits;

    @Column("ROOMS")
    @CsvBindByName(column = "ROOMS")
    @Comment(
            value = "Number of rooms",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noRooms;

    @Column("BEDRM")
    @CsvBindByName(column = "BEDRM")
    @Comment(
            value = "Number of bedrooms",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noBedrooms;

    @Column("AYB")
    @CsvBindByName(column = "AYB")
    @Comment(
            value = "The earliest time the main portion of the building was built." +
                    "It is not affected by subsequent construction.",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer yearBuilt;

    @Column("YR_RMDL")
    @CsvBindByName(column = "YR_RMDL")
    @Comment(
            value = "Last year residence was remodeled",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer yearRemodelled;

    @Column("STORIES")
    @CsvBindByName(column = "STORIES")
    @Comment(
            value = "Stories",
            madeBy = Author.AdrianFijalkowski
    )
    private Float stories;

    @Column("SALESDATE")
    @CsvBindByName(column = "SALESDATE")
    @Comment(
            value = "Date of most recent sale",
            madeBy = Author.AdrianFijalkowski
    )
    private String dateOfRecentSale;

    @Column("PRICE")
    @CsvBindByName(column = "PRICE")
    @Comment(
            value = "Price of most recent sale",
            madeBy = Author.AdrianFijalkowski
    )
    private Float recentSalePrice;

    @Column("SALE_NUM")
    @CsvBindByName(column = "SALE_NUM")
    @Comment(
            value = "Sale number since May 2014",
            madeBy = Author.AdrianFijalkowski
    )
    private String saleNumber;

    @Column("GBA")
    @CsvBindByName(column = "GBA")
    @Comment(
            value = "Gross building area in square feet ",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer gbArea;

    @Column("KITCHENS")
    @CsvBindByName(column = "KITCHENS")
    @Comment(
            value = "Number of kitchens ",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noKitchens;

    @Column("FIREPLACES")
    @CsvBindByName(column = "FIREPLACES")
    @Comment(
            value = "Number of fireplaces ",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer noFireplaces;

    @Column("LANDAREA")
    @CsvBindByName(column = "LANDAREA")
    @Comment(
            value = "Land area of property in square feet",
            madeBy = Author.AdrianFijalkowski
    )
    private Integer landArea;
}
