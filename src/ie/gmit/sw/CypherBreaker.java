package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.*;

public class CypherBreaker {
	private BlockingQueue<Resultable> queue;
	private TextScorer t;
	private String cypherText;
	private Resultable result = new Result(" ", 0, -1000.00);
	public static final int MAX_QUEUE_SIZE = 100;
	private Object lock = new Object();
	private int counter = 2; //counter to be incremented
	
	public CypherBreaker(String cypherText,TextScorer t){
		queue = new ArrayBlockingQueue<Resultable>(MAX_QUEUE_SIZE);
		this.cypherText = cypherText;
		this.t = t;
		init();
	}
	//queue has to wait 
	public void increment(){
		synchronized(lock){
			counter++;
			if(counter == cypherText.length()/2){
				try {
					queue.put(new Poison(" ", 0, -1000.00));//adds poison object to the end.. kills queue
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				
			}
		}
		
	}
	public void init(){
        //start a load of producers
        
        for(int i = 2; i<=cypherText.length()/2; i++){
            new Thread(new Decrypter(queue, cypherText, t, i)).start();
        }
        
        new Thread(new Runnable(){
           
            public void run(){
                while(!queue.isEmpty()){
                    try {
                    	Thread.sleep(100);
						Resultable r = queue.take();
						
						if(r instanceof Poison){
							System.out.println(result.getPlainText());
							System.out.println(result.getKey());
							System.out.println(result.getScore());
							return;
						
						}
						if(result.getScore()<r.getScore()){//compare 2 results
							result = r;
							System.out.println("got new high score for key " + result.getKey());
						}
						increment();//call method
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//generate try/catch
                    //do something                   
                }
            }
            
        }).start();
        
        
        

	}
}
