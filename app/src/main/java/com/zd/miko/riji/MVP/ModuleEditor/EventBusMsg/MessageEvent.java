package com.zd.miko.riji.MVP.ModuleEditor.EventBusMsg;

public class MessageEvent {
    public final String message;
    public final String type ;

    public MessageEvent(String message,String type) {
        this.message = message;
        this.type = type;
    }

}
