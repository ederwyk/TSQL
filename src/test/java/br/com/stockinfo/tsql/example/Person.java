package br.com.stockinfo.tsql.example;

import br.com.stockinfo.tsql.annotations.Column;
import br.com.stockinfo.tsql.annotations.Table;

@Table(dataSource = "")
public class Person {
    @Column(dataSource = "")
    private String firstName;
    @Column(dataSource = "")
    private String lastName;
    @Column(dataSource = "")
    private Integer favouriteNumber;

    private String cpf;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public Integer getFavouriteNumber() {
        return favouriteNumber;
    }

    public void setFavouriteNumber(Integer favouriteNumber) {
        this.favouriteNumber = favouriteNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (favouriteNumber != null ? !favouriteNumber.equals(person.favouriteNumber) : person.favouriteNumber != null)
            return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (favouriteNumber != null ? favouriteNumber.hashCode() : 0);
        return result;
    }
}
