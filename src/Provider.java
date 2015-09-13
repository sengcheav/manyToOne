import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Provider implements Runnable {
	private int id ; 
	private Bulletin board ; 
	private String from ;
	private String to ; 
	private int number ; 
	public int seatLeft;
	private int waiting ;
	private long time ; 
	private long startTime = 0; 
	private long stopTime =0 ;
	private StopWatch sw = new StopWatch() ; 
	public ArrayList<Client> clients = new ArrayList<Client>();
	public Provider( int id , String f, String t, int n, int w,Bulletin b){
		this.id = id ;
		this.from = f ; 
		this.to = t;
		this.number = n ;
		this.seatLeft = n ;
		this.waiting = w ; 
		this.board = b ; 
	}
	public String getFrom(){
		return this.from;
	}
	public String getTo(){
		return this.to;
	}
	public int getSeatLeft(){
		return this.seatLeft ; 
	}
	public int getId(){
		return this.id; 
	}
	public int getNumber(){
		return this.number;
	}
	public void checkFirst() {
		
		try {
			//board.sema.acquire();
			if(board.clientList.isEmpty()){ // if there client at all then post the service
				board.sema.acquire();
				board.providerList.add(this);
				sw.startNano(); 
				board.sema.release();
				System.out.println("New Client List -New Provider "+ id +" POST Service: from-to-passenger "+this.from+"->" +this.to+"-"+ this.number);
			}
			else{ // if there clients then check
				boolean found = false ; 
				sw.startNano() ; 
				board.sema.acquire();
				for (Client c : board.clientList){
					if(c.getFrom().equals(getFrom())&&c.getTo().equals(getTo()) && seatLeft > 0 ){ // if matching the client's need
						
						this.seatLeft-- ; 
						this.clients.add(c);
						board.clientList.remove(c);
						System.out.println("New Provider "+ id +" POST Service: from-to-passenger "+this.from+"->" +this.to+"-"+ this.number);
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						System.out.println("Match service : Delete from ClientList " );
						System.out.println(this.toString() + " --Match to-- " + c.toString());
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						// do not need to remove client from the list because it havent add to the client list yet (Check first)
						if(getSeatLeft() == 0){
							board.providerList.remove(this);
							sw.stopNano() ; 
							time = sw.getElapsedTimeNano() ; 
							board.proEvalTime+= time; 
							System.out.println("Time stop :"+ time); 
						//	break ; 
							
							//System.out.println(time+ " time " +sw.getElapsedTimeNano()+" "+board.proEvalTime +" *"+System.nanoTime()+" "+stopTime +" "+startTime);
						}else {
							this.board.providerList.add(this);
							sw.startNano(); 
						}
						
						found = true ;
						break; 
					}	
				}
				board.sema.release();
				if (!found){
					board.sema.acquire();
					this.board.addProvider(this); 
					sw.startNano(); 
				
					board.sema.release() ; 
					System.out.println("New Provider "+ id +" POST Service: from-to-passenger "+this.from+"->" +this.to+"-"+ this.number);
				}	
		
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		finally {
//			board.sema.release() ; 
//		}
	}
	
	public void checkAfter() {		
		try {
//			board.sema.acquire();
			board.sema.acquire();
			this.board.providerList.add(this);
			sw.startNano(); 
			
		 
			
			board.sema.release(); 
			System.out.println("New Provider "+ id +" POST Service: from-to-passenger "+this.from+"->" +this.to+"-"+ this.number);;
			if ( !board.clientList.isEmpty()){
				board.sema.acquire();
				for( Client c: board.clientList){
					if(c.getFrom().equals(getFrom())&&c.getTo().equals(getTo()) && getSeatLeft() > 0 ){
						
						this.clients.add(c);
						seatLeft-- ; 
						board.clientList.remove(c);// remove after match service (check After)
						
						for(Provider p: board.providerList){
							//if(p == this){
							if(p.equals(this)){
								if(getSeatLeft() == 0){ // check if any seatleft 
									board.providerList.remove(p);
									sw.stopNano();
									time = sw.getElapsedTimeNano() ; 
									System.out.println("Time stop :"+ time  );
									board.proEvalTime+= time ; 
									
								}
								System.out.println("+++++++++++++++++++++++++++++++++++++++");
								System.out.println("Match service : Delete from ClientList "+"|"+System.currentTimeMillis() );
								System.out.println(this.toString() + " --Match to-- " + c.toString());
								System.out.println("+++++++++++++++++++++++++++++++++++++++");
								break ; 
							}
						}
					
						break; 
					}
				}
				board.sema.release();
			}	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	public void checkAgain() {
		try {
			//board.sema.acquire();
			if (this.seatLeft < this.number){
				
				
				for(Provider p: board.providerList){
										//if(p == this){
					if(p.equals(this)){
						board.sema.acquire();
						board.providerList.remove(p);
						sw.stopNano();
						time = sw.getElapsedTimeNano() ; 
						board.sema.release();
						
						break; 
					}
				}
				for( Client c: clients){
					for(Client cinlist : board.clientList){
						//if(cinlist == c){
						if(cinlist.equals(c)){
							board.sema.acquire();
							System.out.println("DDD "+ cinlist.getId() +"-" + c.getId() );
							board.clientList.remove(cinlist);
							board.sema.release();
							break ; 
						}
					}
				}
				
				System.out.println("Time stop at check again:"+ time +"---" + waiting  );
				board.proEvalTime+= time ; 
				System.out.println("+++++++++++++++++++++++++++++++++++++++**");
				System.out.println("Gotta Goooooooo been waiting for "+ waiting +" ms" );
				System.out.println(toString()+ ""+ provideTo());
				System.out.println("+++++++++++++++++++++++++++++++++++++++**");
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		finally{
//			board.sema.release() ; 
//		}
	}
	
	@Override
	public void run() {
		int random = new Random().nextInt(2); 
		if( random == 1){
			checkFirst() ; 
		}else {
			checkAfter() ;
		}
		
		try {
			Thread.sleep(waiting) ;
			checkAgain();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		board.totalTime++ ;//run
	}
	public String toString(){
		return "Provider id: "+id+ " providing service: from-to-passenger "+ getFrom()+"->"+getTo()+"-"+getNumber(); 
	}
	
	public String provideTo(){
		String s = " Passenger :";
		for(Client c : clients){
			s=s+c.getId()+"-";
		}
		return s ; 
	}
	public boolean equals(Provider other){
		if(this.from.equals(other.from) && this.to.equals(other.to) && this.number == other.number && this.id == other.id ){
			return true ;
		}
		return false ; 
	}
	

}
