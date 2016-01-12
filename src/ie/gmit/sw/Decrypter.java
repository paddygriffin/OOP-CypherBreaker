package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;

public class Decrypter implements Runnable {
	private BlockingQueue<Resultable> queue;
	private String cypherText;
	private int key;
	private TextScorer t;

	
	public Decrypter(BlockingQueue<Resultable> queue, String cypherText, TextScorer t, int key) {
		super();
		this.queue = queue;
		this.cypherText = cypherText;
		this.key =key;
		this.t = t;
	}

	public void run() {
		RailFence rf = new RailFence();
		String plainText = rf.decrypt(cypherText, key);
		double score = t.getScore(plainText);
		//get score
		Resultable r = new Result(plainText, key, score );
		
		try {
			System.out.println("putting the score for " + key + " in queue");
			queue.put(r);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Thread for " + key + " is done");
		
	}
	
	
	

}
