package com.example.rickmortyexplorer.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rickmortyexplorer.databinding.RowCharacterBinding;
import com.example.rickmortyexplorer.model.CharacterItem;
import com.example.rickmortyexplorer.util.CharacterUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    public interface OnCharacterClickListener {
        void onCharacterClick(CharacterItem character);
    }

    private final List<CharacterItem> characters = new ArrayList<>();
    private final OnCharacterClickListener listener;

    public CharacterAdapter(OnCharacterClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CharacterItem> newCharacters) {
        characters.clear();
        if (newCharacters != null) {
            characters.addAll(newCharacters);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowCharacterBinding binding = RowCharacterBinding.inflate(inflater, parent, false);
        return new CharacterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        holder.bind(characters.get(position));
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    class CharacterViewHolder extends RecyclerView.ViewHolder {
        private final RowCharacterBinding binding;

        CharacterViewHolder(RowCharacterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CharacterItem character) {
            binding.nameText.setText(CharacterUtils.safeText(character.getName()));
            binding.speciesText.setText(CharacterUtils.formatSubtitle(character));
            binding.statusText.setText(CharacterUtils.formatStatus(character));

            Picasso.get()
                    .load(character.getImage())
                    .fit()
                    .centerCrop()
                    .into(binding.characterImage);

            binding.getRoot().setOnClickListener(view -> listener.onCharacterClick(character));
        }
    }
}
