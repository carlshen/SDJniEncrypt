package com.tongxin.sdjni;

/**
 * Created by carl on 19-11-12.
 *
 * 秘文的key 在 C 的代码，如需修改请先修改JNIEncrypt.c
 * 另外native做了签名校验
 */

public class AESEncrypt {

    static {
        System.loadLibrary("JNIEncrypt");
    }

    /**
     * AES加密
     *
     * @param context
     * @param str
     * @return
     */
    public static native String encode(Object context, String str);


    /**
     * AES 解密
     *
     * @param context
     * @param str
     * @return UNSIGNATURE ： sign not pass .
     */
    public static native String decode(Object context, String str);


    /**
     * 检查 打包签名是否 是正确的 防止被二次打包
     *
     * @param context
     * @return 1 : pass ， -1 or  -2 : error.
     */
    public static native int checkSignature(Object context);

    /**
     * set package name
     * @param str
     * @return 1 : pass ， -1 or  -2 : error.
     */
    public static native long setPackageName(String str);
    public static native long InitParams(long type, String dev);
    public static native long DestroyParams();
    public static native String RefreshDev();
    public static native int ConnectDev(String dev);
    public static native long DisconnectDev(int handle);
    public static native long BeginTransaction(int handle);
    public static native long EndTransaction(int handle);
    public static native String GetFirmVer(int handle);
    public static native String GetFlashID(int handle);
    public static native String ResetCard(int handle);
    public static native long ResetController(int handle, long control);
    public static native byte[] TransmitSd(int handle, byte[] command, long len, long mode);
    public static native byte[] TransmitEx(int handle, byte[] command, long mode);
    public static native String GetSDKVer();
    public static native long GetSCIOType(int handle);

}
