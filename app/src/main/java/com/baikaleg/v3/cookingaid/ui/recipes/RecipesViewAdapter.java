package com.baikaleg.v3.cookingaid.ui.recipes;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baikaleg.v3.cookingaid.R;
import com.baikaleg.v3.cookingaid.data.Repository;
import com.baikaleg.v3.cookingaid.data.model.Recipe;
import com.baikaleg.v3.cookingaid.data.model.Step;
import com.baikaleg.v3.cookingaid.databinding.ItemRecipeBinding;
import com.baikaleg.v3.cookingaid.databinding.ViewStepInItemRecipeBinding;
import com.baikaleg.v3.cookingaid.ui.recipes.item.RecipeItemEventNavigator;
import com.baikaleg.v3.cookingaid.ui.recipes.item.RecipeItemViewModel;
import com.baikaleg.v3.cookingaid.ui.recipestepsdetails.StepDetailsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipesViewAdapter extends RecyclerView.Adapter<RecipesViewAdapter.RecipesViewHolder> {
    private static final String TAG = RecipesViewAdapter.class.getSimpleName();

    private final List<Recipe> recipesList = new ArrayList<>();
    private boolean[] selectionArray;
    private float[] ratioArray;

    private final Context context;
    private final Repository repository;
    private final RecipeItemEventNavigator navigator;

    private int imageHeight, imageWidth;

    RecipesViewAdapter(Context context, Repository repository, RecipeItemEventNavigator navigator) {
        this.context = context;
        this.repository = repository;
        this.navigator = navigator;
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_recipe,
                        parent, false);
        return new RecipesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesViewHolder holder, int position) {
        final Recipe recipe = recipesList.get(position);
        RecipeItemViewModel viewModel = new RecipeItemViewModel(recipe, ratioArray[position], repository, b -> selectionArray[position] = b);
        viewModel.setImageSize(imageWidth, imageHeight);
        viewModel.setIsExpanded(selectionArray[position]);
        holder.recipeItemBinding.setViewmodel(viewModel);

        holder.recipeItemBinding.recountBtn.setOnClickListener(v -> navigator.onClickRecountBtn(position));
        holder.recipeItemBinding.basketBtn.setOnClickListener(v -> navigator.onClickSendBtn(
                viewModel.getRecipe().get(),
                viewModel.getRatio().get()));

        RecipesStepsPagerAdapter pagerAdapter = new RecipesStepsPagerAdapter();
        holder.recipeItemBinding.dots.setupWithViewPager(holder.recipeItemBinding.steps, true);
        holder.recipeItemBinding.steps.setAdapter(pagerAdapter);
    }

    @Override
    public int getItemCount() {
        return this.recipesList.size();
    }

    void setImageSize(int width, int height) {
        this.imageHeight = height;
        this.imageWidth = width;
    }

    boolean[] getSelectionArray() {
        return selectionArray;
    }

    void setSelectionArray(boolean[] selectionArray) {
        this.selectionArray = selectionArray;
    }

    float[] getRatioArray() {
        return ratioArray;
    }

    void setRatioArray(float[] ratioArray) {
        this.ratioArray = ratioArray;
    }

    void refresh(@NonNull List<Recipe> list) {
        this.recipesList.clear();
        this.recipesList.addAll(list);

        if (selectionArray == null) {
            selectionArray = new boolean[list.size()];
        }
        if (ratioArray == null) {
            ratioArray = new float[list.size()];
            Arrays.fill(ratioArray, 1f);
        }

        notifyDataSetChanged();
    }

    void recount(int position, int persons) {
        float oldValue = recipesList.get(position).getServings();
        ratioArray[position] = persons / oldValue;
        notifyItemChanged(position);
    }

    public class RecipesStepsPagerAdapter extends PagerAdapter {
        private Recipe recipe;
        private List<Step> steps = new ArrayList<>();

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            ViewStepInItemRecipeBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.view_step_in_item_recipe, container, false);
            binding.stepShortDescription.setText(context.getString(R.string.step_desc, position, steps.get(position).getShortDescription()));
            binding.stepDescription.setText(steps.get(position).getDescription());
            container.addView(binding.getRoot());

            View page = binding.getRoot();
            page.setOnClickListener(view -> {
                Intent intent = new Intent(context, StepDetailsActivity.class);
                intent.putExtra(StepDetailsActivity.EXTRA_STEP_POSITION, position);
                intent.putParcelableArrayListExtra(StepDetailsActivity.EXTRA_RECIPE, new ArrayList<>(recipe.getSteps()));
                context.startActivity(intent);
            });

            return binding.getRoot();
        }

        public void refresh(Recipe recipe) {
            this.recipe = recipe;
            steps = recipe.getSteps();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return steps.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            try {
                container.removeView((View) object);
            } catch (Exception e) {
                Log.i(TAG, "failed to destroy step in recipe item");
            }
        }
    }

    class RecipesViewHolder extends RecyclerView.ViewHolder {
        final ItemRecipeBinding recipeItemBinding;

        RecipesViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            recipeItemBinding = binding;
        }
    }
}
