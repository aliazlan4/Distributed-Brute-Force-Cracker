package Common;
import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String type, content;

	public Message(String type, String content){
		this.type = type; this.content = content;
	}

	public String toString(){
		return "{type='"+type+"', content='"+content+"'}";
	}
}