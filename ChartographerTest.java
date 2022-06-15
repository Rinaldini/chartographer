package ru.gnkoshelev.kontur.intern;

import org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChartographerTest {

    Chartographer chartographer = new Chartographer();
    String query = "x=25&y=50&width=100&height=200";


    @Test
    public void testGetX() {
        int expected = 25;
        int actual = chartographer.getX(query);
        assertEquals("Error", expected, actual);
    }

    @Test
    public void testGetY() {
        int expected = 50;
        int actual = chartographer.getY(query);
        assertEquals("Error", expected, actual);
    }

    @Test
    public void testGetWidth() {
        int expected = 100;
        int actual = chartographer.getWidth(query);
        assertEquals("Error", expected, actual);
    }

    @Test
    public void testGetHeight() {
        int expected = 200;
        int actual = chartographer.getHeight(query);
        assertEquals("Error", expected, actual);
    }

    @Test
    public void testGetId() {
        String s = "/chartas/0/?x=25&y=50&width=100&height=200";
        String expected = "0";
        String actual = chartographer.getId(s);
        assertEquals("Error", expected, actual);
    }
}
