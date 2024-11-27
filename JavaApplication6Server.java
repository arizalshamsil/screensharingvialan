package javaapplication6server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class JavaApplication6Server {

    public static void main(String[] args) {
        int port = 12345;  // Server listens on port 12345

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for client connections...");

            while (true) {
                // Accept an incoming client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                try {
                    // Spawn a new thread to handle the client connection
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (AWTException ex) {
                    Logger.getLogger(JavaApplication6Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This class handles client communication
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Robot robot;
        private final Dimension screenSize;
        private final Rectangle screenRect;

        public ClientHandler(Socket clientSocket) throws IOException, AWTException {
            this.clientSocket = clientSocket;
            this.robot = new Robot();
            this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.screenRect = new Rectangle(0, 0, screenSize.width, screenSize.height);
        }

        @Override
        public void run() {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

                while (!clientSocket.isClosed()) {
                    if (clientSocket.isConnected()) {
                        sendScreenshot(byteArrayOutputStream, outputStream);
                    } else {
                        System.out.println("Client disconnected, closing connection.");
                        break;
                    }
                    Thread.sleep(500);
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("Error while handling client: " + e.getMessage());
            } finally {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendScreenshot(ByteArrayOutputStream byteArrayOutputStream, ObjectOutputStream outputStream) throws IOException {
            // Capture the screenshot using Robot
            BufferedImage screenImage = robot.createScreenCapture(screenRect);

            // Reset the ByteArrayOutputStream to reuse it
            byteArrayOutputStream.reset();

            // Convert the BufferedImage to byte array
            ImageIO.write(screenImage, "PNG", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Send the image bytes to the client
            outputStream.writeObject(imageBytes);
            outputStream.flush();
            System.out.println("Screenshot sent to client.");
        }
    }
}
