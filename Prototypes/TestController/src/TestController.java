import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class TestController {
    private NXTComm connection;
    private NXTInfo[] info;
    private long start, latency;
    static Boolean readFlag = true;
    static Object lock = new Object();

    public static void main(String[] args) {

    }

}
