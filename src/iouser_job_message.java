import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class iouser_job_message {

    public String writemessage(String sendmessage) throws IOException {
        String reserveid = getfirstLeftPipe(sendmessage);
        String messageSendid = getContentAfterRightPipe(sendmessage);

        System.out.println("left：" + reserveid + " right：" + messageSendid);

        // 构建文件对象
        File directory = new File("src/user_job/");
        File file = new File(directory, reserveid + ".txt");

        try {
            // 检查并创建目录
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("成功创建目录: " + directory.getAbsolutePath());
                } else {
                    throw new IOException("无法创建目录: " + directory.getAbsolutePath());
                }
            }

            // 写入文件
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(messageSendid);
                writer.write(System.lineSeparator()); // 添加换行符
                System.out.println("成功写入: " + messageSendid);
                return "true";
            }
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }

        return "false";
    }
    public String readmessage(String id) throws IOException {
        File file = new File("src/user_job/" + id + ".txt");

        // 检查文件是否存在或是否为空
        if (!file.exists() || file.length() == 0) {
            return "false";
        }

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // 读取文件全部内容
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

            // 清空文件内容
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(""); // 写入空内容覆盖原文件
            }

        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            return "false";
        }

        return content.toString();
    }
    private String getfirstLeftPipe(String str) {
        String[] parts = str.split("\\|");
        // 获取|前的部分
        String numberPart = parts[0];

        return numberPart;
    }
    private  String getContentAfterRightPipe(String originalString)  {
        int pipeIndex = originalString.indexOf('|');

        if (pipeIndex != -1) {
            String result = originalString.substring(pipeIndex + 1);
            return result;
        } else {
            System.out.println("字符串中没有找到 '|'");
        }
        return null;
    }

    private boolean isFriend(File friendFile, String targetId) throws IOException {
        if (!friendFile.exists()) {
            return false; // 文件不存在，说明还没有好友
        }

        try (Scanner scanner = new Scanner(friendFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals(targetId)) {
                    return true; // 找到匹配的好友ID
                }
            }
        }

        return false; // 未找到匹配的好友ID
    }

}
