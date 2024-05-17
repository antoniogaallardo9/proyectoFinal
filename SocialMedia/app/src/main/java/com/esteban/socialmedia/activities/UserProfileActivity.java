package com.esteban.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.adapters.MyPostAdapter;
import com.esteban.socialmedia.models.Post;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.PostProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.esteban.socialmedia.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    LinearLayout mlinearLayoutEditProfile;
    TextView mTextViewUserName;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    TextView mTextViewPostExists;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    FloatingActionButton mFabChat;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    MyPostAdapter mPostAdapter;

    String mExtraIdUser;

    ListenerRegistration mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //mlinearLayoutEditProfile = findViewById(R.id.linearLayoutEditProfile);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewUserName = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mTextViewPostExists = findViewById(R.id.textViewPostExist);
        mImageViewCover = findViewById(R.id.imageViewCoverP);
        mCircleImageProfile = findViewById(R.id.circleImageP);
        mRecyclerView = findViewById(R.id.recyclerViewMyPost);
        mToolbar = findViewById(R.id.toolbar);
        mFabChat = findViewById(R.id.fabChat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();


        mExtraIdUser = getIntent().getStringExtra("id");
        if (mAuthProvider.getUid().equals(mExtraIdUser)) {
            mFabChat.setEnabled(false);
        }

        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity();
            }
        });
        getUser();
        getPostNumber();
        checkIfExistsPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mExtraIdUser);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        mPostAdapter = new MyPostAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, UserProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, UserProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }

    ///PENDIENTE POR REVISAR
    private void checkIfExistsPost() {
        mListener = mPostProvider.getPostByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberPosts = queryDocumentSnapshots.size();
                    if (numberPosts > 0) {
                        mTextViewPostExists.setText("Publicaciones");
                        mTextViewPostExists.setTextColor(Color.BLUE);
                    } else {
                        mTextViewPostExists.setText("No hay publicaciones");
                        mTextViewPostExists.setTextColor(Color.GRAY);
                    }
                } else {
                    // Manejar el caso en que queryDocumentSnapshots sea nulo
                    mTextViewPostExists.setText("Error al obtener publicaciones");
                    mTextViewPostExists.setTextColor(Color.RED);
                }
            }
        });
    }


    private void getPostNumber() {
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberPosts = queryDocumentSnapshots.size();
            mTextViewPostNumber.setText(String.valueOf(numberPosts));
        });
    }

    private void getUser() {
        mUserProvider.getUser(mExtraIdUser).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("email")) {
                    String email = documentSnapshot.getString("email");
                    mTextViewEmail.setText(email);
                }
                if (documentSnapshot.contains("telefono")) {
                    String phone = documentSnapshot.getString("telefono");
                    mTextViewPhone.setText(phone);
                }
                if (documentSnapshot.contains("username")) {
                    String username = documentSnapshot.getString("username");
                    mTextViewUserName.setText(username);
                }
                if (documentSnapshot.contains("image_profile")) {
                    String imageProfile = documentSnapshot.getString("image_profile");
                    if (imageProfile != null) {
                        if (!imageProfile.isEmpty()) {
                            Picasso.get().load(imageProfile).into(mCircleImageProfile);
                        }
                    }
                }
                if (documentSnapshot.contains("image_cover")) {
                    String imageCover = documentSnapshot.getString("image_cover");
                    if (imageCover != null) {
                        if (!imageCover.isEmpty()) {
                            Picasso.get().load(imageCover).into(mImageViewCover);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}