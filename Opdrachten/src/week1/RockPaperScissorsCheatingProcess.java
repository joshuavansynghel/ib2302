package week1;

import java.util.Set;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class RockPaperScissorsCheatingProcess extends Process {
	
	private Set<Channel> incomingChannels = null;
	
	private int n;
	private boolean win = false;
	private boolean lose = false;
	private Item move;

	@Override
	public void init() {
		// Initialize instance variables
		this.n = getOutgoing().size(); // set number of message to send
		this.incomingChannels = getIncoming(); // channels who still need to send message
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
		
		Item moveOpponent = ((RockPaperScissorsMessage) m).getItem();
		
		// choose best move based on first message received
		if ((moveOpponent == Item.PAPER) && (n == getOutgoing().size())) {
			this.move = Item.SCISSORS;
			this.win = true;
		}
		else if ((moveOpponent == Item.ROCK) && (n == getOutgoing().size())) {
			this.move = Item.PAPER;
			this.win = true;
		}
		else if ((moveOpponent == Item.SCISSORS) && (n == getOutgoing().size())) {
			this.move = Item.ROCK;
			this.win = true;
		}
		
		this.n -= 1;
		
		if (moveOpponent.beats(this.move)) {
			this.lose = true;
		}

		// if all message received send message and print result
		if (this.n == 0) {
			for (Channel cOut: getOutgoing()) {
				send(new RockPaperScissorsMessage(this.move), cOut);
			}
			print(this.win + " " + this.lose);
		}
	}
}
