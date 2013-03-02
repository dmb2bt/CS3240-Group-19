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
    private static boolean USBTest = false;
    private NXTInfo[] info;
    private long start, latency;
    static Boolean readFlag = true;
    static Object lock = new Object();
    private OutputStream os;
    private InputStream is;
    private DataOutputStream oHandle;
    private DataInputStream iHandle;

    public static void main(String[] args) throws NXTCommException{
        start = System.currentTimeMillis();

        if(USBTest){
            connection = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
            info = connection.search(null, 0);
        } else {
            connection = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            info = connection.search("NXT", 1234);
        }
        if (info.length == 0) {
            System.out.println("Unable to find device");
            return;
        }

        connection.open(info[0]);
        OutputStream os = connection.getOutputStream();
        InputStream is = connection.getInputStream();

        final DataOutputStream oHandle = new DataOutputStream(os);
        final DataInputStream iHandle = new DataInputStream(is);
        latency = System.currentTimeMillis() - start;
        System.out.printf("Connection is established [%dms]\n", latency);

        String input = "Initiate.";

        Scanner scanner = new Scanner(System.in);

        Thread PCreceiver = new Thread() {
            public void run() {
                while (readFlag) {
                    try {
                        start = System.currentTimeMillis();
                        byte[] buffer = new byte[256];
                        int count = iHandle.read(buffer); // might wnt to check ack later
                        if (count>0){
                            String ret = (new String(buffer)).trim();
                            long l = System.currentTimeMillis() - start;
                            System.out.printf("NXJ: %s [%dms]\n", ret, l);
                        }
                        Thread.sleep(10);
                    } catch (IOException e) {
                        System.out.println("Fail to read from iHandle bc "
                                + e.toString());
                        return;
                    } catch (InterruptedException e){

                    }

                }
            }
        };
        PCreceiver.start();

        System.out.println("\n PC: Waiting on command....");

        do{
            try {
                input = scanner.nextLine();
                start = System.currentTimeMillis();

                oHandle.write(createCommand(input).getBytes());
                oHandle.flush();
                latency = System.currentTimeMillis() - start;

                System.out.println("\nPC: " + input + " [" + latency + "ms]");


            } catch (IOException e) {
                System.out.println("Fail to write to oHandle bc "
                        + e.toString());
                return;
            }
        } while (!input.equalsIgnoreCase("exit"));

        try {
            connection.close();
            readFlag=false; // stop reading threads
            // stop all threads as well
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Ending session...");
    }

    public String createCommand(String cmd){
        // TO DO: Create commands based on the input...
    }


}
