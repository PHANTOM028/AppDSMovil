package centeno.home.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText usernameEdiText, passwordEdiText;
    Button loginButton;

    TextView timerTextView;

    CountDownTimer countDownTimer;

    int loginAttemps = 0;

    CheckBox saveCredentialsCheckBox;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEdiText = findViewById(R.id.usernameEdiText);
        passwordEdiText = findViewById(R.id.passwordEdiText);
        loginButton = findViewById(R.id.loginButton);
        timerTextView = findViewById(R.id.timerTextView);

        saveCredentialsCheckBox = findViewById(R.id.saveCredentialsCheckBox);
        sharedPreferences = getSharedPreferences("com.enrique.login", MODE_PRIVATE);

        //Cargar las credenciales si existen
        String savedUsername = sharedPreferences.getString("username", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedUsername != null && savedPassword != null) {
            usernameEdiText.setText(savedUsername);
            passwordEdiText.setText(savedPassword);
            saveCredentialsCheckBox.setChecked(true);
        }

        saveCredentialsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String username = usernameEdiText.getText().toString();
                    String password = passwordEdiText.getText().toString();

                    if (!username.isEmpty() && !password.isEmpty()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();
                    } else {
                        Toast.makeText(MainActivity.this, "Por favor ingrese credenciales nates de guardar.", Toast.LENGTH_SHORT).show();
                        saveCredentialsCheckBox.setChecked(false); //Desactivar el checkbox
                    }
                } else {
                    //Limpiar las credenciales
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlelogin();
            }
        });
    }

    //FUNCION PARA MANEJAR EL LOGIN
    private void handlelogin() {
        String username = usernameEdiText.getText().toString();
        String password = passwordEdiText.getText().toString();

        //Validación de usuario y contraseña
        if ("Centeno12345".equals(password)) {
            Toast.makeText(this, "Inicio de sesión correcto!", Toast.LENGTH_SHORT).show();
            loginAttemps = 0; //Reiniciar intentos

            //Inciamos la nueva activity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("Nombre", username);
            startActivity(intent);
            finish();

        } else {
            loginAttemps++;
            if (loginAttemps >= 3) {
                //Toast.makeText(this, "Has excedido el numero de intentos. Espera 10 segundos.", Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(false);

                //Desbloquear boton despues de 10 segundos
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        loginButton.setEnabled(true);
//                        loginAttemps = 0;
//                    }
//                }, 10000);

                startCountDown();

            } else {
                passwordEdiText.setError("Usuario o contraseña incorrectas. Intentos " + loginAttemps + " de 3");
            }
        }

    }

    private void startCountDown() {
        timerTextView.setVisibility(View.VISIBLE);
        if (countDownTimer != null){
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                timerTextView.setText(String.valueOf(l / 1000) + "s restantes");
            }

            @Override
            public void onFinish() {
                timerTextView.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                loginAttemps = 0;
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cancelar contador
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}