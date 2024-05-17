package com.esteban.socialmedia.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.activities.EditProfileActivity;
import com.esteban.socialmedia.adapters.MyPostAdapter;
import com.esteban.socialmedia.adapters.PostsAdapter;
import com.esteban.socialmedia.models.Post;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.PostProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    View mView;
    LinearLayout mlinearLayoutEditProfile;
    TextView mTextViewUserName;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    TextView mTextViewPostExists;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    RecyclerView mRecyclerView;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    MyPostAdapter mAdapter;
    ListenerRegistration mListener;


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mlinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewUserName = mView.findViewById(R.id.textViewUsername);
        mTextViewPhone = mView.findViewById(R.id.textViewPhone);
        mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
        mTextViewPostExists = mView.findViewById(R.id.textViewPostExist);
        mImageViewCover = mView.findViewById(R.id.imageViewCoverP);
        mCircleImageProfile = mView.findViewById(R.id.circleImageP);
        mRecyclerView = mView.findViewById(R.id.recyclerViewMyPost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mlinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        getUser();
        getPostNumber();
        checkIfExistsPost();
        return mView;
    }

    private void checkIfExistsPost() {
        mListener = mPostProvider.getPostByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        mAdapter = new MyPostAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int numberPosts = queryDocumentSnapshots.size();
            mTextViewPostNumber.setText(String.valueOf(numberPosts));
        });
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
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
}