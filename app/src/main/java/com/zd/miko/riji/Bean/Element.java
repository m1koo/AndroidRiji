package com.zd.miko.riji.Bean;

public class Element {
    private int elementType;
    private String content;
    private int index;

    @Override
    public String toString() {
        return "Element{" +
                "elementType=" + elementType +
                ", content='" + content + '\'' +
                ", index=" + index +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
