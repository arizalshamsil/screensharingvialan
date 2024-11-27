package javaapplication5_client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaApplication5_Client {

    private static JLabel imageLabel;
    private static JFrame frame;

    public static void main(String[] args) {
        String serverAddress = JOptionPane.showInputDialog(null, "Enter server IP to receive screen capture");

        if (serverAddress.equals("")) {
            serverAddress = "192.168.31.174"; // Default server IP if none is provided
        }

        int port = 12345; // Same port as the server
        Socket socket = null;
        
        try {
            socket = new Socket(serverAddress, port);
        } catch (IOException ex) {
            Logger.getLogger(JavaApplication5_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
 frame = new JFrame("Received Screenshots");
                imageLabel = new JLabel();
                frame.getContentPane().add(imageLabel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
        while (true) {  // Infinite loop to attempt reconnection every 1 second
            try {
                // Create the JFrame once to display the screenshots
               
if(socket== null||socket.isClosed()){
      try {
            socket = new Socket(serverAddress, port);       
            System.out.println("Connected to the server."); 
        } catch (IOException ex) {
            Logger.getLogger(JavaApplication5_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}
                // Attempt to connect to the server
                
         

                // Continuously receive images from the server
                while (true) {
                  //  System.gc();
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                    // Receive the screenshot as a byte array
                    byte[] imageBytes = (byte[]) inputStream.readObject();

                    // Save the image as a PNG file (optional)
                   // saveScreenshot(imageBytes);

                    // Update the displayed image
                    updateImageDisplay(imageBytes);
                }

            } catch (IOException | ClassNotFoundException e) {
                // Connection error: print the error and attempt reconnection after 1 second
                System.out.println("Connection lost or failed: " + e.getMessage());
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // Close the socket if it was opened
                    }
                    System.out.println("Retrying connection in 1 second...");
                    Thread.sleep(200);  // Wait for 1 second before retrying
                } catch (InterruptedException ex) {
                    System.out.println("Reconnection attempt interrupted.");
                    break;  // Break out of the loop if interrupted
                } catch (IOException ex) {
                    System.out.println("Error closing the socket: " + ex.getMessage());
                }
            }
        }
    }

    // Method to save the screenshot as a PNG file
    private static void saveScreenshot(byte[] imageBytes) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("received_screenshot.png")) {
            fileOutputStream.write(imageBytes);
            System.out.println("Screenshot saved as 'received_screenshot.png'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the displayed screenshot
    private static void updateImageDisplay(byte[] imageBytes) {
        try {
            // Convert the byte array into an ImageIcon
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            ImageIcon imageIcon = new ImageIcon(byteArrayInputStream.readAllBytes());

            // Update the JLabel with the new image
            imageLabel.setIcon(imageIcon);
            frame.pack();  // Adjust the size of the JFrame to fit the new image
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
