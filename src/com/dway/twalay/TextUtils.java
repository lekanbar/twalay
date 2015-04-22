package com.dway.twalay;

import java.io.UnsupportedEncodingException;

import net.rim.blackberry.api.browser.URLEncodedPostData;

public class TextUtils {
    /**
     * Tests if a String value is null or empty.
     * 
     * @param value the String value to test
     * @return true if the String is null, zero length, or ""
     */
    public static boolean isEmpty(String value) {
        return (value == null || value.length() == 0 || value == "");
    }

    /**
     * Creates a String from a null-terminated array of default String encoded bytes.
     * 
     * @param bytes the array of bytes that contains the string
     * @param offset the offset in the bytes to start testing for null
     * @return the resulting String value of the bytes from offset to null or end (whichever comes first)
     */
    public static String fromNullTerminatedBytes(byte[] bytes, int offset) {
        if (bytes == null)
            return null;

        if (offset < 0)
            throw new IllegalArgumentException("offset must be >= 0");

        int length = 0;
        while (offset + length < bytes.length && bytes[offset + length] != '\0') {
            length++;
        }
        return new String(bytes, offset, length);
    }

    /**
     * Creates a default encoded array of bytes of the given String value. This is not an efficient implementation, so
     * call sparingly.
     * 
     * @param s the String value to convert to bytes
     * @return the bytes of the String followed by the null terminator '\0'
     */
    public static byte[] toNullTerminatedBytes(String s) {
        byte[] temp = s.getBytes();
        byte[] bytes = new byte[temp.length + 1]; // +1 for null terminator
        System.arraycopy(temp, 0, bytes, 0, bytes.length - 1);
        return bytes;
    }

    public static String getShortClassName(Object o) {
        if (o == null) {
            return "null";
        }

        return getShortClassName(o.getClass());
    }

    public static String getShortClassName(Class c) {
        if (c == null) {
            return "null";
        }

        String name = c.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String getMethodName(String methodName) {
        if (methodName == null) {
            methodName = "()";
        }
        if (methodName.compareTo("()") != 0) {
            methodName = "." + methodName;
        }
        return methodName;
    }

    public static String getShortClassAndMethodName(Object o, String methodName) {
        return getShortClassName(o) + getMethodName(methodName);
    }

    public static String formatNumber(long number, int minimumLength) {
        String s = String.valueOf(number);
        while (s.length() < minimumLength) {
            s = "0" + s;
        }
        return s;
    }

    public static String toString(int[] ints) {
        StringBuffer sb = new StringBuffer();
        if (ints == null) {
            sb.append("null");
        }
        else {
            sb.append("[");
            for (int i = 0; i < ints.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(ints[i]);
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public static String toHexString(byte b[]) {
        if (b == null) {
            return "";
        }
        return toHexString(b, 0, b.length);
    }

    public static String toHexString(byte b[], int offset, int count) {
        if (b == null) {
            return "";
        }
        final char[] hexChars =
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        StringBuffer sb = new StringBuffer();
        for (int i = offset; i < count; i++) {
            sb.append(hexChars[(((int) b[i]) & 0x000000f0) >> 4]);
            sb.append(hexChars[(((int) b[i]) & 0x0000000f)]);
        }
        return sb.toString();
    }

    public static String toHexString(short value) {
        return toHexString(new byte[]
        {
            (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF),
        });
    }

    public static String toHexString(int value) {
        return toHexString(new byte[]
        {
            (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF), (byte) ((value >> 24) & 0xFF),
        });
    }

    public static String toHexString(long value) {
        return toHexString(new byte[]
        {
            (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF), (byte) ((value >> 24) & 0xFF),
            (byte) ((value >> 32) & 0xFF), (byte) ((value >> 40) & 0xFF), (byte) ((value >> 48) & 0xFF),
            (byte) ((value >> 56) & 0xFF),
        });
    }

    public static String toHexString(String value) {
        return toHexString(value.getBytes());
    }

    public static final String ENCODING_CHARSET = "UTF-8";

    public static String urlEncode(String s) {
        URLEncodedPostData urlEncoder = new URLEncodedPostData(ENCODING_CHARSET, false);
        urlEncoder.setData(s);
        return urlEncoder.toString();
    }

    public static String urlEncode(String key, String value) {
        URLEncodedPostData urlEncoder = new URLEncodedPostData(ENCODING_CHARSET, false);
        urlEncoder.append(key, value);
        return urlEncoder.toString();
    }

    public static String urlDecode(String s) throws UnsupportedEncodingException {
        boolean needToChange = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    i++;
                    needToChange = true;
                    break;
                case '%':
                    /*
                     * Starting with this instance of %, process all
                     * consecutive substrings of the form %xy. Each
                     * substring %xy will yield a byte. Convert all
                     * consecutive  bytes obtained this way to whatever
                     * character(s) they represent in the provided
                     * encoding.
                     */

                    try {

                        // (numChars-i)/3 is an upper bound for the number
                        // of remaining bytes
                        if (bytes == null)
                            bytes = new byte[(numChars - i) / 3];
                        int pos = 0;

                        while (((i + 2) < numChars) && (c == '%')) {
                            int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
                            if (v < 0)
                                throw new IllegalArgumentException(
                                                "URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                            bytes[pos++] = (byte) v;
                            i += 3;
                            if (i < numChars)
                                c = s.charAt(i);
                        }

                        // A trailing, incomplete byte encoding such as
                        // "%x" will cause an exception to be thrown

                        if ((i < numChars) && (c == '%'))
                            throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");

                        sb.append(new String(bytes, 0, pos, ENCODING_CHARSET));
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - "
                                        + e.getMessage());
                    }
                    needToChange = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (needToChange ? sb.toString() : s);
    }
}