package maimeng.yodian.app.client.android.network.loader;

/**
 * Created by android on 9/1/15.
 */
public class Circle {
    public Circle setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    public Circle setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    private int borderSize;
    private int borderColor;

    private Circle() {
        borderColor = 0xffffffff;
        borderSize = 5;
    }

    public static Circle obtain() {
        return new Circle();
    }

    int getBorderSize() {
        return borderSize;
    }

    int getBorderColor() {
        return borderColor;
    }
}
