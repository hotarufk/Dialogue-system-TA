package helper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;


import com.restfb.types.Conversation;
import com.restfb.types.Message;
import com.restfb.types.NamedFacebookType;
import com.google.gson.Gson;

public class databaseConnection {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/doujindb";
   static final int menit = 1;
   //  Database credentials
   static final String USER = "root";
   static final String PASS = "root";

   Connection conn;
   Statement stmt;
   
   //check apakah kata adalah kata dasar
   public Vector<String> dictionary(){
	   Vector <String> Kamus = new Vector<String>();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      java.util.Date date= new java.util.Date();
		      Calendar calendar = Calendar.getInstance();
		      calendar.add(Calendar.MINUTE, -10);
		      java.util.Date datebefore = calendar.getTime();
		      
		      String statement = "SELECT * from tb_katadasar ";
		     ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      Gson gson = new Gson();
		      while(rs.next()){
		         Kamus.add(rs.getString("katadasar"));
		      }
		      
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   
	   return Kamus;
   }

   //Mencari text dengan reply yang siap dikirim
   public ArrayList<Message> getReplyText(java.util.Date time){
	   ArrayList<Message> M = new ArrayList<Message> ();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      
		     String statement = "SELECT * FROM `procesed_texts` INNER JOIN `conversations` ON `procesed_texts`.`conversation_id` = `conversations`.`id`"
		    		 +"  INNER JOIN `users` ON `users`.`id` = `conversations`.`user_id`"
		    		 +" WHERE `procesed_texts`.`reply` IS NOT NULL "
		    		 + "AND `procesed_texts`.`updated_at`  >= '"+new Timestamp(time.getTime())+"'";
		     System.out.println("Reply Text Querry : "+statement);
		     ResultSet rs = stmt.executeQuery(statement);
		     
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         //String id = rs.getString("facebook_user_id");
		         String text = rs.getString("reply");
		         String id = rs.getString("facebook_conversation_id");
		        //Display values
		        //////System.out.println(", Raw Data: " + rd);
		        Message temp = new Message();
		        temp.setMessage(text);
		        temp.setId(id);
		        M.add(temp);
		      }
		      
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");
	 //////System.out.println("Message Number :"+Mes.size());  	   
	 return M;

   }
      
   //return raw data created at current time and x minutes before
   public ArrayList<Message> getRawData(java.util.Date date){
	   ArrayList<Message> Mes = new ArrayList<Message>();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		    //  ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      //java.util.Date date= new java.util.Date();
		      Calendar calendar = Calendar.getInstance();
		      calendar.add(Calendar.MINUTE, -menit);
		      //kurangin tanggal date dengan menit yang diminta
		      
		      java.util.Date datebefore = calendar.getTime();
		      
		      String statement = "SELECT * FROM `raw_datas` WHERE `created_at`  >= '"+new Timestamp(date.getTime())+"'";
		     ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      Gson gson = new Gson();
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         String rd = rs.getString("raw_data");
		        //Display values
		        ////System.out.println(", Raw Data: " + rd);
		        Message temp = gson.fromJson(rd, Message.class);
		        Mes.add(temp);
		      }
		      
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");
	 ////System.out.println("Message Number :"+Mes.size());  	   
	 return Mes;
   }   
   
   //return id of user, if null then there is no user with message sender facebook id
   public String getUserId(Message M){
	   String id = null;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		  //    ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		//      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		     String statement = "SELECT * FROM `users` WHERE `facebook_user_id` ='"+M.getFrom().getId()+"'";
		     ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      Gson gson = new Gson();
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         String username = rs.getString("username");
		         id = rs.getString("id");
		         String facebookUserId = rs.getString("facebook_user_id");
		         String email = rs.getString("email");
		        //Display values
	//	        ////System.out.println(" id : " + id);
	//	        ////System.out.println(" username : " + username);
	//	        ////System.out.println(" facebook user id : " + facebookUserId);
	//	        ////System.out.println(" email : " + email);
		      }
		      
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");  	   
	 return id;
   }   
   
   //get user conversation id, if user dont have conversation yet or no active conversation return null
   public String getUserActiveConversation(String userId){
	   String UserActiveConversationId=null;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      String statement = "SELECT * FROM `conversations` WHERE `user_id`  = '"+userId+"'";
		     
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         String id = rs.getString("id");
		        //Display values
		        ////System.out.println(", Conversation Id : " + id);
		        UserActiveConversationId = id;
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   return UserActiveConversationId;
   }

   //get user purchase log id, if user dont have purchase log yet or no active purchase log return null
   public String getUserActivePurchaseLog(int conversationId){
	   String UserActivePurchaseLogId=null;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      String statement = "SELECT * FROM `purchase_logs` INNER JOIN `conversations` ON `purchase_logs`.`user_id` = `conversations`.`user_id` WHERE `conversations`.`id`  = "+conversationId+" AND `purchase_logs`.`status` = 0";
		     
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         String id = rs.getString("id");
		        //Display values
		        ////System.out.println(", Purchase Log Id : " + id);
		        UserActivePurchaseLogId = id;
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   return UserActivePurchaseLogId;
   }
   
   //getTrainingData with Timesatmp not after  T
   public ArrayList<processedText> getTrainingData(String date){
	   ArrayList<processedText> TDList = new ArrayList<processedText>();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      String statement = "SELECT * FROM `training_datas` INNER JOIN `text_categories` ON `training_datas`.`text_category_id` = `text_categories`.`id` WHERE `training_datas`.`created_at`  <= '"+date+"'";
		     
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		     
		      while(rs.next()){
		    	 processedText data = new processedText();
	   			//////System.out.println("test III");
		        //Retrieve by column name
		    	 data.setId(rs.getInt("id"));
		    	 data.setClassificationId(rs.getInt("text_category_id"));
		    	  data.setMessage(rs.getString("message"));
		         TDList.add(data);
		         
		        //Display values
		        ////System.out.println(", Training Data message : " + data.getMessage());
		        ////System.out.println(", Training Data classificationId : " + data.getClassificationId());
		        
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   
	   return TDList;
   }

   //
   public ArrayList<processedText> getProcessedData(int i){
	   ArrayList<processedText> TDList = new ArrayList<processedText>();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      String statement =null;
		      //Dapetin jarak setiap narik data
		      if(i==0) //u/test data
		    	  statement = "SELECT * FROM `procesed_texts` WHERE `text_category_id` IS NULL ";
			  else //u/ ambil reply
				  statement = "SELECT * FROM `procesed_texts` WHERE `procesed_texts`.`reply` IS NULL AND `text_category_id` IS NOT NULL";
		        
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		     
		      while(rs.next()){
		    	 processedText data = new processedText();
	   			//////System.out.println("test III");
		        //Retrieve by column name
		    	 data.setId(rs.getInt("id"));
		    	  data.setConversationId(rs.getInt("conversation_id"));
		    	  data.setMessage(rs.getString("message"));
		    	  if(i!=0) data.setClassificationId(rs.getInt("text_category_id"));
		         TDList.add(data);
		         
		        //Display values
		        ////System.out.println(", Test Data Id : " + data.getConversationId());
		        ////System.out.println(", Test Data Message : " + data.getMessage());
		        
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   
	   return TDList;
	   
   }
   
   //mengembalikan list berupa kode dan nama dari kategori   
   public ArrayList<textCategory> getTextCategory(){
	   ArrayList<textCategory> TDList = new ArrayList<textCategory>();
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      String statement = "SELECT * FROM  `text_categories`";
		     
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		     
		      while(rs.next()){
		    
	   			//////System.out.println("test III");
		        //Retrieve by column name
		    	 textCategory tC = new textCategory();
		    	 tC.setId(rs.getInt("id"));
		    	 tC.setName(rs.getString("name"));
		         TDList.add(tC);
		         
		        //Display values
		        ////System.out.println(", text category  : " + rs.getNString("name"));
		        
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   
	   return TDList;
   }
   
   public String getUserIdFromConversationId(int i){
	   String UserId=null;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      //Dapetin jarak setiap narik data
		      String statement = "SELECT * FROM `users` INNER JOIN `conversations` ON `users`.`id` = `conversations`.`user_id` WHERE `conversations`.`id`  = "+i;
		     
		      ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      while(rs.next()){
	   			//////System.out.println("test III");
		        //Retrieve by column name
		         String id = rs.getString("id");
		        //Display values
		        ////System.out.println(", Purchase Log Id : " + id);
		        UserId = id;
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");	   
	   return UserId;
	   
   }
   
   //Operasi Update
    public ResultSet executeUpdateQuery(String statement){
	   ResultSet rs = null;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      stmt.executeUpdate(statement);
		      

		     
		      //////System.out.println("rs :"+rs.toString());
		      //STEP 6: Clean-up environment
		      //rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");
	   	   
	 return rs;
   }
   
   //Operasi Insert
   public void insert(String sql){
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      ////System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      ////System.out.println("Inserting records into the table...");
		      stmt = conn.createStatement();
		      
		      stmt.executeUpdate(sql);
		      ////System.out.println("Inserted records into the table...");

		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   ////System.out.println("Goodbye!");
	   
	   
   }
   
   /////query untuk DialogueManager
   public ArrayList<Integer> getItemId(String cleankeyword,int type){
	  ArrayList<Integer> relevantId = new ArrayList<Integer>();
	  int id;
	   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      ////System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      ////System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      //String cleankeyword = stem.stem(keyword);
		      //String cleankeyword = keyword;
		      String statement = "SELECT `id` FROM  `items` WHERE  `name` LIKE '"+cleankeyword+"%'LIMIT 5";
		      if(type == 0){
		    	  statement = "SELECT `id` FROM  `items` WHERE  `name` LIKE '"+cleankeyword+" %' OR `name` LIKE '% "+cleankeyword+"'LIMIT 5";		    	  
		      }else if(type==1){
		    	  statement = "SELECT `id` FROM  `items` WHERE  `description` LIKE '"+cleankeyword+" %' OR `description` LIKE '% "+cleankeyword+"' LIMIT 5"; 
		      }
		     
		     ////System.out.println(statement);
		     ResultSet rs = stmt.executeQuery(statement);
		      Gson gson = new Gson();
		      while(rs.next()){
		        //Retrieve by column name
		         id = rs.getInt("id");
		         relevantId.add(id);
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
	   	   ////System.out.println("Goodbye!");  	   
			   
	  return relevantId;
   }
   
   //
   public item getItemData(int itemId){
		 item result = new item();
		 try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      ////System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      ////System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      
			     String statement = "SELECT * FROM  `items` WHERE  `id` = '"+itemId+"' LIMIT 1";
			     ////System.out.println(statement);
			     ResultSet rs = stmt.executeQuery(statement);
			      Gson gson = new Gson();
			      while(rs.next()){
			        //Retrieve by column name
			         result.setName(rs.getString("name"));
			    	 result.setId(rs.getInt("id"));
					 result.setPrice(rs.getInt("price"));
					 result.setQuantity(rs.getInt("quantity"));
					 result.setDescription(rs.getString("description"));
					}
			      //STEP 6: Clean-up environment
			      rs.close();
			      stmt.close();
			      conn.close();
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }// nothing we can do
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }//end try
		   	   ////System.out.println("Goodbye!");  	   
				   
		  return result;	   
   }

   //
   public  ArrayList<item> getPurchaseLogData(String purchaseLogId){
		 ArrayList<item> result = new ArrayList<item>();
		 try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      ////System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      ////System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      
			     String statement = "SELECT * FROM  `purchase_log_items` WHERE  `id` = '"+purchaseLogId+"' LIMIT 1";
			     ////System.out.println(statement);
			     ResultSet rs = stmt.executeQuery(statement);
			      while(rs.next()){
			        //Retrieve by column name
			         item purchase = getItemData(rs.getInt("item_id"));
			         result.add(purchase);
			    	}
			      //STEP 6: Clean-up environment
			      rs.close();
			      stmt.close();
			      conn.close();
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }// nothing we can do
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }//end try
		   	   ////System.out.println("Goodbye!");  	   
				   
		  return result;	   
   }
   
   
   
	public ArrayList<processedText> getTrainingData() {
		   ArrayList<processedText> TDList = new ArrayList<processedText>();
		   try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");
	
			      //STEP 3: Open a connection
			      ////System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);
	
			      //STEP 4: Execute a query
			      ////System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      
			      //Dapetin jarak setiap narik data
			      String statement = "SELECT * FROM `training_datas` INNER JOIN `text_categories` ON `training_datas`.`text_category_id` = `text_categories`.`id`";
			     
			      ////System.out.println(statement);
			     ResultSet rs = stmt.executeQuery(statement);
			     
			      while(rs.next()){
			    	 processedText data = new processedText();
		   			//////System.out.println("test III");
			        //Retrieve by column name
			    	 data.setId(rs.getInt("id"));
			    	 data.setClassificationId(rs.getInt("text_category_id"));
			    	  data.setMessage(rs.getString("message"));
			         TDList.add(data);
			         
			        //Display values
			        ////System.out.println(", Training Data message : " + data.getMessage());
			        ////System.out.println(", Training Data classificationId : " + data.getClassificationId());
			        
			      }
			      //STEP 6: Clean-up environment
			      rs.close();
			      stmt.close();
			      conn.close();
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }// nothing we can do
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }//end try
		   	   ////System.out.println("Goodbye!");	   
		   
		   return TDList;
	}
	 

	//
	public static void main(String[] args) {

		databaseConnection test = new databaseConnection();
		String sql = "SELECT * FROM users";
		//java.util.Date date= new java.util.Date();
		 //////System.out.println(new Timestamp(date.getTime()));
		//String sqli = "INSERT INTO `raw_datas`(`raw_data`, `created_at`, `updated_at`) " +
		//	                   "VALUES ('test1','"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"')";
		
		//test.insert(sqli);
		//ResultSet rs = test.executeQuery(sql);
		//test.getRawData();
	
		
		  
	
	}//end main

	public int getPurchaseLogStatus(int conversationId) {
		// TODO Auto-generated method stub
		int result = -9999;
		 try{
			      //STEP 2: Register JDBC driver
			      Class.forName("com.mysql.jdbc.Driver");

			      //STEP 3: Open a connection
			      ////System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      ////System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      String purchaseLogId = getUserActivePurchaseLog(conversationId);
			     String statement = "SELECT * FROM  `purchase_logs` WHERE  `id` = '"+purchaseLogId+"' LIMIT 1";
			     ////System.out.println(statement);
			     ResultSet rs = stmt.executeQuery(statement);
			      while(rs.next()){
			        //Retrieve by column name
			         result = rs.getInt("status");
			    	}
			      //STEP 6: Clean-up environment
			      rs.close();
			      stmt.close();
			      conn.close();
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }// nothing we can do
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }//end try
		   	   ////System.out.println("Goodbye!");  	   
				   
		  return result;	   
	}

}//end