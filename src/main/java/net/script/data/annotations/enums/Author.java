package net.script.data.annotations.enums;

public enum Author {
    BartoszGoss("Bartosz Goss", 207230),
    AdrianFijalkowski("Adrian Fijałkowski", 210171),
    NotSpecfified("Nikt", 0 );

    private final String name;
    private final Integer index;

    Author(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public String fullName() {
        return name;
    }

    public Integer indexNumber() {
        return index;
    }
}
