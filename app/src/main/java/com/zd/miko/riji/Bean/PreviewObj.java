package com.zd.miko.riji.Bean;

public class PreviewObj {
    @Override
    public String toString() {
        return "PreviewObj{" +
                "type=" + type +
                ", path='" + path + '\'' +
                '}';
    }

    public int type;
    public String path;

    public PreviewObj(int type, String path) {
        this.type = type;
        this.path = path;
    }
}
