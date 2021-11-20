package com.ipb.agrodeo;

public class Profile {
    // string variable for storing name.
    public String name;

    // string variable for storing role
    public String role;

    // string variable for storing commodity .
    public String commodity;

    // string variable for storing location .
    public String location;

    // string variable for storing birthdate .
    public String birthdate;

    // string variable for storing sex .
    public String sex;

    // string variable for storing education .
    public String education;

    // string variable for storing religion .
    // public String religion;

    // string variable for storing email .
    public String email;

    // string variable for storing handphone number .
    public String phone;

    // string variable for storing admin .
    public String admin;

    // string variable for storing seller .
    public String seller;

    // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    public Profile() {

    }

    public Profile(String name, String role, String commodity, String location,
                   String birthdate, String sex, String education, String email,
                   String phone, String admin, String seller) {
        this.name = name;
        this.role = role;
        this.commodity = commodity;
        this.location = location;
        this.birthdate = birthdate;
        this.sex = sex;
        this.education = education;
        // this.religion = religion;
        this.email = email;
        this.phone = phone;
        this.admin = admin;
        this.seller = seller;
    }

    // created getter and setter methods
    // for all our variables.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    /*
    public String getReligion() {
        return religion;
    }
     */

    /*
    public void setReligion(String religion) {
        this.religion = religion;
    }
     */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

}
