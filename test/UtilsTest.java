package test;

import junit.framework.Assert;
import org.junit.Test;
import pathfinder.CostFunctionType;
import pathfinder.Utils;

import java.awt.*;

public class UtilsTest{

    @Test
    public void testIsEmpty() throws Exception {
        boolean[][] array = new boolean[5][5];

        Assert.assertTrue(Utils.isEmpty(array));

        array[4][4] = true;

        Assert.assertFalse(Utils.isEmpty(array));
    }

    @Test
    public void testTranspose() throws Exception {
        // TODO
    }

    @Test
    public void testFindClosest() throws Exception {
        Point currentPosition = new Point(4, 4);
        boolean[][] array = new boolean[10][10];
        Point closest;
        Point actualClosestForDistance;
        Point actualClosestForTime;
        Point actualClosestForEnergy;

        // same point
        array[4][4] = true;

        actualClosestForDistance = new Point(4, 4);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.DISTANCE);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForDistance));

        actualClosestForEnergy = new Point(4, 4);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.ENERGY);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForEnergy));

        actualClosestForTime = new Point(4, 4);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.TIME);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForTime));

        array[4][4] = false;

        // only one point
        array[9][9] = true;

        actualClosestForDistance = new Point(9, 9);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.DISTANCE);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForDistance));

        actualClosestForEnergy = new Point(9, 9);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.ENERGY);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForEnergy));

        actualClosestForTime = new Point(9, 9);
        closest = Utils.findClosest(currentPosition, array, CostFunctionType.TIME);
        Assert.assertTrue(closest != null && closest.equals(actualClosestForTime));

        array[9][9] = false;

        // more points
        // TODO
    }

    @Test
    public void testFindNeighbour() throws Exception {
        boolean[][] array = new boolean[10][10];
        Point currentPosition = new Point(4, 4);
        Point neighbour;

        array[3][3] = true;
        neighbour = Utils.findNeighbour(currentPosition, array, CostFunctionType.TIME);
        Assert.assertTrue(neighbour != null && neighbour.equals(new Point(3, 3)));
        neighbour = Utils.findNeighbour(currentPosition, array, CostFunctionType.DISTANCE);
        Assert.assertTrue(neighbour == null);

        array[3][4] = true;
        neighbour = Utils.findNeighbour(currentPosition, array, CostFunctionType.TIME);
        Assert.assertTrue(neighbour != null);
        neighbour = Utils.findNeighbour(currentPosition, array, CostFunctionType.DISTANCE);
        Assert.assertTrue(neighbour != null &&neighbour.equals(new Point(3, 4)));
        neighbour = Utils.findNeighbour(currentPosition, array, CostFunctionType.ENERGY);
        Assert.assertTrue(neighbour != null &&neighbour.equals(new Point(3, 4)));

    }
}