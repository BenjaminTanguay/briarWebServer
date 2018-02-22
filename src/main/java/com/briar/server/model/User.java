package com.briar.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private long id;
    private String phoneGeneratedId;
    private String password;
    private String ip;
    private boolean isActive;
    private Timestamp created;
    private Timestamp modified;
    private HashMap<String, User> userContacts;

    public User(long id, String phoneGeneratedId, String password, String ip, boolean isActive, Timestamp created, Timestamp modified) {
        this.id = id;
        this.phoneGeneratedId = phoneGeneratedId;
        this.password = password;
        this.ip = ip;
        this.isActive = isActive;
        this.created = created;
        this.modified = modified;
        this.userContacts = new HashMap<String, User>();
    }

    public User(String phoneGeneratedId, String password, String ip, boolean isActive, Timestamp created, Timestamp modified) {
        this.phoneGeneratedId = phoneGeneratedId;
        this.password = password;
        this.ip = ip;
        this.isActive = isActive;
        this.created = created;
        this.modified = modified;
        this.userContacts = new HashMap<String, User>();
    }

    public User() {
        this.userContacts = new HashMap<String, User>();
    }

    public List<User> getAllContacts(){
        return new ArrayList<User>(this.userContacts.values());
    }

    public boolean removeUserContact(String phoneGeneratedId) {
        boolean userExists = this.userContacts.containsKey(phoneGeneratedId);
        if (userExists) {
            this.userContacts.remove(phoneGeneratedId);
        }
        return userExists;
    }

    public boolean addUserContact(User user) {
        String mapKey = user.getPhoneGeneratedId();
        boolean userDoesNotAlreadyExist = !this.userContacts.containsKey(mapKey);
        if (userDoesNotAlreadyExist) {
            this.userContacts.put(mapKey, user);
        }
        return userDoesNotAlreadyExist;
    }

    public boolean updateUserContact(User user) {
        String mapKey = user.getPhoneGeneratedId();
        boolean userExists = this.userContacts.containsKey(mapKey);
        if (userExists) {
            this.userContacts.replace(mapKey, user);
        }
        return userExists;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneGeneratedId() {
        return phoneGeneratedId;
    }

    public void setPhoneGeneratedId(String phoneGeneratedId) {
        this.phoneGeneratedId = phoneGeneratedId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != user.getId()) return false;
        if (isActive() != user.isActive()) return false;
        if (!getPhoneGeneratedId().equals(user.getPhoneGeneratedId())) return false;
        if (!getPassword().equals(user.getPassword())) return false;
        if (getIp() != null ? !getIp().equals(user.getIp()) : user.getIp() != null) return false;
        if (getCreated() != null ? !getCreated().equals(user.getCreated()) : user.getCreated() != null) return false;
        return getModified() != null ? getModified().equals(user.getModified()) : user.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getPhoneGeneratedId().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + (getIp() != null ? getIp().hashCode() : 0);
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phoneGeneratedId='" + phoneGeneratedId + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", isActive=" + isActive +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
