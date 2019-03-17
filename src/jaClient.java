import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.io.*;
        import java.net.Socket;

public class jaClient {

    private static JTextArea textArea;
    private static JTextField textField;
    private static BufferedReader reader; //получает сообщения от сервера
    private static PrintWriter writer; //отправка сообщений на сервер
    private static String login;

    public static void main(String[] args) {
        go();
    }

    public static void go() {

        ImageIcon img = new ImageIcon("src/logo.png");
        JOptionPane.showMessageDialog(null, img, "jaMessenger", -1);

        login = JOptionPane.showInputDialog("Введите логин");

        JFrame frame = new JFrame("JaMessenger 1.0"); //делаем окно
        frame.setResizable(false); //нельзя растягивать
        frame.setLocationRelativeTo(null); //по центру экрана
        JPanel panel = new JPanel();
        textArea = new JTextArea(15, 30); //размеры поля
        textArea.setLineWrap(true); //переносится со строки на строку
        textArea.setEditable(false); //нельзя изменить
        textArea.setWrapStyleWord(true); //слова переносятся полностью
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textField = new JTextField(20);
        JButton sentButton = new JButton("Отправить");
        JButton refreshButton = new JButton("Обновить");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writer.print("");
            }
        });

        sentButton.addActionListener(new Send());

        panel.add(scrollPane);
        panel.add(textField);
        panel.add(sentButton);
        setNet();

        Thread thread = new Thread(new Listener());
        thread.start();

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.NORTH, refreshButton);
        frame.setSize(400, 340);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class Listener implements Runnable{
        @Override
        public void run(){
            String msg;
            try {
                while ((msg = reader.readLine())!= null){
                    textArea.append(msg + "\n");
                }
            }catch (Exception ignore){}
        }
    }

    private static class Send implements ActionListener{ //отправка сообщений

        @Override
        public  void actionPerformed(ActionEvent e){

            String msg = login + ": " + textField.getText();
            writer.println(msg);
            writer.flush(); //закрываем поток

            textField.setText("");
            textField.requestFocus();

        }
    }

    private static void setNet(){ //подключение к серверу
        try {
            Socket socket = new Socket("127.0.0.1", 5000);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isr);
            writer = new PrintWriter(socket.getOutputStream());
        } catch (Exception ignore){}
    }
}
