package com.idexcel.publishToVactrain;

import org.json.JSONObject;

import java.util.List;

public class PublishMessage {

    private String userId;
    private String[] actionRouteValues;
    private String[] notifyUsers;
    private List<String[]> routeValues;
    private String[] values;
    private String environment;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getActionRouteValues() {
        return actionRouteValues;
    }

    public void setActionRouteValues(String[] actionRouteValues) {
        this.actionRouteValues = actionRouteValues;
    }

    public String[] getNotifyUsers() {
        return notifyUsers;
    }

    public void setNotifyUsers(String[] notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

    public List<String[]> getRouteValues() {
        return routeValues;
    }

    public void setRouteValues(List<String[]> routeValues) {
        this.routeValues = routeValues;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        jo.put("userId", this.userId);
        jo.put("actionRouteValues", this.actionRouteValues);
        jo.put("notifyUsers", this.notifyUsers);
        jo.put("routeValues", this.routeValues);
        jo.put("values", this.values );
        return jo;
    }
}
