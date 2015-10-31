package maimeng.yodian.app.client.android.chat.domain;

import com.easemob.chat.EMMessage;

/**
 * Created by android on 2015/11/1.
 */
public class Skill extends maimeng.yodian.app.client.android.model.skill.Skill {
    public static Skill parse(EMMessage message) {
        return new Skill();
    }
}
