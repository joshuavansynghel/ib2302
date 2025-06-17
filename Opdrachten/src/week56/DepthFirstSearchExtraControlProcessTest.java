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
	 * receiveTest4:
	 * Non-initiator receiving TOKEN after finishing: exception.
	 */
	@Test
	void receiveTest4() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		receiveOrCatch(q, new InfoMessage(), n.getChannel("p", "q"));
		receiveOrCatch(q, new InfoMessage(), n.getChannel("r", "q"));

		receiveOrCatch(q, new TokenMessage(), n.getChannel("s", "q"));

		receiveOrCatch(q, new AckMessage(), n.getChannel("p", "q"));
		receiveOrCatch(q, new AckMessage(), n.getChannel("r", "q"));

		assertTrue(q.isPassive());

		assertThrows(IllegalReceiveException.class, () -> q.receive(new TokenMessage(), n.getChannel("p", "q")));
	}

}
