package my.edu.utem.ftmk.ondemandfueldelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerificationRVAdapter extends RecyclerView.Adapter<VerificationRVAdapter.ViewHolder>{

    private ArrayList<User> userArrayList;
    private Context context;
    private VerificationRVAdapter.ItemClickListener mClickListener;

    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    // creating constructor for our adapter class
    public VerificationRVAdapter(ArrayList<User> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public VerificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new VerificationRVAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.verify_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VerificationRVAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        User user = userArrayList.get(position);

        String pictureURL = null;
        pictureURL = user.getPictureURL();
        if(pictureURL == ""){

        }
        else{
            Picasso.with(context).load(pictureURL).into(holder.ivProfilePic);
        }

        holder.tvName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvDate.setText(user.getRequestDate());
        holder.tvTime.setText(user.getRequestTime());

    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return userArrayList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // creating variables for our text views.
        private final CircleImageView ivProfilePic;
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvDate;
        private final TextView tvTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(VerificationRVAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // convenience method for getting data at click position
    public User getItem(int id) {
        return userArrayList.get(id);
    }

}
