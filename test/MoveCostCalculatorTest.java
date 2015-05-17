import org.junit.Assert;
import org.junit.Test;
import pathfinder.CostFunctionType;
import pathfinder.MoveCostCalculator;

public class MoveCostCalculatorTest {

    private double delta = 0.001;

    @Test
    public void testDistance() throws Exception {
        Assert.assertEquals(0, MoveCostCalculator.distance(0, 0, 0, 0), delta);
        Assert.assertEquals(5, MoveCostCalculator.distance(0, 0, 3, 4), delta);
        Assert.assertEquals(5, MoveCostCalculator.distance(3, 4, 0, 0), delta);
        Assert.assertEquals(3, MoveCostCalculator.distance(3, 3, 3, 6), delta);
        Assert.assertEquals(6, MoveCostCalculator.distance(3, 3, 9, 3), delta);
    }

    @Test
    public void testTime() throws Exception {
        Assert.assertEquals(0, MoveCostCalculator.time(0, 0, 0, 0), delta);
        Assert.assertEquals(4, MoveCostCalculator.time(0, 0, 4, 3), delta);
        Assert.assertEquals(4, MoveCostCalculator.time(4, 3, 0, 0), delta);
        Assert.assertEquals(6, MoveCostCalculator.time(3, 3, 9, 0), delta);
        Assert.assertEquals(3, MoveCostCalculator.time(4, 4, 1, 7), delta);
    }

    @Test
    public void testEnergy() throws Exception {
        Assert.assertEquals(0, MoveCostCalculator.energy(0, 0, 0, 0), delta);
        Assert.assertEquals(7, MoveCostCalculator.energy(0, 0, 3, 4), delta);
        Assert.assertEquals(7, MoveCostCalculator.energy(4, 3, 0, 0), delta);
        Assert.assertEquals(6, MoveCostCalculator.energy(0, 0, 3, 3), delta);
        Assert.assertEquals(6, MoveCostCalculator.energy(0, 0, 0, 6), delta);
    }

    @Test
    public void testAdjacency() throws Exception {
        CostFunctionType costType = CostFunctionType.TIME;
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 1, 1, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 1, 2, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 1, 3, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 2, 1, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 2, 3, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 3, 1, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 3, 2, costType));
        Assert.assertTrue(MoveCostCalculator.arePointsAdjacent(2, 2, 3, 3, costType));

        Assert.assertFalse(MoveCostCalculator.arePointsAdjacent(3, 2, 0, 0, costType));
        Assert.assertFalse(MoveCostCalculator.arePointsAdjacent(3, 2, 1, 4, costType));
    }
}