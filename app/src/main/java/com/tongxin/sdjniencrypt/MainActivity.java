package com.tongxin.sdjniencrypt;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tongxin.sdjni.AESEncrypt;

import java.io.File;
import java.util.Arrays;

/**
 * Created by carl on 19-11-12.
 *
 * 用于公司的项目验证。
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = "tongxin";

    private final String str = "123abcABC*%!~#+_/中文测试";
    private final String ecode = "SkiDk/JC5F/BXKf/np7rWNub7ibxzYMjKwkQ7A6AqPw=";
    TextView deviceLog;
    Button devRefresh;
    Button devTest;
    Button devExit;
    File[] appsDir;
    EditText editCommand;
    EditText editRun;
    Button runCommand;
    AppCompatSpinner devSpinner;
    ArrayAdapter<String> adapter;
    String[] arrDevices = {}; //创建ArrayAdapter对象
    long beginTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceLog = (TextView) findViewById(R.id.device_log);
        deviceLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        devRefresh = (Button) findViewById(R.id.device_refresh);
        devTest = (Button) findViewById(R.id.device_test);
        devExit = (Button) findViewById(R.id.device_exit);
        editCommand = (EditText) findViewById(R.id.edit_command);
        editRun = (EditText) findViewById(R.id.edit_run);
        runCommand = (Button) findViewById(R.id.device_command);
        runCommand.setOnClickListener(this);
        devRefresh.setOnClickListener(this);
        devTest.setOnClickListener(this);
        devExit.setOnClickListener(this);

        devSpinner = (AppCompatSpinner) findViewById(R.id.device_spinner);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devSpinner.setAdapter(adapter);
        devSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击了" + arrDevices[position], Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //下面的密文对应的原文：123abcABC&*(@#@#@)+_/中文测试
        //String code = AESEncrypt.encode(this, str);
//        String code = AESEncrypt.decode(this, ecode);
//        Log.i("code", "after    code: " + code);
        Log.i("code", "original code: " + ecode);

        // need init
        callTongfang();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    finish();
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void callTongfang() {
        appsDir = getExternalFilesDirs("/");
        long result = AESEncrypt.setPackageName(getPackageName());
        Log.i(TAG, "getPackageName(): " + getPackageName());
        Log.i(TAG, "setPackageName result: " + result);
    }
    private void SetDevList(String devs) {
        String[] temp = devs.split(",");
        if (temp == null || temp.length < 1) {
            Log.i(TAG, "SetDevList with no device.");
            toast("SetDevList with no device.");
            return;
        }
        arrDevices = new String[temp.length + 1];
        arrDevices[0] = "Device List";
        for(int i = 0; i < temp.length; i++) {
            arrDevices[i+1] = temp[i];
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devSpinner.setAdapter(adapter);
        devSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    handle = AESEncrypt.ConnectDev(arrDevices[position]);
                    deviceLog.setText(deviceLog.getText() + "connect return " + handle + "\n");
                }
                Log.i(TAG, "onItemSelected position = " + position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    int handle = -1;
    String dev = null;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.device_refresh:
                dev = AESEncrypt.RefreshDev();
                deviceLog.setText(deviceLog.getText() + "Refresh return " + dev + "\n");
                Log.i(TAG, "-----------RefreshDev dev: " + dev);
                if (!TextUtils.isEmpty(dev)) {
                    SetDevList(dev);
                } else {
                    toast("Refresh with no device.");
                }
                break;
            case R.id.device_test:
                if (arrDevices == null || arrDevices.length < 2) {
                    toast("Please refresh first.");
                    return;
                }
                handle = AESEncrypt.ConnectDev(arrDevices[1]);
                deviceLog.setText(deviceLog.getText() + "connect return " + handle + "\n");
                Log.i(TAG, "-----------ConnectDev result: " + handle);
                break;
            case R.id.device_command:
                String commands = editCommand.getText().toString().trim();
                // next just test
//                deviceLog.setText(deviceLog.getText() + " commands " + commands + "\n");
//                byte[] test = HexStringToByteArray(commands);
//                deviceLog.setText(deviceLog.getText() + " commands test= " + new String(test) + "\n");
//                String testString = ByteArrayToHexString(test);
//                deviceLog.setText(deviceLog.getText() + " commands testString " + testString + "\n");
                if (TextUtils.isEmpty(commands)) {
                    toast("Please enter command first.");
                    return;
                }
                testCommand(commands);
                break;
            case R.id.device_exit:
                deviceLog.setText("");
                long discon = AESEncrypt.DisconnectDev(handle);
                deviceLog.setText(deviceLog.getText() + "Disconnect return " + discon + "\n");
                Log.i(TAG, "-----------DisconnectDev result: " + discon);
                break;
        }
    }

    private void testCommand(String commands) {
        if (TextUtils.isEmpty(commands)) {
            toast("Please enter command first.");
            return;
        }
        Log.i(TAG, "-----------TransmitSd Command: " + commands);
        String runRound = editRun.getText().toString().trim();
        int round = 1;
        try {
            round = Integer.parseInt(runRound);
        } catch (Exception arg4) {
            arg4.printStackTrace();
        }
        Log.i(TAG, "-----------TransmitSd run round: " + round);
        if (commands.contains("~")) {
            if (round > 1) {
                for (int i = 0; i < round; i++) {
                    testBatchCommand(commands);
                }
            } else {
                testBatchCommand(commands);
            }
        } else {
            testOneCommand(commands);
        }
    }

    private void testBatchCommand(String commands) {
        if (TextUtils.isEmpty(commands)) {
            toast("Please enter command first.");
            return;
        }
        Log.i(TAG, "-----------TransmitSd Command: " + commands);
        if (handle < 0) {
            toast("Please check connection first.");
            return;
        }
        if (!commands.contains("~")) {
            toast("Please check batch command list format: xxx~xxx");
            return;
        }
        String[] command = commands.split("~");
        if (command == null || command.length < 2) {
            toast("Please check command list format: xxx~xxx");
            return;
        }
        if (command[0] == null || (command[0].length() < 1) || (command[0].length() > 2)) {
            toast("Please check command list format: 1~255");
            return;
        }
        if (command[1] == null || (command[1].length() < 1) || (command[1].length() > 2)) {
            toast("Please check command list format: 1~255");
            return;
        }
        try {
            int begin = 0;
            int end   = 0;
            if (command[0].length() == 1){
                begin = Character.digit(command[0].charAt(0), 16);
            } else {
                begin = Character.digit(command[0].charAt(0), 16) * 16 + Character.digit(command[0].charAt(1), 16);
            }
            if (command[1].length() == 1){
                end   = Character.digit(command[1].charAt(0), 16);
            } else {
                end   = Character.digit(command[1].charAt(0), 16) * 16 + Character.digit(command[1].charAt(1), 16);
            }
            if (begin > end) {
                int temp = begin;
                begin = end;
                end = temp;
            }
            int total = 0;
            Log.i(TAG, "-----------TransmitSd begin: " + begin);
            Log.i(TAG, "-----------TransmitSd end: " + end);
            byte[] value;// = new byte[end - begin + 1];
            byte[] checker;// = new byte[end - begin + 1];
            byte high;
            byte low;
            String hexString;
            int position = 0;
            for (int start = begin; start <= end; start++) {
                value = new byte[start - begin + 1];
                checker = new byte[start - begin + 1];
                position = 0;
                for (int index = begin; index <= start; index++) {
//                        Log.i(TAG, "-----------TransmitSd index: " + index);
//                        Log.i(TAG, "-----------TransmitSd position: " + position);
                    hexString = Integer.toHexString(index);
//                        Log.i(TAG, "-----------TransmitSd hexString: " + hexString);
                    if (TextUtils.isEmpty(hexString)) {
                        value[position] = (byte) (0);
                    } else if (hexString.length() == 1) {
                        value[position] = (byte) (Character.digit(hexString.charAt(0), 16) & 0xff);
                    } else {
                        high = (byte) (Character.digit(hexString.charAt(0), 16) & 0xff);
                        low = (byte) (Character.digit(hexString.charAt(1), 16) & 0xff);
                        value[position] = (byte) (high << 4 | low);
                    }
                    hexString = Integer.toHexString(255 - index);
//                        Log.i(TAG, "-----------TransmitSd hexString: " + hexString);
                    if (TextUtils.isEmpty(hexString)) {
                        checker[position] = (byte) (0);
                    } else if (hexString.length() == 1) {
                        checker[position] = (byte) (Character.digit(hexString.charAt(0), 16) & 0xff);
                    } else {
                        high = (byte) (Character.digit(hexString.charAt(0), 16) & 0xff);
                        low = (byte) (Character.digit(hexString.charAt(1), 16) & 0xff);
                        checker[position] = (byte) (high << 4 | low);
                    }
//                        Log.i(TAG, "-----------TransmitSd value[position]: " + String.format("%02X", value[position]));
//                        Log.i(TAG, "-----------TransmitSd checker[position]: " + String.format("%02X", checker[position]));
                    position++;
                }
                Log.i(TAG, "-----------TransmitSd value: " + toHexString(value));
                Log.i(TAG, "-----------TransmitSd checker: " + toHexString(checker));
                long transaction = AESEncrypt.BeginTransaction(handle);
                if (transaction != 0) {
                    deviceLog.setText(deviceLog.getText() + "BeginTransaction return " + transaction + "\n");
                }
                byte[] res = AESEncrypt.TransmitSd(handle, value, value.length, 0);
                Log.i(TAG, "-----------TransmitSd result: " + toHexString(res));
                String result;
                if (res == null || res.length < 1) {
                    result = "fail";
                } else {
                    if (Arrays.equals(checker, res)) {
                        result = "pass";
                        total++;
                    } else {
                        result = "fail";
                    }
                }
                Log.i(TAG, "-----------TransmitSd result: " + result);
                if (!"pass".equalsIgnoreCase(result)) {
                    transaction = AESEncrypt.EndTransaction(handle);
                    deviceLog.setText(deviceLog.getText() + "EndTransaction return " + transaction + "\n");
                    deviceLog.setText(deviceLog.getText() + "Transmit command total: " + (end-begin+1) + ", pass " + total +"\n");
                    return;
                }
//                    deviceLog.setText(deviceLog.getText() + "Transmit command result " + result +"\n");
                transaction = AESEncrypt.EndTransaction(handle);
                if (transaction != 0) {
                    deviceLog.setText(deviceLog.getText() + "EndTransaction return " + transaction + "\n");
                }
            }
            deviceLog.setText(deviceLog.getText() + "Transmit command total: " + (end-begin+1) + ", pass " + total +"\n");
        } catch (Exception arg2) {
            arg2.printStackTrace();
            deviceLog.setText(deviceLog.getText() + arg2.getMessage() + "\n");
        }
    }

    private void testOneCommand(String commands) {
        if (TextUtils.isEmpty(commands)) {
            toast("Please enter command first.");
            return;
        }
        Log.i(TAG, "-----------TransmitSd Command: " + commands);
        if (commands.contains("~")) {
            toast("Please check command format: xxxxxx");
            return;
        }
        if (handle < 0) {
            toast("Please check connection first.");
            return;
        }
        try {
            long transaction = AESEncrypt.BeginTransaction(handle);
            deviceLog.setText(deviceLog.getText() + "BeginTransaction return " + transaction + "\n");
            byte[] command = toByteArray(commands);
//                Log.i(TAG, "--------------------TransmitSd command.length: " + commands.length());
//                Log.i(TAG, "---------------------TransmitSd command.length: " + command.length);
//                for (int i = 0; i < command.length; i++) {
//                    Log.i(TAG, "-----------TransmitSd command: " + command[i]);
//                }
            beginTime = System.currentTimeMillis();
            byte[] res = AESEncrypt.TransmitSd(handle, command, command.length, 0);
            if (res == null || res.length < 1) {
                deviceLog.setText(deviceLog.getText() + "Transmit return failed, command time " + (System.currentTimeMillis() - beginTime) + " ms\n");
                Log.i(TAG, "-----------TransmitSd result failed. ");
            } else {
                String result = toHexString(res);
                deviceLog.setText(deviceLog.getText() + "Transmit return " + result + ", command time " + (System.currentTimeMillis() - beginTime) + " ms\n");
                Log.i(TAG, "-----------TransmitSd result: " + result);
            }
            transaction = AESEncrypt.EndTransaction(handle);
            deviceLog.setText(deviceLog.getText() + "EndTransaction return " + transaction + "\n");
        } catch (Exception arg3) {
            arg3.printStackTrace();
            deviceLog.setText(deviceLog.getText() + arg3.getMessage() + "\n");
        }
    }

    public String toHexString(byte[] byteArray) {
        String str = null;
        if (byteArray != null && byteArray.length > 0) {
            StringBuffer stringBuffer = new StringBuffer(byteArray.length);
            for (byte byteChar : byteArray) {
                stringBuffer.append(String.format("%02X", byteChar));
            }
            str = stringBuffer.toString();
        }
        return str;
    }
    public byte[] toByteArray(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    private void toast(String str)
    {
        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
    }
}
