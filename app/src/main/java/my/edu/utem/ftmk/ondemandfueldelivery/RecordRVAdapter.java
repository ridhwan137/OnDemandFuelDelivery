package my.edu.utem.ftmk.ondemandfueldelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecordRVAdapter extends RecyclerView.Adapter<RecordRVAdapter.ViewHolder> {

    private ArrayList<Record> recordArrayList;
    private RecordRVAdapter.ItemClickListener mClickListener;
    private Context context;
    private AlphaAnimation buttonClicked = new AlphaAnimation(1F,0.8F);

    // creating constructor for our adapter class
    public RecordRVAdapter(ArrayList<Record> recordArrayList, Context context) {
        this.recordArrayList = recordArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new RecordRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.record_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Record record = recordArrayList.get(position);
        holder.tvRecordFuelType.setText(record.getFuelType());
        holder.tvRecordFuelPrice.setText(record.getFuelPrice());
        //holder.tvRecordDate.setText(record.getDate().toString());

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return recordArrayList.size();
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // creating variables for our text views.
        private final TextView tvRecordFuelType;
        private final TextView tvRecordFuelPrice;
        //private final TextView tvRecordDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            tvRecordFuelType = itemView.findViewById(R.id.tvRecordFuelType);
            tvRecordFuelPrice = itemView.findViewById(R.id.tvRecordFuelPrice);
            //tvRecordDate = itemView.findViewById(R.id.tvRecordDate);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            view.startAnimation(buttonClicked);
        }
    }

    // allows clicks events to be caught
    void setClickListener(RecordRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public Record getItem(int id) {
        return recordArrayList.get(id);
    }
}
