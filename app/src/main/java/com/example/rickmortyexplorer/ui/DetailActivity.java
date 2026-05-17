package com.example.rickmortyexplorer.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rickmortyexplorer.R;
import com.example.rickmortyexplorer.databinding.ActivityDetailBinding;
import com.example.rickmortyexplorer.model.CharacterItem;
import com.example.rickmortyexplorer.model.FavoriteCharacter;
import com.example.rickmortyexplorer.rest.RetrofitClient;
import com.example.rickmortyexplorer.rest.api.RickMortyApiService;
import com.example.rickmortyexplorer.util.CharacterUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_CHARACTER_ID = "extra_character_id";
    private static final String DATABASE_URL =
            "https://rickmortyexplorercuyn-default-rtdb.europe-west1.firebasedatabase.app/";

    private ActivityDetailBinding binding;
    private RickMortyApiService apiService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int characterId;
    private DatabaseReference favoriteReference;
    private CharacterItem currentCharacter;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getRetrofit().create(RickMortyApiService.class);
        characterId = getIntent().getIntExtra(EXTRA_CHARACTER_ID, -1);

        binding.backButton.setOnClickListener(view -> finish());
        binding.detailRetryButton.setOnClickListener(view -> loadCharacterDetail());
        binding.favoriteButton.setOnClickListener(view -> toggleFavorite());

        loadCharacterDetail();
    }

    private void loadCharacterDetail() {
        if (characterId <= 0) {
            showError("No se encontro el personaje seleccionado.");
            return;
        }

        showLoading();
        compositeDisposable.add(
                apiService.getCharacterDetail(characterId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showCharacter,
                                throwable -> showError("No se pudo cargar el detalle.")
                        )
        );
    }

    private void showCharacter(CharacterItem character) {
        currentCharacter = character;
        binding.detailToolbarTitle.setText(CharacterUtils.safeText(character.getName()));
        binding.detailNameText.setText(CharacterUtils.safeText(character.getName()));
        binding.detailStatusText.setText(CharacterUtils.formatStatus(character));
        binding.detailInfoText.setText(CharacterUtils.formatDetail(character));

        Picasso.get()
                .load(character.getImage())
                .fit()
                .centerCrop()
                .into(binding.detailImage);

        binding.detailLoadingProgress.setVisibility(View.GONE);
        binding.detailErrorContainer.setVisibility(View.GONE);
        binding.contentContainer.setVisibility(View.VISIBLE);

        prepareFavoriteReference(character);
    }

    private void prepareFavoriteReference(CharacterItem character) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            binding.favoriteButton.setEnabled(false);
            return;
        }

        favoriteReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference("favorites")
                .child(user.getUid())
                .child(String.valueOf(character.getId()));

        favoriteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                updateFavoriteButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.favoriteButton.setEnabled(false);
            }
        });
    }

    private void toggleFavorite() {
        if (favoriteReference == null || currentCharacter == null) {
            return;
        }

        binding.favoriteButton.setEnabled(false);
        if (isFavorite) {
            favoriteReference.removeValue()
                    .addOnSuccessListener(unused -> {
                        isFavorite = false;
                        updateFavoriteButton();
                        Toast.makeText(this, "Eliminado de favoritos.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(error -> {
                        updateFavoriteButton();
                        Toast.makeText(this, "No se pudo eliminar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            favoriteReference.setValue(new FavoriteCharacter(currentCharacter))
                    .addOnSuccessListener(unused -> {
                        isFavorite = true;
                        updateFavoriteButton();
                        Toast.makeText(this, "Guardado en favoritos.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(error -> {
                        updateFavoriteButton();
                        Toast.makeText(this, "No se pudo guardar: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void updateFavoriteButton() {
        binding.favoriteButton.setEnabled(true);
        binding.favoriteButton.setText(isFavorite
                ? getString(R.string.remove_favorite)
                : getString(R.string.add_favorite));
    }

    private void showLoading() {
        binding.detailLoadingProgress.setVisibility(View.VISIBLE);
        binding.detailErrorContainer.setVisibility(View.GONE);
        binding.contentContainer.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.detailLoadingProgress.setVisibility(View.GONE);
        binding.detailErrorText.setText(message);
        binding.detailErrorContainer.setVisibility(View.VISIBLE);
        binding.contentContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
