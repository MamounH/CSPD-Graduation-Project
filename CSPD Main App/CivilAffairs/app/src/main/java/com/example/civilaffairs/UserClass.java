package com.example.civilaffairs;

public class UserClass {
    public String nationalityNumber;
    public String nameFirst;
    public String nameMiddle;
    public String nameLast;

    public String gender;
    public String dateOfBirth;
    public String placeOfRegistration;
    public String email;
    public String phone;
    public String password;
    public String socialStatus;
    public String urlImage;


    public UserClass() {
    }



    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public void setNameMiddle(String nameMeddle) {
        this.nameMiddle = nameMeddle;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setNationalityNumber(String nationalityNumber) {
        this.nationalityNumber = nationalityNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPlaceOfRegistration(String placeOfRegistration) {
        this.placeOfRegistration = placeOfRegistration;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setSocialStatus(String socialStatus) {
        this.socialStatus = socialStatus;
    }

    public String getNationalityNumber() {
        return nationalityNumber;
    }



}


