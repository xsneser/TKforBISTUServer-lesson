import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class adFriend {
    public String addFriend(String str) {
        String friendid = getfirstLeftPipe(str);
        String userdid = getContentAfterRightPipe(str);
        System.out.println("left：" + friendid + " right：" + userdid);

        // 构建文件对象
        File directory = new File("src/user_friend/");
        File friendFile = new File(directory, friendid + ".txt");
        File userFile = new File(directory, userdid + ".txt");

        try {
            // 检查并创建目录
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("成功创建目录: " + directory.getAbsolutePath());
                } else {
                    throw new IOException("无法创建目录: " + directory.getAbsolutePath());
                }
            }

            // 双向检查好友关系
            boolean friendHasUser = isFriend(friendFile, userdid);
            boolean userHasFriend = isFriend(userFile, friendid);

            // 处理不一致的情况（修复数据）
            if (friendHasUser != userHasFriend) {
                System.out.println("检测到好友关系不一致，正在修复...");

                // 如果friend有user，但user没有friend，则添加user->friend
                if (friendHasUser && !userHasFriend) {
                    try (FileWriter writer = new FileWriter(userFile, true)) {
                        writer.write(friendid);
                        writer.write(System.lineSeparator());
                        System.out.println("已修复好友关系，使双向一致");
                        return "fixed_and_true";
                    }
                }
                // 如果user有friend，但friend没有user，则添加friend->user
                else if (!friendHasUser && userHasFriend) {
                    try (FileWriter writer = new FileWriter(friendFile, true)) {
                        writer.write(userdid);
                        writer.write(System.lineSeparator());
                        System.out.println("已修复好友关系，使双向一致");
                        return "fixed_and_true";
                    }
                }
            }

            // 如果已经是双向好友关系，则不添加
            if (friendHasUser && userHasFriend) {
                System.out.println("已经是双向好友关系，无需重复添加");
                return "already_friends";
            }

            // 添加新的双向好友关系
            try (FileWriter friendWriter = new FileWriter(friendFile, true);
                 FileWriter userWriter = new FileWriter(userFile, true)) {

                friendWriter.write(userdid);
                friendWriter.write(System.lineSeparator());

                userWriter.write(friendid);
                userWriter.write(System.lineSeparator());

                System.out.println("成功添加双向好友关系");
                return "true";
            }

        } catch (IOException e) {
            System.err.println("操作文件时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return "false";
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
    // 检查目标ID是否已经在好友文件中
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
