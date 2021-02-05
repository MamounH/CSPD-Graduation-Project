package com.example.civilaffairs.ui.DepartmentServices.PassportServices.Renewal;

public class RenewalClass {

    public String imagePassport ;
    public String passportNumber;
    public String datePassport ;
    public String country ;
    public String address ;
    public  String status ;
    public String pushKey ;
    public String nationalId;

    public RenewalClass() {
    }

     void setImagePassport(String imagePassport) {
        this.imagePassport = imagePassport;
    }

     void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

     void setDatePassport(String datePassport) {
        this.datePassport = datePassport;
    }

     void setCountry(String country) {
        this.country = country;
    }

     void setAddress(String address) {
        this.address = address;
    }

     void setStatus(String status) {
        this.status = status;
    }

     void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
