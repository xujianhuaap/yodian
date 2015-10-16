package maimeng.yodian.app.client.android.model.user;

/**
 * Created by android on 2015/10/16.
 */
public enum Sex {
    NONE(0, "保密"),
    MAN(1, "男"),
    WOMAN(2, "女");
    private final int code;
    private final String name;

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }


    Sex(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Sex create(int asInt) {
        switch (asInt) {
            case 0: {
                return NONE;
            }
            case 1: {
                return MAN;
            }
            case 2: {
                return WOMAN;
            }
            default:
                throw new IllegalArgumentException("Error Status value");
        }
    }
}
