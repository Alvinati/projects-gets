package id.co.mine.myreportadmin.models;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import id.co.mine.myreportadmin.R;

public class Model{
    private String url;
    private String id;

    public Model(String url, String id) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url) {
        this.url = url;

    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

}
