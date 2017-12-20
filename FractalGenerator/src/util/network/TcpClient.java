/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.network;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Gregoire
 */
public class TcpClient {
    private  int port ;
    private  String host; 
    private Socket socket;

    public TcpClient( String host,int port) throws IOException {
        this.port = port;
        this.host = host;
        socket = new Socket(host, port);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public static void main(String[] args) throws IOException {
        
        
    }
   
    
    
}
