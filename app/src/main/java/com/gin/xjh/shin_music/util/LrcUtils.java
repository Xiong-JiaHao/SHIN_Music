package com.gin.xjh.shin_music.util;

import com.gin.xjh.shin_music.bean.Lyric;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class LrcUtils {
    private static List<Lyric> lyricList;

    /**
     * 读取文件
     */
    public static List<Lyric> readLRC(String lyrics) {
        lyricList = new Vector<Lyric>();
        String strTemp = "";
        for (int i = 0; i < lyrics.length(); i++) {
            char c = lyrics.charAt(i);
            if (c != '\n') {
                strTemp += c;
            } else {
                strTemp = processLRC(strTemp);
            }
        }
        if (strTemp != "") {
            processLRC(strTemp);
        }
        // 对歌词进行排序
        Collections.sort(lyricList, new Sort());
        return lyricList;
    }

    /**
     * 处理一行内容
     */
    private static String processLRC(String text) {
        try {
            int pos1 = text.indexOf("[");
            int pos2 = text.indexOf("]");

            if (pos1 >= 0 && pos2 != -1) {
                Long time[] = new Long[getPossiblyTagCount(text)];
                time[0] = timeToLong(text.substring(pos1 + 1, pos2));
                if (time[0] == -1)
                    return "";
                String strLineRemaining = text;
                int i = 1;
                while (pos1 >= 0 && pos2 != -1) {

                    strLineRemaining = strLineRemaining.substring(pos2 + 1);
                    if (strLineRemaining.length() > 15) {
                        String a = strLineRemaining.substring(0, 12);
                        String b = strLineRemaining.substring(13);
                        strLineRemaining = a + '\n' + b;
                    }
                    pos1 = strLineRemaining.indexOf("[");
                    pos2 = strLineRemaining.indexOf("]");
                    if (pos2 != -1) {
                        time[i] = timeToLong(strLineRemaining.substring(
                                pos1 + 1, pos2));
                        if (time[i] == -1)
                            return ""; // LRCText
                        i++;
                    }
                }

                Lyric tl = null;
                //防止有的歌词文件是这种格式：[00:01:23][00:03:02]重复歌词
                //就是歌词重复的放在一起，将多个时间戳放在一起，所以在解析完歌词需要排序一下。
                for (int j = 0; j < time.length; j++) {
                    if (time[j] != null) {
                        tl = new Lyric();
                        tl.timePoint = time[j].intValue();
                        tl.lricString = strLineRemaining;
                        if (tl.lricString != "" && tl.lricString.length() > 0) {
                            lyricList.add(tl);
                        }
                    }
                }
                return strLineRemaining;
            } else
                return "";
        } catch (Exception e) {
            return "";
        }
    }

    //获取一行中的时间标签的个数，为了防止将重复歌词放在一行上显示
    private static int getPossiblyTagCount(String Line) {
        String strCount1[] = Line.split("\\[");
        String strCount2[] = Line.split("\\]");
        if (strCount1.length == 0 && strCount2.length == 0)
            return 1;
        else if (strCount1.length > strCount2.length)
            return strCount1.length;
        else
            return strCount2.length;
    }

    /**
     * 时间转换，将time格式时间转换成秒
     */
    public static long timeToLong(String Time) {
        try {
            String[] s1 = Time.split(":");
            int min = Integer.parseInt(s1[0]);
            String[] s2 = s1[1].split("\\.");
            int sec = Integer.parseInt(s2[0]);
            int mill = 0;
            if (s2.length > 1)
                mill = Integer.parseInt(s2[1]);
            return min * 60 * 1000 + sec * 1000 + mill;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 判断文件编码，防止文件解析成乱码
     */
    public static String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            //一般读取前3个字节就可以判断文件的编码格式
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    //按照timePoint的大小进行升序排列
    private static class Sort implements Comparator<Lyric> {
        public Sort() {
        }

        public int compare(Lyric tl1, Lyric tl2) {
            if (tl1.timePoint < tl2.timePoint)
                return -1;
            else if (tl1.timePoint > tl2.timePoint)
                return 1;
            else
                return 0;
        }
    }
}