package com.example.diy_simulator;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
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

    /*class ViewItem{

        private ImageView imageView;
        private int idx;

        public ViewItem(ImageView imageView, int idx)
        {
            this.imageView = imageView;
            this.idx = idx;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }
    }

     */
    private ProgressDialog pd;
    private LinearLayout simul_menu_layout;
    private LinearLayout blur;
    private View view;
    Animation animation;

    float oldXvalue;
    float oldYvalue;
    RelativeLayout relativeLayout;
    int parentWidth;
    int parentHeight;

    boolean check = false;
    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    //그리드 리싸이클러뷰
    public RecyclerView simul_recyclerview;
    private  List<Tab4_Simulation_Item> simulation_items = new ArrayList<>();
    private final Tab4_Simulation_Adatper simulationAdatper = new Tab4_Simulation_Adatper(getContext(), simulation_items, R.layout.fragment_tab4_simulation);

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef_customer = database.getReference("구매자");
    private DatabaseReference myRef_seller = database.getReference("판매자");
    private DatabaseReference myRef2 = database.getReference("부자재");

    private String cart;
    String[] cart_arr;
    private ImageView trashView, empty_item;
    private int trash_width;
    private int trash_height;

    private ImageButton left_rotate;
    private ImageButton right_rotate;
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

    private float loX;
    private float loy;

    private int touch_cnt;

    //싱글 터치
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (MOVE) {

                case 1: // 기본움직임
                    int parentWidth = ((ViewGroup) v.getParent()).getWidth();    // 부모 View 의 Width
                    int parentHeight = ((ViewGroup) v.getParent()).getHeight();    // 부모 View 의 Height

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // 뷰 누름
                        oldXvalue = event.getX();
                        oldYvalue = event.getY();
                        Log.d("viewTest", "oldXvalue : " + oldXvalue + " oldYvalue : " + oldYvalue);    // View 내부에서 터치한 지점의 상대 좌표값.
                        Log.d("viewTest", "v.getX() : " + v.getX());    // View 의 좌측 상단이 되는 지점의 절대 좌표값.
                        Log.d("viewTest", "RawX : " + event.getRawX() + " RawY : " + event.getRawY());    // View 를 터치한 지점의 절대 좌표값.
                        Log.d("viewTest", "v.getHeight : " + v.getHeight() + " v.getWidth : " + v.getWidth());    // View 의 Width, Height

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        // 뷰 이동 중
                        v.setX(event.getRawX() - oldXvalue);
                        v.setY(event.getRawY() - oldYvalue - 250);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // 뷰에서 손을 뗌
                        Log.d("현재위치", v.getX() + "x " + v.getY());
                        Log.d("타겟", trash_width + "x " + trash_height);
                        int a = (int) v.getX();
                        int b = (int) v.getY();

                        if ((Math.abs((int) a - trash_width) <= 100 && Math.abs((int) b + 200 - trash_height) <= 150)
                                || (Math.abs((int) a - trash_width) <= 500 && Math.abs((int) b + v.getHeight()) > parentHeight)){
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
                            //v.setVisibility(View.GONE);
                            relativeLayout.removeView(v);
                        }

                        if (v.getX() < 0) {
                            v.setX(0);
                        } else if ((v.getX() + v.getWidth()) > parentWidth) {
                            v.setX(parentWidth - v.getWidth());
                        }

                        if (v.getY() < 0) {
                            v.setY(0);
                        } else if ((v.getY() + v.getHeight()) > parentHeight) {
                            v.setY(parentHeight - v.getHeight());
                        }
                    }
                    break;
                case 2:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setRotation(v.getRotation() - angle);
                    }
                    break;
                case 3:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setRotation(v.getRotation() + angle);
                    }
                    break;
                case 4:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.bringToFront();

                        for(ImageView imageView : view_order)
                        {
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
                                imageViews.add(imageView);
                                view_order = imageViews;
                                break;
                            }
                        }
                    }
                    break;
                case 5:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        for(ImageView imageView : view_order)
                        {
                            if(imageView == v) {
                                Log.d("ㅇㅇ","1");
                                ArrayList<ImageView> imageViews = new ArrayList<>();
                                imageViews.add(imageView);
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
        order_front = (ImageButton) rootview.findViewById(R.id.order_front);
        order_back = (ImageButton) rootview.findViewById(R.id.order_back);
        magnify_btn  = (ImageButton) rootview.findViewById(R.id.magnify);
        minimize_btn  = (ImageButton) rootview.findViewById(R.id.minimize);

        relativeLayout = (RelativeLayout) rootview.findViewById(R.id.relative);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        Boolean isSeller = PreferenceUtil.getInstance(getContext()).getBooleanExtra("isSeller");
        trashView = (ImageView) rootview.findViewById(R.id.trash);
        empty_item = rootview.findViewById(R.id.no_simul_item);
        keyring_btn = (ImageButton) rootview.findViewById(R.id.img_but1);
        phonecase_btn = (ImageButton) rootview.findViewById(R.id.img_but2);
        acc_btn = (ImageButton) rootview.findViewById(R.id.img_but3);
        etc_btn = (ImageButton) rootview.findViewById(R.id.img_but4);

        view_order = new ArrayList<>();

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

        showProgress();
        // 로그인 되어있을 경우 유저에 따라 시뮬레이션 아이템 목록 (부자재 번호만) 불러오기
        if(mFirebaseUser != null){
            // 판매자일 경우 내 가게 상품 목록 불러오기
            if(isSeller){
                myRef_seller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())) {
                                cart = ds.child("material").getValue().toString();
                                if(TextUtils.isEmpty(cart))
                                {
                                    empty_item.setVisibility(View.VISIBLE);
                                    empty_item.setBackground(getResources().getDrawable(R.drawable.no_item_seller));
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
                                    empty_item.setBackground(getResources().getDrawable(R.drawable.empty_cart));
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
        // 로그인 안되어 있을 경우

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
                    loX = relativeLayout.getX();
                    loy = relativeLayout.getY();
                    trash_width = (int) trashView.getX();
                    trash_height =(int) trashView.getY();

                    check = false;
                    Log.d("상대레이아웃 넓이",parentWidth+"");
                    Log.d("상대레이아웃 높이",parentHeight+"");
                    Log.d("상대레이아웃 X",loX+"");
                    Log.d("상대레이아웃 Y",loy+"");
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
                showProgress();

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

                // 시뮬레이션 아이템 불러오기

                if(!TextUtils.isEmpty(cart)) {
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
                                    Log.d("궁금 " + ds.getKey(),ds.child("image_url").getChildrenCount()+"");
                                    String url = ds.child("image_RB_url").child(Integer.parseInt(ds.getKey()) + (int) ds.child("image_url").getChildrenCount()+"").getValue().toString();
                                    int width = Integer.parseInt(ds.child("size_width").getValue().toString());
                                    int height = Integer.parseInt(ds.child("size_height").getValue().toString());
                                    String category = ds.child("category").getValue().toString();
                                    addItemToRecyclerView(url, width, height, category);
                                    i++;
                                    continue;
                                }
                            }
                            simulationAdatper.getFilter().filter("");
                            simulation_items = simulationAdatper.getFilteredList();
                            simulationAdatper.getFilter().filter("");
                            simulation_items = simulationAdatper.getFilteredList();
                            hideProgress();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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

        left_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 2) {
                    MOVE = 2;
                    left_rotate.setBackground(getResources().getDrawable(R.drawable.left_mint));
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
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
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_black));
                }
                else {
                    MOVE = 1;
                    right_rotate.setBackground(getResources().getDrawable(R.drawable.right_black));
                }
            }
        });

        order_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MOVE != 4){
                    MOVE = 4;
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
                if(MOVE != 5){
                    MOVE = 5;
                    order_back.setBackground(getResources().getDrawable(R.drawable.back_mint));
                    order_front.setBackground(getResources().getDrawable(R.drawable.front_black));
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
                touch_cnt++;
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
                    Log.d("이전1",view_order.get(i).getScaleX()+"");
                    Log.d("이전2",view_order.get(i).getScaleX()+"");
                    view_order.get(i).setScaleX( (float)(view_order.get(i).getScaleX() * 1.5));
                    view_order.get(i).setScaleY((float) (view_order.get(i).getScaleY() * 1.5) );
                    Log.d("이후1",view_order.get(i).getScaleY()+"");
                    Log.d("이후2",view_order.get(i).getScaleY()+"");
                }
            }
        });
        minimize_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touch_cnt--;
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
                    Log.d("이전1",view_order.get(i).getScaleX()+"");
                    Log.d("이전2",view_order.get(i).getScaleX()+"");
                    view_order.get(i).setScaleX( (float)(view_order.get(i).getScaleX() / 1.5));
                    view_order.get(i).setScaleY((float) (view_order.get(i).getScaleY() / 1.5) );
                    Log.d("이후1",view_order.get(i).getScaleY()+"");
                    Log.d("이후2",view_order.get(i).getScaleY()+"");
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
    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    @SuppressLint("ClickableViewAccessibility")
    public void inflate(int position) {
        //상품 상세 페이지 정보 가져오기
        double width = simulation_items.get(position).getWidth();
        double height = simulation_items.get(position).getHeight();
        String url = simulation_items.get(position).getUrl();
        ImageView iv = new ImageView(getContext());  // 새로 추가할 imageView 생성

        double randomValue = Math.random();
        int intValue = (int) (randomValue * 5) + 2;
        iv.setX( intValue * parentWidth / 9 );
        randomValue = Math.random();
        intValue = (int) (randomValue * 8) + 2;
        iv.setY( intValue * parentHeight / 16 );

        Glide.with(getContext())
                .load(url)
                .into(iv);
        iv.setOnTouchListener(touchListener);
        iv.buildDrawingCache();

        double ratio = (double)parentHeight/parentWidth * 9;

        Log.d("짜이", parentHeight + "");
        Log.d("우람", parentWidth + "");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( (int)  width* parentWidth / 90,(int) (height* parentHeight / (ratio*10) ));

        iv.setLayoutParams(layoutParams);  // imageView layout 설정
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        relativeLayout.addView(iv); // 기존 linearLayout에 imageView 추가
        view_order.add(iv);

    }

    public void addItemToRecyclerView(String url, int width, int height, String category){
        Tab4_Simulation_Item item = new Tab4_Simulation_Item(url, width, height, category);
        simulation_items.add(item);
        simulationAdatper.notifyDataSetChanged();
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
}
