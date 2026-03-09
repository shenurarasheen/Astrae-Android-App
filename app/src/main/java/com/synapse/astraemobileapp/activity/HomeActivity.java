package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private ActivityHomeBinding binding;
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView sideNavigation;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        toolbar = binding.toolbar;
        drawerLayout = binding.drawerLayout;
        sideNavigation = binding.sideNavigationView;
        bottomNavigationView = binding.bottomNavigationView;

        setSupportActionBar(toolbar);

        // handle toggle of the side nav
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });


        sideNavigation.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        Menu sideNavMenu = sideNavigation.getMenu();
        Menu bottomNavMenu = bottomNavigationView.getMenu();

        for (int i = 0; i < sideNavMenu.size(); i++) {
            sideNavMenu.getItem(i).setChecked(false);
        }

        for (int i = 0; i < bottomNavMenu.size(); i++) {
            bottomNavMenu.getItem(i).setChecked(false);
        }

        if (itemId == R.id.side_nav_home || itemId == R.id.bottom_nav_home) {

        } else if (itemId == R.id.side_nav_profile || itemId == R.id.bottom_nav_profile) {

            if (auth.getCurrentUser() == null) {
                updateActivity(SignInActivity.class, true);
            } else {
                updateActivity(ProfileActivity.class, true);
            }

            sideNavMenu.findItem(R.id.side_nav_profile).setChecked(true);

        } else if (itemId == R.id.side_nav_category || itemId == R.id.bottom_nav_category) {

        } else if (itemId == R.id.side_nav_cart || itemId == R.id.bottom_nav_cart) {

            if (auth.getCurrentUser() == null) {
                updateActivity(SignInActivity.class, true);
            } else {
                updateActivity(CartActivity.class, true);
            }

            sideNavMenu.findItem(R.id.side_nav_cart).setChecked(true);

        } else if (itemId == R.id.side_nav_orders) {

        } else if (itemId == R.id.side_nav_settings) {

        } else if (itemId == R.id.side_nav_add_product) {

            if (auth.getCurrentUser() == null) {
                updateActivity(SignInActivity.class, true);
            } else {
                updateActivity(AddProductActivity.class, true);
            }

        } else if (itemId == R.id.side_nav_selling_products) {

        } else if (itemId == R.id.side_nav_login) {
            updateActivity(SignInActivity.class, false);
        } else if (itemId == R.id.side_nav_logout) {

        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void updateActivity(Class<? extends AppCompatActivity> activityClass, boolean needToBack) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        startActivity(intent);
        if (!needToBack) {
            finish();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}