package com.esteban.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.esteban.socialmedia.R;
import com.esteban.socialmedia.activities.PostDetailActivity;
import com.esteban.socialmedia.models.Like;
import com.esteban.socialmedia.models.Post;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.LikesProvider;
import com.esteban.socialmedia.providers.PostProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {
    Context context;
    UserProvider mUserProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    TextView textViewNumberFilter;
    ListenerRegistration mListener;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        textViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if (textViewNumberFilter != null) {
            int numberFilter = getSnapshots().size();
            textViewNumberFilter.setText(String.valueOf(numberFilter));
        }


        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        holder.textViewDescription.setText(post.getDescription());

        // Verificar si la URL de la imagen no está vacía
        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            Glide.with(context)
                    .load(post.getImage1())
                    .transform(new GranularRoundedCorners(30, 30, 0, 0))
                    .placeholder(R.drawable.videojuegos)
                    .error(R.drawable.videojuegos)
                    .into(holder.imageViewPost);
        } else {
            // Si la URL de la imagen está vacía, establecer una imagen de marcador de posición o dejarla vacía
            holder.imageViewPost.setImageResource(R.drawable.videojuegos);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdPost(postId);
                //Pendiente!!!!
                like.setIdUser(mAuthProvider.getUid());
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUserInfo(post.getId(), holder);
        getNumberLikesByPost(postId, holder);
        checkisExistLike(postId, mAuthProvider.getUid(), holder);
    }

    private void getNumberLikesByPost(String idPost,final ViewHolder holder){
        mListener = mLikesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberLikes = queryDocumentSnapshots.size();
                    holder.textViewLikes.setText(String.valueOf(numberLikes) + " Likes");
                } else {
                    // Manejar el caso cuando queryDocumentSnapshots es nulo
                    Log.e("TAG", "Error al obtener los likes del post: " + error.getMessage());
                }
            }
        });
    }


    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikesByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_ligth);
                    mLikesProvider.delete(idLike);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                    mLikesProvider.create(like);
                }
            }
        });
    }

    private void checkisExistLike(String idPost, String idUser, final ViewHolder holder) {
        mLikesProvider.getLikesByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                } else {
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_ligth);
                }
            }
        });
    }


    private void getUserInfo(final String idUser, ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if(documentSnapshot.contains("username")){
                    String username = documentSnapshot.getString("username");
                    holder.textViewUsername.setText("By: " + username);
                }
            }
        });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewUsername;
        TextView textViewLikes;
        ImageView imageViewLike;
        ImageView imageViewPost;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            textViewLikes = view.findViewById(R.id.textViewLikes);
            imageViewLike = view.findViewById(R.id.imageViewLike);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            viewHolder = view;
        }
    }
}
