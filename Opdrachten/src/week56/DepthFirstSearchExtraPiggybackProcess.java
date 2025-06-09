package week56;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class DepthFirstSearchExtraPiggybackProcess extends WaveProcess {
	
	private List<Channel> randomOutgoingChannels;

	@Override
	public void init() {
		randomizeOutgoingChannels();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// invalid message
		if (!(m instanceof TokenWithIdsMessage)) {
			throw new IllegalReceiveException();
		}
		else if ((m instanceof TokenWithIdsMessage) && isPassive()) {
			throw new IllegalReceiveException();
		}
	}
	
	protected void randomizeOutgoingChannels () {
		List<Channel> outgoingChannels = new ArrayList<>(getOutgoing());
		Collections.shuffle(outgoingChannels);
		randomOutgoingChannels = outgoingChannels;
	}
	
	protected List<Channel> getRandomOutgoingChannels () {
		return randomOutgoingChannels;
	}
	
	protected void removeNextOutgoingChannel () {
		randomOutgoingChannels.remove(0);
	}
	
	protected void removeSpecificOutgoingChannel (Channel c) {
		randomOutgoingChannels.remove(c);
	}
	
	// for a given channel, get the channel in other direction
	protected Channel getReversedChannel (Channel c) {
		Channel reversedChannel = null;
		for (Channel cOut: getOutgoing()) {
			if (cOut.getReceiver() == c.getSender()) {
				reversedChannel = cOut;
			}
		}
		return reversedChannel;
	}
	
	// loop over all remaining outgoing channels
	// and remove any which have already been visited
	protected void removeVisitedProcesses (TokenWithIdsMessage m) {
		for (String id: m.getIds()) {
			Iterator<Channel> it = getRandomOutgoingChannels().iterator();
			while (it.hasNext() ) {
				Channel c = it.next();
				if (c.getReceiver().getName().equals(id)) {
					removeSpecificOutgoingChannel(c);
					break;
				}
			}
			/**for (Channel c: getRandomOutgoingChannels()) {
				if (c.getReceiver().getName() == id) {
					removeSpecificOutgoingChannel(c);
				}
			}
			**/
		}
	}
}
