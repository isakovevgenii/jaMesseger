import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

public class jaServer {

    private static ArrayList streams; //тут будут храниться printWriterы

    public static void main(String[] args) {
        go();
    }

    private static void go(){
        streams = new ArrayList<PrintWriter>();
        try {
            ServerSocket ss = new ServerSocket(5000);
            while(true){ // ждет пока кто то подключится
                Socket socket = ss.accept();
                System.out.println("Got user!");
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                streams.add(writer);

                Thread thread = new Thread(new Listener(socket));
                thread.start();
            }
        } catch (Exception ignore){
        }
    }

    private static void tellEveryone(String msg){ //отправляем сообщение всем участникам чата
        int x = msg.indexOf(':');
        String login = msg.substring(0, x);

        java.util.Iterator<PrintWriter> it = streams.iterator();
        while (it.hasNext()){
            try {
                PrintWriter writer = it.next();
                writer.println(msg);
                writer.flush();
            }catch (Exception ignore){
                ignore.printStackTrace();
            }
        }
    }

    private static class Listener implements Runnable{

        BufferedReader reader;

        Listener(Socket socket){
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            }catch (Exception ignore){}
        }

        @Override
        public void run(){ //тут читаем сообщения
            String msg;
            try {
                while ((msg = reader.readLine()) != null){
                    System.out.println(msg);
                    tellEveryone(msg);
                }
            }catch (Exception ignore){}
        }
    }
}
