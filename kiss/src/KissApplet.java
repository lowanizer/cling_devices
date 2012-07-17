import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*; 
import java.io.*;
import java.net.*;
import java.applet.*;
import applet.engine;
import applet.engine_status_listener;

import javax.swing.JApplet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class KissApplet extends JApplet implements engine_status_listener{
	
	private JSObject jso;
	private engine tab_engines[];
	private boolean try_again = false;
	private int engine_init = -1;
	
	/*
	 * Initializes the applet by creating the objects we need. 
	 * Calls the JS function "start()" within the create_engine 
	 * method when it is ready.
	 */
	public void init(){
		
		String command = getParameter("command");
		jso = JSObject.getWindow(this);
		tab_engines = new engine[50];
		
		if(command==null){		
			create_engine(0);
		}
		else{
			try {
				create_sentence_from_string(command);
			} catch (Exception e) {
				System.out.println("Error creating sentence from string : "+e);
			}
			create_engine(0);
			jso.call("rewrite_with_string",new String[] {"0",command});
		}
		
		//create_sentence_from_string("1,3,0,2,1,0"); // "When bob is in living_room I want to open a window blind ." 
		//tab_engines[0].transfer();
	}

	
	// Creates an engine for a sentence
	public void create_engine(int id){
		try {
			tab_engines[id] = new engine();
		} catch (Exception e) {
			System.out.println("Error creating engine : "+e);
		}
		tab_engines[id].add_engine_status_listener(this);
		try {
			tab_engines[id].init();
			engine_init = id;
		} catch (Exception e) {
			System.out.println("Error initializing engine : "+e);
		}
	}
	
	// Resets the sentence of an engine
	public void reset_engine(int id){
		tab_engines[id].reset();
	}
	
	/*
	 * Makes a list of available words depending on the rest of the
	 * sentence, sending it to the JS function "list_words(string)"
	 * in the form of a single string, each word being separated from
	 * the others by a comma. 
	 */
	public void next_possible_words(int id){
		String list = tab_engines[id].next_possible_words();
		jso.call("list_words", new String[] {""+id,list});
	}
	
	// Reads a word
	public void read(int id, String str) throws Exception{
		tab_engines[id].read(str);
	}
	
	// Transfers the sentence
	public void transfer(final int id){
	
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
	         public Void run() {
	        	tab_engines[id].transfer();
				return null;
	         }
	     });
	}
	
	
	// Called when engine starts new asynchoronous operation
	public void engine_initializing(String message){
		jso.call("shadowing", new Boolean[] {true});
		jso.call("notification", new String[] {"Initializing : "+message});
	}

	// Called when engine is in the middle of asynchronous operation
	// to update its status.
	public void engine_status_changed(String new_status){
		jso.call("notification", new String[] {"Status : "+new_status});
	}

	// Called when engine encountered an error in an asynchronous operation
	// return true to retry, false to quit.
	public boolean engine_error(String error_message){
		jso.call("notification", new String[] {"Error : "+error_message});
		jso.call("confirm_try_again", new String[] {"Error : "+error_message+"\nTry again ?"});
		if(!try_again){
			jso.call("shadowing", new Boolean[] {false});
			jso.call("notification", new String[] {""});
		}
		return try_again;
	} 

	// Called when engine is done with asynchronous operation (successfully);
	public void engine_dispose(String message){
		jso.call("shadowing", new Boolean[] {false});
		jso.call("notification", new String[] {""});
		jso.call("alert", new String[] {message});
		if(engine_init!=-1){
			jso.call("start",new String[] {""+engine_init});
			engine_init = -1;
		}
		else jso.call("op_successful",null);
	}
	
	
	// Used to handle the javascript "confirm" response by calling this method 
	public void set_try_again(boolean bool){
		try_again = bool;
	}
	

	public void connexion_test(String url) throws IOException{
		int x = 0;
		try{
			URL u = new URL(url);
			x++;
			URLConnection conn = u.openConnection();
			x++;
			conn.connect();
			System.out.println("connecté");
		}
		catch(IOException e){
			System.out.println("fail "+x);
		}
	}

	/*
	 *  Can be useful in case of sentence creation from only a list of choices
	 *  separated by commas.
	 */
	public void create_sentence_from_string(String str) throws Exception{
		String choices_list[] = str.split(",");
		String poss_words[];
		int i=0;
		String word;
		
		final engine en = new engine();
		en.add_engine_status_listener(this);
		
		while(i<choices_list.length){
			poss_words = en.next_possible_words().split(",");
			
			if(poss_words.length==1){
				word = poss_words[0];
			}
			else{
				word = poss_words[Integer.parseInt(choices_list[i])];
				i++;				
			}
			
			try {
				en.read(word);
				System.out.println(word);
			} catch (Exception e) {
				System.out.println("Error in reading : "+e);
			}
			
			if(word.equals(".")) break;
		}
		
		en.transfer();
	}

}
