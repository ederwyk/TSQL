package br.com.stockinfo.tsql.update;

import br.com.stockinfo.tsql.example.Person;
import br.com.stockinfo.tsql.query.Update;
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
public class UpdateTest {

    Person person = new Person();
    @Mock Connection mockConnection;
    @Mock PreparedStatement mockStatement;

    @Test public void shouldMatchExampleWithNoRestrictions() {
        person.setFavouriteNumber(10);
        person.setFirstName("Wyk");

        String sql = Update.entity(person)
            .set(Person::getFirstName)
            .set(Person::getFavouriteNumber)
            .toSql("");

        assertEquals("UPDATE Person SET firstName = 'Wyk', favouriteNumber = 10", sql.trim());
    }

    @Test public void ab() {
    	person = new Person();
        person.setFavouriteNumber(null);
        person.setFirstName("Gui");

        person.setCpf("oieee");
        String sql = Update.entity(person)
                .allowNull()
                .toSql("");

        assertEquals("UPDATE Person SET firstName = 'Gui', lastName = NULL, favouriteNumber = NULL", sql.trim());
    }


    @Test public void should_match_example_with_restrictions() {
        person.setFavouriteNumber(10);
        person.setFirstName("Gui");

        String sql = Update.entity(person)
            .set(Person::getFirstName)
            .set(Person::getFavouriteNumber)
            .where(Person::getLastName)
            .eq("weber")
            .where(Person::getFirstName)
            .ne("bob")
            .where(Person::getFirstName)
            .like("b%")
            .toSql("");

        assertEquals("UPDATE Person SET firstName = 'Gui', favouriteNumber = 10 WHERE Person.lastName = 'weber' AND Person.firstName != 'bob' AND Person.firstName LIKE 'b%'", sql.trim());
    }

    @Test public void should_set_values() throws SQLException {
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);

        person.setFirstName("asdf");
        person.setFavouriteNumber(55);

//        Update.entity(person)
//            .set(Person::getFirstName)
//            .set(Person::getFavouriteNumber)
//            .where(Person::getLastName)
//            .eq("weber")
//            .where(Person::getFavouriteNumber)
//            .eq(6)
//            .execute(() -> mockConnection);
//
//        verify(mockStatement).setString(1, "asdf");
//        verify(mockStatement).setInt(2, 55);
//        verify(mockStatement).setString(3, "weber");
//        verify(mockStatement).setInt(4, 6);
    }
}
