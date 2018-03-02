package com.briar.server.model.returnedtobriarclasses;


import java.util.ArrayList;
import java.util.List;

public class BriarUserWithContacts {
    private String userName;
    private String ip;
    private String port;
    private List<BriarUser> contactList;

    public BriarUserWithContacts(String userName, String ip, String port) {
        this.userName = userName;
        this.ip = ip;
        this.port = port;
        this.contactList = new ArrayList<BriarUser>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<BriarUser> getContactList() {
        return contactList;
    }

    public void setContactList(List<BriarUser> contactList) {
        this.contactList = contactList;
    }

    public void addUserToContactList(BriarUser user) {
        this.contactList.add(user);
    }

    @Override
    public String toString() {
        return "BriarUserWithContacts{" +
                "userName='" + userName + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", contactList=" + contactList +
                '}';
    }
}
