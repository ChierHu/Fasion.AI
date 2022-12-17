package ai.fasion.fabs.vesta.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;

/**
 * @author peishuai
 */
public class BaseCodeUtils {



    // base 32 字母表 可随意打乱顺序 默认
    private static final char[] ALPHABET = {
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',

            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',

            'y', 'z', '2', '3', '4', '5', '6', '7',

            'q', 'r', 's', 't', 'u', 'v', 'w', 'x'
    };
    //base 32解码表  默认
    private static final byte[] DECODE_TABLE;


    // base62字母表 可随意打乱顺序  转码解码时候对+ / 进行了处理 默认
    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
    //base 62解码表
    private static byte[] decodes;

    //base16  字母表 默认
    private static final String base16EncodeTable = "0123456789ABDEFC";



    static {
        DECODE_TABLE = new byte[128];
        decodes = new byte[256];
        for (int i = 0; i < ALPHABET.length; i++) {
            DECODE_TABLE[(int) ALPHABET[i]] = (byte) i;
        }
        for (int i = 0; i < encodes.length; i++) {
            decodes[encodes[i]] = (byte) i;
        }

    }



    public static String encodeBase32(byte[] data) {

        char[] chars = new char[((data.length * 8) / 5)
                + ((data.length % 5) != 0 ? 1 : 0)];
        for (int i = 0, j = 0, index = 0; i < chars.length; i++) {
            if (index > 3) {
                int b = data[j] & (0xFF >> index);

                index = (index + 5) % 8;

                b <<= index;

                if (j < data.length - 1) {
                    b |= (data[j + 1] & 0xFF) >> (8 - index);
                }
                chars[i] = ALPHABET[b];
                j++;
            } else {
                chars[i] = ALPHABET[((data[j] >> (8 - (index + 5))) & 0x1F)];

                index = (index + 5) % 8;

                if (index == 0) {
                    j++;
                }
            }
        }
        return new String(chars);

    }
    // base32 转码  携带自定义字母表
    public static String encodeBase32WithALP(byte[] data,char[] encodes) {
        if (null == encodes || encodes.length == 0) {
            throw new RuntimeException("base32转码字母表参数有误");
        }

        char[] chars = new char[((data.length * 8) / 5)
                + ((data.length % 5) != 0 ? 1 : 0)];
        for (int i = 0, j = 0, index = 0; i < chars.length; i++) {
            if (index > 3) {
                int b = data[j] & (0xFF >> index);

                index = (index + 5) % 8;

                b <<= index;

                if (j < data.length - 1) {
                    b |= (data[j + 1] & 0xFF) >> (8 - index);
                }
                chars[i] = encodes[b];
                j++;
            } else {
                chars[i] = encodes[((data[j] >> (8 - (index + 5))) & 0x1F)];

                index = (index + 5) % 8;

                if (index == 0) {
                    j++;
                }
            }
        }
        return new String(chars);

    }
    public static byte[] decodeBase32(String s) throws Exception {
        char[] stringData = s.toCharArray();

        byte[] data = new byte[(stringData.length * 5) / 8];

        for (int i = 0, j = 0, index = 0; i < stringData.length; i++) {

            int val;

            try {
                val = DECODE_TABLE[stringData[i]];
            } catch(ArrayIndexOutOfBoundsException e) {
                throw new Exception("Illegal character");
            }

            if (val == 0xFF) {
                throw new Exception("Illegal character");
            }

            if (index <= 3) {

                index = (index + 5) % 8;

                if (index == 0) {
                    data[j++] |= val;
                } else {
                    data[j] |= val << (8 - index);
                }
            }
            else {
                index = (index + 5) % 8;

                data[j++] |= (val >> index);
                if (j < data.length) {
                    data[j] |= val << (8 - index);
                }
            }
        }
        return data;
    }
    //base32 解码 自定义字母表
    public static byte[] decodeBase32WithALP(String s,char[] encodes) throws Exception {
        char[] stringData = s.toCharArray();
        if (null == encodes || encodes.length == 0) {
            throw new RuntimeException("base32解码字母表参数有误");
        }

        byte[] data = new byte[(stringData.length * 5) / 8];

        byte[] decodes = new byte[128];
        for (int i = 0; i < encodes.length; i++) {
            decodes[(int) encodes[i]] = (byte) i;
        }


        for (int i = 0, j = 0, index = 0; i < stringData.length; i++) {

            int val;

            try {
                val = decodes[stringData[i]];
            } catch(ArrayIndexOutOfBoundsException e) {
                throw new Exception("Illegal character");
            }

            if (val == 0xFF) {
                throw new Exception("Illegal character");
            }

            if (index <= 3) {

                index = (index + 5) % 8;

                if (index == 0) {
                    data[j++] |= val;
                } else {
                    data[j] |= val << (8 - index);
                }
            }
            else {
                index = (index + 5) % 8;

                data[j++] |= (val >> index);
                if (j < data.length) {
                    data[j] |= val << (8 - index);
                }
            }
        }
        return data;
    }



    public static StringBuffer encodeBase62(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            val = (val << 8) | (data[i] & 0xFF);
            pos += 8;
            while (pos > 5) {
                char c = encodes[val >> (pos -= 6)];
                sb.append(
                        /**/c == 'i' ? "ia" :
                                /**/c == '+' ? "ib" :
                                /**/c == '/' ? "ic" : c);
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            char c = encodes[val << (6 - pos)];
            sb.append(
                    /**/c == 'i' ? "ia" :
                            /**/c == '+' ? "ib" :
                            /**/c == '/' ? "ic" : c);
        }
        return sb;
    }
    //base62 编码 自定义字母表
    public static StringBuffer encodeBase62WithALP(byte[] data,char[] encodes) {
        if (null == encodes || encodes.length == 0) {
            throw new RuntimeException("base62转码字母表参数有误");
        }
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            val = (val << 8) | (data[i] & 0xFF);
            pos += 8;
            while (pos > 5) {
                char c = encodes[val >> (pos -= 6)];
                sb.append(
                        /**/c == 'i' ? "ia" :
                                /**/c == '+' ? "ib" :
                                /**/c == '/' ? "ic" : c);
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            char c = encodes[val << (6 - pos)];
            sb.append(
                    /**/c == 'i' ? "ia" :
                            /**/c == '+' ? "ib" :
                            /**/c == '/' ? "ic" : c);
        }
        return sb;
    }

    public static byte[] decodeBase62(char[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[++i];
                c =
                        /**/c == 'a' ? 'i' :
                        /**/c == 'b' ? '+' :
                        /**/c == 'c' ? '/' : data[--i];
            }
            val = (val << 6) | decodes[c];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }
    //base62 解码  自定义字母表
    public static byte[] decodeBase62WithALP(char[] data,char[] encodes) {
        if (null == encodes || encodes.length == 0) {
            throw new RuntimeException("base62解码字母表参数有误");
        }

        byte[] decodes = new byte[256];
        for (int i = 0; i < encodes.length; i++) {
            decodes[encodes[i]] = (byte) i;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[++i];
                c =
                        /**/c == 'a' ? 'i' :
                        /**/c == 'b' ? '+' :
                        /**/c == 'c' ? '/' : data[--i];
            }
            val = (val << 6) | decodes[c];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }

    public static String base16Encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            //向右移动4bit，获得高4bit
            int highByte = (data[i] >> 4) & 0x0F;
            //与0x0f做位与运算，获得低4bit
            int lowByte = data[i] & 0x0F;
            result.append(base16EncodeTable.charAt(highByte));
            result.append(base16EncodeTable.charAt(lowByte));
        }
        return result.toString();
    }
    // base16 转码 自定义字母表
    public static String base16EncodeWithALP(byte[] data,char[] table) {
        if (null == table || table.length == 0) {
            throw new RuntimeException("base16转码字母表参数有误");
        }

        if (data == null || data.length == 0) {
            return "";
        }
        char[] dst   = new char[data.length * 2];
        for (int si = 0, di = 0; si < data.length; si++) {
            byte b = data[si];
            dst[di++] = table[(b & 0xf0) >>> 4];
            dst[di++] = table[(b & 0x0f)];
        }

        return String.valueOf(dst);
    }


    //把16进制字符转换成10进制表示的数字
    private static int hex2dec(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        } else if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        } else {
            return 0;
        }
    }

    public static byte[] base16Decode(String input) {
        int inputLength = input.length();
        int halfInputLength = inputLength / 2;
        byte[] output = new byte[halfInputLength];
        for (int i = 0; i < halfInputLength; i++) {
            //16进制数字转换为10进制数字的过程
            output[i] = (byte) (hex2dec(input.charAt(2 * i)) * 16 + hex2dec(input.charAt(2 * i + 1)));
        }
        return output;
    }

    public static byte[] base16DecodeWithALP(String input,char[] encodes) {
        if (null == encodes || encodes.length == 0) {
            throw new RuntimeException("base16转码字母表参数有误");
        }

        byte[] decode = new byte[128];
        for (int i = 0; i < encodes.length; i++) {
            decode[encodes[i]] = (byte) i;
        }
        char[] src = input.toCharArray();
        byte[] dst = new byte[src.length / 2];

        for (int si = 0, di = 0; di < dst.length; di++) {
            byte high = decode[src[si++] & 0x7f];
            byte low  = decode[src[si++] & 0x7f];
            dst[di] = (byte) ((high << 4) + low);
        }

        return dst;
    }


    public static void main(String[] args) throws Exception {
        String plainStr = "peishuai321";
        byte[] plainBts = plainStr.getBytes("UTF-8");

        String encode = encodeBase32WithALP(plainBts,"efaghijkl3udmnopyz0vwxbc1245qrst".toCharArray());
        System.out.println("base32 转码后结果：   "+encode);

        byte[] decode = decodeBase32WithALP(encode,"efaghijkl3udmnopyz0vwxbc1245qrst".toCharArray());
        String decodeResult = new String(decode, "UTF-8");
        System.out.println("base32 解码后结果：   "+decodeResult);




        StringBuffer stringBuffer = encodeBase62WithALP(plainBts,"A234STUPQGWXYZabcdrst78efEFBVJKLMNOCvwxyz01DHIghijklmnopq9".toCharArray());
        System.out.println("base62 转码后结果：   "+stringBuffer.toString());
        byte[] bytes = decodeBase62WithALP(stringBuffer.toString().toCharArray(),"A234STUPQGWXYZabcdrst78efEFBVJKLMNOCvwxyz01DHIghijklmnopq9".toCharArray());
        String decodeResult2 = new String(bytes, "UTF-8");
        System.out.println("base62 解码后结果：   "+decodeResult2);

        String base16Str = base16EncodeWithALP(plainBts,"7485963210AKYTES".toCharArray());
        System.out.println("base16 转码后结果：   "+base16Str);
        byte[] bytes1 = base16DecodeWithALP(base16Str,"7485963210AKYTES".toCharArray());
        String decodeResult3 = new String(bytes1, "UTF-8");
        System.out.println("base16 解码后结果：   "+decodeResult3);



    }


}
