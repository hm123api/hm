import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public class hmdos {
    // 模拟文件系统（保留原有逻辑，同步本地文件）
    static Map<String, String> fileSystem = new HashMap<>();
    // 模拟进程列表
    static List<String> processList = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static boolean isRunning = true;
    // 当前程序根目录
    static final String ROOT_DIR = System.getProperty("user.dir");
    // 当前工作目录（初始为根目录）
    static String currentWorkingDir = ROOT_DIR;
    // 升级压缩包名称
    static final String UPDATE_ZIP = "update.zip";
    // 系统版本与制作者信息
    static final String SYS_VERSION = "v2.1";
    static final String AUTHOR = "HM开发者";

    public static void main(String[] args) {
        // 开机启动
        bootSystem();
        // 同步本地文件到模拟文件系统
        syncLocalFilesToFileSystem();
        // 系统主循环（模拟操作系统内核）
        while (isRunning) {
            // 显示当前工作目录的DOS风格提示符
            System.out.print("\n" + getDosStylePath(currentWorkingDir) + "> ");
            if (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                executeCommand(command);
            } else {
                isRunning = false;
            }
        }
        scanner.close();
    }
    // 转换为DOS风格路径（例如：C:\project\test）
    static String getDosStylePath(String path) {
        // 替换路径分隔符为Windows风格
        String dosPath = path.replace(File.separator, "\\");
        // 如果是Windows系统，保留盘符；否则模拟C盘
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return dosPath;
        } else {
            return "C:" + dosPath.substring(dosPath.lastIndexOf("\\"));
        }
    }
    // 开机启动
    static void bootSystem() {
        System.out.println("======================================");
        System.out.println("       hmdos 操作系统  " + SYS_VERSION);
        System.out.println("======================================");
        System.out.println("正在加载内核...");
        try { Thread.sleep(800); } catch (Exception ignored) {}
        System.out.println("正在初始化硬件...");
        try { Thread.sleep(500); } catch (Exception ignored) {}
        System.out.println("正在挂载文件系统...");
        try { Thread.sleep(500); } catch (Exception ignored) {}
        System.out.println("\n系统启动完成！输入 help 查看命令\n");
    }
    // 执行命令
    static void executeCommand(String cmd) {
        if (cmd.isEmpty()) return;
        String[] cmdArr = cmd.split(" ");
        switch (cmdArr[0]) {
            case "help":
                showHelp();
                break;
            case "about":
                showAbout();
                break;
            case "dir":
                listFiles();
                break;
            case "create":
                createFile(cmd);
                break;
            case "del":
                deleteFile(cmd);
                break;
            case "mkdir":
                createDir(cmd);
                break;
            case "rmdir":
                deleteDir(cmd);
                break;
            case "cd":
                changeDirectory(cmd);
                break;
            case "run":
                runProcess(cmd);
                break;
            case "ps":
                showProcess();
                break;
            case "kill":
                killProcess(cmd);
                break;
            case "cls":
                clearScreen();
                break;
            case "shutdown":
                shutdown();
                break;
            case "update":
                doUpdate();
                break;
            default:
                System.out.println("错误：不支持的命令");
        }
    }

    // 系统关于界面（版本+制作者，复用全局scanner，修复输入流bug）
    static void showAbout(){
        System.out.println("========== 关于 hmdos ==========");
        System.out.println("系统版本：" + SYS_VERSION);
        System.out.println("制作者：" + AUTHOR);
        System.out.println("提示：输入exit退出关于");
        System.out.println("================================");
        while(true){
            System.out.print("[about]> ");
            String input = scanner.nextLine().trim();
            if("exit".equalsIgnoreCase(input)){
                break;
            }
            if("中华人民共和国".equals(input)){
                System.out.println("☆☆一一一一一一一一》====꧁中国人民☬永垂不朽꧂====《一一一一一一一一☆☆");
                System.out.println("☆☆一一一一一一一一》====꧁人民☆万岁꧂====《一一一一一一一一☆☆");
            }else{
                System.out.println("在关于界面，输入exit离开");
            }
        }
    }

    // 帮助命令
    static void showHelp() {
        System.out.println("===== hmdos 命令列表 =====");
        System.out.println("help        - 查看帮助");
        System.out.println("about       - 系统关于（版本、制作者信息）");
        System.out.println("dir         - 查看当前目录下文件/目录列表");
        System.out.println("create 文件名 - 在当前目录创建文件");
        System.out.println("del 文件名   - 删除当前目录下的文件");
        System.out.println("mkdir 目录名 - 在当前目录创建子目录");
        System.out.println("rmdir 目录名 - 删除当前目录下的子目录");
        System.out.println("cd 目录名    - 进入指定目录（cd .. 返回上级目录）");
        System.out.println("run 进程名   - 运行进程");
        System.out.println("ps          - 查看进程");
        System.out.println("kill 编号    - 结束进程");
        System.out.println("cls         - 清屏");
        System.out.println("shutdown    - 关机");
        System.out.println("update      - 系统升级(删除java/class + 解压update.zip)");
    }
    // 同步当前目录文件到模拟文件系统
    static void syncLocalFilesToFileSystem() {
        File dir = new File(currentWorkingDir);
        File[] files = dir.listFiles();
        if (files == null) return;
        fileSystem.clear(); // 清空旧数据，同步当前目录新数据
        for (File f : files) {
            if (f.isFile()) {
                fileSystem.put(f.getName(), "本地文件：" + f.getAbsolutePath());
            }
        }
    }
    // 切换目录命令实现
    static void changeDirectory(String cmd) {
        String[] parts = cmd.split(" ");
        // 处理 cd 无参数情况（返回根目录）
        if (parts.length < 2) {
            currentWorkingDir = ROOT_DIR;
            System.out.println("已返回根目录：" + getDosStylePath(currentWorkingDir));
            syncLocalFilesToFileSystem();
            return;
        }
        String targetDir = parts[1];
        File newDir;
        // 处理返回上级目录（cd ..）
        if ("..".equals(targetDir)) {
            File parentDir = new File(currentWorkingDir).getParentFile();
            if (parentDir == null) {
                System.out.println("已经是根目录，无法返回上级");
                return;
            }
            newDir = parentDir;
        } else {
            // 处理进入子目录
            newDir = new File(currentWorkingDir, targetDir);
        }
        // 验证目录是否存在且是目录类型
        if (!newDir.exists()) {
            System.out.println("切换失败：目录不存在");
            return;
        }
        if (!newDir.isDirectory()) {
            System.out.println("切换失败：目标不是目录");
            return;
        }
        // 更新当前工作目录并同步文件系统
        currentWorkingDir = newDir.getAbsolutePath();
        System.out.println("已进入目录：" + getDosStylePath(currentWorkingDir));
        syncLocalFilesToFileSystem();
    }
    // ===================== 升级核心方法 =====================
    static void doUpdate() {
        System.out.println("===== 开始执行系统升级 =====");
        File zipFile = new File(ROOT_DIR, UPDATE_ZIP); // 升级包始终在根目录
        // 1. 检查升级包是否存在
        if (!zipFile.exists()) {
            System.out.println("错误：未找到升级包 " + UPDATE_ZIP + "，升级终止！");
            return;
        }
        // 2. 删除根目录下所有 .java 和 .class 文件
        System.out.println("\n[1/2] 清理旧版本文件...");
        cleanOldClassAndJava();
        // 3. 解压 update.zip 到根目录
        System.out.println("\n[2/2] 解压升级包...");
        try {
            unzip(zipFile, ROOT_DIR);
            System.out.println("\n===== 系统升级完成！请重启系统生效 =====");
        } catch (IOException e) {
            System.out.println("升级失败：解压文件异常 -> " + e.getMessage());
        }
    }
    // 删除根目录下 .java .class 文件
    static void cleanOldClassAndJava() {
        File dir = new File(ROOT_DIR);
        File[] files = dir.listFiles();
        if (files == null) {
            System.out.println("目录为空，无需清理");
            return;
        }
        int count = 0;
        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".java") || name.endsWith(".class")) {
                    if (f.delete()) {
                        System.out.println("已删除：" + f.getName());
                        count++;
                    } else {
                        System.out.println("删除失败：" + f.getName());
                    }
                }
            }
        }
        System.out.println("本次共清理 " + count + " 个旧文件");
    }
    // 解压ZIP文件到目标目录
    static void unzip(File zipFile, String destDir) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                File outFile = new File(destDir, entryName);
                // 创建父目录
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                // 跳过目录项
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                    continue;
                }
                // 写入文件
                try (InputStream in = zip.getInputStream(entry);
                     FileOutputStream out = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                }
                System.out.println("已解压：" + entryName);
            }
        }
    }
    // 查看当前目录下的文件和目录
    static void listFiles() {
        File dir = new File(currentWorkingDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("当前目录为空");
            return;
        }
        System.out.println("当前目录内容：");
        for (File f : files) {
            String type = f.isDirectory() ? "[目录]" : "[文件]";
            System.out.println(type + " " + f.getName());
        }
    }
    // 在当前目录创建文件
    static void createFile(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：create 文件名");
            return;
        }
        String fileName = parts[1];
        File newFile = new File(currentWorkingDir, fileName);
        try {
            if (newFile.createNewFile()) {
                fileSystem.put(fileName, "本地文件：" + newFile.getAbsolutePath());
                System.out.println("文件创建成功：" + fileName);
            } else {
                System.out.println("创建失败：文件已存在");
            }
        } catch (IOException e) {
            System.out.println("创建失败：" + e.getMessage());
        }
    }
    // 删除当前目录下的文件
    static void deleteFile(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：del 文件名");
            return;
        }
        String fileName = parts[1];
        File delFile = new File(currentWorkingDir, fileName);
        if (!delFile.exists()) {
            System.out.println("删除失败：文件不存在");
            return;
        }
        if (!delFile.isFile()) {
            System.out.println("删除失败：目标不是文件（请使用 rmdir 删除目录）");
            return;
        }
        if (delFile.delete()) {
            fileSystem.remove(fileName);
            System.out.println("文件已删除：" + fileName);
        } else {
            System.out.println("删除失败：文件被占用或权限不足");
        }
    }
    // 在当前目录创建子目录
    static void createDir(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：mkdir 目录名");
            return;
        }
        String dirName = parts[1];
        File newDir = new File(currentWorkingDir, dirName);
        if (newDir.exists()) {
            System.out.println("创建失败：目录已存在");
            return;
        }
        if (newDir.mkdirs()) {
            System.out.println("目录创建成功：" + dirName);
        } else {
            System.out.println("创建失败：权限不足或路径无效");
        }
    }
    // 删除当前目录下的子目录
    static void deleteDir(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：rmdir 目录名");
            return;
        }
        String dirName = parts[1];
        File delDir = new File(currentWorkingDir, dirName);
        if (!delDir.exists()) {
            System.out.println("删除失败：目录不存在");
            return;
        }
        if (!delDir.isDirectory()) {
            System.out.println("删除失败：目标不是目录（请使用 del 删除文件）");
            return;
        }
        // 递归删除目录（包括子文件/子目录）
        if (deleteDirectory(delDir)) {
            System.out.println("目录已删除：" + dirName);
        } else {
            System.out.println("删除失败：目录非空或权限不足");
        }
    }
    // 递归删除目录及内容
    static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        return dir.delete();
    }
    // 运行进程（原有逻辑）
    static void runProcess(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：run 进程名");
            return;
        }
        String p = parts[1];
        processList.add(p);
        System.out.println("进程启动：" + p + " | 编号：" + (processList.size() - 1));
    }
    // 查看进程（原有逻辑）
    static void showProcess() {
        if (processList.isEmpty()) {
            System.out.println("无运行中的进程");
            return;
        }
        System.out.println("运行中的进程：");
        for (int i = 0; i < processList.size(); i++) {
            System.out.println(i + " - " + processList.get(i));
        }
    }
    // 结束进程（原有逻辑）
    static void killProcess(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length < 2) {
            System.out.println("用法：kill 进程编号");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);
            if (id >= 0 && id < processList.size()) {
                String killed = processList.remove(id);
                System.out.println("进程已结束：" + killed);
            } else {
                System.out.println("进程编号无效");
            }
        } catch (Exception e) {
            System.out.println("编号必须是数字");
        }
    }
    // 清屏（原有逻辑）
    static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception ignored) {}
    }
    // 关机（原有逻辑）
    static void shutdown() {
        System.out.println("\n正在保存数据...");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        System.out.println("正在关闭内核...");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        System.out.println("hmdos 已安全关机");
        isRunning = false;
    }
}
