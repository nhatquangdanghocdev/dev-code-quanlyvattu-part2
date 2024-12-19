
package Model;

import java.util.Date;

public class PhieuXuat {
    String maPhieuXuat_PX;
    String ngayXuat;
    String maKho;
    String maPhongBan;

    public PhieuXuat() {
    }

    public PhieuXuat(String maPhieuXuat_PX, String ngayXuat, String maKho, String maPhongBan) {
        this.maPhieuXuat_PX = maPhieuXuat_PX;
        this.ngayXuat = ngayXuat;
        this.maKho = maKho;
        this.maPhongBan = maPhongBan;
    }

    
    public String getMaPhieuXuat_PX() {
        return maPhieuXuat_PX;
    }

    public void setMaPhieuXuat_PX(String maPhieuXuat_PX) {
        this.maPhieuXuat_PX = maPhieuXuat_PX;
    }

    public String getNgayXuat() {
        return ngayXuat;
    }

    public void setNgayXuat(String ngayXuat) {
        this.ngayXuat = ngayXuat;
    }

    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public String getMaPhongBan() {
        return maPhongBan;
    }

    public void setMaPhongBan(String maPhongBan) {
        this.maPhongBan = maPhongBan;
    }

    
    
}
