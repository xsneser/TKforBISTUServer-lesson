import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// 新增的用户仓库类
class UserRepository {
    public synchronized int in_user(String message) throws IOException {
        int id = readFirstNumber();
        PrintWriter printWriter = null;
        try {
            FileWriter fileWriter = new FileWriter("src/user_information.txt", true);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println(id + 1 + "|" + message);
            return id + 1;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        System.out.println("写入用户表：" + message);
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

    public synchronized String OUT_user(String str) throws IOException {
        String[] parts = str.split("\\|");
        String numberPart = parts[0];
        int result = Integer.parseInt(numberPart) + 1;
        String namepassword = readline(result);
        System.out.println("namepassword:" + namepassword);
        if (namepassword != null) {
            String name = getfirstLeftPipe(namepassword);
            String password = getContentAfterLastPipe(namepassword);
            System.out.println("password:" + password + " p2 " + getContentAfterLastPipe(str) + " name " + name);
            if (password.equals(getContentAfterLastPipe(str))) {
                return name;
            }
        }
        System.out.println("登录失败");
        return null;
    }

    private String readline(int targetLine) {
        String filePath = "src/user_information.txt";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8
                )
        )) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine == targetLine) {
                    return getContentAfterLeftPipe(line);
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

    private String getContentAfterLastPipe(String input) {
        if (input == null) {
            return null;
        }
        int lastIndex = input.lastIndexOf('|');
        if (lastIndex == -1) {
            return input;
        }
        return input.substring(lastIndex + 1);
    }

    private String getContentAfterLeftPipe(String originalString) {
        int pipeIndex = originalString.indexOf('|');
        if (pipeIndex != -1) {
            return originalString.substring(pipeIndex + 1);
        } else {
            System.out.println("字符串中没有找到 '|'");
        }
        return null;
    }

    private String getfirstLeftPipe(String str) {
        String[] parts = str.split("\\|");
        return parts[0];
    }
}