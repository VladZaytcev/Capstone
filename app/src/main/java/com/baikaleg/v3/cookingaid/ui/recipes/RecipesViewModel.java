package com.baikaleg.v3.cookingaid.ui.recipes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baikaleg.v3.cookingaid.data.Repository;
import com.baikaleg.v3.cookingaid.data.model.Recipe;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecipesViewModel extends AndroidViewModel implements Observable {

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final Repository repository;

    private String recipeCategory;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    @Bindable
    public final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();

    @Bindable
    public final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    @Bindable
    public final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();

    @Bindable
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    RecipesViewModel(@NonNull Application application) {
        super(application);
        this.repository = Repository.getInstance(application);
        this.compositeDisposable = new CompositeDisposable();

        load();
    }

    public void load() {
        isLoading.setValue(true);
        compositeDisposable.clear();
        Disposable disposable = repository.getRecipes()
                .flatMap(io.reactivex.Observable::fromIterable)
                .filter(recipe -> recipe.getCategory().equals(recipeCategory))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recipes -> showData(recipes, false), throwable -> {
                    Log.i("Recipes download error", throwable.getMessage());
                    showData(null, true);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    private void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    private void showData(List<Recipe> recipes, boolean error) {
        isLoading.setValue(false);
        isError.setValue(error);

        if (recipes != null) {
            data.setValue(recipes);
            isEmpty.setValue(recipes.size() == 0 && !error);
        }

        notifyChange();
    }

    void onDestroyed() {
        compositeDisposable.clear();
        repository.onDestroyed();
    }

    void setCategory(String category) {
        recipeCategory = category;
    }
}
