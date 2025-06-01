package com.example.moviebooking.Trailer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviebooking.R;

import java.util.Calendar;

public class MembershipActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etDOB;
    private RadioGroup rgGender;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);

        // Date picker cho DOB
        etDOB.setOnClickListener(v -> showDatePicker());

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            int selectedGenderId = rgGender.getCheckedRadioButtonId();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(selectedGenderId);
            String gender = selectedGender.getText().toString();

            String cardCode = "CINES" + (int)(Math.random() * 90000 + 10000);

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.icon_success)
                    .setTitle("Registration Successful!")
                    .setMessage("Thank you, " + name + "!\n\nYour membership card code is: " + cardCode +
                            "\n\nPlease visit the nearest cinema to receive your physical card.\n\n⚠️ Please do not share this code with anyone to protect your membership.")
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // mặc định 18 tuổi
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) ->
                        etDOB.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        dpd.show();
    }
}
