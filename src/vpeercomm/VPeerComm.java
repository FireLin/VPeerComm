/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vpeercomm;
import java.io.*;
/**
 *
 * @author FireLin
 */
public class VPeerComm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Test(args[0], args[1], args[2], args[3], args[4]);
    }
    
    
    public static void Test(String Name, String IP, String Port, final String broadcast, final String Locator){
        try{
              int CommPort = 1234;
              Peer me = new Peer(IP,Integer.parseInt(Port), Name);
             
              final Thread t = new PeerEngine(me);
              
              Thread t2 = new Thread () {
                        public void run () {
                              ((PeerEngine)t).transmitStream(Locator, broadcast);
                             }
                          };

              t2.start();
              t.start();
              
              String s = "";
              while(!s.equalsIgnoreCase("e")){
               BufferedReader keyin = new BufferedReader(new InputStreamReader(System.in));
               System.out.print("Enter r : follower by IP to join session");
               while ((s = keyin.readLine()) != null && s.length() != 0){
                 String[] tokens = s.split(":");
                 if(s.length() >= 2){
                 if(tokens[0].equalsIgnoreCase("r")){
                     System.out.print("Request initiated");
                    ((PeerEngine)t).requestToJoinSession(tokens[1], CommPort);
                   } 
                  }
                }
               }
              }
        catch(Exception e){
           e.printStackTrace();
        }
    }
    
}
