package edu.upenn.mkse212;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.upenn.mkse212.IKeyValueStorage;
import edu.upenn.mkse212.KeyValueStoreFactory;

public class ParseInputGraph {
	
  public static void main(String[] args) throws IOException {
    IKeyValueStorage socialGraph = KeyValueStoreFactory.getKeyValueStore(KeyValueStoreFactory.STORETYPE.BERKELEY, 
		    "socialGraph", "/home/mkse212/bdb/", "user", "authKey", false);

    File here = new File(".");

    int count = 0;

    //while there are still lines to be read, the buffered reader
    //takes in a line and splits on a tab. it then put the node (arr[0])
    //and the node it points to (arr[1]) and puts them into the
    //key value storage and increments the count
    for ( String fname : here.list()) {
      if (fname.startsWith("visualize")) {
    	  System.out.println("Parsing " + fname + "...");
    	  BufferedReader br = new BufferedReader(new FileReader(fname));
    	  while (br.ready()) {
    		  String line = br.readLine();
    		  String[] arr = line.split("\t");
    		  String node = arr[0];
    		  String points = arr[1];
			socialGraph.put(node, points);
			count++;
    	  }

      }
    }

    System.out.println("\nLoaded " + count + " edges");

    socialGraph.close();
  }
}
