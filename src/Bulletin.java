import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Bulletin {
	public static ArrayList<Provider> providerList ; 
	public static ArrayList<Client> clientList; 
	Semaphore sema = new Semaphore(1, true) ;
	//private static final int time = 500;
    private static final int sleepTime = 100;
    private static final int noThreads = 50 ;  // number of threads
    public boolean manyToMany = false ; 
    public int totalTime = 0 ; 
    public long proEvalTime = 0 ;
	public long cliEvalTime = 0 ; 
	public long totalTimeWait = 0 ;
    private Object lock = new Object() ; 
	
	public Bulletin( ){
		providerList = new ArrayList<Provider>() ; 
		clientList = new ArrayList<Client>() ; 
	}
	
	public ArrayList<Provider> getProviderList(){
		return this.providerList ;
	}
	
	public ArrayList<Client> getClientList() {
		return this.clientList ; 
	}
	
	public void addClient( Client c){
		this.clientList.add(c);
	}
	
	public void addProvider( Provider provider ){
		this.providerList.add(provider) ; 
	}
	
	
    public static void main(String[] args) {
    	StopWatch totalTimeW = new StopWatch();
       
    	ExecutorService executor = Executors.newCachedThreadPool();
    	totalTimeW.start();
    	final Bulletin board = new Bulletin() ; 
    	board.manyToMany = true ; 
    	StopWatch wait = new StopWatch(); // waiting time 
    	for ( int i = 0 ; i<noThreads; i++ ){
    		 
    		 String from = "" + new Random().nextInt(5);
             String to = "" + new Random().nextInt(5);
             // client from to 
             String from1 = "" + new Random().nextInt(5);
             String to1 = "" + new Random().nextInt(5);
             int passenger = 1 +new Random().nextInt(3); 
             int waiting = 100 + new Random().nextInt(100);
             try {
                 Thread.sleep(sleepTime);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             if( i>0 ){
                 wait.stop();
                 long g = wait.getElapsedTime();
                 System.out.println("Timewait : "+ g );
                 board.totalTimeWait += g ;
                 wait.restartNano();
                 }
        
             Runnable provider = new Provider(i, from, to,passenger,waiting,  board);
             Runnable client = new Client(i, from1, to1, board); 
             wait.start();
             executor.execute(provider);
             executor.execute(client) ;
            
             
            
    	}
    	
    	executor.shutdown() ;
    	totalTimeW.stop() ; 
    	System.out.println("Service and client left" + providerList.size() +" Providers and "+clientList.size() +" Clients");
    	for (Provider p : providerList){
    		System.out.println(p.toString()+"--j>" +p.provideTo());
    	}
    	System.out.println("----------------------------------");
    	for (Client c : clientList){
    		System.out.println(c.toString());
    	}
    	 try {
             executor.awaitTermination(1, TimeUnit.MINUTES);System.out.println("run"+board.totalTime);
             System.out.println("exit");
    	 } catch (InterruptedException e) {
             e.printStackTrace();
    	 }
    	 
         
        System.out.println("run"+board.totalTime);
        System.out.println("Stopwatch :"+totalTimeW.getElapsedTime() +"ms");
        System.out.println("Client eval time : "+board.cliEvalTime +" ns "+ board.cliEvalTime/1000000 + " ms");
     	System.out.println( "Provider eval time : "+board.proEvalTime +" ns " + board.proEvalTime/1000000 + " ms");
     	System.out.println("Total Time Wait to generate offer/request: "+ board.totalTimeWait +" ms");
     	System.out.println(totalTimeW.getElapsedTime() +"-"+(board.cliEvalTime/1000000)+"-"+(board.proEvalTime/1000000)+"-"+board.totalTimeWait);
    }
}
