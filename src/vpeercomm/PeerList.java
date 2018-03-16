/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vpeercomm;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author FireLin
 */
public class PeerList extends ArrayList<Peer> {
    
public PeerList(){
    super();
}

public boolean add(Peer p){
    boolean flag = false;
    if(!isExists("IP", p)){
     flag = super.add(p);
    }
    
    return flag;
}

public void UpdateList(PeerList list){
     for (Iterator<Peer> i = list.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
      if(!isExists("IP", p)){
        p.setIsNew(true);
        this.add(p);
      }
     }
}

public void setAllPeersOld(){
     for (Iterator<Peer> i = this.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
      p.setIsNew(false);
     }
}

public String[] convertToStreamSessionList(){
    
    String [] x = new String[this.size()];
    
    int count = 0;
    
    for (Iterator<Peer> i = this.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
      String temp = p.getIP()+"/"+String.valueOf(p.getPortNo())+"/3";
      x[count] = temp;
      count++;
    }
    
    return x;
}

public boolean isExists(String filter, Peer x){
    for (Iterator<Peer> i = this.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
      
     if(filter.equals("Name")){
        if(p.getUName().equals(x.getUName())){
            return true;
        }
     }
      
     if(filter.equals("IP")){
         if(p.getIP().equals(x.getIP())){
            return true;
        }
     }
    }
    
    return false;
}

public boolean isExists(String filter, String x){
    for (Iterator<Peer> i = this.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
      
     if(filter.equals("Name")){
        if(p.getUName().equals(x)){
            return true;
        }
     }
      
     if(filter.equals("IP")){
         if(p.getIP().equals(x)){
            return true;
        }
     }
    }
    
    return false;
}


public Peer findPeerBy(String filter, String value ){
  
  for (Iterator<Peer> i = this.iterator(); i.hasNext(); ){
      Peer p  = (Peer) i.next();
     
      if(filter.equals("Name")){
        if(p.getUName().equals(value)){
            return p;
        }
     }
      
     if(filter.equals("IP")){
         if(p.getIP().equals(value)){
            return p;
        }
     }
  }
  return null;
}

    
}
