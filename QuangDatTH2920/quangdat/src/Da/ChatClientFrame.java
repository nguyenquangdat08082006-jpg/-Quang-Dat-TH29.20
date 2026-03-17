package da; // Đã đổi tên package

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class ChatClientFrame extends JFrame {
    private JTextArea area = new JTextArea();
    private JTextField input = new JTextField();
    private String user;
    private PrintWriter out;

    public ChatClientFrame(String user) {
        this.user = user;
        setTitle("Messenger - " + user);
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(15, 15));

        area.setEditable(false);
        area.setBackground(new Color(248, 249, 250));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new BorderLayout(10, 0));
        pnlBot.setOpaque(false);
        pnlBot.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
        
        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        RoundButton btnSend = new RoundButton("GỬI", new Color(46, 204, 113));
        btnSend.setPreferredSize(new Dimension(80, 40));

        pnlBot.add(input, BorderLayout.CENTER);
        pnlBot.add(btnSend, BorderLayout.EAST);
        add(pnlBot, BorderLayout.SOUTH);

        connect();
        btnSend.addActionListener(e -> send());
        input.addActionListener(e -> send());
    }

    private void connect() {
        try {
            Socket s = new Socket("127.0.0.1", 12345);
            out = new PrintWriter(s.getOutputStream(), true);
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    String l;
                    while((l = in.readLine()) != null) {
                        area.append(l + "\n");
                        area.setCaretPosition(area.getDocument().getLength());
                    }
                } catch(Exception e) {}
            }).start();
        } catch(Exception e) {
            area.append("[HỆ THỐNG] Lỗi kết nối Server!\n");
        }
    }

    private void send() {
        if(!input.getText().isEmpty()) {
            out.println(user + ": " + input.getText());
            input.setText("");
        }
    }

    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Nhập tên tài khoản:", "ĐĂNG NHẬP", JOptionPane.QUESTION_MESSAGE);
        if(name != null && !name.isEmpty()) {
            new ChatClientFrame(name).setVisible(true);
        }
    }
}