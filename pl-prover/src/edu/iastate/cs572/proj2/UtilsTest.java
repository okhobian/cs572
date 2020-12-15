package edu.iastate.cs572.proj2;

import static org.junit.Assert.*;

public class UtilsTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void getElements1() {
        String testStr = "( Rain && Outside ) => Wet";
        String[] expected = {"(","Rain","&&", "Outside", ")", "=>", "Wet"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements2() {
        String testStr = "( Warm && ~Rain ) => Pleasant";
        String[] expected = {"(","Warm","&&", "~", "Rain", ")", "=>", "Pleasant"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements3() {
        String testStr = "(Warm&&~Rain)=>Pleasant";
        String[] expected = {"(","Warm","&&", "~", "Rain", ")", "=>", "Pleasant"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements4() {
        String testStr = "~Wet";
        String[] expected = {"~","Wet"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements5() {
        String testStr = "(Warm&&~Rain)<=>Pleasant";
        String[] expected = {"(","Warm","&&", "~", "Rain", ")", "<=>", "Pleasant"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements6() {
        String testStr = "~(Warm&&~Rain)<=>Pleasant";
        String[] expected = {"~","(","Warm","&&", "~", "Rain", ")", "<=>", "Pleasant"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements7() {
        String testStr = "~(Warm&&~Rain)<=>~Pleasant";
        String[] expected = {"~","(","Warm","&&", "~", "Rain", ")", "<=>", "~", "Pleasant"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void getElements8() {
        String testStr = "~(Warm||~Rain)<=>~Pleasant&&Nice";
        String[] expected = {"~","(","Warm","||", "~", "Rain", ")", "<=>", "~", "Pleasant", "&&", "Nice"};
        assertEquals(expected, Utils.getElements(testStr));
    }

    @org.junit.Test
    public void isPartOfOpeartor1() {
        char c = '&';
        assertTrue(Utils.isPartOfOperator(c));
    }

    @org.junit.Test
    public void infixToPostfix1() {
        String[] testStr = {"Warm","=>","Pleasant"};
        String[] expected = {"Warm","Pleasant","=>"};
        assertEquals(expected, Utils.infixToPostfix(testStr));
    }

    @org.junit.Test
    public void infixToPostfix2() {
        String[] testStr = {"~", "Warm", "=>", "Pleasant"};
        String[] expected = {"Warm","~", "Pleasant","=>"};
        assertEquals(expected, Utils.infixToPostfix(testStr));
    }

    @org.junit.Test
    public void infixToPostfix3() {
        //~(𝑃&&~𝑄)||𝑅=>𝑆&&~𝑇    //infix
        // P~Q&&~R||S~T&&=>     // postfix
        String[] testStr = {"~","(","P","&&","~","Q",")","||","R","=>","S","&&","~","T"};
        String[] expected = {"P","Q","~","&&","~","R","||","S","T","~","&&","=>"};
        assertEquals(expected, Utils.infixToPostfix(testStr));
    }

}