package my.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PositionSelectorTest {

    private PositionSelector positionSelector;

    @Test
    public void randomFreePositionWorksWhenOnlyOneFree() {
        positionSelector = new PositionSelector(5, 5);
        for (int i = 0; i < 24; i++) {
            assertEquals("Should be able to occupy " + i, true, positionSelector.occupy(new int[] { i / 5, i % 5 }));
        }
        for (int i = 0; i < 10; i++) {
            int[] position = positionSelector.randomUnoccupiedPosition();
            assertEquals("Only one value can now be returned " + i + " time. Depth.", 4, position[0]);
            assertEquals("Only one value can now be returned " + i + " time. Width.", 4, position[1]);
        }
        positionSelector.occupy(new int[] { 4, 4 });
        positionSelector.unoccupy(new int[] { 0, 0 });

        for (int i = 0; i < 10; i++) {
            int[] position = positionSelector.randomUnoccupiedPosition();
            assertEquals("Only one value can now be returned " + i + " time. Depth.", 0, position[0]);
            assertEquals("Only one value can now be returned " + i + " time. Width.", 0, position[1]);
        }
    }

    @Test
    public void cannotOccupySameSpotTwiceInARow() {
        positionSelector = new PositionSelector(5, 5);
        assertEquals("Should be able to occupy [0,0]", true, positionSelector.occupy(new int[] { 0, 0 }));
        assertEquals("Should not be able to occupy [0,0] the second time.", false,
                positionSelector.occupy(new int[] { 0, 0 }));
    }

    @Test
    public void cannotUnoccupyUnoccupiedSpot() {
        positionSelector = new PositionSelector(5, 5);
        assertEquals("Should not be able to unoccupy [0,0]", false, positionSelector.unoccupy(new int[] { 0, 0 }));
    }

    @Test
    public void randomFreeOccupyWorks() {
        positionSelector = new PositionSelector(5, 5);
        for (int i = 0; i < 25; i++) {
            int[] position = positionSelector.randomUnoccupiedPosition();
            assertEquals("Should be able to occupy [" + position[0] + ", [" + position[1] + "]", true,
                    positionSelector.occupy(position));
        }
        for (int i = 0; i < 25; i++) {
            assertEquals("Should not be able to occupy any here.", false,
                    positionSelector.occupy(new int[] { i / 5, i % 5 }));
        }
    }
}
