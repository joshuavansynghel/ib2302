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
	 * receiveTest10:
	 * Non-initiator receives TOKEN: should forward when applicable
	 */
	@Test
	void receiveTest10() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 10; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q0");
		q.init();

		// Receive first token, set parent
		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q0"));

		// Determine which process is the current child; receive ack messages from all other neighbours
		int child = -1;
		Set<Integer> children = new HashSet<Integer>();
		for (int i = 1; i < 10; i++) {
			if (n.getChannel("q0", "q" + i).getContent().size() == 0) {
				assertEquals(-1, child);
				child = i;
				children.add(i);
			} else {
				receiveOrCatch(q, new AckMessage(), n.getChannel("q" + i, "q0"));
			}
		}
		assertEquals(1, children.size());

		// Should forward on receive
		for (int j = 0; j < 5; j++) {
			// Receive token from the last child
			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + child, "q0"));

			// Find the new child
			for (int i = 1; i < 10; i++) {
				if (!children.contains(i) && n.getChannel("q0", "q" + i).getContent().size() == 2) {
					assertTrue(n.getChannel("q0", "q" + i).getContent().toArray()[1] instanceof TokenMessage);

					child = i;
					children.add(i);
					break;
				}
			}

			assertEquals(j + 2, children.size());
		}
	}

}
