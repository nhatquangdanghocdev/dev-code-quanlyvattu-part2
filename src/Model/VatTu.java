package Model;

public class VatTu {

    String Mavattu;
    String TenVattu;
    String Donvitinh;
    int Soluong;
    String MaKho;

    public VatTu() {
    }

    public VatTu(String Mavattu, String TenVattu, String Donvitinh, int Soluong, String MaKho) {
        this.Mavattu = Mavattu;
        this.TenVattu = TenVattu;
        this.Donvitinh = Donvitinh;
        this.Soluong = Soluong;
        this.MaKho = MaKho;
    }

    public String getMavattu() {
        return Mavattu;
    }

    public void setMavattu(String Mavattu) {
        this.Mavattu = Mavattu;
    }

    public String getTenVattu() {
        return TenVattu;
    }

    public void setTenVattu(String TenVattu) {
        this.TenVattu = TenVattu;
    }

    public String getDonvitinh() {
        return Donvitinh;
    }

    public void setDonvitinh(String Donvitinh) {
        this.Donvitinh = Donvitinh;
    }

    public int getSoluong() {
        return Soluong;
    }

    public void setSoluong(int Soluong) {
        this.Soluong = Soluong;
    }

    public String getMaKho() {
        return MaKho;
    }

    public void setMaKho(String MaKho) {
        this.MaKho = MaKho;
    }

    
}
