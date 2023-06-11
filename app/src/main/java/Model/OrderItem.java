package Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int ID;
    private String comment;
    private int orderID;
    private int quantity;
    private float rate;
    private int variantID;


    @Exclude
    public double getPrice() {
        return price;
    }

    @Exclude
    public void setPrice(double price) {
        this.price = price;
    }

    @Exclude
    private double price;
    private String color;
    private String size;
    private String image;
    private String productName;

    public void setOrderItem(@NonNull CartItem cartItem) {
        quantity = cartItem.getQuantity();
        variantID = cartItem.getVariantID();
        image = cartItem.getImage();
        productName = cartItem.getProductName();
        comment = "";
        rate = 0;
        price = cartItem.getPrice();
        size = cartItem.getSize();
        color = cartItem.getColor();
    }

    public void setRate(float rate) { this.rate = rate; }

    public float getRate() {return rate;}

    public String getComment() {return comment;}

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderID() { return orderID; }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getVariantID() {
        return variantID;
    }

    public void setVariantID(int variantID) {
        this.variantID = variantID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
