package com.github.jradolfo.ajunte;

import java.util.Arrays;

public class Tag {
    private String content;
    private String type;
    private String[] attributes;

    public Tag(String content, String type, String[] attributes) {
        this.content = content;
        this.type = type;
        this.attributes = attributes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }
}
