package com.example.donation_drive_app;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.donation_drive_app.databinding.ActivityUserBinding; // Update to use ActivityUserBinding

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding; // Update to ActivityUserBinding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view using the new activity_user layout file
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find the BottomNavigationView from the binding
        BottomNavigationView navView = binding.navView; // No need for findViewById

        // Set up the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_user);

        // Set up the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(navView, navController);
    }
}
