package Model;

import java.sql.Date;

public class Kho {

    private String maKho;
    private String tenKho;
    private String Diachi;

    public Kho(String maKho, String tenKho, String Diachi) {
        this.maKho = maKho;
        this.tenKho = tenKho;
        this.Diachi = Diachi;
    }
   public Kho() {
    }
  
    public String getMaKho() {
        return maKho;
    }

    public void setMaKho(String maKho) {
        this.maKho = maKho;
    }

    public String getTenKho() {
        return tenKho;
    }

    public void setTenKho(String tenKho) {
        this.tenKho = tenKho;
    }

    public String getDiachi() {
        return Diachi;
    }

    public void setDiachi(String Diachi) {
        this.Diachi = Diachi;
    }

 

  

   
}
