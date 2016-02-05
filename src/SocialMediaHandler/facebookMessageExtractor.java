package SocialMediaHandler;

import helper.databaseConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import com.google.gson.Gson;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.Conversation;
import com.restfb.types.FacebookType;
import com.restfb.types.Message;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;


public class facebookMessageExtractor {
	//class ToDoList
	//extract 
	

	// DefaultFacebookClient is the FacebookClient implementation
	// that ships with RestFB. You can customize it by passing in
	// custom JsonMapper and WebRequestor implementations, or simply
	// write your own FacebookClient instead for maximum control.

	
	//harusnya baca dari database/ disediain sebagai parameters
	private String PAGE_ID = "892094664157476";
	private String MY_ACCESS_TOKEN = "CAACEdEose0cBAEPPmZBAUBQ2hD7ZBfc6wAV3OEeeNdyfZA51MkPubsHcN3EdW3UL28zt7mIoEKb862c5cVLxdXifx31U2TfKpKILKYhnUH5Ffa2mXnlnkdK5Xb3jB88k7rFHJZCXhEPcxxYSvkM6q9ojNism5GFIiu1qGlz4LojING1iWZCOayQZCIZC4adqvYAG8YaXRBcYwZDZD";

	
	private databaseConnection dbC = new databaseConnection();
	private FacebookClient facebookClient;
	private String fullmessage=null;
	private Gson gson = new Gson();
	
	public facebookMessageExtractor(String id, String Token){
		facebookClient = new DefaultFacebookClient(Token);
		PAGE_ID = id;
		System.out.println("Page Id : "+PAGE_ID);
		System.out.println("Page Token : "+Token);
	}
	public facebookMessageExtractor(){
		facebookClient = new DefaultFacebookClient(MY_ACCESS_TOKEN);
		PAGE_ID = PAGE_ID;
	}
	
	public void unreadMessage(){
		FacebookClient facebookClient = new DefaultFacebookClient(MY_ACCESS_TOKEN);
		Connection<JsonObject> conversationsConnection = facebookClient.fetchConnection("me/inbox", JsonObject.class,Parameter.with("fields", "unread"));
		//System.out.print(conversationsConnection);
		for (List<JsonObject> myFeedConnectionPage : conversationsConnection)
			  for (JsonObject post : myFeedConnectionPage){
				  int num = Integer.parseInt(post.get("unread").toString());
				  if(num > 0){ //ambil message dengan jumlah unread >0
					  System.out.println("Count: " + post.get("unread").toString() + " , message_id: "+ post.get("id").toString());
					  //ambil konten
				  }
			  }
				  
	}
	
	public void replyableConversation(){
	    Connection<Conversation> conversations = facebookClient.fetchConnection(PAGE_ID+"/conversations", Conversation.class);
	    
	    for(List<Conversation> conversationPage : conversations) {
	        for(Conversation conversation : conversationPage) {
	            Message lastMessage = null;
	            if(conversation.getCanReply() == true && !PAGE_ID.equals(conversation.getMessages().get(0).getFrom().getId())){
	            	fullmessage="";
	            	int i=0;
	            	for(Message asu :conversation.getMessages()){
	            		if(i == conversation.getUnreadCount() || PAGE_ID.equals(conversation.getMessages().get(i).getFrom().getId()) )
	            			break;
	            		fullmessage +=asu.getMessage()+"\n";
	            		//System.out.println(i);
	            		i++;
	            	}
	            	
	            	lastMessage = conversation.getMessages().get(0);
	            	lastMessage.setMessage(fullmessage);
	            	lastMessage.setId(conversation.getId());
	            	java.util.Date date= new java.util.Date();
	            	
	            	//System.out.println("JSON : "+gson.toJson(lastMessage));
	            	String sqli = "INSERT INTO `raw_datas`(`raw_data`, `created_at`, `updated_at`) " +
			                   "VALUES ('"+gson.toJson(lastMessage)+ "','"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"')";
	            	dbC.insert(sqli);
	            }
	        }    
	    }	  
	}
	
	
	
	public void sendReply(java.util.Date time){
		//ambil pesan yg reply nya ga null dan waktunya sesuai timestamp
		ArrayList<Message> M = dbC.getReplyText(time);
		System.out.println("Start Reply Text !");
		for(Message m : M){
			facebookClient.publish(m.getId()+"/messages", FacebookType.class,
					Parameter.with("message", m.getMessage()));
			
		}
		
	}
	
	
	
	public String getPAGE_ID() {
		return PAGE_ID;
	}
	public void setPAGE_ID(String pAGE_ID) {
		PAGE_ID = pAGE_ID;
	}
	public String getMY_ACCESS_TOKEN() {
		return MY_ACCESS_TOKEN;
	}
	public void setMY_ACCESS_TOKEN(String mY_ACCESS_TOKEN) {
		MY_ACCESS_TOKEN = mY_ACCESS_TOKEN;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// For all API calls, you need to tell RestFB how to turn the JSON
		// returned by Facebook into Java objects.  In this case, the data
		// we get back should be mapped to the User and Page types, respectively.
		// You can write your own types too!

		//User user = facebookClient.fetchObject("me", User.class);
		//Page page = facebookClient.fetchObject("DoujinDalamBotol", Page.class);

		//System.out.println("User name: " + user.getName());
		//System.out.println("Page likes: " + page.getLikes());
		//facebookMessageExtractor test = new facebookMessageExtractor();
		//test.replyableConversation();

	}

}
