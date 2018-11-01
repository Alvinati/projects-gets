package id.co.mine.myreportadmin.fragments;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import id.co.mine.myreportadmin.main.MainActivity;


public  class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private final List<JSONParser.NotifItem> mValues;
    private final Fragment userFragment;
    private Context context;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

       return new MyViewHolder(view);
    }

    public NotificationAdapter(List<JSONParser.NotifItem> myDataset, Fragment fragment) {
        this.mValues = myDataset;
        this.userFragment = fragment;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONParser.NotifItem item = (JSONParser.NotifItem) view.getTag();
            context = view.getContext();
            updateReadGET(item.idNotif, context);
            view.findViewById(R.id.list_notif_isread).setVisibility(View.INVISIBLE);

            if (item.tipePesan.contains("Reset")) {
                android.support.v4.app.FragmentTransaction transaction = ((AppCompatActivity)context)
                        .getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, userFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                readNotif(context, item.tipePesan, item.pesan, item.tglInput, item.namaVendor, item.jamInput);
            }

        }
    };

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
        final int pos = position;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                JSONParser.NotifItem notifItem = mValues.get(pos);
                setItem(notifItem);
                return false;
            }
        });

        holder.mJudulNotif.setText(mValues.get(position).tipePesan);
        holder.mPesanNotif.setText(mValues.get(position).pesan);
        if(mValues.get(position).tipePesan.contains("EMERGENCY")) {
            holder.mIconNotif.setImageResource(R.drawable.ic_siren);

            if(mValues.get(position).pesan.contains("Kebakaran")){
                holder.mJudulNotif.setTextColor(Color.RED);
            } else if(mValues.get(position).pesan.contains("Teror")){
                holder.mJudulNotif.setTextColor(Color.rgb(80, 9, 86));
            } else if(mValues.get(position).pesan.contains("Perampokan")) {
                holder.mJudulNotif.setTextColor(Color.rgb(224, 118, 4));
            } else if(mValues.get(position).pesan.contains("Kebanjiran")) {
                holder.mJudulNotif.setTextColor(Color.BLUE);
            } else if(mValues.get(position).pesan.contains("Gempa")) {
                holder.mJudulNotif.setTextColor(Color.rgb(51, 27, 1));
            } else if(mValues.get(position).pesan.contains("Demo")) {
                holder.mJudulNotif.setTextColor(Color.rgb(255, 238, 0));
            } else if(mValues.get(position).pesan.contains("Listrik")) {
                holder.mJudulNotif.setTextColor(Color.BLACK);
            } else if(mValues.get(position).pesan.contains("ATM")) {
                holder.mJudulNotif.setTextColor(Color.GRAY);
            }

        } else if(mValues.get(position).tipePesan.contains("DAS")) {
            holder.mIconNotif.setImageResource(R.drawable.ic_tap);
        } else {
            holder.mIconNotif.setImageResource(R.drawable.ic_bell_custom);
        }

        if(mValues.get(position).status.equals("1")){
            holder.mIconRead.setVisibility(View.INVISIBLE);
        } else if(mValues.get(position).status.equals("0")){
            holder.mIconRead.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView mJudulNotif;
        final TextView mPesanNotif;
        final ImageView mIconNotif;
        final ImageView mIconRead;

        public MyViewHolder(View v){
           super(v);
           v.setOnCreateContextMenuListener(this);
           mJudulNotif = v.findViewById(R.id.list_notif_judul);
           mPesanNotif = v.findViewById(R.id.list_notif_message);
           mIconNotif = v.findViewById(R.id.list_notif_icon);
           mIconRead = v.findViewById(R.id.list_notif_isread);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, R.id.action_delete, 0, "Hapus");//groupId, itemId, order, title
        }
    }


    private Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private void updateReadGET(String id, Context context){
        RequestQueue queue = SingletonRequestQueue.getInstance(context).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate?" +"id=" +id+"&identifier="+"notif";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("update status notif", response);
            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        queue.add(stringRequest);
    }

    private void readNotif(Context context, String tipePesan, String pesan, String tanggal, String vendor, String jam) {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
        View notifDetail = inflater.inflate(R.layout.notif_detail, null);

        TextView jamTanggal = notifDetail.findViewById(R.id.notif_detail_tanggal);
        String waktu = tanggal + " " + jam;
        jamTanggal.setText(waktu);

        TextView pengirim = notifDetail.findViewById(R.id.notif_detail_vendor);
        pengirim.setText(vendor);

        TextView isiPesan = notifDetail.findViewById(R.id.notif_detail_pesan);
        isiPesan.setText(pesan);

        alertDialog.setTitle(tipePesan);
        alertDialog.setView(notifDetail);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private JSONParser.NotifItem item;

    public JSONParser.NotifItem getPosition() {
        return item;
    }

    public void setItem(JSONParser.NotifItem item) {
        this.item = item;
    }
}


