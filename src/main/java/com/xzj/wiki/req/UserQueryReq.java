package com.xzj.wiki.req;

public class UserQueryReq extends PageReq {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserQueryReq{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}