/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author HP
 */
public class PhongBan {
    String MaPB ;
    String TenPB ;
    String Diachi ;
    String SoDienThoai ;
    String Email ;

    public PhongBan(String MaPB, String TenPB, String Diachi, String SoDienThoai, String Email) {
        this.MaPB = MaPB;
        this.TenPB = TenPB;
        this.Diachi = Diachi;
        this.SoDienThoai = SoDienThoai;
        this.Email = Email;
    }

    public PhongBan() {
    }

    public String getMaPB() {
        return MaPB;
    }

    public void setMaPB(String MaPB) {
        this.MaPB = MaPB;
    }

    public String getTenPB() {
        return TenPB;
    }

    public void setTenPB(String TenPB) {
        this.TenPB = TenPB;
    }

    public String getDiachi() {
        return Diachi;
    }

    public void setDiachi(String Diachi) {
        this.Diachi = Diachi;
    }

    public String getSoDienThoai() {
        return SoDienThoai;
    }

    public void setSoDienThoai(String SoDienThoai) {
        this.SoDienThoai = SoDienThoai;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
    
    
    
}
