package com.esteban.socialmedia.InicioApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.activities.PostActivity;
import com.esteban.socialmedia.fragments.ChatsFragment;
import com.esteban.socialmedia.fragments.FiltersFragment;
import com.esteban.socialmedia.fragments.HomeFragment;
import com.esteban.socialmedia.fragments.ProfileFragment;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.TokenProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.esteban.socialmedia.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InicioApp extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TokenProvider mtokenProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mtokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();
        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, InicioApp.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, InicioApp.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.itemHome) {
                        openFragment(new HomeFragment());
                    } else if (item.getItemId() == R.id.itemFiltrar) {
                        openFragment(new FiltersFragment());
                    } else if (item.getItemId() == R.id.itemChat) {
                        openFragment(new ChatsFragment());
                    } else if (item.getItemId() == R.id.itemPerfil) {
                        openFragment(new ProfileFragment());
                    }
                    return true;
                }
            };

    private void createToken(){
        mtokenProvider.create(mAuthProvider.getUid());
    }

}
