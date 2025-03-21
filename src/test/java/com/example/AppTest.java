package com.example;

import draw.core.Form;
import draw.core.GroupForm;
import draw.core.Rectangle;
import draw.core.RegularPolygon;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testRectangleTranslation() {
        Rectangle r = new Rectangle(0, 0, 10, 10, 0, 0, 0);
        r.translate(10, 10);
        assertEquals(10.0f, r.getX());
        assertEquals(10.0f, r.getY());
    }

    public void testPolygonRotation() {
        RegularPolygon poly = new RegularPolygon(0, 0, 5, 50, 0, 255, 0);
        poly.rotate(45);
        assertEquals(45.0f, poly.getRotate());
    }

    public void testSetColor() {
        Rectangle r = new Rectangle(0, 0, 10, 10, 0, 0, 0);
        r.setColor(255, 255, 255);
        int[] color = r.getColor();
        assertEquals(255, color[0]);
        assertEquals(255, color[1]);
        assertEquals(255, color[2]);
    }

    public void testRectangleResize() {
        Rectangle r = new Rectangle(0, 0, 10, 10, 0, 0, 0);
        r.resize(20, 20);
        assertEquals(20, r.getWidth());
        assertEquals(20, r.getHeight());
    }

    public void testAddAndGetChild() {
        Form forms = new GroupForm();
        Form rect = new Rectangle(0, 0, 10, 10, 255, 0, 0);
        forms.add(rect);
        assertEquals(rect, forms.getChild(0));
    }
}
