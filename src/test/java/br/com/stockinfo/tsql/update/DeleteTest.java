package br.com.stockinfo.tsql.update;

import br.com.stockinfo.tsql.example.Person;
import br.com.stockinfo.tsql.query.Delete;
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
public class DeleteTest {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockStatement;

    @Test public void shouldatchExampleWithNoRestrictions() {
        String sql = Delete.from(Person.class).toSql("");
        assertEquals("DELETE FROM Person", sql.trim());
    }

    @Test public void shouldMatchExampleWithRestrictions() {
        String sql = Delete.from(Person.class)
            .where(Person::getLastName)
            .eq("weber")
            .where(Person::getFavouriteNumber)
            .eq(6)
            .where(Person::getFirstName)
            .like("%w%")
            .where(Person::getFirstName)
            .ne("bob")
            .toSql("");

        assertEquals("DELETE FROM Person WHERE Person.lastName = 'weber' AND Person.favouriteNumber = 6 AND Person.firstName LIKE '%w%' AND Person.firstName != 'bob'", sql.trim());
    }

    @Test public void joinExample() {
//        String sql = Delete.from(relationship(Conspiracy.class, Person.class))
//                .whereLeft(Conspiracy::getName)
//                .equalTo("nsa")
//                .andRight(Person::getLastName)
//                .equalTo("smith")
//                .toSql();
//
//        assertEquals("DELETE FROM conspiracy_person WHERE conspiracy_name = ? AND person_last_name = ?", sql.trim());
    }

    @Test public void shouldSetValues() throws SQLException {
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);

//        Delete.from(Person.class)
//                .where(Person::getLastName)
//                .eq("weber")
//                .where(Person::getFavouriteNumber)
//                .eq(6)
//                .execute(() -> mockConnection);
//
//        verify(mockStatement).setString(1,"weber");
//        verify(mockStatement).setInt(2,6);
    }
}
