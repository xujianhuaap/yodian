package maimeng.yodian.app.client.android.view.deal;

/**
 * Created by android on 15-9-24.
 */
public enum BindStatus {
    NO_CARD(-1, "未绑定"),
    WAITCONFIRM(0, "待审核"),
    PASS(2, "审核通过"),
    DENY(3, "拒绝"),
    CANCEL(4, "用户取消绑定");
    private final int value;
    private final String name;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }


    BindStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static BindStatus create(int asInt) {
        switch (asInt) {
            case -1: {
                return NO_CARD;
            }
            case 0: {
                return WAITCONFIRM;
            }
            case 2: {
                return PASS;
            }
            case 3: {
                return DENY;
            }
            case 4: {
                return CANCEL;
            }
            default:
                throw new IllegalArgumentException("Error Status value");
        }
    }
}
