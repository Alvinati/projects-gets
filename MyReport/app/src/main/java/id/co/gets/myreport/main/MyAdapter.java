package id.co.gets.myreport.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.co.gets.myreport.R;
import id.co.gets.myreport.network.JSONParser;


public  class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final List<JSONParser.InvoiceItem> mValues;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_invoice, parent, false);

       return new MyViewHolder(view);
    }

    public MyAdapter(List<JSONParser.InvoiceItem> myDataset) {
        mValues = myDataset;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONParser.InvoiceItem item = (JSONParser.InvoiceItem) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, InvoiceDetailActivty.class);
            intent.putExtra("ID", item.idInvoice);
            context.startActivity(intent);
        }
    };


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);

        String date = mValues.get(position).tglInvoice;
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            Log.e("error parsing date", e.getMessage());
        }
        spf = new SimpleDateFormat("EEEE, dd MMM yyyy");
        date = spf.format(newDate);

        holder.mIdInvoice.setText(mValues.get(position).idInvoice);
        holder.mNoInvoice.setText(mValues.get(position).nomorInvoice);
        holder.mTglInvoice.setText(date);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView mTglInvoice;
        final TextView mIdInvoice;
        final TextView mNoInvoice;

        public MyViewHolder(View v){
           super(v);

           mTglInvoice = v.findViewById(R.id.list_tanggal);
           mNoInvoice = v.findViewById(R.id.nomor_invoice);
           mIdInvoice = v.findViewById(R.id.invoice_id);
        }
    }




}


