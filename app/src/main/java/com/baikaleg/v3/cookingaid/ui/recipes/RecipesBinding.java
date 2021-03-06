package com.baikaleg.v3.cookingaid.ui.recipes;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baikaleg.v3.cookingaid.data.model.Ingredient;
import com.baikaleg.v3.cookingaid.data.model.Recipe;
import com.baikaleg.v3.cookingaid.ui.recipes.RecipesViewAdapter.RecipesStepsPagerAdapter;
import com.baikaleg.v3.cookingaid.ui.recipes.item.RecipeIngredientView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipesBinding {

    /**
     * Prevent instantiation
     */
    private RecipesBinding() {
    }

    @BindingAdapter({"app:imageUrl", "app:imageWidth", "app:imageHeight"})
    public static void showImage(@NonNull ImageView imageView, @NonNull String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Picasso.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .resize(width, height)
                .into(imageView);
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:recipes")
    public static void setRecipes(RecyclerView recyclerView, List<Recipe> recipes) {
        RecipesViewAdapter adapter = (RecipesViewAdapter) recyclerView.getAdapter();
        if (adapter != null && recipes != null) {
            adapter.refresh(recipes);
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:recipesRefresh")
    public static void setRecipesRefreshListener(SwipeRefreshLayout view, final RecipesViewModel viewModel) {
        view.setOnRefreshListener(viewModel::load);
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter({"app:ingredients", "app:ratio"})
    public static void setIngredients(LinearLayout layout, List<Ingredient> ingredients, float ratio) {
        if (layout.getChildCount() > 0) {
            layout.removeAllViews();
        }
        for (int i = 0; i < ingredients.size(); i++) {
            RecipeIngredientView ingredientView = new RecipeIngredientView(layout.getContext());
            ingredientView.setIngredient(ingredients.get(i), ratio);
            layout.addView(ingredientView);
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:steps")
    public static void setStepTitles(ViewPager pager, Recipe recipe) {
        RecipesStepsPagerAdapter adapter = (RecipesStepsPagerAdapter) pager.getAdapter();
        if (adapter != null) {
            adapter.refresh(recipe);
        }
    }
}
