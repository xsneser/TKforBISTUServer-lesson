import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class iouser_message {

    public String writemessage(String sendmessage) throws IOException {
        String reserveid = getfirstLeftPipe(sendmessage);
        String messageSendid = getContentAfterRightPipe(sendmessage);
        String Sendid = getContentAfterRightPipe(messageSendid);
        System.out.println("left：" + reserveid + " right：" + messageSendid+"sendid:"+Sendid);
        File directory1 = new File("src/user_friend/");
        File friendFile = new File(directory1, reserveid + ".txt");
        if (!isFriend(friendFile,Sendid)){
            System.out.println(reserveid+"不是好友");
            return "false";
        }

        // 获取当前系统时间
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = currentTime.format(formatter);

        // 构建文件对象
        File directory = new File("src/user/");
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
                writer.write("[" + timestamp + "]|"+messageSendid);
                writer.write(System.lineSeparator()); // 添加换行符
                System.out.println("成功写入: " + "[" + timestamp + "] "+messageSendid);
                return "true";
            }
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }

        return "false";
    }
    public String readmessage(String id) throws IOException {
        File file = new File("src/user/" + id + ".txt");

        // 检查文件是否存在或是否为空
        if (!file.exists() || file.length() == 0) {
            return "false";
        }

        String firstLine = null;
        List<String> remainingLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // 读取第一行
            firstLine = reader.readLine();
            if (firstLine == null) {
                return "false";
            }

            // 读取剩余行
            String line;
            while ((line = reader.readLine()) != null) {
                remainingLines.add(line);
            }

        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            return "false";
        }

        // 将剩余行写回文件
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (String line : remainingLines) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
            return "false";
        }

        return firstLine;
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
