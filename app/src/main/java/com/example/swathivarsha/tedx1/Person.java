package com.example.swathivarsha.tedx1;

/**
 * Created by Varsha on 23-10-2017.
 */

public class Person {
    String name;
    String email;
    String phone;

    public Person() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
