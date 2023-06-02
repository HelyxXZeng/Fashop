package Model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductModel implements Serializable  {
//    @PropertyName("ID")
    private int ID;
    private String name;
    private int categoryID;
    private String sex;
    private double price;
    private String description;
    private List<String> images;

    private int numberInCart;

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
    private List<String> colorList = new ArrayList<>();
    private List<String> sizeList = new ArrayList<>();

    public List<String> getColorList() {
        return colorList;
    }

    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }

    public List<String> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<String> sizeList) {
        this.sizeList = sizeList;
    }

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
