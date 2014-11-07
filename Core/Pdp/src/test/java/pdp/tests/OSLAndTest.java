package pdp.tests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class OSLAndTest {
	private static Logger _logger = LoggerFactory.getLogger(PDPJavaTest.class);

	private static PolicyDecisionPoint _pdp = null;

	private static Map<String,String> _params = new HashMap<>();

	private File settingsFile = new File("uc.properties");

	private static Map<String,String> params = new HashMap<>();

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			new RuntimeException(e.getMessage());
		}
	}


	@Test
	public void test() {

		_pdp = new PolicyDecisionPoint();

		_pdp.deployPolicyURI("src/test/resources/testPolicies/testAnd.xml");

		params.clear();

		/*
		 * This tests the AND operator. The event should be inhibited as soon
		 * as action1 and action2 happened within the same timestep.
		 */

		/*
		 * The first occurrence of action1 is allowed.
		 */
		IResponse res = _pdp.notifyEvent(new EventBasic("action1", params, true));
		Assert.assertTrue(res.isAuthorizationAction(EStatus.ALLOW));

		sleep(50);

		/*
		 * The first occurrence of action2 is inhibited.
		 */
		res = _pdp.notifyEvent(new EventBasic("action2", params, true));
		Assert.assertTrue(res.isAuthorizationAction(EStatus.INHIBIT));

		/*
		 * The second occurrence of action1 is also inhibited
		 */
		res = _pdp.notifyEvent(new EventBasic("action1", params, true));
		Assert.assertTrue(res.isAuthorizationAction(EStatus.INHIBIT));

		sleep(200);

		/*
		 * After waiting for the next timestep, action1 is again allowed.
		 */
		res = _pdp.notifyEvent(new EventBasic("action1", params, true));
		Assert.assertTrue(res.isAuthorizationAction(EStatus.ALLOW));
	}

}