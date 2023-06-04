package Model;

import java.io.Serializable;

public class ModelImage implements Serializable {
    private int ID;
    private int modelID;
    private String url;

    public ModelImage() {
    }

    public ModelImage(int ID, int modelID, String url) {
        this.ID = ID;
        this.modelID = modelID;
        this.url = url;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}