import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class findPasswordofZip {
	private long progressVar = 0;
	private long totalIterations = 0;
	private long showProgress = 0;
	bruteforceThread threads[];
	boolean passFound = false;
	
	public findPasswordofZip(String filePath, String range, long startIndex, long endIndex, 
			int minLength, int maxLength){
		this.totalIterations = endIndex - startIndex;
		this.showProgress = this.totalIterations / 50;
		
		threads = new bruteforceThread[Runtime.getRuntime().availableProcessors()];
		long portions = (endIndex - startIndex) / threads.length;
		
		for(int i = 0; i < threads.length; i++){
			long start = (i * portions) + startIndex;
			long end = ((i+1) * portions) + startIndex - 1;
			if(i == threads.length - 1)
				end++;
			
			System.out.println("Thread Creating " + i + ": start[" + start + "] end[" + end + "]");
			threads[i] = new bruteforceThread(this, filePath, range, start, end, minLength, maxLength);
		}
	}
	
	public void start() throws InterruptedException{
		System.out.println("Starting Cracking!");
		
		long startTime = System.currentTimeMillis();
		
		for(int i = 0; i < threads.length; i++){
			if(!threads[i].isAlive()){
				threads[i].start();
				System.out.println("Thread started: " + i);
			}
		}
		for(int i = 0; i < threads.length; i++){
				threads[i].join();
				System.out.println("Thread Ended: " + i);
		}
		
//		for(int i = 0; i < threads.length; i++){
//			if(!threads[i].isInterrupted()){
//				System.out.println("in If: " + i);
//				Thread.sleep(100);
//				i--;
//			}
//		}
		
		if(!this.passFound)
			System.out.println("Password not found!");
		
		System.out.println("Ending Cracking!");
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time taken: " + ((double)totalTime/1000) + "sec");
	}
	
	public void updateProgress(){
		this.progressVar++;
		if(this.progressVar % this.showProgress == 0){
			System.out.println("Completed " + this.progressVar + " iterations. (" + 
					(int)(((double)this.progressVar / (double)this.totalIterations) * 100) + "%)");
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
			System.out.println("Error: File not found!");
			e.printStackTrace();
		}
	}

	public void run(){
		for(long i = 0; i < bf.getTotalIndexes(); i++){
			if(isInterrupted())
				return;
			parent.updateProgress();
			try{
				zipFile.setPassword(bf.getNext());
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
		this.interrupt();
		System.out.println("Ending Thread!");
	}
	
	public void passwordFound(String password){
		System.out.println("Password Found: " + password);
		parent.passFound = true;
		
		for(int i = 0; i < parent.threads.length; i++)
				parent.threads[i].interrupt();
	}
}