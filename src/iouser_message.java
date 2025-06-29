import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class iouser_message {

    public String writemessage(String sendmessage){
        String reserveid = getfirstLeftPipe(sendmessage);
        String messageSendid = getContentAfterRightPipe(sendmessage);
        System.out.println("lift：" + reserveid+ "right" + messageSendid);
        return sendmessage;
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

    private Void in_user(String message) {
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

}
