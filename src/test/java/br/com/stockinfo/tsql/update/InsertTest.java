package br.com.stockinfo.tsql.update;

import br.com.stockinfo.tsql.example.Conspiracy;
import br.com.stockinfo.tsql.example.Person;
import br.com.stockinfo.tsql.query.Insert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InsertTest {

    Person person = new Person();
    @Mock Connection mockConnection;
    @Mock PreparedStatement mockStatement;

    @Test public void should_match_example() {
        person.setFirstName("Gui");
        person.setFavouriteNumber(10);

        String sql = Insert.entity(person)
            .set(Person::getFirstName)
            .set(Person::getFavouriteNumber)
                //.where(Person::getFavouriteNumber).equalTo(10)
            .toSql("");

        assertEquals("INSERT INTO Person (firstName, favouriteNumber) VALUES ('Gui', 10)", sql.trim());
    }

    @Test public void joinExample() {
        Person smith = new Person("agent","smith");
        Conspiracy nsa = new Conspiracy("nsa");
//
//        String sql = Insert.entity(nsa, smith)
//                .valueLeft(Conspiracy::getName)
//                .valueRight(Person::getLastName)
//                .valueRight(Person::getFirstName)
//                .toSql();
//
//        assertEquals("INSERT INTO conspiracy_person (conspiracy_name, person_last_name, person_first_name) VALUES ( ?, ?, ? )", sql.trim());
    }

    @Test public void shouldSetValues() throws SQLException {
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);

        person.setFirstName("asdf");
        person.setFavouriteNumber(55);
//
//        Insert.entity(person)
//            .set(Person::getFirstName)
//            .set(Person::getFavouriteNumber)
//            .execute(() -> mockConnection);
//
//        verify(mockStatement).setString(1, "asdf");
//        verify(mockStatement).setInt(2, 55);
    }
}
