package com.esteban.socialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.models.Message;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.esteban.socialmedia.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {

    Context context;
    UserProvider mUsersProvider;
    AuthProvider mAuthProvider;

    public MessagesAdapter(FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String messageId = documentSnapshot.getId();
        holder.textViewMessage.setText(message.getMessage());

        String relativeTime = RelativeTime.timeFormatAMPM(message.getTimestamp(), context);
        holder.textViewDate.setText(relativeTime);

        if (message.getIdSender().equals(mAuthProvider.getUid())) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150, 0, 0, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 0, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageViewViewed.setVisibility(View.VISIBLE);
            holder.textViewMessage.setTextColor(context.getResources().getColor(R.color.dark_blue));
            holder.textViewDate.setTextColor(context.getResources().getColor(R.color.dark_blue));
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 150, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 30, 20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_ligth));
            holder.imageViewViewed.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(context.getResources().getColor(R.color.black));
            holder.textViewDate.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(message.isViewed()){
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_ligth);
        } else {
            holder.imageViewViewed.setImageResource(R.drawable.icon_check_blue);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewDate;
        ImageView imageViewViewed;
        LinearLayout linearLayoutMessage;
        View viewHolder;

        public ViewHolder(@NonNull View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDate = view.findViewById(R.id.textViewDateMessage);
            imageViewViewed = view.findViewById(R.id.imageViewViewedMessage);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);
            viewHolder = view;
        }
    }
}
