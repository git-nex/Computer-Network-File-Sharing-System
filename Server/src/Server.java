package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Server class. Able to accomodate multiple clients on the file sharing system
 * using multi-threading.   
 * @authors Aayush Verma (BT19CSE012 IIITN) , Ansh Garewal (BT19CSE001 IIITN).
 */
public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public Server () throws IOException {
              
        System.out.print("Enter port number: ");
        Scanner keys = new Scanner(System.in);
        int port = keys.nextInt();
        
        connectServer(port); 
        acceptClients();
    }
    public static void main(String[] args) throws IOException  {
        Server server = new Server();
    }
    
    /**
     
     * @param port Port number to connect Server on.
     * @throws IOException 
     */
    private static void connectServer(int port) throws IOException{
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running..");
        } catch (IOException e) {
            System.err.println("Port already in use.\nTry using another port number.");
            Server server = new Server();
        }        
    }
    
    
    private static void acceptClients(){
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection: " + clientSocket);

                Thread server = new Thread(new ThreadedClient(clientSocket));
                server.start();

            } catch (IOException e) {
                System.err.println("Error: "+ clientSocket + " could not connect.\n" + e);
            }
        }
    }
}