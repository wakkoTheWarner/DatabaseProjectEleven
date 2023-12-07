package org.eduardososa.databaseprojecteleven.backend;

public class User {
    private int userID;
    private String userPassword;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userAccountType;

    public User(int userID, String userPassword, String userEmail, String userFirstName, String userLastName, String userAccountType) {
        this.userID = userID;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userAccountType = userAccountType;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }
}
