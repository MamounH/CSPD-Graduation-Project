package com.example.civilaffairs.ui.DepartmentServices.Provider;

public class ProviderClass {

    String status ;
    String nameStatus ;
    String pushKey ;

    public ProviderClass() {
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getNameStatus() {
        return nameStatus;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
