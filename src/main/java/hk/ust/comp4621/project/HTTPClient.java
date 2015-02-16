package hk.ust.comp4621.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class HTTPClient {

  // A generic function to generate HTTP request
  // host: Hostname of server (e.g. sing.cse.ust.hk)
  // port: Port number (e.g. Web 80 FTP 21 SMTP 25)d
  // path: URL path e.g. "/index.php"
  // method: GET or POST
  public static void HTTP_Request(String host, int port, String path,
      String method, String data) throws UnknownHostException, IOException {
    //Resolve the hostname to an IP address
    InetAddress ip = InetAddress.getByName(host);

    //Open socket to a specific host and port
    Socket socket = new Socket(host, port);
        
    //Get input and output streams for the socket
    OutputStream out = socket.getOutputStream();
    InputStream in = socket.getInputStream();

    // HTTP GET
    if (method.equals("GET")) {
      // Constructe a HTTP GET request
      // The end of HTTP GET request should be \r\n\r\n
      String request = "GET " + path + "?" + data + " HTTP/1.0\r\n"
          + "Accept: */*\r\n" + "Host: "+host+"\r\n"
          + "Connection: Close\r\n\r\n";
    
      // Sends off HTTP GET request
      out.write(request.getBytes());
      out.flush();
    } else if (method.equals("POST")) { // HTTP POST
      // Constructs a HTTP POST request
      // The end of HTTP POST header should be \r\n\r\n
      // After HTTP POST header, it's HTTP POST data
      // POST it's different from GET: the data of POST is added at the end of HTTP request
      String request = "POST " + path + " HTTP/1.0\r\n" + "Accept: */*\r\n"
         + "Host: " + host + "\r\n"
         + "Content-Type: application/x-www-form-urlencoded\r\n"
         + "Content-Length: " + data.length() + "\r\n\r\n" + data;

      // Send off HTTP POST request
      out.write(request.getBytes());
      out.flush();
    } else {
      System.out.println("Invalid HTTP method");
      socket.close();
      return;
    }
        
    // Reads the server's response
    StringBuffer response=new StringBuffer();
    byte[] buffer = new byte[4096];
    int bytes_read;

    // Reads HTTP response
    while ((bytes_read = in.read(buffer, 0, 4096)) != -1) {
      // Print server's response 
      for(int i = 0; i < bytes_read; i++)
        response.append((char)buffer[i]);
    }
        
    if (response.substring(response.indexOf(" ") + 1,
        response.indexOf(" ") + 4).equals("200")) {
      //Save the payload of the HTTP response message
      File file = new File("index.html");
      PrintWriter printWriter = new PrintWriter(file);
      printWriter.println(response.substring(response.indexOf("\r\n\r\n") + 4));
      printWriter.close();
    } else
      System.out.println("HTTP request failed");
 
    // Closes socket
    socket.close();
  }
    
  public static void main(String[] args) throws Exception {
     while(true) {
       // Hostname
       String host = "sing.cse.ust.hk";

       // Port
       int port = 80;

       // Path
       String path = "/comp4621/index.php";
       System.out.println("Which HTTP method(GET/POST) do you want to use?");
       Scanner scanner = new Scanner(System.in);
       // Method GET/POST
       String method = scanner.nextLine();
       method = method.toUpperCase();
       System.out.println(method);

       // GET/POST data
       String data = "name=POON";
       
       // Generates HTTP request
       HTTP_Request(host, port, path, method, data);
    }
  }
}
