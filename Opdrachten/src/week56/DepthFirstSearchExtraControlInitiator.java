package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlInitiator extends DepthFirstSearchExtraControlProcess {

	@Override
	public void init() {
		super.init();
		Channel futureChild = getRandomOutgoingChannels().get(0);
		removeNextOutgoingChannel();
		for (Channel c: getRandomOutgoingChannels()) {
			send(new InfoMessage(), c);
		}
		if (getIncomingInfoFromProcesses().size() == getRandomOutgoingChannels().size()) {
			send(new TokenMessage(), futureChild);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		
		//System.out.println("Ack sent status: " + allIncomingProcessesHaveSentAck());
		System.out.println("Random outgoing: " + getRandomOutgoingChannels());
		System.out.println("Ack messages: " + getIncomingAcksFromProcesses());
		
		// if all tokens received, finish algorithm
		System.out.println("all process have sent ack: " + allIncomingProcessesHaveSentAck());
		
		// if token received an all channels have sent info or token, finish
		if ((m instanceof TokenMessage) && allIncomingProcessesHaveSentInfo()) {
			//System.out.println("\nAll incoming processes have sent info.");
			done();
			//System.out.println("Done status: " + isPassive() + "\n\n");
		}
		// if process still holds token and all acks are received
		// forward token to next process
		else if (allIncomingProcessesHaveSentAck() &&
				(!getRandomOutgoingChannels().isEmpty())) {
			send (new TokenMessage(), getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
			System.out.println("\nReset Ack list");
			resetIncomingAcksProcesses();
		}
		// if token was received and not all processes have sent info message
		// forward token to next process
		else if (m instanceof TokenMessage) {
			send (new TokenMessage(), getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
	}
}
