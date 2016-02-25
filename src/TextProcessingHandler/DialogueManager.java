package TextProcessingHandler;

import helper.databaseConnection;
import helper.item;
import helper.processedText;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DialogueManager {
	//attribute
	private ArrayList<processedText> data = new ArrayList<processedText>();
	private databaseConnection dc = new databaseConnection();
	private stemmer st = new stemmer();
	
	//fungction
	public void generateReply(){
		//ambil message dengan reply == null
		data = dc.getProcessedData(1);
		for(processedText text : data){
			//check jenis nya apa
			String Answer = "Test";
			System.out.println("Processed String : "+text.getMessage());
			//check klasifikasi, kalo > 4 ga perlu search 
			if(text.getClassificationId() > 4){ //tidak perlu id barang
				if(text.getClassificationId()== 5){ //biaya Pengiriman
					String purchaselogId = 	dc.getUserActivePurchaseLog(text.getConversationId());
					ArrayList<item> purchases = dc.getPurchaseLogData(purchaselogId);
					int price = 0;
					String rincian="";
					for(item purchase:purchases){
						price+=purchase.getPrice();
						rincian += purchase.getName() +" x1 @"+purchase.getPrice()+" \n";
					}
					Answer = "Total belanja ada adalah : "+price+" \n Dengan Rincian : "+rincian;	
//////////////////////////////////////
				}else if(text.getClassificationId()== 6){ //cara pembayaran
					Answer = "Pembayaran dapat dilakukan melalui Bank BCA dengan nomor xxx-xxx-xxxxx-xx atas nama  YYY, maksimal pembayaran 2 hari setelah pemesanan.";
//////////////////////////////////////
				}else if(text.getClassificationId()== 7){//status pengiriman
					int status = dc.getPurchaseLogStatus(text.getConversationId());
					Answer = "Status Pengiriman Anda : "+status;
//////////////////////////////////////
				}else //tidak termasuk 7 ertanyaan awal
				Answer = "Maaf pertanyaan anda kurang jelas ";
//////////////////////////////////
			}else{ //perlu id barang
				int itemid = createSearcList(text.getMessage());	
				if(text.getClassificationId()== 1){//ketersediaan
					if(itemid != -9999){
						item product = dc.getItemData(itemid);
						if(product.getQuantity()>0) Answer = "Saat ini produk "+product.getName()+" tersedia sebanyak "+product.getQuantity()+" unit";
						else Answer = "Mohon Maaf saat ini produk "+product.getName()+" sudah habis";
					}else Answer = "Maaf kami tidak memiliki produk yang anda tanyakan";
//////////////////////////////////////					
				}else if(text.getClassificationId()== 2){ //harga
					if(itemid != -9999){
						item product = dc.getItemData(itemid);
						Answer = "Harga Produk "+product.getName()+" adalah Rp."+product.getPrice()+" per unit";
					}else Answer = "Maaf kami tidak memiliki produk yang anda tanyakan harganya";	
//////////////////////////////////////
				}else if(text.getClassificationId()== 3){ //deskripsi
					if(itemid != -9999){
						item product = dc.getItemData(itemid);
						Answer = "Berikut adalah detail dari produk "+product.getName()+" \n "+product.getDescription();
					}else Answer = "Maaf kami tidak memiliki produk yang anda minta detailnya";
//////////////////////////////////////
				}else if(text.getClassificationId()== 4){ //Pesan
					if(itemid != -9999){
						item product = dc.getItemData(itemid);
						if(product.getQuantity()>0){
							String purchaselogId= dc.getUserActivePurchaseLog(text.getConversationId());
							java.util.Date date= new java.util.Date();
							String query="";
							if(purchaselogId == null){///create new purchase_log then
								//////////
								System.out.println("text conversation id : "+text.getConversationId());
								String userid= dc.getUserIdFromConversationId(text.getConversationId());
								///masalah pertama
								query = "INSERT INTO `purchase_logs`(`user_id`, `status`,`date`, `created_at`, `updated_at`) VALUES ("+userid+",0,'"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"')";
								System.out.println("query make new purchase log : "+query);
								dc.insert(query);
								purchaselogId= dc.getUserActivePurchaseLog(text.getConversationId());
							}
							query = "INSERT INTO `purchase_log_items`(`purchase_log_id`, `item_id`, `created_at`, `updated_at`) VALUES ("+purchaselogId+","+itemid+",'"+new Timestamp(date.getTime())+"','"+new Timestamp(date.getTime())+"')";
							System.out.println("query pesan : "+query);
							dc.insert(query);	
							Answer = "Produk "+product.getName()+" Telah berhasil anda pesan sebanyak 1 pcs";
						}else Answer = "Mohon Maaf produk "+product.getName()+" tidak dapat dipesan, produk sudah habis";
					}else Answer = "Maaf kami tidak memiliki produk yang anda pesan";
//////////////////////////////////////				
				}
			}
			updateClassifiedText(text.getId(), Answer);
			System.out.println();
		}
		
		
	}
	
	void updateClassifiedText(Integer id,String reply){
		java.util.Date date = new java.util.Date();
		String sqli = "UPDATE `procesed_texts` SET `reply`='"+reply+"',"
				+ "`updated_at`='"+new Timestamp(date.getTime())+"' WHERE `id`='"+id+"'";
		System.out.println("Querry : "+sqli);
		dc.executeUpdateQuery(sqli);
				
	}
	
	void getHarga(){
		
		//check apakah barang x ada di toko
		
		
		//kalo ga k da
		
	}
	

	int createSearcList(String message){
		
		//bagi string ke dalam keywords
		String [] keywords = message.split(" ");
		HashMap map = new HashMap();
		for (String keyword : keywords){
			//lakukan query ke tabel barang, cari nama barang yg sesuai 
			String kw = st.stem(keyword);
			//check kalo string nya kosong atau cuma spasi mending skip
			if (!kw.equalsIgnoreCase("")){ //string  ga kosong
				System.out.println("Word : "+keyword);
				System.out.println("Stemmed Word :"+ kw);
				for(int i=0;i<2;i++){
					ArrayList<Integer> result = dc.getItemId(kw,i);
					 for(int id : result){
						 int additional = 0;
						 if(i == 0) //nama
							 additional = 25;
						 else if(i == 1) // deskripsi
							 additional = 5;	 
						 if(map.containsKey(id)){
							 //tambah
							int num = (int) map.get(id)+additional;
							map.put(id, num); 
						 }else map.put(id, additional);
					 }
					 System.out.println("isi map (itemId,Score) : "+map.toString());				
				}				
			}
 
		}
		//sort map
		int count = 0;
		int id = -9999;
		Iterator it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if((int)pair.getValue()> count){id = (int) pair.getKey(); count = (int) pair.getValue();}
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    System.out.println("Id : "+id+" , Count : "+count);
	    return id;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DialogueManager Asw = new DialogueManager();
		Asw.generateReply();
		System.out.println("asw");

	}

}
