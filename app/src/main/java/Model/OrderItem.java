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
    private double price;
    @Exclude
    private String color;
    @Exclude
    private String size;
    @Exclude
    private String image;
    @Exclude
    private String productName;

    public OrderItem(){}
    public OrderItem(OrderItem item) {
        this.ID = item.getID();
        this.orderID = item.getOrderID();
        this.comment = item.getComment();
        this.quantity = item.getQuantity();
        this.rate = item.getRate();
        this.variantID = item.getVariantID();
    }

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

    @Exclude
    public double getPrice() {
        return price;
    }

    @Exclude
    public void setPrice(double price) {
        this.price = price;
    }

    public void setRate(float rate) { this.rate = rate; }

    public float getRate() {return rate;}

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {return comment;}

    @Exclude
    public String getColor() {
        return color;
    }
    @Exclude
    public void setColor(String color) {
        this.color = color;
    }
    @Exclude
    public String getSize() {
        return size;
    }
    @Exclude
    public void setSize(String size) {
        this.size = size;
    }
    @Exclude
    public String getProductName() {
        return productName;
    }
    @Exclude
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderID() { return orderID; }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    @Exclude
    public String getImage() {
        return image;
    }
    @Exclude
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
