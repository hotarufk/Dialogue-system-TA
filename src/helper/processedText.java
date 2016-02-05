package helper;

public class processedText {
	
	//attribut
	private int id;
	private String Message;
	private String Reply;
	private String FacebookClasificationId;
	private int ClassificationId;
	private int ConversationId;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getConversationId() {
		return ConversationId;
	}
	public void setConversationId(int conversationId) {
		ConversationId = conversationId;
	}
	//function
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public int getClassificationId() {
		return ClassificationId;
	}
	public void setClassificationId(int classification) {
		ClassificationId = classification;
	}
	public String getReply() {
		return Reply;
	}
	public void setReply(String reply) {
		Reply = reply;
	}
	public String getFacebookClasificationId() {
		return FacebookClasificationId;
	}
	public void setFacebookClasificationId(String facebookClasificationId) {
		FacebookClasificationId = facebookClasificationId;
	}
	
	
	

}
