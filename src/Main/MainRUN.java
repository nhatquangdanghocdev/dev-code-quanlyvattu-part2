package Main;

import Login.Login;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRUN {
    public static void main(String[] args) {
        // Cài đặt giao diện FlatLaf
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("dev.Menu");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatLightLaf.setup();
        // Chạy giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        // Sử dụng ExecutorService cho các tác vụ nền
        ExecutorService executor = Executors.newFixedThreadPool(4);
        // Tác vụ chạy nền (background task)
        Runnable backgroundTask = () -> {
            // Công việc nặng, ví dụ như xử lý dữ liệu, tải file,...
            System.out.println("Đang Khởi Chạy Chương Trình Quản Lý Vật Tư: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000); // Mô phỏng tác vụ mất thời gian
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        // Gửi các tác vụ vào ExecutorService
        for (int i = 0; i < 4; i++) {
            executor.execute(backgroundTask);
        }
        // Đóng ExecutorService sau khi hoàn tất
        executor.shutdown();
    }
}
