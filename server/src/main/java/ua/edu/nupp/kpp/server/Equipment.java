package ua.edu.nupp.kpp.server;

public class Equipment {
    private Long id;
    private String name;
    private String category;
    private double rentalRate;

    public Equipment() {
    }

    public Equipment(Long id, String name, String category, double rentalRate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rentalRate = rentalRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(double rentalRate) {
        this.rentalRate = rentalRate;
    }
}
