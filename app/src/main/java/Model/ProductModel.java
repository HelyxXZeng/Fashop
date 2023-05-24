package Model;

import java.util.List;

public class ProductModel {
    private int ID;
    private String name;
    private int categoryID;
    private String sex;
    private double price;
    private String description;
    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public ProductModel() {
    }

    public ProductModel(int ID, String name, int categoryID, String sex, double price, String description, List<String> images) {
        this.ID = ID;
        this.name = name;
        this.categoryID = categoryID;
        this.sex = sex;
        this.price = price;
        this.description = description;
        this.images = images;
    }

    public ProductModel(int ID, String name, int categoryID, String sex, double price, String description) {
        this.ID = ID;
        this.name = name;
        this.categoryID = categoryID;
        this.sex = sex;
        this.price = price;
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
