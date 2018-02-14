package com.sehaj.bani.navigation.home;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sehaj.bani.R;
import com.sehaj.bani.navigation.home.adapter.RaagiInfoAdapter;
import com.sehaj.bani.navigation.home.presenter.HomePresenterImpl;
import com.sehaj.bani.navigation.home.view.HomeView;

/**
 * Created by ivesingh on 2/2/18.
 */

public class HomeFragment extends Fragment implements HomeView {

    private HomePresenterImpl homePresenterImpl;
    private RecyclerView raagi_RV;
    private RecyclerView.LayoutManager layoutManager;

    public HomeFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_home, container, false);

        homePresenterImpl = new HomePresenterImpl(this, getActivity());
        homePresenterImpl.init(view);
        homePresenterImpl.prepareRaagis();

        return view;
    }


    @Override
    public void init(View view) {
        raagi_RV = view.findViewById(R.id.raagi_RV);
        layoutManager = new GridLayoutManager(getActivity(), 3);
    }

    @Override
    public void showRaagis(RaagiInfoAdapter raagiInfoAdapter) {
        raagi_RV.setLayoutManager(layoutManager);
        raagi_RV.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        raagi_RV.setItemAnimator(new DefaultItemAnimator());
        raagi_RV.setAdapter(raagiInfoAdapter);
        raagi_RV.setNestedScrollingEnabled(false);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;


        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }


    }
}
