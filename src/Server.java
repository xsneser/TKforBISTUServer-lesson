import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程服务器实现
 * 使用线程池处理客户端连接，支持高并发
 */
public class Server {
    // 服务器配置参数
    private int serverPort = 29898; // 服务器监听端口
    private ServerSocket serverSocket = null; // 服务器套接字
    private final ExecutorService threadPool = Executors.newCachedThreadPool(); // 线程池（核心组件）
    private UserRepository userRepository = new UserRepository(); // 用户数据存储库

    /**
     * 服务器构造函数
     * 初始化服务器并开始监听客户端连接
     */
    public Server() {
        try {
            // 创建服务器套接字并绑定端口
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server Started on port " + serverPort);

            // 主循环：持续接受客户端连接
            while (true) {
                // 阻塞等待客户端连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected: " + clientSocket.getInetAddress());

                // 为每个客户端创建处理任务并提交到线程池
                // 这是实现多线程处理的关键步骤
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            // 服务器启动失败处理
            System.err.println("服务器启动失败: " + e.getMessage());
        } finally {
            // 确保服务器关闭时释放资源
            shutdownServer();
        }
    }

        /**
         * 客户端处理程序（内部类）
         * 每个客户端连接都会创建一个此类的实例
         * 在独立线程中运行
         */
        private class ClientHandler implements Runnable {
            private final Socket clientSocket; // 客户端套接字

            /**
             * 构造函数
             * @param socket 客户端套接字
             */
            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }

        /**
         * 客户端处理线程的主逻辑
         * 负责与客户端通信
         */
        @Override
        public void run() {
            // 使用try-with-resources确保自动关闭所有流
            try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                 PrintWriter printWriter = new PrintWriter(dataOutputStream, true); // 自动刷新输出
                 BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream))) {

                String message;
                // 持续读取客户端消息
                while ((message = reader.readLine()) != null) {
                    // 打印接收到的消息（带客户端地址）
                    System.out.println("来自客户端 " + clientSocket.getInetAddress() + " 的消息：" + message);

                    // 处理不同类型的客户端请求
                    if ("Bye".equalsIgnoreCase(message)) {
                        // 处理断开连接请求
                        printWriter.println("再见");
                        break; // 退出循环，结束此客户端连接
                    } else if (message.startsWith("I")) {
                        // 处理用户注册请求
                        // 格式: I|用户名|密码
                        String content = message.substring(1); // 移除前缀'I'
                        int id = userRepository.in_user(content); // 调用用户仓库
                        printWriter.println("" + id); // 返回用户ID
                    } else if (message.startsWith("D")) {
                        // 处理用户登录请求
                        // 格式: D|用户名|密码
                        String content = message.substring(1); // 移除前缀'D'
                        String name = userRepository.OUT_user(content); // 调用用户仓库验证
                        if (name != null) {
                            // 登录成功
                            printWriter.println("" + name);
                            System.out.println(clientSocket.getInetAddress() + " 登录成功");
                        } else {
                            // 登录失败
                            printWriter.println("登录失败");
                        }
                    }

                    // 通用响应，确认消息已接收
                    printWriter.println("服务器已接收");
                }
            } catch (IOException e) {
                // 处理通信异常
                System.err.println("处理客户端连接时出错: " + e.getMessage());
            } finally {
                // 确保关闭客户端资源
                closeClientResources();
                System.out.println("客户端断开: " + clientSocket.getInetAddress());
            }
        }

        /**
         * 关闭客户端资源
         */
        private void closeClientResources() {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close(); // 关闭客户端套接字
                }
            } catch (IOException e) {
                System.err.println("关闭客户端套接字失败: " + e.getMessage());
            }
        }
    }

    /**
     * 关闭服务器
     * 释放所有资源
     */
    private void shutdownServer() {
        try {
            // 1. 关闭线程池（不再接受新任务）
            threadPool.shutdown();

            // 2. 关闭服务器套接字（停止接受新连接）
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server Stopped");
            }
        } catch (IOException e) {
            System.err.println("关闭服务器套接字失败: " + e.getMessage());
        }
    }

    /**
     * 服务器入口点
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        new Server(); // 启动服务器实例
    }
}