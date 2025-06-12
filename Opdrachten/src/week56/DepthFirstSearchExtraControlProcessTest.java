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
	 * receiveTest3:
	 * Initiator receiving TOKEN after finishing: exception.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 5; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		int child = -1;

		// Receive all acks
		for (int i = 0; i < 5; i++) {
			if (n.getChannel("p", "q" + i).getContent().size() != 0) {
				receiveOrCatch(p, new AckMessage(), n.getChannel("q" + i, "p"));
			} else {
				assertEquals(-1, child);
				child = i;
			}
		}
		assertNotEquals(-1, child);

		// Receive all info
		for (int i = 0; i < 5; i++) {
			if (i != child) {
				receiveOrCatch(p, new InfoMessage(), n.getChannel("q" + i, "p"));
			}
		}

		// Receive token back from child
		receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + child, "p"));

		assertTrue(p.isPassive());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new TokenMessage(), n.getChannel("q17", "p")));
	}

}
