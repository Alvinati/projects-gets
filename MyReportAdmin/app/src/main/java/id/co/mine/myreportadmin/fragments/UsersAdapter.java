package id.co.mine.myreportadmin.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.List;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;


public  class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private final List<JSONParser.UserItem> mValues;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_users, parent, false);

       return new MyViewHolder(view);
    }

    public UsersAdapter(List<JSONParser.UserItem> myDataset) {
        mValues = myDataset;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONParser.UserItem item = (JSONParser.UserItem) view.getTag();
        }
    };

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,  int position) {
        holder.itemView.setTag(mValues.get(position));
        final int pos = position;
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                JSONParser.UserItem userItem = mValues.get(pos);
                setItem(userItem);
                return false;
            }
        });

        holder.mNamaLengkap.setText(mValues.get(position).namaLengkap);
        holder.mCompany.setText(mValues.get(position).namaVendor);
        holder.mTanggal.setText(mValues.get(position).tglInput);
        holder.mEmail.setText(mValues.get(position).email);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(mValues.get(position).idUser);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        final TextView mNamaLengkap;
        final TextView mCompany;
        final TextView mTanggal;
        final TextView mEmail;

        public MyViewHolder(View v){
           super(v);
           v.setOnCreateContextMenuListener(this);
           mNamaLengkap = v.findViewById(R.id.list_users_nama);
           mCompany = v.findViewById(R.id.list_users_company);
           mTanggal = v.findViewById(R.id.list_users_tanggal);
           mEmail = v.findViewById(R.id.list_users_email);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, R.id.action_reset, 0, "Reset");//groupId, itemId, order, title
            menu.add(0, R.id.action_lihat, 0, "Lihat");
            menu.add(0, R.id.action_delete, 0, "Hapus");
        }

    }

        private JSONParser.UserItem item;

        public JSONParser.UserItem getPosition() {
            return item;
        }

        public void setItem(JSONParser.UserItem item) {
            this.item = item;
        }

}


