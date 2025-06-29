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
	 * receiveTest17:
	 * Initiator should first wait for all ACKs, and then send TOKEN to child
	 */
	@Test
	void receiveTest17() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		String child = "";
		String nonchild1 = "";
		String nonchild2 = "";
		if (n.getChannel("p", "q").getContent().size() == 0) {
			child = "q";
			nonchild1 = "r";
			nonchild2 = "s";
		} else if (n.getChannel("p", "r").getContent().size() == 0) {
			child = "r";
			nonchild1 = "q";
			nonchild2 = "s";
		} else {
			child = "s";
			nonchild1 = "q";
			nonchild2 = "r";
		}

		assertEquals(0, n.getChannel("p", child).getContent().size());

		receiveOrCatch(p, new AckMessage(), n.getChannel(nonchild1, "p"));
		assertEquals(0, n.getChannel("p", child).getContent().size());

		receiveOrCatch(p, new AckMessage(), n.getChannel(nonchild2, "p"));
		assertEquals(1, n.getChannel("p", child).getContent().size());
		assertTrue(n.getChannel("p", child).getContent().iterator().next() instanceof TokenMessage);
	}

	
	
}
