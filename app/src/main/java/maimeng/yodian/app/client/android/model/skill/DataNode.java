package maimeng.yodian.app.client.android.model.skill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by android on 8/20/15.
 */
public class DataNode {

    public List<Skill> getList() {
        return list;
    }

    public void setList(List<Skill> list) {
        this.list = list;
    }

    private List<Skill> list;
    private List<Banner> banner;
    private List<Theme> category;

    public List<Theme> getCategory() {
        return category;
    }

    public void setCategory(List<Theme> category) {
        this.category = category;
    }

    @SerializedName("skill")
    private Head headSkill;

    public Head getHeadUser() {
        return headUser;
    }

    public void setHeadUser(Head headUser) {
        this.headUser = headUser;
    }

    public Head getHeadSkill() {
        return headSkill;
    }

    public void setHeadSkill(Head headSkill) {
        this.headSkill = headSkill;
    }

    @SerializedName("user")

    private Head headUser;

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

}
