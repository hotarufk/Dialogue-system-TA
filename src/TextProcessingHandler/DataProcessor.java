package TextProcessingHandler;

import helper.databaseConnection;

import java.sql.Timestamp;
import java.util.ArrayList;

import SocialMediaHandler.facebookMessageExtractor;

import com.restfb.types.Conversation;
import com.restfb.types.Message;
import com.restfb.types.Page;
import com.restfb.types.User;

public class DataProcessor {
	
	//attribute
	databaseConnection dc =new databaseConnection();
	ArrayList<Message> listMessage = new ArrayList<Message>(); 
	java.util.Date date;
	String sql=null;;
	String userId=null;
	String conversationId=null;;
	
	//function
	//set message to its conversation with another user
	public void setMessagetoConversation(java.util.Date date){
		//buka database
		listMessage = dc.getRawData(date);
		//query rawdata untuk ambil data yg terakhir diambil
		for(Message message : listMessage){
			
			userId = dc.getUserId(message);
			if(userId == null){ ///user belum ada di database
				//buat user baru
				System.out.println("create new User !");
				userId=createNewUser(message);
			}else System.out.println("User Found !");
			//cek apakah user sudah memiliki conversation/ada conversation yg aktif ?
			
			conversationId=dc.getUserActiveConversation(userId);
			if(conversationId==null){
			   	//simpan nomor con yang baru dibuat
				conversationId=createNewConversation(message);
			}else System.out.println("Conversation Found !");			
			
			//masukan message ke conversation yang bersangkutan
			addMessagetoConversation(message);
		}

	}
	
	String createNewUser(Message message){
		userId = null;
		date= new java.util.Date();
		sql = "INSERT INTO `users`(`username`, `role`, `facebook_user_id`, `created_at`, `updated_at`) " +
                "VALUES ('"+message.getFrom().getName()+"',1,"+message.getFrom().getId()+",'"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"')";
		System.out.println("Sql : "+sql);
		dc.insert(sql);
		//masukin userId setelah dibikin yg baru
		userId = dc.getUserId(message);
	return userId;
	}
	
	String createNewConversation(Message message){
		//belum ada conversation yg didmiliki user/tidak ada convversation aktif
	   	sql = "INSERT INTO `conversations`(`user_id`, `created_at`, `updated_at`) " +
                   "VALUES ("+userId+",'"+new Timestamp(message.getCreatedTime().getTime())+"','"+new Timestamp(message.getCreatedTime().getTime())+"')";
	   	System.out.println("Sql : "+sql);
	   	dc.insert(sql);
	   	//simpan nomor con yang baru dibuat
	   	conversationId=dc.getUserActiveConversation(userId);
		return conversationId;
	}
	
	void addMessagetoConversation(Message message){
		java.util.Date date= new java.util.Date();
		sql = "INSERT INTO `procesed_texts`( `message`, `conversation_id`,`facebook_conversation_id`,`created_at`, `updated_at`) " +
                "VALUES ('"+message.getMessage()+"',"+conversationId+",'"+message.getId()+"','"+ new Timestamp(message.getCreatedTime().getTime())+"','"+new Timestamp(date.getTime())+"')";
		//System.out.println("Sql : "+sql);
		dc.insert(sql);
	}
	
		
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// For all API calls, you need to tell RestFB how to turn the JSON
		// returned by Facebook into Java objects.  In this case, the data
		// we get back should be mapped to the User and Page types, respectively.
		// You can write your own types too!

		DataProcessor test = new DataProcessor();
		//test.setMessagetoConversation();

	}
	
}
