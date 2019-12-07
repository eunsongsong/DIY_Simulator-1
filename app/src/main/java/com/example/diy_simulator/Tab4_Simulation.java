package com.example.diy_simulator;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tab4_Simulation extends Fragment {

    private ProgressDialog pd;
    private LinearLayout simul_menu_layout;
    private LinearLayout blur;
    private View view;
    Animation animation;
    Switch all_etc_item_show;
    Switch my_item_show;
    ImageButton add_item_btn;

    RelativeLayout relativeLayout;
    int parentWidth;
    int parentHeight;

    boolean check = false;
    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    //그리드 리싸이클러뷰
    public RecyclerView simul_recyclerview;
    private List<Tab4_Simulation_Item> simulation_items = new ArrayList<>();
    private Tab4_Simulation_Adatper simulationAdatper;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef_customer = database.getReference("구매자");
    private DatabaseReference myRef_seller = database.getReference("판매자");
    private DatabaseReference myRef2 = database.getReference("부자재");

    private String cart;
    String[] cart_arr;
    private ImageView trashView, empty_item;

    private ImageButton left_rotate;
    private ImageButton right_rotate;
    private ImageButton order_front_1;
    private ImageButton order_back_1;
    private ImageButton order_front;
    private ImageButton order_back;
    private ImageButton magnify_btn;
    private ImageButton minimize_btn;
    private int MOVE = 1;
    private float angle = 5.0f;

    private ArrayList<ImageView> view_order;

    private ImageButton keyring_btn;
    private ImageButton phonecase_btn;
    private ImageButton acc_btn;
    private ImageButton etc_btn;

    private int touch_cnt;

    private int _xDelta;
    private int _yDelta;

    private int tra_h;
    private View dashline_var;

    // The ‘active pointer’ is the one currently moving our object.
    //싱글 터치

    //View dashline  // 새로 추가할 대시 라인

    private View.OnTouchListener touchListener_rel = new View.OnTouchListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dashline_var.setVisibility(View.GONE);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

            }

            return true;
        }
    };


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();

            switch (MOVE) {

                case 1: // 기본움직임
                    int parentWidth = ((ViewGroup) v.getParent()).getWidth();    // 부모 View 의 Width
                    //int parentHeight = ((ViewGroup) v.getParent()).getHeight();    // 부모 View 의 Height
                    // View 내부에서 터치한 지점의 상대 좌표값.
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // 뷰 누름
                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        // 뷰 이동 중
                        //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();

                        v.setTranslationX(X - _xDelta);
                        v.setTranslationY(Y - _yDelta);
                        dashline_var.setTranslationX(X - _xDelta);
                        dashline_var.setTranslationY(Y - _yDelta);

                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        //pa - (pa - tra) <= location[1] + v.getHeight() / 10
                        if ( parentHeight -  tra_h < v.getY() + v.getHeight() / 2 ){
                            trashView.setImageDrawable(getResources().getDrawable(R.drawable.trash_mint));
                        }
                        else
                            trashView.setImageDrawable(getResources().getDrawable(R.drawable.trash));
                        break;

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // 뷰에서 손을
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);

                        if ( parentHeight - tra_h < v.getY() + v.getHeight() / 2 ){
                            int find_idx = 0;
                            for(ImageView imageView : view_order)
                            {
                                if( v == imageView)
                                {
                                    view_order.remove(find_idx);
                                    break;
                                }
                                find_idx++;
                            }
                            relativeLayout.removeView(v);
                            dashline_var.setVisibility(View.GONE);
                            trashView.setImageDrawable(getResources().getDrawable(R.drawable.trash));
                        }

                        if ( v.getX() + v.getWidth() / 2 < 0 ) {
                            v.setX(0);
                            dashline_var.setX(0);
                            Log.d("2",parentWidth+"");
                        } else if (  v.getX() + v.getWidth() / 2  > parentWidth) {
                            v.setX(parentWidth - v.getWidth());
                            dashline_var.setX(parentWidth - v.getWidth());
                            Log.d("3",parentWidth+"");
                        }
                        if ( v.getY() + v.getHeight() / 2 < 0) {
                            v.setY(0);
                            dashline_var.setY(0);
                            Log.d("4",parentWidth+"");
                        }
                    }
                    break;
                case 2:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setRotation(v.getRotation() - angle);
                        dashline_var.setRotation(v.getRotation());
                    }
                    break;
                case 3:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setRotation(v.getRotation() + angle);
                        dashline_var.setRotation(v.getRotation());
                        // Create an animation instance
                    }
                    break;
                case 4: // 하나 앞으로
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                        for(ImageView imageView : view_order) {
                            if(imageView == v) {
                                Log.d("ㅇㅇ","1");
                                ArrayList<ImageView> imageViews = new ArrayList<>();
                                int a = view_order.indexOf(v);
                                Log.d("dd", "인덱스"+view_order.indexOf(v));
                                Log.d("사이즈","?"+view_order.size());

                                for(int i = 0; i < view_order.size(); i++) {
                                    if (a == i && i == view_order.size()-1){
                                        view_order.get(i).bringToFront();
                                        imageViews.add(view_order.get(i));
                                        Log.d("oo","마지막 앞으로");
                                        continue;
                                    }
                                    if (i == a) {
                                        view_order.get(i+1).bringToFront();
                                        imageViews.add(view_order.get(i+1));
                                        view_order.get(i).bringToFront();
                                        imageViews.add(view_order.get(i));
                                        continue;
                                    }
                                    if (i == a+1) {
                                        continue;
                                    }
                                    Log.d("ㅇㅇ", "앞으로!!" + view_order.indexOf(view_order.get(i)));
                                    view_order.get(i).bringToFront();
                                    imageViews.add(view_order.get(i));
                                }
                                Log.d("ㅇㅇ","4");

                                view_order = imageViews;
                                break;
                            }
                        }

                    }
                    break;
                case 5: // 하나 뒤로
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                        for(ImageView imageView : view_order) {
                            if(imageView == v) {
                                Log.d("ㅇㅇ","1");
                                ArrayList<ImageView> imageViews = new ArrayList<>();
                                int a = view_order.indexOf(v);
                                Log.d("dd", "인덱스"+view_order.indexOf(v));
                                Log.d("사이즈","?"+view_order.size());

                                for(int i = 0; i < view_order.size(); i++) {
                                    if (a == i && i == 0){
                                        imageViews.add(view_order.get(0));
                                        continue;
                                    }
                                    if (i == a - 1) {
                                        Log.d("ㅇㅇ", "넘어가고");
                                        continue;
                                    }
                                    if (i == a) {
                                        view_order.get(i).bringToFront();
                                        imageViews.add(view_order.get(i));
                                        Log.d("00", "하나만뒤로" + a);
                                        view_order.get(i - 1).bringToFront();
                                        imageViews.add(view_order.get(i - 1));
                                        Log.d("00", "그다음");
                                        continue;
                                    }
                                    Log.d("ㅇㅇ", "앞으로!!" + view_order.indexOf(view_order.get(i)));
                                    view_order.get(i).bringToFront();
                                    imageViews.add(view_order.get(i));
                                }
                                Log.d("ㅇㅇ","4");

                                view_order = imageViews;
                                break;
                            }
                        }
                    }
                    break;
                case 6: // 맨 앞으로
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                        v.bringToFront();
                        for(ImageView imageView : view_order) {
                            if(imageView == v) {
                                Log.d("ㅇㅇ","1");
                                ArrayList<ImageView> imageViews = new ArrayList<>();
                                for(ImageView imageView1 : view_order)
                                {
                                    Log.d("ㅇㅇ","2");
                                    if( imageView1 == v)
                                    {
                                        Log.d("ㅇㅇ","3");
                                        continue;
                                    }
                                    imageViews.add(imageView1);
                                }
                                //imageViews.add(0, imageView);
                                imageViews.add(imageView);
                                view_order = imageViews;
                                Log.d("dd","ddd"+view_order.indexOf(v));
                                break;
                            }
                        }
                    }
                    break;
                case 7: // 맨 뒤로
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        _xDelta = (int) (X - v.getTranslationX());
                        _yDelta = (int) (Y - v.getTranslationY());
                        addGuideView(v, X, Y, _xDelta, _yDelta);

                        for(ImageView imageView : view_order)
                        {
                            if(imageView == v) {
                                Log.d("ㅇㅇ","1");
                                ArrayList<ImageView> imageViews = new ArrayList<>();
                                imageViews.add(imageView);
                                Log.d("사이즈","?"+view_order.size());
                                Log.d("dd", "인덱스"+view_order.indexOf(v));
                                for(int i = 0; i < view_order.size(); i++){
                                    if( view_order.get(i) == v) {
                                        Log.d("ㅇㅇ", "2");
                                        continue;
                                    }
                                    Log.d("ㅇㅇ","3");
                                    view_order.get(i).bringToFront();
                                    imageViews.add(view_order.get(i));
                                }
                                Log.d("ㅇㅇ","4");

                                view_order = imageViews;
                                break;
                            }
                        }
                    }
                    break;

            }
            return true;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab4_simulation, container, false);

        left_rotate = (ImageButton) rootview.findViewById(R.id.left_rotate);
        right_rotate = (ImageButton) rootview.findViewById(R.id.right_rotate);
        order_front_1 = (ImageButton) rootview.findViewById(R.id.order_front_1);
        order_back_1 = (ImageButton) rootview.findViewById(R.id.order_back_1);
        order_front = (ImageButton) rootview.findViewById(R.id.order_front);
        order_back = (ImageButton) rootview.findViewById(R.id.order_back);
        magnify_btn  = (ImageButton) rootview.findViewById(R.id.magnify);
        minimize_btn  = (ImageButton) rootview.findViewById(R.id.minimize);

        relativeLayout = (RelativeLayout) rootview.findViewById(R.id.relative);
        all_etc_item_show =(Switch) rootview.findViewById(R.id.all_etc_item_show_checkbox);
        my_item_show = (Switch) rootview.findViewById(R.id.myitem_checkbox);

        boolean isSeller = PreferenceUtil.getInstance(getContext()).getBooleanExtra("isSeller");
        simulationAdatper = new Tab4_Simulation_Adatper(getContext(), simulation_items, R.layout.fragment_tab4_simulation);


        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        trashView = (ImageView) rootview.findViewById(R.id.trash);
        empty_item = rootview.findViewById(R.id.no_simul_item);
        keyring_btn = (ImageButton) rootview.findViewById(R.id.img_but1);
        phonecase_btn = (ImageButton) rootview.findViewById(R.id.img_but2);
        acc_btn = (ImageButton) rootview.findViewById(R.id.img_but3);
        etc_btn = (ImageButton) rootview.findViewById(R.id.img_but4);
        add_item_btn = (ImageButton) rootview.findViewById(R.id.add_btn);

        view_order = new ArrayList<>();

        add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getContext(), CustomerImageUploadActivity.class);
                startActivity(mainIntent);
            }
        });

        keyring_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                simulationAdatper.getFilter().filter("키링");
                simulation_items = simulationAdatper.getFilteredList();
                simulationAdatper.getFilter().filter("키링");
                simulation_items = simulationAdatper.getFilteredList();
                hideProgress();
            }
        });
        phonecase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                simulationAdatper.getFilter().filter("폰케이스");
                simulation_items = simulationAdatper.getFilteredList();
                simulationAdatper.getFilter().filter("폰케이스");
                simulation_items = simulationAdatper.getFilteredList();
                hideProgress();
            }
        });
        acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                simulationAdatper.getFilter().filter("액세서리");
                simulation_items = simulationAdatper.getFilteredList();
                simulationAdatper.getFilter().filter("액세서리");
                simulation_items = simulationAdatper.getFilteredList();
                hideProgress();
            }
        });
        etc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                simulationAdatper.getFilter().filter("기타");
                simulation_items = simulationAdatper.getFilteredList();
                simulationAdatper.getFilter().filter("기타");
                simulation_items = simulationAdatper.getFilteredList();
                hideProgress();
            }
        });

        // 로그인 되어있을 경우 유저에 따라 시뮬레이션 아이템 목록 (부자재 번호만) 불러오기
        if(mFirebaseUser != null){
            showProgress();

            cart= "";
            // 판매자일 경우 내 가게 상품 목록 불러오기
            if(isSeller){
                add_item_btn.setVisibility(View.GONE);
                my_item_show.setVisibility(View.GONE);
                myRef_seller.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())) {
                                cart = ds.child("material").getValue().toString();
                                if(TextUtils.isEmpty(cart))
                                {
                                    empty_item.setVisibility(View.VISIBLE);
                                    empty_item.setImageDrawable(getResources().getDrawable(R.drawable.no_item_seller));
                                }
                                else
                                    empty_item.setVisibility(View.GONE);
                                break;
                            }
                        }
                        hideProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            // 고객일 경우 장바구니 목록 불러오기
            else{
                myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())) {
                                cart = ds.child("cart").getValue().toString();
                                if(TextUtils.isEmpty(cart))
                                {
                                    empty_item.setVisibility(View.VISIBLE);
                                    empty_item.setImageDrawable(getResources().getDrawable(R.drawable.empty_cart));
                                }
                                else
                                    empty_item.setVisibility(View.GONE);
                                break;
                            }
                        }
                        hideProgress();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        ImageButton menubtn = rootview.findViewById(R.id.simulation_menu_button);
        ImageButton x_btn = rootview.findViewById(R.id.x_button);
        simul_menu_layout = rootview.findViewById(R.id.simul_menu_layout);
        view = rootview.findViewById(R.id.side_btn);
        blur = rootview.findViewById(R.id.blur);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        simul_recyclerview = rootview.findViewById(R.id.simulation_menu_recycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        simul_recyclerview.setHasFixedSize(true);
        simul_recyclerview.setLayoutManager(layoutManager);
        simul_recyclerview.setAdapter(simulationAdatper);

        simulation_items.clear();
        check = true;

        ViewTreeObserver viewTreeObserver = relativeLayout.getViewTreeObserver();
        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("ClickableViewAccessibility")
            public void onGlobalLayout() {
                if(check) {
                    parentWidth = relativeLayout.getWidth();    // 부모 View 의 Width
                    parentHeight = relativeLayout.getHeight();    // 부모 View 의 Height
                    relativeLayout.setOnTouchListener(touchListener_rel);
                    tra_h= trashView.getHeight();
                    int[] location = new int[2];
                    relativeLayout.getLocationOnScreen(location);
                    trashView.getLocationOnScreen(location);
                    check = false;

                    dashline_var = new View(getContext());
                    dashline_var.setVisibility(View.GONE);
                    relativeLayout.addView(dashline_var);
                    dashline_var.setBackground(getResources().getDrawable(R.drawable.view_line_dotted));

                }
            }
        };

        viewTreeObserver.addOnGlobalLayoutListener(mOnGlobalLayoutListener);



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

                int width = dm.widthPixels;

                animation = new TranslateAnimation(0, width, 0, 0);
                animation.setDuration(100);
                view.setVisibility(View.GONE);
                simul_menu_layout.setVisibility(View.GONE);
                simul_menu_layout.setAnimation(animation);
                blur.setVisibility(View.VISIBLE);
            }
        });

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
                //    Log.d("촉",cart);
                int width = dm.widthPixels;
                int f_width = width - (int) (width * 0.8);
                simulation_items.clear();
                animation = new TranslateAnimation(width, f_width, 0, 0);
                animation.setDuration(100);
                simul_menu_layout.setVisibility(View.VISIBLE);
                simul_menu_layout.setAnimation(animation);
                animation = new TranslateAnimation(f_width, 0, 0, 0);
                animation.setDuration(25);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.VISIBLE);
                        view.setAnimation(animation);
                    }
                }, 75);

                blur.setVisibility(View.GONE);

                loadSimulationItems(all_etc_item_show.isChecked());
            }
        });

        x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

                int width = dm.widthPixels;

                animation = new TranslateAnimation(0, width, 0, 0);
                animation.setDuration(100);
                view.setVisibility(View.GONE);
                simul_menu_layout.setVisibility(View.GONE);
                simul_menu_layout.setAnimation(animation);
                blur.setVisibility(View.VISIBLE);

            }
        });

        //기타 이미지 보이기 스위치 버튼
        all_etc_item_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CheckChangedListener();
            }
        });

        left_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 2) {
                    MOVE = 2;
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_mint));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                }
                else {
                    MOVE = 1;
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                }
            }
        });

        right_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 3) {
                    MOVE = 3;
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_mint));
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                }
                else {
                    MOVE = 1;
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
            }
        });

        order_front_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 4){
                    MOVE = 4;
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_mint_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
                else{
                    MOVE = 1;
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                }
            }
        });

        order_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 5){
                    MOVE = 5;
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_mint_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
                else{
                    MOVE = 1;
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                }
            }
        });

        order_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 6){
                    MOVE = 6;
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_mint));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
                else{
                    MOVE = 1;
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                }
            }
        });

        order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 7){
                    MOVE = 7;
                    order_front_1.setBackground(getResources().getDrawable(R.drawable.front_black_1));
                    order_back_1.setBackground(getResources().getDrawable(R.drawable.back_black_1));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_mint));
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_black));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
                else{
                    MOVE = 1;
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                }
            }
        });
        magnify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touch_cnt++; // 터치 수

                //가이드라인 크기
                dashline_var.setScaleX((float)(dashline_var.getScaleX() * 1.5));
                dashline_var.setScaleY((float)(dashline_var.getScaleY() * 1.5));
                if(touch_cnt > 0) {
                    magnify_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_in_mint));
                    minimize_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_out_black));
                }
                else if( touch_cnt == 0)
                {
                    magnify_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_in_black));
                    minimize_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_out_black));
                }

                for(int i = 0; i < view_order.size(); i++)
                {
                    view_order.get(i).setScaleX((float) (view_order.get(i).getScaleX() * 1.5));
                    view_order.get(i).setScaleY((float) (view_order.get(i).getScaleY() * 1.5));
                }
            }
        });
        minimize_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touch_cnt--;
                dashline_var.setScaleX((float)(dashline_var.getScaleX() / 1.5));
                dashline_var.setScaleY((float)(dashline_var.getScaleY() / 1.5));
                if(touch_cnt < 0) {
                    magnify_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_in_black));
                    minimize_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_out_mint));
                }
                else if( touch_cnt == 0)
                {
                    magnify_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_in_black));
                    minimize_btn.setImageDrawable(getResources().getDrawable(R.drawable.zoom_out_black));
                }
                for(int i = 0; i < view_order.size(); i++)
                {
                    view_order.get(i).setScaleX( (float)(view_order.get(i).getScaleX() / 1.5));
                    view_order.get(i).setScaleY((float) (view_order.get(i).getScaleY() / 1.5) );
                }
            }
        });

        simulationAdatper.setOnItemClickListener(new Tab4_Simulation_Adatper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                inflate(position);
            }
        });

        return rootview;

    }

    @SuppressLint("ClickableViewAccessibility")
    public void inflate(int position) {
        double width = simulation_items.get(position).getWidth();
        double height = simulation_items.get(position).getHeight();
        double depth = simulation_items.get(position).getDepth();
        String preview = simulation_items.get(position).getPreview_url();
        ImageView iv = new ImageView(getContext());  // 새로 추가할 imageView 생성

        double randomValue = Math.random();
        int intValue = (int) (randomValue * 4) + 2;

        if(simulation_items.get(position).isSide()){
            width = depth;
        }

        iv.setX( intValue * parentWidth / 9 * (float)Math.pow(1.5, -Math.abs(touch_cnt)));
        randomValue = Math.random();
        intValue = (int) (randomValue * 4) + 1;
        iv.setY( intValue * parentHeight / 16 * (float)Math.pow(1.5,-Math.abs(touch_cnt)));

        Glide.with(getContext())
                .load(preview)
                .into(iv);
        iv.setOnTouchListener(touchListener);
        iv.buildDrawingCache();

        double ratio = (double)parentHeight/parentWidth * 9;

        Log.d("짜이", (int) ( width* parentWidth / 90 * Math.pow(1.5, touch_cnt)) + "");
        Log.d("우람", parentWidth + "");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( (int) ( width* parentWidth / 90 ),(int) (height* parentHeight / (ratio*10)));
        iv.setAdjustViewBounds(true);
        iv.setLayoutParams(layoutParams);  // imageView layout 설정
        iv.setScaleX(iv.getScaleX() * (float) Math.pow(1.5, touch_cnt));
        iv.setScaleY(iv.getScaleY() * (float) Math.pow(1.5, touch_cnt));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        relativeLayout.addView(iv); // 기존 linearLayout에 imageView 추가
        view_order.add(iv);

    }

    public void addItemToRecyclerView(String preview, String[] url, int width, int height, int depth, String category, String name, boolean isSide, boolean isMy){
        Tab4_Simulation_Item item = new Tab4_Simulation_Item(preview, url, width, height, depth, category, name, isSide, isMy);
        simulation_items.add(item);
        simulationAdatper.notifyDataSetChanged();
    }

    //시뮬레이션 아이템 불러오기
    private void loadSimulationItems(final boolean show_all){
        // 시뮬레이션 아이템 불러오기
        if(!TextUtils.isEmpty(cart)) {

            Log.i("시뮬 로드합니다", show_all+"" +simulation_items.size() + " 사이즈");

            showProgress();

            empty_item.setVisibility(View.GONE);
            cart_arr = cart.split("#");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Log.d("제발잠좀","자게해줘"+dataSnapshot.getValue().toString());
                    //String url = dataSnapshot.child("image_RB_url").child(d.getKey()).getValue().toString();
                    simulation_items.clear();
                    int i = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if ( i == cart_arr.length)
                            break;
                        if(cart_arr[i].equals(ds.getKey())){

                            String[] url = new String[(int)ds.child("image_RB_url").getChildrenCount()];
                            int k = 0 ;
                            for(DataSnapshot ds2 : ds.child("image_RB_url").getChildren()) {
                                url[k] = ds2.getValue().toString();
                                k++;
                            }

                            String[] url_side = new String[(int)ds.child("image_RB_SIDE_url").getChildrenCount()];
                            k = 0 ;
                            for(DataSnapshot ds2 : ds.child("image_RB_SIDE_url").getChildren()) {
                                url_side[k] = ds2.getValue().toString();
                                Log.i("사이드", url_side[k] + " 얍");
                                k++;
                            }

                            String preview_url = url[0];
                            //Log.d("궁금 " + ds.getKey(),ds.child("image_url").getChildrenCount()+"");
                            //String url = ds.child("image_RB_url").child(Integer.parseInt(ds.getKey()) + (int) ds.child("image_url").getChildrenCount()+"").getValue().toString();
                            int width = Integer.parseInt(ds.child("size_width").getValue().toString());
                            int height = Integer.parseInt(ds.child("size_height").getValue().toString());
                            int depth = Integer.parseInt(ds.child("size_depth").getValue().toString());
                            String name = ds.child("material_name").getValue().toString();
                            String category = ds.child("category").getValue().toString();
                            if(show_all){
                                int m;
                                for (m = 0; m < url.length; m++) {
                                    addItemToRecyclerView(url[m], url, width, height, depth, category, name+" - "+(m+1), false,false);
                                }
                                for (int n = 0; n < url_side.length; n++) { //측면
                                    addItemToRecyclerView(url_side[n], url, width, height, depth, category, name+" - "+(m+1), true,false);
                                    m++;
                                }
                            }
                            else{
                                addItemToRecyclerView(preview_url, url, width, height, depth, category, name, false,false);
                            }
                            i++;
                            continue;
                        }
                    }
                    simulationAdatper.getFilter().filter("");
                    simulation_items = simulationAdatper.getFilteredList();
                    simulationAdatper.getFilter().filter("");
                    simulation_items = simulationAdatper.getFilteredList();
                    simulationAdatper.notifyDataSetChanged();
                    hideProgress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void CheckChangedListener(){
        if(all_etc_item_show.isChecked()) {
            Log.i("켜졌습니다", "ㅛ4ㄷ");
            loadSimulationItems(true);
            simulationAdatper.getFilter().filter("");
            simulation_items = simulationAdatper.getFilteredList();
            simulationAdatper.getFilter().filter("");
            simulation_items = simulationAdatper.getFilteredList();
        }
        else{
            Log.d("꺼졌다", "ㅛ4ㄷ");
            loadSimulationItems(false);
            simulationAdatper.getFilter().filter("");
            simulation_items = simulationAdatper.getFilteredList();
            simulationAdatper.getFilter().filter("");
            simulation_items = simulationAdatper.getFilteredList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(getContext(), R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
            Log.d("ㅇㅇ","dnfka");
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress(){
        if( pd != null && pd.isShowing() ){
            pd.dismiss();
        }
    }

    private void addGuideView(View v, int X, int Y,int _xDelta, int _yDelta)
    {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( v.getWidth(), v.getHeight());
        dashline_var.setX(X - _xDelta);
        dashline_var.setY(Y - _yDelta);
        dashline_var.setScaleX(v.getScaleX());
        dashline_var.setScaleY(v.getScaleY());
        dashline_var.setLayoutParams(layoutParams);
        dashline_var.setRotation(v.getRotation());
        dashline_var.bringToFront();
        dashline_var.setVisibility(View.VISIBLE);
    }
}