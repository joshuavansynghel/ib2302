package week56;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.Channel;
import framework.Process;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class DepthFirstSearchExtraControlProcess extends WaveProcess {

	private List<Channel> randomOutgoingChannels;
	private List<Process> incomingInfoFromProcessses = new ArrayList<>();
	private List<Process> incomingAcksFromProcessses = new ArrayList<>();

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
		// if process is done, it should not receive a token
		else if ((m instanceof TokenMessage) && isPassive()) {
			throw new IllegalReceiveException();
		}
		else if (m instanceof InfoMessage) {
			// add process to list of process who have sent token
			incomingInfoFromProcessses.add(c.getSender());
			
			// send ack message in reverse direction
			send(new AckMessage(), getReversedChannel(c));
		}
		else if (m instanceof AckMessage) {
			// capture all acks if you have sent the info message
			incomingAcksFromProcessses.add(c.getSender());
		}

		// if all incoming channels, have sent info message, finish algorithm
		if (allIncomingProcessesHaveSentInfo()) {
			done();
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
	
	protected List<Process> getIncomingInfoFromProcesses () {
		return incomingInfoFromProcessses;
	}
	
	protected List<Process> getIncomingAcksFromProcesses () {
		return incomingAcksFromProcessses;
	}
	
	protected List<Channel> getReversedChannels(List<Channel> channels) {
		List<Channel> reversedChannels = new ArrayList<>();
		for (Channel c : channels) {
			reversedChannels.add(getReversedChannel(c));
		}
		return reversedChannels;
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
	
	// reset the list of incoming acks from processes
	// is needed if this process sends a token multiple times
	protected void resetIncomingAcksProcesses () {
		incomingAcksFromProcessses = new ArrayList<>();
	}
	
	protected boolean listContainsSameProcessses (List<Process> p1, List<Channel> p2) {
		// check if channels in c1 exists in c2
		for (Process p: p1) {
			if (!p2.contains(p)) {
				return false;
			}
		}
		// final check that sizes must be equal
		return p1.size() == p2.size();
	}

	protected boolean allIncomingProcessesHaveSentInfo () {
		for (Channel c : getIncoming()) {
			if (!getIncomingInfoFromProcesses().contains(c.getSender())) {
				return false;
			}
		}
		return true;
	}
}
