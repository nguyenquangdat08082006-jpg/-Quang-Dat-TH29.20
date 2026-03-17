package da; // Đã đổi tên package

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServerFrame extends JFrame {
    private JTextArea txtLog = new JTextArea();
    private DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Người gửi", "Nội dung"}, 0);
    private JTable tbl = new JTable(model);
    private JTextField txtAdmin = new JTextField(), txtSearch = new JTextField(15);
    private CopyOnWriteArrayList<PrintWriter> clients = new CopyOnWriteArrayList<>();

    public ChatServerFrame() {
        setTitle("SERVER CONTROL - PACKAGE DA");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));
        setLayout(new BorderLayout(20, 20));

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        RoundButton btnStart = new RoundButton("KÍCH HOẠT SERVER", new Color(41, 128, 185));
        btnStart.setPreferredSize(new Dimension(0, 60));
        pnlHeader.add(btnStart, BorderLayout.CENTER);
        add(pnlHeader, BorderLayout.NORTH);

        // Body
        JPanel pnlBody = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlBody.setOpaque(false);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Nhật ký màu tối
        txtLog.setBackground(new Color(30, 39, 46));
        txtLog.setForeground(new Color(0, 216, 214));
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder(null, "NHẬT KÝ", 0, 0, null, Color.GRAY));

        // Quản lý tin nhắn
        JPanel pnlData = new JPanel(new BorderLayout(10, 10));
        pnlData.setOpaque(false);
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlTools.setOpaque(false);
        
        RoundButton btnFind = new RoundButton("Tìm", new Color(52, 152, 219));
        RoundButton btnEdit = new RoundButton("Sửa", new Color(241, 196, 15));
        RoundButton btnDel = new RoundButton("Xóa", new Color(231, 76, 60));
        
        pnlTools.add(new JLabel("Tìm kiếm:")); pnlTools.add(txtSearch);
        pnlTools.add(btnFind); pnlTools.add(btnEdit); pnlTools.add(btnDel);
        
        tbl.setRowHeight(30);
        pnlData.add(pnlTools, BorderLayout.NORTH);
        pnlData.add(new JScrollPane(tbl), BorderLayout.CENTER);

        pnlBody.add(scrollLog);
        pnlBody.add(pnlData);
        add(pnlBody, BorderLayout.CENTER);

        // Admin input
        txtAdmin.setPreferredSize(new Dimension(0, 40));
        add(txtAdmin, BorderLayout.SOUTH);

        // Sự kiện
        btnStart.addActionListener(e -> {
            new Thread(this::startServer).start();
            btnStart.setEnabled(false);
            btnStart.setBackground(new Color(39, 174, 96));
            loadData();
        });
        btnFind.addActionListener(e -> loadData());
        btnDel.addActionListener(e -> deleteAction());
        txtAdmin.addActionListener(e -> {
            broadcast("[ADMIN]: " + txtAdmin.getText());
            MessageService.saveMsg("ADMIN", txtAdmin.getText());
            txtAdmin.setText("");
            loadData();
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection c = MessageService.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM Messages WHERE Content LIKE ? ORDER BY ID DESC");
            ps.setString(1, "%" + txtSearch.getText() + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (Exception e) {}
    }

    private void deleteAction() {
        int r = tbl.getSelectedRow();
        if(r == -1) return;
        try (Connection c = MessageService.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM Messages WHERE ID=?");
            ps.setInt(1, (int)model.getValueAt(r, 0));
            ps.executeUpdate();
            loadData();
        } catch (Exception e) {}
    }

    private void startServer() {
        try (ServerSocket ss = new ServerSocket(12345)) {
            while(true) {
                Socket s = ss.accept();
                new Thread(() -> handleClient(s)).start();
            }
        } catch (Exception e) {}
    }

    private void handleClient(Socket s) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            clients.add(out);
            String line;
            while((line = in.readLine()) != null) {
                txtLog.append(line + "\n");
                String[] p = line.split(": ", 2);
                MessageService.saveMsg(p[0], p[1]);
                broadcast(line);
                loadData();
            }
        } catch (Exception e) {}
    }

    private void broadcast(String m) { for(PrintWriter p : clients) p.println(m); }
    public static void main(String[] args) { new ChatServerFrame().setVisible(true); }
}