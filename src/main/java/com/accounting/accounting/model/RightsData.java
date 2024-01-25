package com.accounting.accounting.model;

public class RightsData {
    private Integer user_id;
    private Integer group_id;
    private Integer[] rights_id;

    public RightsData(Integer user_id, Integer group_id, Integer[] rights_id) {
        this.user_id = user_id;
        this.group_id = group_id;
        this.rights_id = rights_id;
    }

    public Integer getUserId() {
        return this.user_id;
    }
    public Integer getGroupId() {
        return this.group_id;
    }
    public Integer[] getRightsId() {
        return this.rights_id;
    }
}
