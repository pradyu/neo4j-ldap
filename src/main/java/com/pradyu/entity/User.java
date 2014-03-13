package com.pradyu.entity;

import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class User extends Entity {

    private boolean isSelectedUser;

    public User() {
        super();
    }

    public User(String name) {
        super(Type.USER, name, name);
    }

    public User(String canonicalName, String name) {
        super(Type.USER, name, canonicalName);
    }

    public boolean isSelectedUser() {
        return isSelectedUser;
    }

    public void setSelectedUser(boolean selectedUser) {
        isSelectedUser = selectedUser;
    }


}
