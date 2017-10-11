package com.zd.miko.riji.Bean;
public class LoginExcution {

    String id;
    int state;

    public LoginExcution(String id, int state) {
        this.id = id;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}