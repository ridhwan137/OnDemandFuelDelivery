package my.edu.utem.ftmk.ondemandfueldelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;

public class RecordRVAdapter extends RecyclerView.Adapter<RecordRVAdapter.ViewHolder> {

    private ArrayList<Record> recordArrayList;
    private Context context;

    // creating constructor for our adapter class
    public RecordRVAdapter(ArrayList<Record> recordArrayList, Context context) {
        this.recordArrayList = recordArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.record_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Record record = recordArrayList.get(position);
        holder.tvRecordFuelType.setText(record.getTypeoffuel());
        holder.tvRecordFuelPrice.setText("RM" +record.getPrice());
        holder.tvRecordDate.setText(record.getDate().toString());

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return recordArrayList.size();
    }

    public interface ItemClickListener {
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView tvRecordFuelType;
        private final TextView tvRecordFuelPrice;
        private final TextView tvRecordDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            tvRecordFuelType = itemView.findViewById(R.id.tvRecordFuelType);
            tvRecordFuelPrice = itemView.findViewById(R.id.tvRecordFuelPrice);
            tvRecordDate = itemView.findViewById(R.id.tvRecordDate);

        }
    }

    // convenience method for getting data at click position
    public Record getItem(int id) {
        return recordArrayList.get(id);
    }

}
