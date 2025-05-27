package week1;

import java.util.Set;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;


public class RockPaperScissorsProcess extends Process {
	
	private Set<Channel> incomingChannels = null;
	
	private int n;
	private boolean win = false;
	private boolean lose = false;
	private Item move;
	
	@Override
	public void init() {
		// Initialize instance variables
		this.n = getOutgoing().size(); // set number of message to receive
		this.incomingChannels = getIncoming(); // channels who still need to send message
		this.move = Item.random(); // a random move
		
		// Send message through all outgoing channels
		Message m = new RockPaperScissorsMessage(this.move);
		for (Channel c: getOutgoing()) {
			send(m, c);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// if channel not supposed to or already sent message, throw error
		if (!this.incomingChannels.remove(c)) {
			throw new IllegalReceiveException();
		}
		// if message not of correct class, throw error
		else if (!(m instanceof RockPaperScissorsMessage)) {
			throw new IllegalReceiveException();
		}
		
		this.n -= 1;
		Item moveOpponent = ((RockPaperScissorsMessage) m).getItem();
		
		if (this.move.beats(moveOpponent)) {
			this.win = true;
		}
		if (moveOpponent.beats(this.move)) {
			this.lose = true;
		}

		// if all message received print result
		if (this.n == 0) {
			print(this.win + " " + this.lose);
		}
		
	}
}
