package br.com.stockinfo.tsql.query;

import br.com.stockinfo.tsql.example.Person;
import br.com.stockinfo.tsql.interfaces.Modifiyer;
import br.com.stockinfo.tsql.struct.projection.SQLFunction;
import br.com.stockinfo.tsql.results.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.stockinfo.tsql.results.Mapper.mapper;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SelectTest {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockStatement;
    @Mock ResultSet mockResults;
    Mapper<Person> personMapper = mapper(Person::new).set(Person::setFirstName).set(Person::setLastName).set(Person::setFavouriteNumber);

    @Test public void shouldMatchExample() {
        String sql = Select.from(Person.class)
                .where(Person::getFirstName).eq("benji")
                .where(Person::getLastName).ne("foo")
                .where(Person::getLastName).like("web%")
                .where(Person::getFavouriteNumber).eq(5)
                .toSql();

        assertEquals("SELECT Person.firstName as \"Person.firstName\", Person.lastName as \"Person.lastName\", Person.favouriteNumber as \"Person.favouriteNumber\" FROM Person WHERE Person.firstName = 'benji' AND Person.lastName != 'foo' AND Person.lastName LIKE 'web%' AND Person.favouriteNumber = 5", sql.trim());
    }

    @Test public void a(){
        String sql = Select.from(Person.class)
                .orderByDesc(Person::getLastName, SQLFunction.MAX).toSql();

        assertEquals("SELECT Person.firstName as \"Person.firstName\", Person.lastName as \"Person.lastName\", Person.favouriteNumber as \"Person.favouriteNumber\" FROM Person ORDER BY MAX(Person.lastName) DESC", sql);
    }

    @Test public void ab(){
        String sql = Select.from(Person.class)
                .where(Person::getFirstName, Modifiyer.LOWER).eq("batata")
                .orderByDesc(Person::getLastName, SQLFunction.MAX).toSql();

        assertEquals("SELECT Person.firstName as \"Person.firstName\", Person.lastName as \"Person.lastName\", Person.favouriteNumber as \"Person.favouriteNumber\" FROM Person WHERE LOWER(Person.firstName) = 'batata' ORDER BY MAX(Person.lastName) DESC", sql);
    }

    @Test public void oie(){
        String sql = Select.from(Person.class)
                .project(Person::getFavouriteNumber, SQLFunction.SUM)
                .project(Person::getLastName, SQLFunction.MAX)
                .where(Person::getLastName).like("batata")
                .orderBy(Person::getFirstName)
                .orderBy(Person::getFirstName, SQLFunction.SUM)
                .groupBy(Person::getFirstName)
                .orderByDesc(Person::getFirstName).toSql();

        assertEquals("SELECT SUM(Person.favouriteNumber) as \"Person.favouriteNumber\", MAX(Person.lastName) as \"Person.lastName\" FROM Person WHERE Person.lastName LIKE 'batata' GROUP BY Person.firstName ORDER BY Person.firstName, SUM(Person.firstName), Person.firstName DESC", sql);

    }

    @Test public void shouldAllowJoins() {
//        String sql = from(Person.class)
//                .where(Person::getLastName)
//                .eq("smith")
//                .join(relationship(Conspiracy.class, Person.class).invert())
//                .using(Person::getFirstName, Person::getLastName)
//                .join(Conspiracy.class)
//                .on(Conspiracy::getName)
//                .where(Conspiracy::getName)
//                .eq("nsa")
//                .toSql();

//        assertEquals(
//            "SELECT * FROM person " +
//            "JOIN conspiracy_person " +
//            "ON person.first_name = conspiracy_person.person_first_name " +
//            "AND person.last_name = conspiracy_person.person_last_name " +
//            "JOIN conspiracy " +
//            "ON conspiracy_person.conspiracy_name = conspiracy.name " +
//            "WHERE person.last_name = ? " +
//            "AND conspiracy.name = ?",
//
//            sql
//        );
    }

    @Test public void shouldSetValues() throws SQLException {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResults);
//
//        Optional<Person> result = Select.from(Person.class)
//                .where(Person::getFirstName)
//                .eq("benji")
//                .where(Person::getLastName)
//                .ne("foo")
//                .where(Person::getLastName)
//                .like("web%")
//                .where(Person::getFavouriteNumber)
//                .eq(5)
//                .select(personMapper, () -> mockConnection);
//
//        verify(mockStatement).setString(1,"benji");
//        verify(mockStatement).setString(2,"foo");
//        verify(mockStatement).setString(3,"web%");
//        verify(mockStatement).setInt(4, 5);
    }

    @Test public void shouldMapResults() throws SQLException {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResults);
//        when(mockResults.next()).thenReturn(true);
//        when(mockResults.getObject("first_name")).thenReturn("fname");
//        when(mockResults.getObject("last_name")).thenReturn("lname");
//        when(mockResults.getObject("favourite_number")).thenReturn(9001);
//
//        Optional<Person> result = Select.from(Person.class)
//                .where(Person::getFirstName)
//                .eq("benji")
//                .where(Person::getLastName)
//                .ne("foo")
//                .where(Person::getLastName)
//                .like("web%")
//                .where(Person::getFavouriteNumber)
//                .eq(5)
//                .select(personMapper, () -> mockConnection);
//
//        assertEquals("fname", result.get().getFirstName());
//        assertEquals("lname", result.get().getLastName());
//        assertEquals((Integer)9001, result.get().getFavouriteNumber());
    }

    @Test public void shouldMapResultsList() throws SQLException {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
//        when(mockStatement.executeQuery()).thenReturn(mockResults);
//        when(mockResults.next()).thenReturn(true).thenReturn(false);
//        when(mockResults.getObject("first_name")).thenReturn("fname");
//        when(mockResults.getObject("last_name")).thenReturn("lname");
//        when(mockResults.getObject("favourite_number")).thenReturn(9001);
//
//        List<Person> result = Select.from(Person.class)
//                .where(Person::getFirstName)
//                .eq("benji")
//                .where(Person::getLastName)
//                .ne("foo")
//                .where(Person::getLastName)
//                .like("web%")
//                .where(Person::getFavouriteNumber)
//                .eq(5)
//            //    .list(personMapper, () -> mockConnection);
//
//        assertEquals(1, result.size());
//        assertEquals("fname", result.get(0).getFirstName());
//        assertEquals("lname", result.get(0).getLastName());
//        assertEquals((Integer)9001, result.get(0).getFavouriteNumber());
    }


}
