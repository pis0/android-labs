package com.multimobile.printer.leopardoa7;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {


    private static String TAG = "BluetoothPrinter";
    static int GALLERY_REQUEST_CODE = 1 << 0;
    static int BLUETOOTH_ACTION = 1 << 1;


    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private OutputStream outputStream;
    private InputStream inputStream;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private EditText txt;
    private TextView lblPrinterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnDisconnect = findViewById(R.id.btnDisconnect);
        Button btnPrintText = findViewById(R.id.btnPrintText);
        Button btnPrintImage = findViewById(R.id.btnPrintImage);

        txt = findViewById(R.id.txt);

        lblPrinterName = findViewById(R.id.lblPrinterName);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findBluetoothDevice();
                } catch (Exception e) {
                    Log.e(TAG, "btnConnect error: " + e.getMessage(), e);
                }

            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "btnDisconnect error: " + e.getMessage(), e);
                }
            }
        });

        btnPrintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //TODO to review
                    //printText(txt.getText().toString());


                    //format[2] = ((byte)(0x8  | formatHelper[2]));  // Bold
                    //format[2] = ((byte)(0x10 | formatHelper[2]));  // Height
                    //format[2] = ((byte)(0x20 | formatHelper[2]));  // Width
                    //format[2] = ((byte)(0x80 | formatHelper[2]));  // Underline
                    //format[2] = ((byte)(0x2  | formatHelper[2]));  // Small
                    //format[2] = ((byte)(0x1  | formatHelper[2]));  // Smallest

                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_30);

                    byte[] format = {27, 33, 0};
                    byte[] formatHelper = {27, 33, 0};

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("Cooperativa Central Aurora Alimentos").getBytes());
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x8 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("Comprovante de Coleta de Leite").getBytes());
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("Produtor:").getBytes());
                    outputStream.write(PrinterCommands.FEED_LINE);
//                    format[2] = formatHelper[2];
                    format[2] = ((byte) (0x8 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("341517 - ADILSON NISTERVITZ").getBytes());
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_24);
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("Placa:  ").getBytes());
//                    format[2] = formatHelper[2];
                    format[2] = ((byte) (0x8 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("MEH1605").getBytes());
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_30);
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);


                    format[2] = ((byte) (0x8 | formatHelper[2]));  // Bold
                    format[2] = ((byte) (0x20 | formatHelper[2]));  // Width
                    outputStream.write(format);
                    outputStream.write(("COLETA REJEITADA").getBytes());
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_24);
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("LEITE COM + DE 48 HORAS").getBytes());
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_30);
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("05/02/2020").getBytes());
                    outputStream.write(PrinterCommands.SET_LINE_SPACING_24);
                    outputStream.write(PrinterCommands.FEED_LINE);

                    format[2] = ((byte) (0x2 | formatHelper[2]));
                    outputStream.write(format);
                    outputStream.write(("www.auroraalimentos.com.br").getBytes());
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);
                    outputStream.write(PrinterCommands.FEED_LINE);


                } catch (Exception e) {
                    Log.e(TAG, "btnPrintText error: " + e.getMessage(), e);
                }
            }
        });

        btnPrintImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pickFromGallery();
                } catch (Exception e) {
                    Log.e(TAG, "btnPrintImage error: " + e.getMessage(), e);
                }
            }
        });

    }

    private void findBluetoothDevice() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                lblPrinterName.setText("No Bluetooth Adapter Found");
                return;
            }

            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, BLUETOOTH_ACTION);
            }

            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice pairedDevice : bondedDevices) {

                    Log.d(TAG, "\nname: " + pairedDevice.getName() + "\naddress: " + pairedDevice.getAddress());

                    if (pairedDevice.getName().equals("MPT-III")) {
                        bluetoothDevice = pairedDevice;
                        lblPrinterName.setText("Bluetooth Printer Attached: " + pairedDevice.getName());

                        openBluetoothPrinter();

                        break;
                    }

                }
            }


        } catch (Exception e) {
            Log.e(TAG, "findBluetoothDevice error: " + e.getMessage(), e);
        }
    }


    private void openBluetoothPrinter() {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            bluetoothSocket.connect();

            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();


            beginListenData();


        } catch (Exception e) {
            Log.e(TAG, "openBluetoothPrinter error: " + e.getMessage(), e);
        }
    }

    private void beginListenData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];

                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, StandardCharsets.US_ASCII);
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            stopWorker = true;
                        }
                    }
                }
            });

            thread.start();

        } catch (Exception e) {
            Log.e(TAG, "beginListenDataForText error: " + e.getMessage(), e);
        }
    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //print text
    private void printText(String msg) {
        try {
            outputStream.write(msg.getBytes());
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printText(byte[] msg) {
        try {
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            Log.e(TAG, "printText error: " + e.getMessage(), e);
        }
    }

    private void printImage(Bitmap image) {
        try {

            // TODO to fix
            final int MAX_SIZE = 592;
            int originalW = image.getWidth();
            int originalH = image.getHeight();
            Bitmap imageToUpload = Bitmap.createScaledBitmap(
                    image,
                    MAX_SIZE,
                    264, //2728, //(int) (((double) MAX_SIZE / (double) originalW) * (double) originalH),
                    false);
            if (imageToUpload != null) {
                Log.d(TAG, "image - " + originalW + ", " + originalH);
                Log.d(TAG, "imageToUpload - " + imageToUpload.getWidth() + ", " + imageToUpload.getHeight() + ", " + imageToUpload.getDensity());
            } else {
                Log.d(TAG, "imageToUpload is null");
                return;
            }

            //TODO to delete
            // D/BluetoothPrinter: image - 480, 960
            // D/BluetoothPrinter: imageToUpload - 592, 2728 - r 4.6081
//            return;
            printText(Objects.requireNonNull(PrinterUtils.decodeBitmap(
                    imageToUpload // image
            )));

            lblPrinterName.setText("Printing Image...");
        } catch (Exception e) {
            Log.e(TAG, "printImage error: " + e.getMessage(), e);
        }
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {

                Uri selectedImage = Objects.requireNonNull(data).getData();
                try {

                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(Objects.requireNonNull(selectedImage), "r");
                    FileDescriptor fileDescriptor = Objects.requireNonNull(parcelFileDescriptor).getFileDescriptor();
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();

                    Log.d(TAG, "image: " + image);

                    printImage(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BitSet dots;

    private void convertARGBToGS(Bitmap original, int width, int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = original.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 55) {
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "convertARGBToGS error: " + e.getMessage(), e);
        }
    }


    private void disconnect() {
        try {

            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();

            lblPrinterName.setText("Printer Disconnected");

        } catch (Exception e) {
            Log.e(TAG, "printData error: " + e.getMessage(), e);
        }
    }


}

class PrinterCommands {
    public static final byte HT = 0x9;
    public static final byte LF = 0x0A;
    public static final byte CR = 0x0D;
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;

    public static final byte[] INIT = {27, 64};
    public static byte[] FEED_LINE = {10};

    public static byte[] SELECT_FONT_A = {20, 33, 0};

    public static byte[] SET_BAR_CODE_HEIGHT = {29, 104, 100};
    public static byte[] PRINT_BAR_CODE_1 = {29, 107, 2};
    public static byte[] SEND_NULL_BYTE = {0x00};

    public static byte[] SELECT_PRINT_SHEET = {0x1B, 0x63, 0x30, 0x02};
    public static byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};

    public static byte[] SELECT_CYRILLIC_CHARACTER_CODE_TABLE = {0x1B, 0x74, 0x11};

    //public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, -128, 0};
    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) 255, 3};
    public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
    public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};

    public static byte[] TRANSMIT_DLE_PRINTER_STATUS = {0x10, 0x04, 0x01};
    public static byte[] TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = {0x10, 0x04, 0x02};
    public static byte[] TRANSMIT_DLE_ERROR_STATUS = {0x10, 0x04, 0x03};
    public static byte[] TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = {0x10, 0x04, 0x04};

    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[]{0x1B, 'r', 0x00};
    public static final byte[] FS_FONT_ALIGN = new byte[]{0x1C, 0x21, 1, 0x1B, 0x21, 1};
    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{0x1B, 0x45, 0};


    /*********************************************/
    public static final byte[] ESC_HORIZONTAL_CENTERS = new byte[]{0x1B, 0x44, 20, 28, 00};
    public static final byte[] ESC_CANCLE_HORIZONTAL_CENTERS = new byte[]{0x1B, 0x44, 00};
    /*********************************************/

    public static final byte[] ESC_ENTER = new byte[]{0x1B, 0x4A, 0x40};
    public static final byte[] PRINTE_TEST = new byte[]{0x1D, 0x28, 0x41};

}


class PrinterUtils {


    // UNICODE 0x23 = #
    public static final byte[] UNICODE_TEXT = new byte[]{0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>(); //binaryString list
        StringBuffer sb;

        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

//        float[] hsv = new float[3];
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                // new
//                Color.colorToHSV(color, hsv);
//                if (hsv[2] > 0.5f) sb.append("0");
//                else sb.append("1");

                // old
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");

            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer.toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
//        if (heightHexString.length() > 2) {
//            Log.e("decodeBitmap error", " height is too large");
//            return null;
//        } else if (heightHexString.length() == 1) {
//            heightHexString = "0" + heightHexString;
//        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}