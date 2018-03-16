/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vpeercomm;

/**
 *
 * @author FireLin
 */
public class Peer implements java.io.Serializable {
    private String ip;
    private int portNo;
    private String uName;
    boolean isNew;
    
public Peer(String ip, int portNo, String uName){
    this.ip = ip;
    this.portNo = portNo;
    this.uName = uName;
    this.isNew = true;
}

//Getters

public String getIP(){
    return this.ip;
}

public int getPortNo(){
    return this.portNo;
}

public String getUName(){
    return this.uName;
}

public boolean getIsNew(){
    return this.isNew;
}

public void setIsNew(boolean value){
    this.isNew = value;
}
    
}
