package com.likeit.as51scholarship.model.userapply;

/**
 * Created by Administrator on 2017/8/30.
 */

public class UserDistrictBean {

    /**
     * id : 1
     * name : 英国
     */

    private String id;
    private String name;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}