package somethingrandom.notetaker.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class MainTests {
    @Test
    public void addOneAddsOne() {
        assertEquals(Main.addOne(1), 2);
    }
}
