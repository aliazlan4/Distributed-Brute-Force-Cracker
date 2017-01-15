import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class findPasswordofZip {
	connectionThread parent;
	private long progressVar = 0;
	private long totalIterations = 0;
	private long showProgress = 0;
	private int updateInterval = 50;
	bruteforceThread threads[];
	boolean passFound = false;
	
	public findPasswordofZip(connectionThread parent, String filePath, String range, long startIndex, long endIndex, 
			int minLength, int maxLength){
		this.parent = parent;
		this.totalIterations = endIndex - startIndex;
		this.showProgress = this.totalIterations / updateInterval;
		
		threads = new bruteforceThread[Runtime.getRuntime().availableProcessors()];
		long portions = (endIndex - startIndex) / threads.length;
		
		for(int i = 0; i < threads.length; i++){
			long start = (i * portions) + startIndex;
			long end = ((i+1) * portions) + startIndex - 1;
			if(i == threads.length - 1)
				end++;
			
			parent.parent.Log("Cracking Thread Creating " + i + ": start[" + start + "] end[" + end + "]");
			threads[i] = new bruteforceThread(this, filePath, range, start, end, minLength, maxLength);
		}
	}
	
	public void start() throws InterruptedException{
		parent.parent.Log("Starting Cracking!");
		
		long startTime = System.currentTimeMillis();
		
		for(int i = 0; i < threads.length; i++){
			if(!threads[i].isAlive()){
				threads[i].start();
				parent.parent.Log("Cracking thread started: " + i);
			}
		}
		for(int i = 0; i < threads.length; i++){
				threads[i].join();
				parent.parent.Log("Cracking thread Ended: " + i);
		}
		
//		for(int i = 0; i < threads.length; i++){
//			if(!threads[i].isInterrupted()){
//				System.out.println("in If: " + i);
//				Thread.sleep(100);
//				i--;
//			}
//		}
		
		if(!this.passFound){
			parent.parent.Log("Password not found!");
			parent.sendMessage(new Message("passwordNotFound", ""));
		}
		
		parent.parent.Log("Ending Cracking!");
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		parent.parent.Log("Total time taken: " + ((double)totalTime/1000) + "sec");
	}
	
	public void updateProgress(){
		this.progressVar++;
		if(this.progressVar % this.showProgress == 0){
			parent.parent.Log("Completed " + this.progressVar + " iterations. (" + 
					(int)(((double)this.progressVar / (double)this.totalIterations) * 100) + "%)");
//			parent.sendMessage(new Message("updateOfCracking", "" + progressVar));
		}
	}
}

class bruteforceThread extends Thread{
	private BruteForce bf;
	private ZipFile zipFile = null;
	private findPasswordofZip parent;
	
	public bruteforceThread(findPasswordofZip parent, String filePath, String range, long startIndex, 
			long endIndex, int minLength, int maxLength){
		
		this.parent = parent;
		this.bf = new BruteForce(range, startIndex, endIndex, minLength, maxLength);
		
		try {
			zipFile = new ZipFile(filePath);
		} catch (ZipException e) {
			parent.parent.parent.Log("Error: File not found!");
			parent.parent.parent.Log("Error log: " + e.getMessage());
		}
	}

	public void run(){
		for(long i = 0; i < bf.getTotalIndexes(); i++){
			if(isInterrupted())
				return;
			parent.updateProgress();
			try{
				zipFile.setPassword(bf.getNext());
				//parent.parent.parent.Log(bf.getCurrent());
				//System.out.println(bf.getCurrent());
		    	zipFile.extractAll("temp/");

				passwordFound(bf.getCurrent());
		    	return;
			}
			catch (net.lingala.zip4j.exception.ZipException e) {
				if(e.getMessage().contains("cannot set file properties: file doesnot exist")){
					bf.setCurrentIndex(bf.getCurrentIndex() - 1);
				}
			}
		}
		parent.parent.parent.Log("Ending Thread!");
		this.interrupt();
	}
	
	public void passwordFound(String password){
		parent.parent.parent.Log("Password Found: " + password);
		parent.parent.sendMessage(new Message("passwordFound", password));
		parent.passFound = true;
		
		for(int i = 0; i < parent.threads.length; i++)
				parent.threads[i].interrupt();
	}
}