package com.example.rickmortyexplorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rickmortyexplorer.databinding.ActivityFavoritesBinding;
import com.example.rickmortyexplorer.model.BackendTipResponse;
import com.example.rickmortyexplorer.model.FavoriteCharacter;
import com.example.rickmortyexplorer.rest.BackendRetrofitClient;
import com.example.rickmortyexplorer.rest.api.BackendApiService;
import com.example.rickmortyexplorer.ui.adapter.FavoriteAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritesActivity extends AppCompatActivity {
    private static final String DATABASE_URL =
            "https://rickmortyexplorercuyn-default-rtdb.europe-west1.firebasedatabase.app/";

    private ActivityFavoritesBinding binding;
    private FavoriteAdapter adapter;
    private BackendApiService backendApiService;
    private DatabaseReference favoritesReference;
    private ValueEventListener favoritesListener;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backendApiService = BackendRetrofitClient.getRetrofit().create(BackendApiService.class);
        adapter = new FavoriteAdapter(character -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_CHARACTER_ID, character.getId());
            startActivity(intent);
        });

        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.favoritesRecyclerView.setAdapter(adapter);
        binding.backButton.setOnClickListener(view -> finish());

        loadFavorites();
    }

    private void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showEmpty("Inicia sesion para ver tus favoritos.");
            return;
        }

        favoritesReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("favorites")
                .child(user.getUid());

        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FavoriteCharacter> favorites = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FavoriteCharacter character = child.getValue(FavoriteCharacter.class);
                    if (character != null) {
                        favorites.add(character);
                    }
                }
                showFavorites(favorites);
                loadFavoriteSummary(favorites.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmpty("No se pudieron cargar los favoritos.");
            }
        };

        favoritesReference.addValueEventListener(favoritesListener);
    }

    private void showFavorites(List<FavoriteCharacter> favorites) {
        binding.favoritesProgress.setVisibility(View.GONE);
        adapter.submitList(favorites);
        binding.favoritesRecyclerView.setVisibility(favorites.isEmpty() ? View.GONE : View.VISIBLE);
        binding.emptyText.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(String message) {
        binding.favoritesProgress.setVisibility(View.GONE);
        binding.favoritesRecyclerView.setVisibility(View.GONE);
        binding.emptyText.setText(message);
        binding.emptyText.setVisibility(View.VISIBLE);
    }

    private void loadFavoriteSummary(int count) {
        compositeDisposable.add(
                backendApiService.getFavoriteSummary(count)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showFavoriteSummary,
                                throwable -> binding.favoriteSummaryText.setText(
                                        "Resumen local: tienes " + count + " favoritos guardados."
                                )
                        )
        );
    }

    private void showFavoriteSummary(BackendTipResponse response) {
        String title = response.getTitle() == null ? "Resumen de favoritos" : response.getTitle();
        String message = response.getMessage() == null ? "" : response.getMessage();
        binding.favoriteSummaryText.setText(title + ": " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoritesReference != null && favoritesListener != null) {
            favoritesReference.removeEventListener(favoritesListener);
        }
        compositeDisposable.clear();
    }
}
