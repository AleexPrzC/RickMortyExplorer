package com.example.rickmortyexplorer.rest.api;

import com.example.rickmortyexplorer.model.BackendTipResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface BackendApiService {
    @GET("character-tip")
    Single<BackendTipResponse> getCharacterTip();

    @GET("favorite-summary")
    Single<BackendTipResponse> getFavoriteSummary(@retrofit2.http.Query("count") int count);
}
