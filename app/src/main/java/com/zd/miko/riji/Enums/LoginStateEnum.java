package com.zd.miko.riji.Enums;

/**
 * 使用枚举字典表示常量数据字典
 * Created by Miko on 2017/2/27.
 */
public enum LoginStateEnum {
    ID_HAD_CREATE(1,"用户已存在"),
    CREATE_NEW_ID(0,"新建用户"),
    INNER_ERROR(-2,"系统异常");
    private int state;
    private String stateInfo;

    public static LoginStateEnum stateOf(int index){
        for(LoginStateEnum state:values()){
            if(state.getState() == index){
                return state;
            }
        }
        return null;
    }

    LoginStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }
    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
}
