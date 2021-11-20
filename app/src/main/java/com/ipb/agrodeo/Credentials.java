package com.ipb.agrodeo;

public class Credentials {
    String Username;
    String Password;
    String UnitId;

    Credentials(String username, String password, String unitid){
        this.Username = username;
        this.Password = password;
        this.UnitId = unitid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUnitId(){ return UnitId; }

    public void setUnitId(String unitid){ UnitId = unitid; }
}
