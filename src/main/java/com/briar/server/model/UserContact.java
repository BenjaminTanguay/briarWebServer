package com.briar.server.model;

import java.sql.Timestamp;

public class UserContact {
    private long id;
    private User firstUser;
    private boolean firstUserContactAcceptance;
    private User secondUser;
    private boolean secondUserContactAcceptance;
    private boolean isActive;
    private Timestamp created;
    private Timestamp modified;

    public UserContact() {

    }

    public UserContact(long id, User firstUser, boolean firstUserContactAcceptance, User secondUser, boolean secondUserContactAcceptance, boolean isActive, Timestamp created, Timestamp modified) {
        this.id = id;
        this.firstUser = firstUser;
        this.firstUserContactAcceptance = firstUserContactAcceptance;
        this.secondUser = secondUser;
        this.secondUserContactAcceptance = secondUserContactAcceptance;
        this.isActive = isActive;
        this.created = created;
        this.modified = modified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public boolean isFirstUserContactAcceptance() {
        return firstUserContactAcceptance;
    }

    public void setFirstUserContactAcceptance(boolean firstUserContactAcceptance) {
        this.firstUserContactAcceptance = firstUserContactAcceptance;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public boolean isSecondUserContactAcceptance() {
        return secondUserContactAcceptance;
    }

    public void setSecondUserContactAcceptance(boolean secondUserContactAcceptance) {
        this.secondUserContactAcceptance = secondUserContactAcceptance;
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
        if (!(o instanceof UserContact)) return false;

        UserContact that = (UserContact) o;

        if (getId() != that.getId()) return false;
        if (isFirstUserContactAcceptance() != that.isFirstUserContactAcceptance()) return false;
        if (isSecondUserContactAcceptance() != that.isSecondUserContactAcceptance()) return false;
        if (isActive() != that.isActive()) return false;
        if (!getFirstUser().equals(that.getFirstUser())) return false;
        if (!getSecondUser().equals(that.getSecondUser())) return false;
        if (getCreated() != null ? !getCreated().equals(that.getCreated()) : that.getCreated() != null) return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getFirstUser().hashCode();
        result = 31 * result + (isFirstUserContactAcceptance() ? 1 : 0);
        result = 31 * result + getSecondUser().hashCode();
        result = 31 * result + (isSecondUserContactAcceptance() ? 1 : 0);
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "id=" + id +
                ", firstUser=" + firstUser +
                ", firstUserContactAcceptance=" + firstUserContactAcceptance +
                ", secondUser=" + secondUser +
                ", secondUserContactAcceptance=" + secondUserContactAcceptance +
                ", isActive=" + isActive +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }

    public User getOtherUser(User user) {
        if (user.equals(this.firstUser)) {
            return this.secondUser;
        } else {
            return this.firstUser;
        }
    }

    public boolean isContactBilateral() {
        return firstUserContactAcceptance && secondUserContactAcceptance && isActive;
    }


}
