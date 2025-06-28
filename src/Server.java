import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


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
                        }else if(message.startsWith("I")){
                            String content = message.substring(1);
                            int id =in_user(content);
                            printWriter.println(""+id);

                        }else if(message.startsWith("D")){
                            String content = message.substring(1);
                            String name= OUT_user(content);
                            if(name!=null){;
                            printWriter.println(""+name);
                            System.out.println("登录成功");
                            }

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
    private synchronized int in_user(String message) throws IOException {
        int id = readFirstNumber();
        PrintWriter printWriter = null;
        try {
            // 创建FileWriter并追加到文件
            FileWriter fileWriter = new FileWriter("src/user_information.txt", true);
            // 包装FileWriter为PrintWriter以便使用println方法
            printWriter = new PrintWriter(fileWriter);

            // 直接写入单个消息
            printWriter.println(id+1+"|"+message);
            return id+1;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 确保资源被关闭
            if (printWriter != null) {
                printWriter.close();
            }
        }
        System.out.println("写入用户表："+message);
        return -1;
    }

    private int readFirstNumber() throws IOException {
        Path filePath = Paths.get("src/user_information.txt");
        List<String> lines = Files.readAllLines(filePath);

        if (lines.isEmpty()) {
            throw new IOException("文件为空");
        }

        int firstNumber = Integer.parseInt(lines.get(0).trim());
        int updatedNumber = firstNumber + 1;
        lines.set(0, String.valueOf(updatedNumber));

        Files.write(filePath, lines);
        return firstNumber;
    }

    private synchronized String OUT_user(String str) throws IOException {
        String[] parts = str.split("\\|");
        // 获取|前的部分
        String numberPart = parts[0];
        // 转换为int
        int result = Integer.parseInt(numberPart)+1;
        String namepassword = readline(result);
        System.out.println("namepassword:" + namepassword);
        String name = getfirstLeftPipe(namepassword);
        String password = getContentAfterLastPipe(namepassword);
        if(namepassword !=null){
            System.out.println("password:" + password +"p2 "+getContentAfterLastPipe(str)+"name"+name);
            if(password.equals(getContentAfterLastPipe(str))){
                return  name;
            }
        }
        System.out.println("登录失败");
        return null;
    }
    private  String readline(int targetLine) {
        String filePath = "src/user_information.txt";
        //int targetLine = 3; // 假设读取第3行

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8
                )
        )) {
            String line;
            int currentLine = 1;

            // 逐行读取，直到目标行或文件结束
            while ((line = reader.readLine()) != null) {
                if (currentLine == targetLine) {
                    return  getContentAfterLeftPipe(line);
                }
                currentLine++;
            }

            if (currentLine < targetLine) {
                System.out.println("文件行数不足，只有" + (currentLine - 1) + "行");
            }
        } catch (IOException e) {
            System.out.println("读取文件时出错：" + e.getMessage());
        }
        return null;
    }

    public static String getContentAfterLastPipe(String input) {
        if (input == null) {
            return null;
        }
        int lastIndex = input.lastIndexOf('|');
        if (lastIndex == -1) {
            return input; // 若没有|，则返回原字符串
        }

        return input.substring(lastIndex + 1);
    }

    public static String getContentAfterLeftPipe(String originalString)  {
        int pipeIndex = originalString.indexOf('|');

        if (pipeIndex != -1) {
            String result = originalString.substring(pipeIndex + 1);
            return result;
        } else {
            System.out.println("字符串中没有找到 '|'");
        }
        return null;
    }
    public static String getfirstLeftPipe(String str) {
        String[] parts = str.split("\\|");
        // 获取|前的部分
        String numberPart = parts[0];
        // 转换为int
        return numberPart;
    }
}