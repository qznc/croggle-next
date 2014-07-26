package de.croggle.game.level;

import de.croggle.Croggle;
import de.croggle.test.PlatformTestCase;
import de.croggle.test.TestHelper;

public class LevelControllerTest extends PlatformTestCase {

	LevelController controller;

	@Override
	protected void setUp() throws Exception {
		Croggle app = TestHelper.getApp(this);
		controller = new LevelController(0, app);
	}

	public void testSize() {
		assertTrue(controller.getPackageSize() == 12);
	}

	public void testGetter() {
		controller.getPackageIndex();
	}
}
