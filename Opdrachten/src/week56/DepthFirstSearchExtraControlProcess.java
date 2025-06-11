package week56;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class DepthFirstSearchExtraControlProcess extends WaveProcess {

	private List<Channel> randomOutgoingChannels;

	@Override
	public void init() {
		randomizeOutgoingChannels();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// invalid message
		if (!((m instanceof TokenMessage) || (m instanceof InfoMessage) ||
				(m instanceof AckMessage))) {
			throw new IllegalReceiveException();
		}
		else if ((m instanceof TokenMessage) && isPassive()) {
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
	
	protected Channel getReversedChannel (Channel c) {
		Channel reversedChannel = null;
		for (Channel cOut: getOutgoing()) {
			if (cOut.getReceiver() == c.getSender()) {
				reversedChannel = cOut;
			}
		}
		return reversedChannel;
	}
}
