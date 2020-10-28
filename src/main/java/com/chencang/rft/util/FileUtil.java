package com.chencang.rft.util;

import com.chencang.rft.config.RftConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileUtil {

    @Autowired
    public static RftConfig rftConfig;

    /**
     * 读取txt文件
     *
     * @param path
     * @return
     */
    public static String readTxt(String path) {
        File file = new File(path);
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result = result + "\n" + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取文件夹名称
     *
     * @return
     */
    public static List<String> folder(String basePath) {
        String[] list = new File(basePath).list();
        if(list == null){
            System.out.println("获取文件列表为空");
            return null;
        }
        List<String> folderName = new ArrayList<String>();
        for (String str : list) {
            if (isAcronym(str))
                folderName.add(str);
        }
        return folderName;
    }

    /**
     * 判断字符串大小写
     *
     * @param
     * @return
     */
    public static boolean isAcronym(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 替换文件中的字符串，并覆盖原文件
     *
     * @param filePath
     * @param oldstr
     * @param newStr
     * @throws IOException
     */
    public static void autoReplaceStr(String filePath, String oldstr, String newStr) throws IOException {
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContext = new byte[fileLength.intValue()];
        FileInputStream in = null;
        PrintWriter out = null;
        in = new FileInputStream(filePath);
        in.read(fileContext);
        // 避免出现中文乱码
        String str = new String(fileContext, "gb18030");//字节转换成字符
        str = str.replace(oldstr, newStr);
        out = new PrintWriter(filePath, "gb18030");//写入文件时的charset
        out.write(str);
        out.flush();
        out.close();
        in.close();
    }

    /**
     * 设置环境变量
     */
    public static void setProperties(String value) throws IOException {
        excuteCMDBatFile("setx /m rftParameter \"" +value+"\"");
        String info = System.getenv("rftParameter");
        log.info(info);
//        System.out.println("打印 环境变量...");
//        Map<String,String> map = System.getenv();
//        for (Iterator<String> it = map.keySet().iterator();it.hasNext();){
//            String key = it.next();
//            String value2 = map.get(key);
//            System.out.println(key+"\t"+value2);
//        }
    }

    /**
     * cmd写入bat文件
     *
     * @param exportFile
     * @param content
     * @return
     */
    public static boolean writeFile(File exportFile, final String content) {
        if (exportFile == null || StringUtils.isEmpty(content)) {
            return false;
        }
        if (!exportFile.exists()) {
            try {
                exportFile.getParentFile().mkdirs();
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("create local json file exception: " + e.getMessage());
                return false;
            }
        }

        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream os = new FileOutputStream(exportFile);
            FileDescriptor fd = os.getFD();

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(os,"gb2312"));
            bufferedWriter.write(content);

            //Flush the data from the streams and writes into system buffers
            //The data may or may not be written to disk.
            bufferedWriter.flush();

            //block until the system buffers have been written to disk.
            //After this method returns, the data is guaranteed to have
            //been written to disk.
            fd.sync();
        } catch (UnsupportedEncodingException e) {
            log.error("saveDBData#catch an UnsupportedEncodingException (" + e.getMessage() + ")");
            return false;
        } catch (FileNotFoundException e) {
            log.error("saveDBData#catch an FileNotFoundException (" + e.getMessage() + ")");
            return false;
        } catch (IOException e) {
            log.error("saveDBData#catch an IOException (" + e.getMessage() + ")");
            return false;
        } catch (Exception e) {
            log.error("saveDBData#catch an exception (" + e.getMessage() + ")");
            return false;
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
            } catch (IOException e) {
                log.error("writeJsonToFile#catch an exception (" + e.getMessage() + ")");
            }
        }
        return true;
    }

    /**
     * 执行cmd命令
     *
     * @param cmd
     * @return
     */
    public static boolean excuteCMDBatFile(String cmd) throws IOException {
        final String METHOD_NAME = "excuteCMDBatFile#";
        boolean result = true;
        Process p;
        File batFile = new File("C:/test/cmd.bat");
        log.info(batFile.getAbsolutePath());
        boolean isSuccess = writeFile(batFile, cmd);
        //toANSI("C:/test/cmd.bat");
        if (!isSuccess) {
            log.error(METHOD_NAME + "write cmd to File failed.");
            return false;
        }

        String batFilePath = "\"" + batFile.getAbsolutePath() + "\"";
        log.info("cmd path:" + batFilePath);
        try {
            p = Runtime.getRuntime().exec(batFilePath);
            InputStream fis = p.getErrorStream();//p.getInputStream();
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            p.waitFor();
            int i = p.exitValue();
            log.info(METHOD_NAME + "exitValue = " + i);
            if (i != 0 && !builder.toString().equals("")) {
                result = false;
                log.error(METHOD_NAME + "excute cmd failed, [result = " + result + ", error message = " + builder.toString() + "]");
                log.error("cmd命令执行失败");
            } else {
                // logger.debug(METHOD_NAME + "excute cmd result = " + result);
                log.info(METHOD_NAME + "result = " + result);
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
            log.error(METHOD_NAME + "fail to excute bat File [ErrMsg=" + e.getMessage() + "]");
        }

        return result;
    }

    /**
     * 检测进程是否启动
     *
     * @return
     * @throws IOException
     */
    public static boolean isIEStart(String course) throws IOException {
        Process p = Runtime.getRuntime().exec("tasklist ");
        BufferedReader bw = new BufferedReader(new InputStreamReader(p
                .getInputStream()));
        String str = "";
        StringBuffer sb = new StringBuffer();
        while (true) {
            str = bw.readLine();
            if (str != null) {
                sb.append(str.toLowerCase());
            } else {
                break;
            }
        }
        String ie = course;
        if (sb.toString().indexOf(ie) != -1) {
            log.info(course+"已经启动！执行关闭"+course+"命令！");
            return true;
        } else
            return false;
    }

}
