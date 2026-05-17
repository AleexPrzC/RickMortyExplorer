package com.example.rickmortyexplorer.rest.api;

import com.example.rickmortyexplorer.model.CharacterItem;
import com.example.rickmortyexplorer.model.CharacterListResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RickMortyApiService {
    @GET("character")
    Single<CharacterListResponse> getCharacters(@Query("page") int page);

    @GET("character/{id}")
    Single<CharacterItem> getCharacterDetail(@Path("id") int id);
}
