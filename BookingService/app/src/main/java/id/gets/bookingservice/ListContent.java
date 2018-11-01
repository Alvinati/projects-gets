package id.gets.bookingservice;


import java.io.Serializable;

public class ListContent implements Serializable {

    private String mtanggalBook;
    private String mServis;
    private String mMobil;
    private String mUid;
    private String mStatus;
    private String mIduser;
    private String mHargaServis;
    private String mtotalBiaya;

    public ListContent(String tanggal, String servis, String mobil, String uid, String status, String iduser,
                        String hargaServis){
        mtanggalBook = tanggal;
        mServis = servis;
        mMobil = mobil;
        mUid = uid;
        mStatus = status;
        mIduser = iduser;
        mHargaServis = hargaServis;
    }
    public String getMtanggalBook(){
        return mtanggalBook;
    }
    public String getmServis(){
        return mServis;
    }
    public String getmMobil() {
        return mMobil;
    }
    public String getmUid() {
        return mUid;
    }
    public String getmStatus() {
        return mStatus;
    }
    public String getmIduser() {
        return mIduser;
    }
    public String getmHargaServis() {return mHargaServis;}
}




