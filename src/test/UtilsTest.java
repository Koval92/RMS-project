package test;

import junit.framework.Assert;
import junit.framework.TestCase;
import production.Utils;

public class UtilsTest extends TestCase {

    public void testIsEmpty() throws Exception {
        boolean[][] array = new boolean[5][5];

        Assert.assertTrue(Utils.isEmpty(array));

        array[4][4] = true;

        Assert.assertFalse(Utils.isEmpty(array));
    }

    public void testTranspose() throws Exception {
        // TODO
    }
}