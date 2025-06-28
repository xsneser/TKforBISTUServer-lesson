import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int serverPort = 29898; // 变量名改为小写开头的驼峰命名法
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null; // 变量名改为更具描述性的clientSocket
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private PrintWriter printWriter = null;
    private BufferedReader reader = null; // 变量名改为小写开头

    public Server() {
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server Started on port " + serverPort);

            while (true) { // 使用循环可以接受多个客户端连接
                clientSocket = serverSocket.accept();
                System.out.println("Client Connected: " + clientSocket.getInetAddress());

                try {
                    // 为每个客户端创建输入输出流
                    dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                    dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    printWriter = new PrintWriter(dataOutputStream, true); // 启用自动刷新
                    reader = new BufferedReader(new InputStreamReader(dataInputStream));

                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("来自客户端的消息：" + message);
                        if ("Bye".equalsIgnoreCase(message)) { // 使用equalsIgnoreCase更健壮
                            printWriter.println("再见");
                            break;
                        }
                        printWriter.println("服务器已接收");
                    }
                } catch (IOException e) {
                    System.err.println("处理客户端连接时出错: " + e.getMessage());
                } finally {
                    // 关闭当前客户端的资源
                    closeResources();
                    System.out.println("Client disconnected");
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        } finally {
            // 关闭服务器套接字
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("Server Stopped");
                }
            } catch (IOException e) {
                System.err.println("关闭服务器套接字失败: " + e.getMessage());
            }
        }
    }

    private void closeResources() {
        // 按相反顺序关闭资源
        try {
            if (reader != null) reader.close();
            if (printWriter != null) printWriter.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (dataInputStream != null) dataInputStream.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("关闭资源失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server(); // 创建服务器实例
    }
}