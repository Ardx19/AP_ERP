package edu.univ.erp.model;

public class UserDisplay {
    private int userId;
    private String loginName;
    private String userRole;

    // Constructor
    public UserDisplay(int userId, String loginName, String userRole) {
        this.userId = userId;
        this.loginName = loginName;
        this.userRole = userRole;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getLoginName() { return loginName; }
    public String getUserRole() { return userRole; }
}