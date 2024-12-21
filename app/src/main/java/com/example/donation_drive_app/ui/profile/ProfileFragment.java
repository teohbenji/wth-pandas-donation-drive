package com.example.donation_drive_app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.example.donation_drive_app.R;

public class ProfileFragment extends Fragment {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private EditText passwordEditText;
    private ImageView profileImageView;
    private Button saveButton;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views
        usernameEditText = root.findViewById(R.id.username);
        emailEditText = root.findViewById(R.id.email);
        addressEditText = root.findViewById(R.id.address);
        passwordEditText = root.findViewById(R.id.password);
        profileImageView = root.findViewById(R.id.profileImage);
        saveButton = root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveProfile(v);  // Calls your onSaveProfile method when the button is clicked
            }
        });
        // Pre-populate with current user data (if available)
        populateProfileData();

        return root;
    }

    private void populateProfileData() {
        // This method would get data from your user model, shared preferences, or database
        // For now, we're just populating with some example data.
        usernameEditText.setText("john_doe");
        emailEditText.setText("john.doe@example.com");
        addressEditText.setText("123 Main St, City, Country");
        passwordEditText.setText("password123");
    }

    // Method to handle saving the profile data
    public void onSaveProfile(View view) {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Save the data (in a database, shared preferences, etc.)
        // Example: saveToDatabase(username, email, address, password);

        // Provide feedback to the user (e.g., Toast or Snackbar)
        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
    }

    // Method to handle profile image click (change profile picture)
    public void onProfileImageClick(View view) {
        // Logic to let the user pick a new profile image (e.g., from gallery or camera)
        // This could be done by starting an activity or using an image picker library.
        // For now, we'll just show a toast to indicate the action.
        Toast.makeText(getContext(), "Change profile image", Toast.LENGTH_SHORT).show();
    }
}