/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author HP
 */
public class NhaCungCap {
    private String Manhacungcap;
    private String Tennhacungcap;
    private String SodienThoai;
    private String  Email;
    private String DiaChi;

    public NhaCungCap(String tenNCC, String sdt, String email, String diaChi) {
    }

    public NhaCungCap(String Manhacungcap, String Tennhacungcap, String SodienThoai, String Email, String DiaChi) {
        this.Manhacungcap = Manhacungcap;
        this.Tennhacungcap = Tennhacungcap;
        this.SodienThoai = SodienThoai;
        this.Email = Email;
        this.DiaChi = DiaChi;
    }

    public String getManhacungcap() {
        return Manhacungcap;
    }

    public void setManhacungcap(String Manhacungcap) {
        this.Manhacungcap = Manhacungcap;
    }

    public String getTennhacungcap() {
        return Tennhacungcap;
    }

    public void setTennhacungcap(String Tennhacungcap) {
        this.Tennhacungcap = Tennhacungcap;
    }

    public String getSodienThoai() {
        return SodienThoai;
    }

    public void setSodienThoai(String SodienThoai) {
        this.SodienThoai = SodienThoai;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String DiaChi) {
        this.DiaChi = DiaChi;
    }

    
    
    
}
