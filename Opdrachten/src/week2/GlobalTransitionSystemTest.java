package week2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for GlobalTransitionSystem.hasExecution(...)
 */

class GlobalTransitionSystemTest {

	/**
	 * hasExecutionTest1:
	 * See exercise I-1a from the exercises on Chapter 2,
	 * in particular the transition system in the solution.
	 * 
	 * Here we use our classes to define the transition system,
	 * and check that only possible paths (executions) are
	 * recognized as valid.
	 * To understand what this test does: draw the transition system 
	 * on a piece of paper. g0, g1, ... are the nodes, and
	 * the transitions are described as follows:
	 * g0--a@p-->g1 means "transition starting from node g0
	 * and ending in node g1, corresponding to the action
	 * a@p (action a occurring in process p)".
	 * The string starts with "g0 ", which means that g0
	 * is the initial state of the transition system.
	 * Once you have drawn the whole transition system,
	 * you will see that it is the same as the one shown
	 * in the solution for exercise I-1a.
	 * Now it should be easier to see which paths are possible
	 * (tested with assertTrue), and which are not (assertFalse).
	 */
	@Test
	void hasExecutionTest1() {
		String s = "g0 ";
		s += "g0--a@p->g1 g1--b@p->g2 g2--e@r->g3 g3--f@r->g4 g4--d@q->g5 ";
		s += "g6--b@p->g7 g7--e@r->g8 g8--f@r->g9 g9--d@q->g10 ";
		s += "g1--c@q->g6 g2--c@q->g7 g3--c@q->g8 g4--c@q->g9 g5--c@q->g10";

		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g7 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g6 g7 g8 g9 g10", configurations)));

		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g3 g4 g5 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5", configurations))); //This does not end in a final state, so it is not valid
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g11 g10", configurations)));
	}

	/**
	 * hasExecutionTest2:
	 * See exercise I-2a from the exercises on Chapter 2.
	 * We do the same as in hasExecutionTest1.
	 */
	@Test
	void hasExecutionTest2() {
		String s = "g0 ";
		s += "g0--c@q->g1 g1--d@q->g2 g2--a@p->g3 g3--b@p->g4 g4--e@q->g5 ";
		s += "g6--d@q->g7 g7--a@p->g8 g8--b@p->g9 g9--e@q->g10 ";
		s += "g1--f@r->g6 g2--f@r->g7 g3--f@r->g8 g4--f@r->g9 g5--f@r->g10";

		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g7 g8 g9 g10", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g6 g7 g8 g9 g10", configurations)));

		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g3 g4 g5 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g10", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g3 g4 g5 g11 g10", configurations)));
	}

	/**
	 * hasExecutionTest3:
	 * See exercise I-3a from the exercises on Chapter 2.
	 * We do the same as in hasExecutionTest1 and 2.
	 */
	@Test
	void hasExecutionTest3() {
		String s = "g0 ";
		s += "g0--a@p->g1 g1--c@r->g2 ";
		s += "g3--a@p->g4 g4--c@r->g5 ";
		s += "g6--a@p->g7 g7--c@r->g8 ";
		s += "g0--b@q->g3 g1--b@q->g4 g2--b@q->g5 ";
		s += "g3--d@r->g6 g4--d@r->g7 g5--d@r->g8";

		Map<String, Configuration> configurations = new HashMap<>();

		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g4 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g4 g7 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g4 g5 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g4 g7 g8", configurations)));
		assertTrue(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g3 g6 g7 g8", configurations)));

		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g1 g2 g5 g8", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g8", configurations)));
		assertFalse(GlobalTransitionSystem.parse(s, configurations)
				.hasExecution(Configuration.parseList("g0 g1 g2 g5 g9 g8", configurations)));
	}

	/**
	 * hasExecutionTest4, 5, 6, 7:
	 * We generate random transition systems of different sizes,
	 * and check that random existing paths are recognized
	 * as valid (tests 4 and 5), while invalid paths are recognized
	 * as invalid (tests 6 and 7). An invalid path is obtained by
	 * intentionally sabotaging a valid path in different ways.
	 * It is not necessary to understand precisely how these paths
	 * are generated.
	 */
	@Test
	void hasExecutionTest4() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 3, 4, 12);
		for (int i = 0; i < 10; i++) {
			assertTrue(system.hasExecution(system.randomExecution(i)));
		}
	}

	@Test
	void hasExecutionTest5() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		for (int i = 0; i < 10; i++) {
			assertTrue(system.hasExecution(system.randomExecution(i)));
		}
	}

	@Test
	void hasExecutionTest6() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 3, 4, 12);
		for (int i = 0; i < 10; i++) {
			assertFalse(system.hasExecution(system.randomNonExecution(i)));
		}
	}

	@Test
	void hasExecutionTest7() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		for (int i = 0; i < 100; i++) {
			assertFalse(system.hasExecution(system.randomNonExecution(i)));
		}
	}

	/**
	 * hasExecutionTest8:
	 * An empty path must always be recognized as invalid.
	 */
	@Test
	void hasExecutionTest8() {
		GlobalTransitionSystem system = GlobalTransitionSystem.random(0, 30, 40, 120);
		assertFalse(system.hasExecution(new ArrayList<>()));
	}

	//	@Test
	//	void parseTest1() {
	//		String s = "g0 g0--a@p->g1";
	//		Map<String, Configuration> configurations = new LinkedHashMap<>();
	//
	//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
	//	}
	//
	//	@Test
	//	void parseTest2() {
	//		String s = "g0 g0--s(p,q,1)->g1 g1--r(p,q,1)->g2 g3--a@q->g0";
	//		Map<String, Configuration> configurations = new LinkedHashMap<>();
	//
	//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
	//	}
	//
	//	@Test
	//	void parseTest3() {
	//		String s = "g0 g0--s(p,q,1)->g1 g1--r(p,q,1)->g2 g1--s(p,q,2)->g4 g3--a@q->g0";
	//		Map<String, Configuration> configurations = new LinkedHashMap<>();
	//
	//		assertEquals(s, GlobalTransitionSystem.parse(s, configurations).toString(configurations));
	//	}
}
