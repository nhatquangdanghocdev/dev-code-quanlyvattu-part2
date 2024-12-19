package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupportForm extends JFrame {

    // Các thành phần của form
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextArea txtMessage;
    private JButton btnSubmit;
    private JLabel lblStatus;

    public SupportForm() {
        setTitle("Form Hỗ Trợ Khách Hàng");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị form giữa màn hình
        setLayout(new BorderLayout());

        // Panel chứa form
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label và TextField: Tên khách hàng
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);

        // Label và TextField: Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);

        // Label và TextArea: Nội dung yêu cầu
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nội dung yêu cầu:"), gbc);
        gbc.gridx = 1;
        txtMessage = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        panel.add(scrollPane, gbc);

        // Nút Gửi
        gbc.gridx = 1; gbc.gridy = 3;
        btnSubmit = new JButton("Gửi yêu cầu");
        panel.add(btnSubmit, gbc);

        // Label trạng thái (hiển thị thông báo)
        lblStatus = new JLabel();
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setForeground(Color.BLUE);

        // Thêm panel và label trạng thái vào frame
        add(panel, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        // Xử lý sự kiện cho nút gửi
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        setVisible(true);
    }

    private void handleSubmit() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String message = txtMessage.getText().trim();

        // Kiểm tra dữ liệu nhập
        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            lblStatus.setText("Vui lòng điền đầy đủ thông tin!");
            lblStatus.setForeground(Color.RED);
        } else if (!email.contains("@")) {
            lblStatus.setText("Email không hợp lệ!");
            lblStatus.setForeground(Color.RED);
        } else {
            lblStatus.setText("Yêu cầu đã được gửi thành công!");
            lblStatus.setForeground(Color.GREEN);
            // Bạn có thể thêm code để gửi dữ liệu đi (ví dụ lưu vào database hoặc gửi email)
            clearForm();
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtMessage.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupportForm());
    }
}
