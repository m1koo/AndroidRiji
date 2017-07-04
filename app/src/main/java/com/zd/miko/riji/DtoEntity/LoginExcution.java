package com.zd.miko.riji.DtoEntity;

/**
 * Created by Miko on 2017/3/6.
 */
public class LoginExcution {
    boolean success;
    int state;
    String info;
    String md5;

    public LoginExcution(boolean isSuccess, int state, String info, String md5) {
        this.success = isSuccess;
        this.state = state;
        this.info = info;
        this.md5 = md5;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        success = success;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
