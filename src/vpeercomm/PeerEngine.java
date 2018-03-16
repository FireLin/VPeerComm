/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vpeercomm;

import java.net.*;
import java.io.*;
import java.util.Iterator;
import javax.media.Format;
import javax.media.MediaLocator;

/**
 *
 * @author Omran
 */
public class PeerEngine extends Thread {
    Peer me;
    final PeerList others;
    ServerSocket RequestReceiver;
    PeerStreamSender myStream;
    int StreamPort;
    int CommPort = 1234;
    
    public PeerEngine(Peer me) throws IOException {
        this.me = me;
        StreamPort = me.getPortNo();
        others = new PeerList();
        others.add(me);
         
        RequestReceiver = new ServerSocket(CommPort);
    }
    
    public void requestToJoinSession(String IP, int port){
        
      try
      {
         String serverName = IP;
         System.out.println("Connecting to " + serverName + " on port " + port);
         Socket client = new Socket(serverName, port);
         System.out.println("Sending Join request to " + client.getRemoteSocketAddress());
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);
         out.writeUTF("join");
         InputStream inFromServer = client.getInputStream();
         
         ObjectInputStream in = new ObjectInputStream(inFromServer);
         Object respond = in.readObject();
         
         if(respond instanceof PeerList )
         {
           others.setAllPeersOld();
           PeerList list = (PeerList) respond;
           others.UpdateList(list);
           System.out.println("Server says participants number = " + others.size());
           
           for (Iterator<Peer> i = others.iterator(); i.hasNext(); ){
                  Peer p  = (Peer) i.next();
             if(p.getIsNew() == true || !p.getIP().equalsIgnoreCase(me.getIP())){     
             final String temp = p.getIP()+"/"+String.valueOf(p.getPortNo())+"/3";
             Thread thread1 = new Thread () {
                      public void run () {
                    receiveStreams(temp);
                               }
                     };
              thread1.start();
              sendUpdateRequest();
            }
           }
         }
         else{
           System.out.println("Server says Exists !");  
         }
         
         client.close();
      }catch(Exception e)
      {
         
      }
       
    }
    
    public void transmitStream(String Locator, String broadcastIP){
        // We need three parameters to do the transmission
	// For example,
	//   java AVTransmit2 file:/C:/media/test.mov  129.130.131.132 42050
        
	Format fmt = null;
	int i = 0;

	// Create a audio transmit object with the specified params.
	PeerStreamSender at = new PeerStreamSender(new MediaLocator(Locator),
                                       broadcastIP, String.valueOf(StreamPort), fmt);
	// Start the transmission
	String result = at.start();

	// result will be non-null if there was an error. The return
	// value is a String describing the possible error. Print it.
	if (result != null) {
	    System.err.println("Error : " + result);
	    System.exit(0);
	}
	
	System.err.println("Start transmission ...");
        
	// Transmit for 60 seconds and then close the processor
	// This is a safeguard when using a capture data source
	// so that the capture device will be properly released
	// before quitting.
	// The right thing to do would be to have a GUI with a
	// "Stop" button that would call stop on AVTransmit2
	//try {
	  //  Thread.currentThread().sleep(6000000);
	//} catch (InterruptedException ie) {
	//}

	// Stop the transmission
	//at.stop();
	
	//System.err.println("...transmission ended.");

	//System.exit(0);
    }
    
    public void receiveStreams(String session){
        
        String[] sessions = {session};
	PeerStreamReceiver avReceive = new PeerStreamReceiver(sessions);
	if (!avReceive.initialize()) {
	    System.err.println("Failed to initialize the sessions.");
	    System.exit(-1);
	}

	// Check to see if AVReceive2 is done.
	try {
	    while (!avReceive.isDone())
		Thread.sleep(1000000);
	} catch (Exception e) {}

	System.err.println("Exiting AVReceive2");
    }

    public void sendUpdateRequest(){
      for (Iterator<Peer> i = others.iterator(); i.hasNext(); ){
       Peer p  = (Peer) i.next();
       
       try{
           Socket client = new Socket(p.getIP(), CommPort);
           System.out.println("Sending update request to " + client.getRemoteSocketAddress());
           OutputStream outToServer = client.getOutputStream();
           DataOutputStream out = new DataOutputStream(outToServer);
           out.writeUTF("update");
           ObjectOutputStream myPeer = new ObjectOutputStream(outToServer);
           myPeer.writeObject(me);
          }
       catch(Exception e){
       }
     }
    }
    
    public void run(){
     while(true){
         try{
            System.out.println("Waiting for client on port " + RequestReceiver.getLocalPort() + "...");
            Socket server = RequestReceiver.accept();
            String ip = server.getInetAddress().getHostAddress();
            System.out.println("Just connected to " + ip );
            DataInputStream in = new DataInputStream(server.getInputStream());
            String request = in.readUTF();
            
            if(request.equalsIgnoreCase("join")){
                System.out.println("Receiving Join Request ... ");
                if(!others.isExists("IP",ip)){
                  ObjectOutputStream listoutput = new ObjectOutputStream(server.getOutputStream());
                  listoutput.writeObject(others);
                  System.out.println(others.size());
                }
            }
           
            else if (request.equalsIgnoreCase("leave")){
                 System.out.print("Not Implemented Yet");
            }
              
            else if (request.equalsIgnoreCase("update")) {
               ObjectInputStream objectInput = new ObjectInputStream(server.getInputStream());
               Peer x = (Peer)objectInput.readObject();
               System.out.println("Receiving Update Request from " + x.getIP() + "...");
                
               if(!x.getIP().equalsIgnoreCase(me.getIP()) && !others.isExists("IP", x) ){
                x.setIsNew(true);
                others.add(x);
                final String info = x.getIP()+"/"+String.valueOf(x.getPortNo())+"/3";
                Thread thread1 = new Thread () {
                      public void run () {
                     receiveStreams(info);
                      }
                };
                thread1.start();
               }
            }
            
            
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
            server.close();
         }
         catch(Exception e){
            
         }
     }   
    }
    
}
