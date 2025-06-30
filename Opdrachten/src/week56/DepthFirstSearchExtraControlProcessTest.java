package week56;

import static org.junit.jupiter.api.Assertions.*;
import static framework.ProcessTests.*;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import framework.IllegalReceiveException;
import framework.Message;
import framework.Channel;
import framework.Network;

class DepthFirstSearchExtraControlProcessTest {

	/**
	 * simulationTest1:
	 * Simulate a full run of the protocol.
	 * All processes should be finished at the end.
	 */
	@Test
	void simulationTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 2; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();
		Map<String, Collection<String>> output = new HashMap<String, Collection<String>>();

		try {
			assertTrue(n.simulate(output));
		} catch (IllegalReceiveException e) {
			e.printStackTrace();
			System.exit(0);
			assertTrue(false);
		}

		// No output, check internal state
		// All processes should have finished
		assertTrue(((WaveProcess) n.getProcess("p")).isPassive());
		for (int i = 0; i < 2; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}

}
