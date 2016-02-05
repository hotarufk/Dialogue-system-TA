

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import SocialMediaHandler.facebookMessageExtractor;
import TextProcessingHandler.DataProcessor;
import TextProcessingHandler.DialogueManager;
import TextProcessingHandler.LearnerAndClassifier;

/**
 * Servlet implementation class DialogueSystem
 */
@WebServlet("/DialogueSystem")
public class DialogServlet extends HttpServlet implements Runnable {
	//attribut
	facebookMessageExtractor facebookHandler = new  facebookMessageExtractor();
	DataProcessor dataprocessor = new DataProcessor();
	LearnerAndClassifier wekaHandler = new LearnerAndClassifier();
	DialogueManager DM= new DialogueManager();
	
	String userToken;
	String page_id;
	int retrive_interval;
	int learning_method;
	String training_data_limit;
	Thread dm;
	int count =0;
	boolean running;
	static int second = 1;
	static int milisecond =1000;
	
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DialogServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Set response content type
	      response.setContentType("text/html");
	      String message ="Dialogue System Terminated";
	      //
	      running = false;
	      // Actual logic goes here.
	      PrintWriter out = response.getWriter();
	      out.println(message);
	      System.out.println(message);
	      
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 handleRequest(request, response);


	}
	
	public void init(){
		running = false;
		dm = new Thread(this);
	    dm.setPriority(Thread.MIN_PRIORITY);
	    dm.start();	
	}
	
	public void destroy() {
		    dm.stop();
		  }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			while(running){
			try {
				//java.util.Date time = new java.util.Date();
				System.out.println("test1");
				//ambil riwayat chat
				java.util.Date time = new java.util.Date();
				facebookHandler.replyableConversation();
				System.out.println("test2");
				//simpan riwayat chat didalam tabel conversation
				dataprocessor.setMessagetoConversation(time);
				
				System.out.println("test3");
				//lakukan klasifikasi terhadap chat
				if (learning_method == 0){
					wekaHandler.createTrainingSet();
				}else wekaHandler.createTrainingSet(training_data_limit);
				wekaHandler.createTestSet();
				wekaHandler.makePrediction(wekaHandler.getTrain());
				System.out.println("test4");
				//pembangunan reply oleh dialogue System
				DM.generateReply();
				//facebook handlermengirimkan pesan balasan
				int minute = retrive_interval;
				int sleeptime = milisecond*second*minute;				
				facebookHandler.sendReply(time);
				System.out.println("TEXT Replied SLEEP "+ retrive_interval+" Seconds ");
				//System.out.println("TEXT Replyed SLEEP 10 Minutes ");

				Thread.sleep(sleeptime);				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println ("shit happen! ");
				}
			}
			try {
				//System.out.println("Waiting System Initiated, SLEEP for 1 minutes ");
				
				Thread.sleep(retrive_interval*second*milisecond);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		PrintWriter out = res.getWriter();
		res.setContentType("text/plain");
		String respon = "";

		Enumeration e = req.getParameterNames();
		while(e.hasMoreElements()){
			String param = (String) e.nextElement();
			System.out.println(param);
			}
		//parameter acc token
		String paramName = "permission_token";
		userToken = req.getParameter(paramName);
		
		paramName = "page_id";
		page_id = req.getParameter(paramName);
		
		paramName = "retrive_interval";
		retrive_interval = Integer.parseInt(req.getParameter(paramName));
		
		paramName = "learning_method";
		learning_method = Integer.parseInt(req.getParameter(paramName));
		
		paramName = "training_data_limit";
		training_data_limit = req.getParameter(paramName);
				
		if(userToken != null && retrive_interval >0 && page_id != null ){
			if(learning_method == 0 || (learning_method ==1 &&training_data_limit != null)){
			facebookHandler = new facebookMessageExtractor(page_id,userToken);
			running = true;
			System.out.println("System Started");
			}
		}else System.out.println("parameter not complete !");
		

		
		//System.out.println(req.getParameterNames().toString());
		System.out.println(respon);
		out.write(respon);
		out.close();
	      

	}
}
