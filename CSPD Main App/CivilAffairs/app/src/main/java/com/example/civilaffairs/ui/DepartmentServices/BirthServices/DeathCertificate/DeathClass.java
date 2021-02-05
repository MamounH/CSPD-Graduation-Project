package com.example.civilaffairs.ui.DepartmentServices.BirthServices.DeathCertificate;

public class DeathClass {
    public String cardImageUrl;
    public String relationDeath;
    public String imageFamilyUrl;
    public String name ;
    public String status;
    public String pushKey;
    public String nationalId;


    public DeathClass() {
    }

     void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

     void setRelationDeath(String relationDeath) {
        this.relationDeath = relationDeath;
    }

     void setImageFamilyUrl(String imageFamilyUrl) {
        this.imageFamilyUrl = imageFamilyUrl;
    }

     void setName(String name) {
        this.name = name;
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
