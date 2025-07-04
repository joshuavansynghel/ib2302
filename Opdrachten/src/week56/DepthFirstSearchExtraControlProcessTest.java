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
	 * initTest1:
	 * In the initiator (with more than 2 processes),
	 * init() should send an INFO message
	 * to all neighbors except one (the chosen child).
	 */
	@Test
	void initTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 38; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertTrue(p.isActive());
		assertFalse(p.isPassive());

		int sum = 0;
		for (Channel d : p.getOutgoing()) {
			if (d.getContent().size() > 0) {
				assertEquals(1, d.getContent().size());
				assertTrue(d.getContent().iterator().next() instanceof InfoMessage);
				sum++;
			}
		}
		assertEquals(37, sum);
	}

	/**
	 * initTest2:
	 * In a 2-node network, the initiator should send only the TOKEN
	 * to its neighbor and remain active without sending INFO messages.
	 */
	@Test
	void initTest2() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertTrue(p.isActive());
		assertFalse(p.isPassive());

		assertEquals(1, n.getChannel("p", "q").getContent().size());
		assertTrue(n.getChannel("p", "q").getContent().iterator().next() instanceof TokenMessage);
	}

	/**
	 * initTest3:
	 * Non-initiators must not send any messages on init()
	 * and must stay active until they first receive the token.
	 */
	@Test
	void initTest3() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());

		for (Channel d : q.getOutgoing()) {
			assertEquals(0, d.getContent().size());
		}
	}

	/**
	 * receiveTest1:
	 * Receiving an invalid message type must throw IllegalReceiveException.
	 */
	@Test
	void receiveTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertThrows(IllegalReceiveException.class, () -> p.receive(Message.DUMMY, n.getChannel("q", "p")));
	}

	/**
	 * receiveTest2:
	 * Non-initiator receiving invalid message type also throws.
	 */
	@Test
	void receiveTest2() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertThrows(IllegalReceiveException.class, () -> q.receive(Message.DUMMY, n.getChannel("p", "q")));
	}

	/**
	 * receiveTest3:
	 * Initiator receiving TOKEN after finishing: exception.
	 */
	@Test
	void receiveTest3() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		int child = -1;

		// Receive all acks
		for (int i = 0; i < 100; i++) {
			if (n.getChannel("p", "q" + i).getContent().size() != 0) {
				receiveOrCatch(p, new AckMessage(), n.getChannel("q" + i, "p"));
			} else {
				assertEquals(-1, child);
				child = i;
			}
		}
		assertNotEquals(-1, child);

		// Receive all info
		for (int i = 0; i < 100; i++) {
			if (i != child) {
				receiveOrCatch(p, new InfoMessage(), n.getChannel("q" + i, "p"));
			}
		}

		// Receive token back from child
		receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + child, "p"));

		assertTrue(p.isPassive());

		assertThrows(IllegalReceiveException.class, () -> p.receive(new TokenMessage(), n.getChannel("q17", "p")));
	}

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

	/**
	 * receiveTest5:
	 * With more than two processes in the network,
	 * on first TOKEN receive, non-initiator should not finish
	 * (need to send INFO to all other neighbors).
	 */
	@Test
	void receiveTest5() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		assertTrue(q.isActive());
		assertFalse(q.isPassive());
	}


	/**
	 * receiveTest6:
	 * With more than two processes in the network,
	 * on first TOKEN receive, non-initiator should
	 * broadcast INFO to all other neighbors.
	 */
	@Test
	void receiveTest6() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());
		assertEquals(0, n.getChannel("q", "s").getContent().size());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		// p is parent, one of r,s is future child, the other should receive an info message
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(1, n.getChannel("q", "r").getContent().size() + n.getChannel("q", "s").getContent().size());
		if (n.getChannel("q", "r").getContent().size() > 0) {
			assertTrue(n.getChannel("q", "r").getContent().iterator().next() instanceof InfoMessage);
		} else {
			assertTrue(n.getChannel("q", "s").getContent().iterator().next() instanceof InfoMessage);
		}

		assertFalse(q.isPassive());
	}

	/**
	 * receiveTest7:
	 * With more than two processes in the network,
	 * on first TOKEN receive, non-initiator should
	 * directly forward the token if no INFO broadcast
	 * is needed.
	 */
	@Test
	void receiveTest7() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(0, n.getChannel("q", "r").getContent().size());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		// p is parent and r is future child, so should just forward
		assertEquals(0, n.getChannel("q", "p").getContent().size());
		assertEquals(1, n.getChannel("q", "r").getContent().size());
		assertTrue(n.getChannel("q", "r").getContent().iterator().next() instanceof TokenMessage);

		assertFalse(q.isPassive());
	}

	/**
	 * receiveTest8:
	 * On first TOKEN receive, non-initiator that
	 * can find no candidate for a child should
	 * return the TOKEN to the parent, then finish.
	 */
	@Test
	void receiveTest8() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertTrue(q.isActive());
		assertFalse(q.isPassive());
		assertEquals(0, n.getChannel("q", "p").getContent().size());

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		// p is parent and there are no other neighbours: q should return and finish
		assertFalse(q.isActive());
		assertTrue(q.isPassive());
		assertEquals(1, n.getChannel("q", "p").getContent().size());
		assertTrue(n.getChannel("q", "p").getContent().iterator().next() instanceof TokenMessage);
	}

	/**
	 * receiveTest9:
	 * Initiator has chosen a child, and sent all INFO messages,
	 * as well as the TOKEN to the child.
	 * All ACK have been received.
	 * When the TOKEN is received from the current child: a new
	 * child must be chosen among the available candidates.
	 */
	@Test
	void receiveTest9() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		// Determine which process is the current child; receive ack messages from all other neighbours
		int child = -1;
		Set<Integer> children = new HashSet<Integer>();
		for (int i = 0; i < 100; i++) {
			if (n.getChannel("p", "q" + i).getContent().size() == 0) {
				assertEquals(-1, child);
				child = i;
				children.add(i);
			} else {
				receiveOrCatch(p, new AckMessage(), n.getChannel("q" + i, "p"));
			}
		}
		assertEquals(1, children.size());

		// Should forward on receive
		for (int j = 0; j < 50; j++) {
			// Receive token from the last child
			receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + child, "p"));

			// Find the new child
			for (int i = 0; i < 100; i++) {
				if (!children.contains(i) && n.getChannel("p", "q" + i).getContent().size() == 2) {
					assertTrue(n.getChannel("p", "q" + i).getContent().toArray()[1] instanceof TokenMessage);

					child = i;
					children.add(i);
					break;
				}
			}

			assertEquals(j + 2, children.size());
		}
	}

	/**
	 * receiveTest10:
	 * Non-initiator receives TOKEN: should forward when applicable
	 */
	@Test
	void receiveTest10() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 100; i++) {
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
		for (int i = 1; i < 100; i++) {
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
		for (int j = 0; j < 50; j++) {
			// Receive token from the last child
			receiveOrCatch(q, new TokenMessage(), n.getChannel("q" + child, "q0"));

			// Find the new child
			for (int i = 1; i < 100; i++) {
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

	/**
	 * receiveTest11:
	 * Initiator receives TOKEN: should finish when applicable
	 */
	@Test
	void receiveTest11() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		assertFalse(p.isPassive());

		int child = -1;

		// Receive ack from all neighbours except child
		for (int i = 0; i < 100; i++) {
			if (n.getChannel("p", "q" + i).getContent().size() != 0) {
				receiveOrCatch(p, new AckMessage(), n.getChannel("q" + i, "p"));
			} else {
				assertEquals(-1, child);
				child = i;
			}
		}
		assertNotEquals(-1, child);

		// Receive info from all neighbours except child
		for (int i = 0; i < 100; i++) {
			if (i != child) {
				receiveOrCatch(p, new InfoMessage(), n.getChannel("q" + i, "p"));
			}
		}

		assertFalse(p.isPassive());

		// Receive token back from child: should finish
		receiveOrCatch(p, new TokenMessage(), n.getChannel("q" + child, "p"));

		assertTrue(p.isPassive());
	}

	/**
	 * receiveTest12:
	 * Non-initiator receives TOKEN: return to parent and finish when applicable
	 */
	@Test
	void receiveTest12() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		// Parent is p, child is either r or s. Determine which and receive ack and info from the other.
		String child = "r";
		if (n.getChannel("q", "r").getContent().size() > 0) {
			child = "s";
			receiveOrCatch(q, new AckMessage(), n.getChannel("r", "q"));
			receiveOrCatch(q, new InfoMessage(), n.getChannel("r", "q"));
		} else {
			receiveOrCatch(q, new AckMessage(), n.getChannel("s", "q"));
			receiveOrCatch(q, new InfoMessage(), n.getChannel("s", "q"));
		}

		assertFalse(q.isPassive());
		assertEquals(0, n.getChannel("q", "p").getContent().size());

		// Receive token back from child: should now finish and return token to p.
		receiveOrCatch(q, new TokenMessage(), n.getChannel(child, "q"));

		assertTrue(q.isPassive());
		assertEquals(1, n.getChannel("q", "p").getContent().size());
		assertTrue(n.getChannel("q", "p").getContent().iterator().next() instanceof TokenMessage);
	}

	/**
	 * receiveTest13:
	 * Initiator receives INFO: should return ACK
	 */
	@Test
	void receiveTest13() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 100; i++) {
			n.addProcess("q" + i, "week56.DepthFirstSearchExtraControlNonInitiator");
		}
		n.makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		int child = -1;
		int nonchild = -1;

		// Receive ack from all neighbours except child. Pick one non-child neighbour.
		for (int i = 0; i < 100; i++) {
			if (n.getChannel("p", "q" + i).getContent().size() != 0) {
				receiveOrCatch(p, new AckMessage(), n.getChannel("q" + i, "p"));
				nonchild = i;
			} else {
				assertEquals(-1, child);
				child = i;
			}
		}

		assertEquals(1, n.getChannel("p", "q" + nonchild).getContent().size());

		// Receive info from chosen non-child. Should return ack.
		receiveOrCatch(p, new InfoMessage(), n.getChannel("q" + nonchild, "p"));

		assertEquals(2, n.getChannel("p", "q" + nonchild).getContent().size());
		assertTrue(n.getChannel("p", "q" + nonchild).getContent().toArray()[1] instanceof AckMessage);
	}

	/**
	 * receiveTest14:
	 * Non-initiator receives INFO: should return ACK
	 */
	@Test
	void receiveTest14() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		assertEquals(0, n.getChannel("q", "r").getContent().size());

		receiveOrCatch(q, new InfoMessage(), n.getChannel("r", "q"));

		assertEquals(1, n.getChannel("q", "r").getContent().size());
		assertTrue(n.getChannel("q", "r").getContent().iterator().next() instanceof AckMessage);
	}

	/**
	 * receiveTest15:
	 * Initiator receives unexpected ACK: should throw exception
	 */
	@Test
	void receiveTest15() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess p = (WaveProcess) n.getProcess("p");
		p.init();

		// Determine child and pick one non-child from q,r,s
		String child = "";
		String nonchild = "";
		if (n.getChannel("p", "q").getContent().size() == 0) {
			child = "q";
			nonchild = "r";
		} else if (n.getChannel("p", "r").getContent().size() == 0) {
			child = "r";
			nonchild = "q";
		} else {
			child = "s";
			nonchild = "q";
		}

		// Should not receive ack from child
		final String fChild = child;
		assertThrows(IllegalReceiveException.class, () -> p.receive(new AckMessage(), n.getChannel(fChild, "p")));

		// Should not receive two acks from chosen non-child
		final String fNonchild = nonchild;
		receiveOrCatch(p, new AckMessage(), n.getChannel(nonchild, "p"));
		assertThrows(IllegalReceiveException.class, () -> p.receive(new AckMessage(), n.getChannel(fNonchild, "p")));
	}

	/**
	 * receiveTest16:
	 * Non-initiator receives unexpected ACK: should throw exception
	 */
	@Test
	void receiveTest16() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		// Pick one non-child from r,s
		String nonchild = "";
		if (n.getChannel("q", "s").getContent().size() == 0) {
			nonchild = "r";
		} else {
			nonchild = "s";
		}

		// Should not receive unexpected ack
		assertThrows(IllegalReceiveException.class, () -> q.receive(new AckMessage(), n.getChannel("p", "q")));

		// Should not receive two acks from chosen non-child
		final String fNonchild = nonchild;
		receiveOrCatch(q, new AckMessage(), n.getChannel(nonchild, "q"));
		assertThrows(IllegalReceiveException.class, () -> q.receive(new AckMessage(), n.getChannel(fNonchild, "q")));
	}

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

	/**
	 * receiveTest18:
	 * Non-initiator should wait for all ACKs, and then send TOKEN to child
	 */
	@Test
	void receiveTest18() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator q,r,s,t:week56.DepthFirstSearchExtraControlNonInitiator").makeComplete();

		WaveProcess q = (WaveProcess) n.getProcess("q");
		q.init();

		receiveOrCatch(q, new TokenMessage(), n.getChannel("p", "q"));

		String child = "";
		String nonchild1 = "";
		String nonchild2 = "";
		if (n.getChannel("q", "r").getContent().size() == 0) {
			child = "r";
			nonchild1 = "s";
			nonchild2 = "t";
		} else if (n.getChannel("q", "s").getContent().size() == 0) {
			child = "s";
			nonchild1 = "r";
			nonchild2 = "t";
		} else {
			child = "t";
			nonchild1 = "r";
			nonchild2 = "s";
		}

		assertEquals(0, n.getChannel("q", child).getContent().size());

		receiveOrCatch(q, new AckMessage(), n.getChannel(nonchild1, "q"));
		assertEquals(0, n.getChannel("q", child).getContent().size());

		receiveOrCatch(q, new AckMessage(), n.getChannel(nonchild2, "q"));
		assertEquals(1, n.getChannel("q", child).getContent().size());
		assertTrue(n.getChannel("q", child).getContent().iterator().next() instanceof TokenMessage);
	}

	/**
	 * simulationTest1:
	 * Simulate a full run of the protocol.
	 * All processes should be finished at the end.
	 */
	@Test
	void simulationTest1() {
		Network n = Network.parse(true, "p:week56.DepthFirstSearchExtraControlInitiator");
		for (int i = 0; i < 15; i++) {
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
		for (int i = 0; i < 15; i++) {
			assertTrue(((WaveProcess) n.getProcess("q" + i)).isPassive());
		}
	}

}
