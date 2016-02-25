package TextProcessingHandler;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.stemmers.Stemmer;
import helper.databaseConnection;

public class stemmer implements Stemmer {
	private databaseConnection dc = new databaseConnection();
	private String kataAsal;
	private Vector<String> KamusKata;
	public boolean cekKamus(String kata){
		// cari di kamuskata
		for(String kk : KamusKata){
			if(kata.compareToIgnoreCase(kk) == 0){
				return true;
			}
		}
		return false; // jika tidak ada FALSE
		}
	
	
	public stemmer() {
		//load data kamus kata
		KamusKata  = dc.dictionary();
		
	}
	
	public String Del_Inflection_Suffixes(String kata){
		kataAsal = kata;
		String pattern = "([km]u|nya|[kl]ah|pun)$";
	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);
	      // Now create matcher object.
	      Matcher m = r.matcher(kataAsal);
	      if (m.find( )) {
	    	  //System.out.println("found");
	    	  String __kata = kataAsal.replaceAll("([km]u|nya|[kl]ah|pun)$", "");	  
	    	  return __kata;
	      }
	      return kataAsal;
		}
		
	// Cek Prefix Disallowed Sufixes (Kombinasi Awalan dan Akhiran yang tidak diizinkan)
	public boolean Cek_Prefix_Disallowed_Sufixes(String kata){
		String pattern = "^(be)[[:alpha:]]+(i)$";
	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);
	      // Now create matcher object.
	      Matcher m = r.matcher(kata);
	      if (m.find( ))
	    	  return true;
	      
	      pattern = "^(se)[[:alpha:]]+(i|kan)$";
	      r = Pattern.compile(pattern);
	      m = r.matcher(kata);
	      // se- dan -i,-kan
	      if (m.find( ))
	    	  return true;
	      
	return false;
	}

	// Hapus Derivation Suffixes (Åg-iÅh, Åg-anÅh atau Åg-kanÅh)
	public String Del_Derivation_Suffixes(String kata){
	kataAsal = kata;
	String pattern = "(i|an)$";
    // Create a Pattern object
    Pattern r = Pattern.compile(pattern);
    // Now create matcher object.
    Matcher m = r.matcher(kata);
    if (m.find( )){
   	 // Cek Suffixes
    	String __kata = kata.replaceAll("(i|an)$", "");
    	if(cekKamus(__kata)) // Cek Kamus
    		return __kata;    	
    }
	/* Jika Tidak ditemukan di kamus */
	return kataAsal;
	}

	// Hapus Derivation Prefix (Ågdi-Åh, Ågke-Åh, Ågse-Åh, Ågte-Åh, Ågbe-Åh, Ågme-Åh, atau Ågpe-Åh)
	public String Del_Derivation_Prefix(String kata){
	String kataAsal = kata;

	/*  Tentukan Tipe Awalan*/
	// Jika di-,ke-,se-
	String pattern = "^((di)|([ks]e))";
    // Create a Pattern object
    Pattern r = Pattern.compile(pattern);
    // Now create matcher object.
    Matcher m = r.matcher(kata);
    if (m.find( )){
    	String __kata = kata.replaceAll("^((di)|([ks]e))", "");
    	if(cekKamus(__kata))
    		return __kata; // Jika ada balik
    String __kata__ = Del_Derivation_Suffixes(__kata);
    	if(cekKamus(__kata__))
    		return __kata__;
    }

	//System.out.println("_kata : "+__kata);
	
	
	
	/*end Ågdiper-Åh,*/		
	String __kata = kata.replaceAll("^(diper)", "");
	if(cekKamus(__kata))
		return __kata; // Jika ada balik
	
	/*end Ågdiper-Åh,*/
	
	//Jika awalannya adalah Ågte-Åh, Ågme-Åh, Ågbe-Åh, atau Ågpe-Åh
	__kata = kata.replaceAll("^([tmbp]e)", "");
	if(cekKamus(__kata))
		return __kata; // Jika ada balik
	
	///*  Cek Ada Tidaknya Prefik/Awalan (Ågdi-Åh, Ågke-Åh, Ågse-Åh, Ågte-Åh, Ågbe-Åh, Ågme-Åh, atau Ågpe-Åh) */
	pattern = "^(di|[kstbmp]e)";
    // Create a Pattern object
    r = Pattern.compile(pattern);
    // Now create matcher object.
    m = r.matcher(__kata);
    if (!m.find( ))
    	return kataAsal;
	

	return kataAsal;
	}

	@Override
	public String stem (String  word){

		String kataAsal =  word;

		/* 1. Cek Kata di Kamus jika Ada SELESAI */
		//System.out.println("test 1");
		if(cekKamus( word)){ // Cek Kamus
		return  word; // Jika Ada kembalikan
		}

		//System.out.println("test 2");
		/* 2. Buang Infection suffixes (\-lahÅh, \-kahÅh, \-kuÅh, \-muÅh, atau \-nyaÅh) */
		 word = Del_Inflection_Suffixes( word);
		 
		 //System.out.println("test 3");
		/* 3. Buang Derivation suffix (\-iÅh or \-anÅh) */
		 word = Del_Derivation_Suffixes( word);

		 //System.out.println("test 4");
		/* 4. Buang Derivation prefix */
		 word = Del_Derivation_Prefix( word);
		 
		return word.replace(" ", "");

		}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		stemmer s = new stemmer();
		System.out.println(s.stem(" "));

	}

	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}

}










