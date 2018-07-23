package com.baikaleg.v3.cookingaid.ui.storage;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.baikaleg.v3.cookingaid.data.Repository;
import com.baikaleg.v3.cookingaid.data.callback.OnProductEntityLoadedListener;
import com.baikaleg.v3.cookingaid.data.database.entity.product.ProductEntity;

import java.util.List;

public class StorageViewModel extends AndroidViewModel {
    private final static int STATE = 3;
    private final Repository repository;

    private OnProductEntityLoadedListener loadedListener = new OnProductEntityLoadedListener() {
        @Override
        public void onAllProductEntitiesLoaded(List<ProductEntity> list) {
            data.setValue(list);
        }

        @Override
        public void onProductEntityByIdLoaded(ProductEntity entity) {

        }
    };

    public final MutableLiveData<List<ProductEntity>> data = new MutableLiveData<>();

    StorageViewModel(@NonNull Application application) {
        super(application);
        this.repository = Repository.getInstance(application);
        loadData();
    }

    private void loadData() {
        repository.loadAllProductEntities(loadedListener, STATE);
    }

    public void remove(ProductEntity entity) {
        repository.removeProductEntity(entity);
    }

    public void onDestroy() {
        repository.onDestroyed();
        loadedListener = null;
    }
}
