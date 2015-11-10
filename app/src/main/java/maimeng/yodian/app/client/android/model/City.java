package maimeng.yodian.app.client.android.model;

import java.util.ArrayList;

/**
 * Created by android on 15-10-19.
 */
@org.parceler.Parcel
public class City {
    private int type;
    private String name;
    private ArrayList<City> sub = new ArrayList<>();

    public City() {

    }

    public City(String text) {
        this.name = text;
    }

    @Override
    public String toString() {
        return "City{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", sub=" + sub +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<City> getSub() {
        return sub;
    }

    public void setSub(ArrayList<City> sub) {
        this.sub = sub;
    }
}
