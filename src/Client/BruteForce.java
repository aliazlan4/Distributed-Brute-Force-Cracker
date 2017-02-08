package Client;
public class BruteForce {
	private String currentString = "";
	private long currentIndex = -1;
	private long startIndex = -1;
	private long endIndex = -1;
	private long totalPossibleCombinations = -1;
	private long totalIndexes = -1;
	private int minLength = -1;
	private int maxLength = -1;
	private boolean smallAlphabets = false;
	private boolean largeAlphabets = false;
	private boolean numericCharacters = false;
	private char chosenCharactersArray[] = null;
	
	private String smallAplhabetsArray = "abcdefghijklmnopqrstuvwxyz";
	private String largeAlphabetsArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String numericCharacterArray = "0123456789";
	
	public BruteForce(String range, long startIndex, long endIndex, int minLength, int maxLength){
		if(startIndex < 0 || endIndex < 0 || minLength < 1 || maxLength < 1){
			System.out.println("Exiting: startIndex, endIndex, StartIndex & EndIndex values are not correct!");
			System.exit(0);
		}
		if(startIndex >= endIndex){
			System.out.println("Exiting: StartIndex should be smaller than EndIndex!");
			System.exit(0);
		}
		if(minLength > maxLength){
			System.out.println("Exiting: minLength should be smaller than or equal to maxLength!");
			System.exit(0);
		}
		
		this.currentIndex = startIndex - 1;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.totalIndexes = endIndex - startIndex;
		
		initializeCharactersArray(range);
		calculateTotalCombinations(minLength, maxLength);
	}

	private void initializeCharactersArray(String range){
		String temp = "";
		if(range.contains("0-9"))
			temp += numericCharacterArray;
		if(range.contains("a-z"))
			temp += smallAplhabetsArray;
		if(range.contains("A-Z"))
			temp += largeAlphabetsArray;
		
		this.chosenCharactersArray = temp.toCharArray();
	}
	
	private void calculateTotalCombinations(int minLength, int maxLength) {
		long temp = 0;
		for(int i = maxLength; i >= minLength; i--){
			temp += Math.pow(this.chosenCharactersArray.length, i);
		}
		this.totalPossibleCombinations = temp;
		System.out.println("Total Combinations: " + this.totalPossibleCombinations);
		
		if(this.endIndex > this.totalPossibleCombinations){
			this.endIndex = this.totalPossibleCombinations;
			this.totalIndexes = this.endIndex - this.startIndex;
		}
	}
	
	private void initializeFirstIndex(int minLength){
		String temp = "";
		for(int i = 1; i <= minLength; i++)
			temp += this.chosenCharactersArray[0];
		this.currentString = temp;
	}
	
	private void seekIndex(long seek){
		long checkLength = (long) Math.pow(chosenCharactersArray.length, this.currentString.length());

		if(seek >= checkLength){
			this.currentString = chosenCharactersArray[0] + this.currentString;
			seekIndex(seek - checkLength);
			return;
		}
		
		int index = 0;
		for(int i = (this.currentString.length() - 1); i > 0; i--){
			checkLength = (long) Math.pow(this.chosenCharactersArray.length, i);
			int temp = (int) (seek / checkLength);
			if(temp > 0){
				char[] charArray = this.currentString.toCharArray();
				charArray[index] = this.chosenCharactersArray[temp];
				this.currentString = new String(charArray);
				seek -= (temp * checkLength);
			}
			index++;
		}
		
		if(seek > 0 && seek != this.chosenCharactersArray.length){
			char[] charArray = this.currentString.toCharArray();
			charArray[index] = this.chosenCharactersArray[(int) seek];
			this.currentString = new String(charArray);
		}
	}
	
	public String getNext(){
		this.currentIndex++;
		initializeFirstIndex(this.minLength);
		seekIndex(this.currentIndex);
		return this.currentString;
	}
	
	public String getCurrent(){
		if(this.currentString.equals("")){
			initializeFirstIndex(this.minLength);
			seekIndex(this.currentIndex);
		}
		return this.currentString;
	}
	
	public long getCurrentIndex(){
		return this.currentIndex;
	}
	
	public void setCurrentIndex(long index){
		this.currentIndex = index;
	}
	
	public long getTotalIndexes(){
		return this.totalIndexes;
	}
	
	public long getTotalPossibleCombinations(){
		return this.totalPossibleCombinations;
	}
}
