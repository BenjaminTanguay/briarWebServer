package com.briar.server.model.domainmodelclasses;

public class UserContact {
    private long id;
    private String firstUser;
    private long firstUserId;
    private boolean firstUserContactAcceptance;
    private String secondUser;
    private long secondUserId;
    private boolean secondUserContactAcceptance;

    public UserContact() {

    }

    public UserContact(long id, String firstUser, long firstUserId, boolean firstUserContactAcceptance, String secondUser, long secondUserId, boolean secondUserContactAcceptance) {
        this.id = id;
        this.firstUser = firstUser;
        this.firstUserId = firstUserId;
        this.firstUserContactAcceptance = firstUserContactAcceptance;
        this.secondUser = secondUser;
        this.secondUserId = secondUserId;
        this.secondUserContactAcceptance = secondUserContactAcceptance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(String firstUser) {
        this.firstUser = firstUser;
    }

    public boolean isFirstUserContactAcceptance() {
        return firstUserContactAcceptance;
    }

    public void setFirstUserContactAcceptance(boolean firstUserContactAcceptance) {
        this.firstUserContactAcceptance = firstUserContactAcceptance;
    }

    public String getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(String secondUser) {
        this.secondUser = secondUser;
    }

    public boolean isSecondUserContactAcceptance() {
        return secondUserContactAcceptance;
    }

    public void setSecondUserContactAcceptance(boolean secondUserContactAcceptance) {
        this.secondUserContactAcceptance = secondUserContactAcceptance;
    }

    public long getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(long firstUserId) {
        this.firstUserId = firstUserId;
    }

    public long getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(long secondUserId) {
        this.secondUserId = secondUserId;
    }

    public String getOtherUser(String user) {
        if (user.equals(this.firstUser)) {
            return this.secondUser;
        } else {
            return this.firstUser;
        }
    }

    public boolean isContactBilateral() {
        return firstUserContactAcceptance && secondUserContactAcceptance;
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "id=" + id +
                ", firstUser='" + firstUser + '\'' +
                ", firstUserId=" + firstUserId +
                ", firstUserContactAcceptance=" + firstUserContactAcceptance +
                ", secondUser='" + secondUser + '\'' +
                ", secondUserId=" + secondUserId +
                ", secondUserContactAcceptance=" + secondUserContactAcceptance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserContact)) return false;

        UserContact that = (UserContact) o;

        if (getId() != that.getId()) return false;
        if (getFirstUserId() != that.getFirstUserId()) return false;
        if (isFirstUserContactAcceptance() != that.isFirstUserContactAcceptance()) return false;
        if (getSecondUserId() != that.getSecondUserId()) return false;
        if (isSecondUserContactAcceptance() != that.isSecondUserContactAcceptance()) return false;
        if (getFirstUser() != null ? !getFirstUser().equals(that.getFirstUser()) : that.getFirstUser() != null)
            return false;
        return getSecondUser() != null ? getSecondUser().equals(that.getSecondUser()) : that.getSecondUser() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getFirstUser() != null ? getFirstUser().hashCode() : 0);
        result = 31 * result + (int) (getFirstUserId() ^ (getFirstUserId() >>> 32));
        result = 31 * result + (isFirstUserContactAcceptance() ? 1 : 0);
        result = 31 * result + (getSecondUser() != null ? getSecondUser().hashCode() : 0);
        result = 31 * result + (int) (getSecondUserId() ^ (getSecondUserId() >>> 32));
        result = 31 * result + (isSecondUserContactAcceptance() ? 1 : 0);
        return result;
    }

    @Override
    public UserContact clone() {
        return new UserContact(id, firstUser, firstUserId, firstUserContactAcceptance, secondUser, secondUserId, secondUserContactAcceptance);
    }

    public void copy(UserContact userContact) {
        this.id = userContact.getId();
        this.firstUser = userContact.getFirstUser();
        this.firstUserId = userContact.getFirstUserId();
        this.firstUserContactAcceptance = userContact.isFirstUserContactAcceptance();
        this.secondUser = userContact.getSecondUser();
        this.secondUserId = userContact.getSecondUserId();
        this.secondUserContactAcceptance = userContact.isSecondUserContactAcceptance();
    }
}
