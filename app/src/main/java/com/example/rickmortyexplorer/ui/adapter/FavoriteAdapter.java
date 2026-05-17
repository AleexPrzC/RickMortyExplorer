package com.example.rickmortyexplorer.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rickmortyexplorer.databinding.RowFavoriteBinding;
import com.example.rickmortyexplorer.model.FavoriteCharacter;
import com.example.rickmortyexplorer.util.CharacterUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteCharacter character);
    }

    private final List<FavoriteCharacter> favorites = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public FavoriteAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteCharacter> newFavorites) {
        favorites.clear();
        if (newFavorites != null) {
            favorites.addAll(newFavorites);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowFavoriteBinding binding = RowFavoriteBinding.inflate(inflater, parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(favorites.get(position));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final RowFavoriteBinding binding;

        FavoriteViewHolder(RowFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FavoriteCharacter character) {
            binding.favoriteNameText.setText(CharacterUtils.safeText(character.getName()));
            binding.favoriteInfoText.setText(CharacterUtils.safeText(character.getSpecies())
                    + " - " + CharacterUtils.safeText(character.getStatus()));

            Picasso.get()
                    .load(character.getImage())
                    .fit()
                    .centerCrop()
                    .into(binding.favoriteImage);

            binding.getRoot().setOnClickListener(view -> listener.onFavoriteClick(character));
        }
    }
}
