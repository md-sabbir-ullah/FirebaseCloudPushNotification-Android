package esenteq.project.pushnotificationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    //1.Notification Channel
    //2.Notification Builder
    //3.Notification Manager
    public static final String CHANNEL_ID = "push_notification";
    private static final String CHANNEL_NAME = "Push Notification";
    private static final String CHANNEL_DESC = "Push Notification Application";

    private EditText et_email, et_pass;
    private ProgressBar progbar_login;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        et_email = findViewById(R.id.et_emailID);
        et_pass = findViewById(R.id.et_passwordID);
        progbar_login = findViewById(R.id.progbar_loginpageID);
        progbar_login.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_signupID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


    }

    private void createUser(){
        final String email = et_email.getText().toString().trim();
        final String pass = et_pass.getText().toString().trim();
        if(email.isEmpty()){
            et_email.setError("Email Required!");
            et_email.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            et_pass.setError("Password Required!");
            et_pass.requestFocus();
            return;
        }
        if(pass.length()<6){
            et_pass.setError("Atleast 6 char required!");
            et_pass.requestFocus();
            return;
        }

        progbar_login.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startProfileActivity();
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                userLogin(email,pass);
                            }
                            else{
                                progbar_login.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void userLogin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startProfileActivity();
                        }
                        else{
                            progbar_login.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            startProfileActivity();
        }
    }

    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
