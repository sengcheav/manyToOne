import java.util.Random;


public class Client implements Runnable {
	private int id; 
	private String from ;
	private String to; 
	private Bulletin board ; 
	StopWatch w = new StopWatch() ;
	long time = 0 ; 
	
	public Client( int id , String f, String t, Bulletin board){
		this.id = id ;
		this.from = f ; 
		this.to = t; 
		this.board = board ; 
		 
	}
	
	
	public int getId(){
		return this.id ;
	}
	
	public String getFrom(){
		return this.from ;
	}
	public String getTo(){
		return this.to;
	}
	
	
	
public void checkFirst() {		
		try {
			w.startNano();
//			board.sema.acquire();
			if(board.providerList.isEmpty()){
				//Post the message 
				board.sema.acquire();
				this.board.addClient(this); 
				board.sema.release();
				System.out.println("New Provider List- New Client "+ id +" Looking for Service from-to: "+this.from+"->"+this.to);
			}
			else{
				
				boolean found = false ; 
				board.sema.acquire();
				for (Provider p : board.providerList){
					if(p.getFrom().equals(getFrom()) && p.getTo().equals(getTo()) && p.getSeatLeft() >0 ){
						
						p.clients.add(this);
						p.seatLeft-- ;
						System.out.println("New Client "+ id +" Looking for Service: from-to "+this.from+"->"+this.to);
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						System.out.println(this.toString() + " --Match to-- " + p.toString());
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						if(p.getSeatLeft() == 0){
							board.providerList.remove(p); 
						}	
						w.stopNano() ; 
						time = w.getElapsedTimeNano() ; 
						board.cliEvalTime+= time ;
						found = true ; 
						
						break ; 
					}
				}
				board.sema.release(); 
				if(!found){
					this.board.addClient(this);
					System.out.println("New Client "+ id +" Looking for Service: from-to "+this.from+"->"+this.to);
					time = 0 ;
					w = new StopWatch() ; 
				}
			}
		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}finally {
//			board.sema.release() ; 
		}
	}

public void checkAfter() {		
	try {
//		board.sema.acquire();
		board.sema.acquire();
		this.board.addClient(this);
		w.startNano() ; 
		board.sema.release(); 
		System.out.println("New Client "+ id +" Looking for Service from-to: "+this.from+"-"+this.to);
		if ( !board.providerList.isEmpty()){
			board.sema.acquire();
			boolean found = false ; 
			for( Provider p: board.providerList){
				if(p.getFrom().equals(getFrom()) && p.getTo().equals(getTo()) && p.getSeatLeft() >0 ){
					
					p.clients.add(this); 
					p.seatLeft-- ;
					if(p.getSeatLeft() == 0 ){
						board.providerList.remove(p);
					}
					for(Client c: board.clientList){
						if(c.equals(this)){
							board.clientList.remove(c);
							w.stopNano();
							time = w.getElapsedTimeNano() ; 
							board.cliEvalTime += time ; 
							System.out.println("+++++++++++++++++++++++++++++++++++++++");
							System.out.println("Match service" );
							System.out.println(this.toString() + " --Match to-- " + p.toString());
							System.out.println("+++++++++++++++++++++++++++++++++++++++");
							break ; 
						}
					}
					
					break; 
				}
			}
			if(!found ){
				time = 0 ; 
				w = new StopWatch() ;
			}
			board.sema.release();
		}	
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
//		finally {
//		board.sema.release() ; 
//	}
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
			this.board.sema.acquire();
			this.board.totalTime++ ;
			this.board.sema.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public boolean equals(Client other){
		if(this.from.equals(other.from) && this.to.equals(other.to)  && this.id == other.id ){
			return true ;
		}
		return false ; 
	}
	public String toString(){
		return "Client id: "+id+ " Looking for service: from-to "+ this.from +"->"+this.to; 
	}
}
