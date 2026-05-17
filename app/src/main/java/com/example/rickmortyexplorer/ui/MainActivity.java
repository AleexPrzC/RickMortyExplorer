package com.example.rickmortyexplorer.ui;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rickmortyexplorer.databinding.ActivityMainBinding;
import com.example.rickmortyexplorer.model.BackendTipResponse;
import com.example.rickmortyexplorer.model.CharacterItem;
import com.example.rickmortyexplorer.model.DeviceStatus;
import com.example.rickmortyexplorer.rest.BackendRetrofitClient;
import com.example.rickmortyexplorer.rest.RetrofitClient;
import com.example.rickmortyexplorer.rest.api.BackendApiService;
import com.example.rickmortyexplorer.rest.api.RickMortyApiService;
import com.example.rickmortyexplorer.ui.adapter.CharacterAdapter;
import com.example.rickmortyexplorer.util.BackendTipCache;
import com.example.rickmortyexplorer.util.BatteryUtils;
import com.example.rickmortyexplorer.util.CharacterCache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String DATABASE_URL =
            "https://rickmortyexplorercuyn-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final float SHAKE_THRESHOLD = 18f;
    private static final long SHAKE_COOLDOWN_MS = 2500L;

    private ActivityMainBinding binding;
    private RickMortyApiService apiService;
    private BackendApiService backendApiService;
    private CharacterAdapter adapter;
    private CharacterCache characterCache;
    private BackendTipCache backendTipCache;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private DeviceStatus currentDeviceStatus;
    private long lastShakeTime;
    private final List<CharacterItem> allCharacters = new ArrayList<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getRetrofit().create(RickMortyApiService.class);
        backendApiService = BackendRetrofitClient.getRetrofit().create(BackendApiService.class);
        characterCache = new CharacterCache(this);
        backendTipCache = new BackendTipCache(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        adapter = new CharacterAdapter(character -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_CHARACTER_ID, character.getId());
            startActivity(intent);
        });

        binding.charactersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.charactersRecyclerView.setAdapter(adapter);
        binding.retryButton.setOnClickListener(view -> loadCharacters());
        binding.logoutButton.setOnClickListener(view -> logout());
        binding.favoritesButton.setOnClickListener(view ->
                startActivity(new Intent(this, FavoritesActivity.class))
        );
        binding.refreshTipButton.setOnClickListener(view -> loadBackendTip());
        binding.saveDeviceStatusButton.setOnClickListener(view -> saveDeviceStatus());
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                filterCharacters(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        showCurrentUser();
        updateDeviceStatus();

        loadCharacters();
        loadBackendTip();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadCharacters() {
        updateConnectivityIndicator();
        if (!isOnline()) {
            showCachedCharacters("Modo offline: mostrando la ultima lista guardada.");
            return;
        }

        showLoading();
        compositeDisposable.add(
                apiService.getCharacters(1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    List<CharacterItem> characters = response.getResults();
                                    characterCache.saveCharacters(characters);
                                    submitCharacters(characters);
                                    binding.connectivityText.setText("Modo online: lista actualizada y guardada en cache.");
                                    showContent();
                                },
                                throwable -> showCachedCharacters(
                                        "Sin respuesta de la API: mostrando cache local."
                                )
                        )
        );
    }

    private void loadBackendTip() {
        if (!isOnline()) {
            showCachedBackendTip("Backend Flask no consultado en modo offline.");
            return;
        }

        binding.backendTipText.setText("Backend Flask: cargando consejo...");
        compositeDisposable.add(
                backendApiService.getCharacterTip()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showBackendTip,
                                throwable -> showCachedBackendTip(
                                        "Backend Flask no disponible. Arranca Docker o Flask en el puerto 5000."
                                )
                        )
        );
    }

    private void showBackendTip(BackendTipResponse tip) {
        String title = tip.getTitle() == null ? "Backend Flask" : tip.getTitle();
        String message = tip.getMessage() == null ? "" : tip.getMessage();
        String formattedTip = title + ": " + message;
        backendTipCache.saveTip(formattedTip);
        binding.backendTipText.setText(formattedTip);
    }

    private void showCachedBackendTip(String fallbackMessage) {
        String cachedTip = backendTipCache.getTip();
        if (cachedTip == null) {
            binding.backendTipText.setText(fallbackMessage);
            return;
        }
        binding.backendTipText.setText("Consejo cacheado: " + cachedTip);
    }

    private void showCachedCharacters(String statusMessage) {
        List<CharacterItem> cachedCharacters = characterCache.getCharacters();
        if (cachedCharacters.isEmpty()) {
            binding.connectivityText.setText(statusMessage);
            showError("No hay conexion y todavia no existe cache local.");
            return;
        }

        submitCharacters(cachedCharacters);
        binding.connectivityText.setText(statusMessage);
        showContent();
    }

    private void submitCharacters(List<CharacterItem> characters) {
        allCharacters.clear();
        if (characters != null) {
            allCharacters.addAll(characters);
        }
        filterCharacters(binding.searchEditText.getText().toString());
    }

    private void filterCharacters(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.submitList(allCharacters);
            return;
        }

        String normalizedQuery = query.trim().toLowerCase();
        List<CharacterItem> filteredCharacters = new ArrayList<>();
        for (CharacterItem character : allCharacters) {
            String name = character.getName() == null ? "" : character.getName().toLowerCase();
            if (name.contains(normalizedQuery)) {
                filteredCharacters.add(character);
            }
        }
        adapter.submitList(filteredCharacters);
    }

    private void showCurrentUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            binding.userEmailText.setText("Sesion iniciada");
            return;
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        binding.userEmailText.setText(email == null ? "Sesion iniciada" : "Sesion: " + email);
    }

    private void updateDeviceStatus() {
        currentDeviceStatus = BatteryUtils.readDeviceStatus(this);
        binding.deviceStatusText.setText("Contexto: bateria " + currentDeviceStatus.getBatteryLevel()
                + "% | cargando: " + (currentDeviceStatus.isCharging() ? "si" : "no"));
        binding.deviceRecommendationText.setText(currentDeviceStatus.getRecommendation());
    }

    private void saveDeviceStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Inicia sesion para guardar el estado.", Toast.LENGTH_SHORT).show();
            return;
        }

        updateDeviceStatus();

        FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("users")
                .child(user.getUid())
                .child("deviceStatus")
                .push()
                .setValue(currentDeviceStatus)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Estado guardado en Firebase.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(this, "No se pudo guardar: " + error.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
        long now = System.currentTimeMillis();
        if (acceleration > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = now;
            Toast.makeText(this, "Sacudida detectada: pidiendo recomendacion cloud.", Toast.LENGTH_SHORT).show();
            loadBackendTip();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateConnectivityIndicator() {
        binding.connectivityText.setText(isOnline() ? "Modo online" : "Modo offline");
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showLoading() {
        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.errorContainer.setVisibility(View.GONE);
        binding.charactersRecyclerView.setVisibility(View.GONE);
    }

    private void showContent() {
        binding.loadingProgress.setVisibility(View.GONE);
        binding.errorContainer.setVisibility(View.GONE);
        binding.charactersRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        binding.loadingProgress.setVisibility(View.GONE);
        binding.errorText.setText(message);
        binding.errorContainer.setVisibility(View.VISIBLE);
        binding.charactersRecyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
