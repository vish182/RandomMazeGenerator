package com.exvishwajit.pathfindalgo;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.Random;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment {
    private static final String TAG = "Fragment1";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final int VISITED = 3;
    static final int WHITE = 0;
    static final int BLACK = 2;

    static final int LEFT = 3;
    static final int TOP = 0;
    static final int RIGHT = 1;
    static final int BOTTOM = 2;

    static final int BOTTOM_RIGHT_WALL = 5;
    static final int RIGHT_WALL = 6;
    static final int BOTTOM_WALL = 7;
    static final int NO_WALL = 8;
    static boolean GENERATED = false;

    static int SPEED = 25;

    static final int dir[] = {LEFT, RIGHT, BOTTOM, TOP};

    AnimationDrawable fadeAni;
    AnimationDrawable rightAni;
    AnimationDrawable bottomAni;
    AnimationDrawable bottomrightAni;
    AnimationDrawable darkAni;
    AnimationDrawable right_color;
    AnimationDrawable bottom_color;
    AnimationDrawable bottom_right_color;
    AnimationDrawable fade_trunc;
    ImageView imgGrid[][] = new ImageView[24][20];
    //ImageView imgBlack[][] = new ImageView[24][20];
    boolean visited2D[][] = new boolean[24][20];
    boolean visited1D[] = new boolean[480];
    int blockState[][] = new int[24][20];
    Button outline;
    Button maze;
    Button dijkstras;
    SeekBar speedSlide;
    TextView speedText;

    boolean matrix[][] = new boolean[480][480];
    boolean matrixMaze[][] = new boolean[480][480];
    static int count = 0;
    static boolean TERMINATE = false;

    //Dijkstras
    int borderState[][] = new int[24][20];

    boolean visitedDijkstras[] = new boolean[480];
    int distance[] = new int[480];
    int pathLink[] = new int[480];


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 480; i++) {
            for (int j = 0; j < 480; j++) {
                matrix[i][j] = false;
                matrix[j][i] = false;
                matrixMaze[i][j] = false;
                matrixMaze[j][i] = false;

                //blockState[i][j] = blockState[j][i] = 1;
            }

        }


        for (int i = 0; i < 480; i++) {
            visited1D[i] = false;

            /*if ((i % 20) - 1 >= 0) {
                matrix[i][i - 1] = true;
                matrix[i - 1][i] = true;
            }
            if (i - 20 >= 0) {
                matrix[i][i - 20] = true;
                matrix[i - 20][i] = true;
            }

            if ((i % 20) + 1 < 20) {
                matrix[i][i + 1] = true;
                matrix[i + 1][i] = true;
            }
            if ((i + 20) < 480) {
                matrix[i][i + 20] = true;
                matrix[i + 20][i] = true;
            }*/

        }


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_1, container, false);

        outline = view.findViewById(R.id.button10);
        maze = view.findViewById(R.id.button9);
        dijkstras = view.findViewById(R.id.button8);
        speedSlide = view.findViewById(R.id.speedSlider);
        speedText = view.findViewById(R.id.showSpeed);

        speedSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SPEED = 200 - (progress * 2);
                speedText.setText(progress + "% ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dijkstras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dijkstras(view);
            }
        });



        /*outline.setOnClickListener(new View.OnClickListener()  {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // mazeGenerate(0, view);
                if(GENERATED){

                    getFragmentManager().beginTransaction().detach(this).attach(this).commit();

                }else {
                    mazeIterGenerate(view);
                    //t.start();
                }
            }
        });*/

        maze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateWalls(view);
            }
        });

        class genLister extends Fragment implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + v.getId());
                TERMINATE = true;
                initDijkstas();
                if (GENERATED) {

                    for (int i = 0; i < 480; i++) {
                        for (int j = 0; j < 480; j++) {
                            matrix[i][j] = false;
                            matrix[j][i] = false;
                            matrixMaze[i][j] = false;
                            matrixMaze[j][i] = false;

                            //blockState[i][j] = blockState[j][i] = 1;
                        }

                    }


                    for (int i = 0; i < 480; i++) {
                        visited1D[i] = false;

                        /*if ((i % 20) - 1 >= 0) {
                            matrix[i][i - 1] = true;
                            matrix[i - 1][i] = true;
                        }
                        if (i - 20 >= 0) {
                            matrix[i][i - 20] = true;
                            matrix[i - 20][i] = true;
                        }

                        if ((i % 20) + 1 < 20) {
                            matrix[i][i + 1] = true;
                            matrix[i + 1][i] = true;
                        }
                        if ((i + 20) < 480) {
                            matrix[i][i + 20] = true;
                            matrix[i + 20][i] = true;
                        }*/

                    }
                    mazeIterGenerate(view);

                } else {
                    mazeIterGenerate(view);
                    outline.setText("New Maze!");
                    GENERATED = true;
                    //t.start();
                }
            }

        }
        genLister genListerInstance = new genLister();

        outline.setOnClickListener(genListerInstance);

        class imgLister implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + v.getId());
                changeImage(view, v.getId());
            }

        }
        imgLister imgListerInstance = new imgLister();

        class imgHoldLister implements View.OnLongClickListener {


            @Override
            public boolean onLongClick(View v) {
                setBlack(view, v.getId());
                return true;
            }
        }
        imgHoldLister imgHoldListerInstance = new imgHoldLister();


        class imgTouchLister implements View.OnTouchListener {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    //SAVE YOUR VIEWS ID//
                    changeImage(view, v.getId());
                    return true;
                }
                return false;
            }

        }
        imgTouchLister imgTouchListerInstance = new imgTouchLister();

        String temp_id;
        ImageView img1;
        Resources res = getResources();
        int id;
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 20; j++) {
                temp_id = "i" + i + "_" + j;
                id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
                img1 = (ImageView) view.findViewById(id);
                img1.setOnClickListener(imgListerInstance);
                img1.setOnTouchListener(imgTouchListerInstance);
                img1.setOnLongClickListener(imgHoldListerInstance);
                img1.setId(i * 100 + j);
                imgGrid[i][j] = img1;

            }
        }


        //   final ImageView img1 = (ImageView) view.findViewById(R.id.i0_0);
        //  img1.setId(11);


      /*  img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+ v.getId());
                changeImage(view, img1, v.getId());
            }
        });*/

        return view;
    }


    public void changeImage(View view, int id) {
        Log.d(TAG, "changeImage: " + id);
        ImageView image = view.findViewById(id);

        int col = id % 100;
        int row = (id - col) / 100;

        if (blockState[row][col] == WHITE) {

            image.setBackgroundResource(R.drawable.fade_trunc);
            if (borderState[row][col] == BOTTOM_RIGHT_WALL) {
                image.setBackgroundResource(R.drawable.bottom_right_fade_color);
                bottom_right_color = (AnimationDrawable) image.getBackground();
                bottom_right_color.start();
            } else if (borderState[row][col] == RIGHT_WALL) {
                image.setBackgroundResource(R.drawable.right_fade_color);
                right_color = (AnimationDrawable) image.getBackground();
                right_color.start();
            } else if (borderState[row][col] == BOTTOM_WALL) {
                image.setBackgroundResource(R.drawable.bottom_fade_color);
                bottom_color = (AnimationDrawable) image.getBackground();
                bottom_color.start();
            } else {
                image.setBackgroundResource(R.drawable.fade_trunc);
                fade_trunc = (AnimationDrawable) image.getBackground();
                fade_trunc.start();
            }
            blockState[row][col] = VISITED;
        } else if (blockState[row][col] == VISITED) {
            image.setBackgroundResource(R.drawable.dark_1);
            blockState[row][col] = WHITE;
            //darkAni = (AnimationDrawable) image.getBackground();
            //darkAni.start();
        }


        // image.setImageResource(R.drawable.transition_orange_framed);


    }


    public void drawOutline(final View view) {
        //  Log.d(TAG, "changeImage: ");
       /* String temp_id;
        int id;
        Resources res = getResources();*/


        Handler handler1 = new Handler();
        for (int a = 1; a < 20; a++) {
            final int finalA = a;
            handler1.postDelayed(new Runnable() {


                String temp_id;
                int id;
                Resources res = getResources();

                @Override
                public void run() {
                    temp_id = "i0" + "_" + finalA;
                    id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
                    ImageView image = view.findViewById(id);
                    //  Thread.sleep(150);
                    image.setBackgroundResource(R.drawable.black_fade);
                    darkAni = (AnimationDrawable) image.getBackground();
                    darkAni.start();
                }
            }, 100 * a);
        }



       /* for(int i=1; i<20; i++){
            temp_id = "i0"  + "_" + i;
            id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
            ImageView image = view.findViewById(id);
          //  Thread.sleep(150);
            image.setBackgroundResource(R.drawable.black_fade);
            darkAni = (AnimationDrawable) image.getBackground();
            darkAni.start();



        }*/


        // image.setImageResource(R.drawable.transition_orange_framed);

    }

    void setBlack(View view, int id) {

        ImageView image = view.findViewById(id);

        int col = id % 100;
        int row = (id - col) / 100;
        if (blockState[row][col] != BLACK) {
            image.setBackgroundResource(R.drawable.black_fade);
            blockState[row][col] = BLACK;
            darkAni = (AnimationDrawable) image.getBackground();
            darkAni.start();
        } else {
            image.setBackgroundResource(R.drawable.dark_1);
            blockState[row][col] = WHITE;
            // darkAni = (AnimationDrawable) image.getBackground();
            // darkAni.start();
        }

    }

    boolean dirValidation(int cellno, int dir) {
        int col = cellno % 20;
        int row = (cellno - col) / 20;


        if (dir == LEFT) {
            if (!(col - 1 >= 0) || visited1D[cellno - 1])
                return false;
        }
        if (dir == TOP) {
            if (!(cellno - 20 >= 0) || visited1D[cellno - 20])
                return false;
        }

        if (dir == RIGHT) {
            if (!(col + 1 < 20) || visited1D[cellno + 1])
                return false;
        }
        if (dir == BOTTOM) {
            if (!((cellno + 20) < 480) || visited1D[cellno + 20])
                return false;
        }

        return true;

    }

    boolean hasNeighbours(int cellno) {
        int col = cellno % 20;
        int row = (cellno - col) / 20;


        if ((col - 1 >= 0)) {
            if (!visited1D[cellno - 1])
                return true;
        }
        if (cellno - 20 >= 0) {
            if (!(visited1D[cellno - 20]))
                return true;
        }

        if (col + 1 < 20) {
            if (!(visited1D[cellno + 1]))
                return true;
        }
        if (((cellno + 20) < 480)) {
            if (!(visited1D[cellno + 20]))
                return true;
        }

        return false;

    }

    void setRightWall(View view, int id) {

        ImageView image = view.findViewById(id);

        image.setBackgroundResource(R.drawable.right_fade);
        rightAni = (AnimationDrawable) image.getBackground();
        rightAni.start();

    }

    void setBottomWall(View view, int id) {
        ImageView image = view.findViewById(id);

        image.setBackgroundResource(R.drawable.bottom_fade);
        bottomAni = (AnimationDrawable) image.getBackground();
        bottomAni.start();
    }

    void setRightBottomWall(View view, int id) {
        ImageView image = view.findViewById(id);

        image.setBackgroundResource(R.drawable.bottom_right_fade);
        bottomrightAni = (AnimationDrawable) image.getBackground();
        bottomrightAni.start();
    }

    void setWhite(View view, int id) {
        ImageView image = view.findViewById(id);

        image.setBackgroundResource(R.drawable.white);
        // bottomrightAni = (AnimationDrawable) image.getBackground();
        // bottomrightAni.start();
    }


    @SuppressLint("ResourceType")
    void animateWalls(final View view) {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 20; j++) {
                //Log.d(TAG, "options right:  "+(i * 20 + j) + " - " + (i * 20 + j + 1) + !matrixMaze[i * 20 + j][i * 20 + j + 1] ) ;
                //  Log.d(TAG, "options bottom:  "+(i * 20 + j) + " - " + (i * 20 + j + 20) + !matrixMaze[i * 20 + j][i * 20 + j + 20] ) ;

                //if (j < 19 && i < 23) {
                if ((j < 19 && i < 23) && !matrixMaze[i * 20 + j][i * 20 + j + 1] && !matrixMaze[i * 20 + j][i * 20 + j + 20]) {
                    Log.d(TAG, "animateWalls: " + (i * 20 + j) + " both");

                    borderState[i][j] = BOTTOM_RIGHT_WALL;
                    Log.d(TAG, "run: SET BLOCK State: " + i + "-" + j + " " + borderState[i][j] + "bottom right");
                    //setRightBottomWall(view, (finalI * 100 + finalJ));
                    Handler handler1 = new Handler();

                    final int finalI = i;
                    final int finalJ = j;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageView image = view.findViewById(finalI * 100 + finalJ);

                            image.setBackgroundResource(R.drawable.bottom_right_fade);
                            bottomrightAni = (AnimationDrawable) image.getBackground();
                            bottomrightAni.start();

                        }
                    }, 100 * finalI + 30 * finalJ);


                }
                //}
                // else if (j < 19) {

                else if ((j < 19) && !matrixMaze[i * 20 + j][i * 20 + j + 1]) {
                    Log.d(TAG, "animateWalls: " + (i * 20 + j) + " right");
                    borderState[i][j] = RIGHT_WALL;
                    Log.d(TAG, "run: SET BLOCK State: " + i + "-" + j + " " + borderState[i][j] + "right");
                    // setRightWall(view, (i * 100 + j));

                    Handler handler1 = new Handler();

                    final int finalI = i;
                    final int finalJ = j;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageView image = view.findViewById(finalI * 100 + finalJ);

                            image.setBackgroundResource(R.drawable.right_fade);
                            rightAni = (AnimationDrawable) image.getBackground();
                            rightAni.start();
                        }
                    }, 100 * finalI + 30 * finalJ);

                }
                // }

                // else if (i < 23) {
                else if ((i < 23) && !matrixMaze[i * 20 + j][i * 20 + j + 20]) {
                    // setBottomWall(view, (i * 100 + j));
                    Log.d(TAG, "animateWalls: " + (i * 20 + j) + " bottom");
                    borderState[i][j] = BOTTOM_WALL;
                    Log.d(TAG, "run: SET BLOCK State: " + i + "-" + j + " " + borderState[i][j] + " bottom ");

                    Handler handler1 = new Handler();

                    final int finalI = i;
                    final int finalJ = j;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageView image = view.findViewById(finalI * 100 + finalJ);

                            image.setBackgroundResource(R.drawable.bottom_fade);
                            bottomAni = (AnimationDrawable) image.getBackground();
                            bottomAni.start();
                        }
                    }, 100 * finalI + 30 * finalJ);

                } else {
                    borderState[i][j] = NO_WALL;
                    Log.d(TAG, "run: SET BLOCK State: " + i + "-" + j + " " + borderState[i][j] + "no wall");

                    Handler handler1 = new Handler();

                    final int finalI = i;
                    final int finalJ = j;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ImageView image = view.findViewById(finalI * 100 + finalJ);

                            image.setBackgroundResource(R.drawable.white);
                            //setWhite(view, (finalI * 100 + finalI*30));
                        }
                    }, 100 * finalI + 30 * finalJ);
                }
                // }
            }

        }

        ImageView image = view.findViewById(0);

        if (!matrixMaze[0][1]) {
            image.setImageResource(R.drawable.rocket_right);
        } else if (!matrixMaze[0][20]) {
            image.setImageResource(R.drawable.rocket_bottom);
        }

        image = view.findViewById(2319);
        image.setImageResource(R.drawable.bomb);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void mazeGenerate(int current, View v) {
        count++;
        int col = current % 20;
        int row = (current - col) / 20;

        int randomNum;
        // boolean tried[] = {false,false,false,false};
        shuffleArray(dir);
        // Log.d(TAG, "count: "+ count + " ++" + dir[0] + " " + dir[1] + " " + dir[2] + " " + dir[3]);

        // stk.push(current);
        visited1D[current] = true;
        //boolean flag = true;

        for (int i = 0; i < 4; i++) {
            //  Log.d(TAG, "mazeGenerate: accepted = " + i);
            if (dirValidation(current, dir[i])) {
                //flag = false;
                switch (dir[i]) {
                    case LEFT:
                        matrixMaze[current][current - 1] = true;
                        matrixMaze[current - 1][current] = true;
                        //changeImage(v, row*100 + col);
                        mazeGenerate(current - 1, v);
                        break;
                    case RIGHT:
                        matrixMaze[current][current + 1] = true;
                        matrixMaze[current + 1][current] = true;
                        // changeImage(v, row*100 + col);
                        mazeGenerate(current + 1, v);
                        break;
                    case TOP:
                        matrixMaze[current][current - 20] = true;
                        matrixMaze[current - 20][current] = true;
                        // changeImage(v, row*100 + col);
                        mazeGenerate(current - 20, v);
                        break;
                    case BOTTOM:
                        matrixMaze[current][current + 20] = true;
                        matrixMaze[current + 20][current] = true;
                        // changeImage(v, row*100 + col);
                        mazeGenerate(current + 20, v);
                        break;

                }
                break;

            } else {
                // Log.d(TAG, "mazeGenerate: REJECTED" + i);
            }
        }

        //  Log.d(TAG, "mazeGenerate: has neighbours" + hasNeighbours(current));
        while (hasNeighbours(current)) {
            mazeGenerate(current, v);
        }


    }

    Stack<Integer> stk = new Stack<Integer>();


    void mazeIterGenerate(final View view) {
        int temp;
        int current = 0;
        stk.push(0);
        boolean flag = true;
        int count = 1;

        Handler handler1 = new Handler();

        while (!stk.empty()) {


            final int finalCount = count;
            final int finalCurrent = current;
            handler1.postDelayed(new Runnable() {

                int col = finalCurrent % 20;
                int row = (finalCurrent - col) / 20;

                // String temp_id;
                // int id;
                //  Resources res = getResources();

                @Override
                public void run() {
                    // temp_id = "i0" + "_" + finalCount;
                    //  id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
                    ImageView image = view.findViewById(row * 100 + col);
                    //  Thread.sleep(150);
                    image.setBackgroundResource(R.drawable.black_fade);
                    darkAni = (AnimationDrawable) image.getBackground();
                    darkAni.start();
                }
            }, 10 * count);


            count++;
            shuffleArray(dir);
            visited1D[current] = true;
            flag = true;

            for (int i = 0; i < 4; i++) {
                //  Log.d(TAG, "mazeGenerate: accepted = " + i);
                if (dirValidation(current, dir[i])) {
                    flag = false;
                    switch (dir[i]) {
                        case LEFT:
                            matrixMaze[current][current - 1] = true;
                            Log.d(TAG, "connection: " + current + "-" + (current - 1));
                            matrixMaze[current - 1][current] = true;
                            //changeImage(v, row*100 + col);
                            stk.push(current);
                            current = current - 1;

                            break;
                        case RIGHT:
                            matrixMaze[current][current + 1] = true;
                            Log.d(TAG, "connection: " + current + "-" + (current + 1));
                            matrixMaze[current + 1][current] = true;
                            // changeImage(v, row*100 + col);
                            stk.push(current);
                            current = current + 1;
                            break;
                        case TOP:
                            matrixMaze[current][current - 20] = true;
                            Log.d(TAG, "connection: " + current + "-" + (current - 20));
                            matrixMaze[current - 20][current] = true;
                            // changeImage(v, row*100 + col);
                            stk.push(current);
                            current = current - 20;
                            break;
                        case BOTTOM:
                            matrixMaze[current][current + 20] = true;
                            Log.d(TAG, "connection: " + current + "-" + (current + 20));
                            matrixMaze[current + 20][current] = true;
                            // changeImage(v, row*100 + col);
                            stk.push(current);
                            current = current + 20;
                            break;

                    }
                    break;

                }
            }

            if (flag) {
                current = stk.pop();
            }

        }
        Log.d(TAG, "mazeIterGenerate: Exit");
    }

    private static void shuffleArray(int[] array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    void initDijkstas() {
        for (int i = 0; i < 480; i++) {
            pathLink[i] = 0;
            visitedDijkstras[i] = false;
            distance[i] = 5000;
        }
    }

    void dijkstras(final View view) {
        int j, k, min, current;

        initDijkstas();
        TERMINATE = false;

        for (int i = 0; i < 23; i++) { // for all verices
            //visitedDijkstras[i] = 0; // set all as not-visited
            if (matrixMaze[0][i]) { // if there is a path from source to i
                distance[i] = 1;//matrixMaze[0][i] ? 1:0; // set distance from infinite to real distance
            } else {
                distance[i] = 32767; // set distance to infinity frm source
                pathLink[i] = 0;
            }
        }

        current = 0; // set current as source, mark it visited
        visitedDijkstras[current] = true;
        distance[0] = 0; // source distance will be 0 from itself


        for (int i = 0; i < 480; i++) {


            if (current == 479) {
                backTrack(view, i);
                return;
            }


            Handler handler1 = new Handler();


            final int finalCount = count;
            final int finalCurrent = current;
            handler1.postDelayed(new Runnable() {

                int col = finalCurrent % 20;
                int row = (finalCurrent - col) / 20;

                // String temp_id;
                // int id;
                //  Resources res = getResources();

                @Override
                public void run() {
                    /*if(TERMINATE){
                        return;
                    }*/
                    // temp_id = "i0" + "_" + finalCount;
                    //  id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
                    ImageView image = view.findViewById(row * 100 + col);
                    //  Thread.sleep(150);
                    Log.d(TAG, "run: BLOCK State: " + row + "-" + col + " " + borderState[row][col]);
                    if (borderState[row][col] == BOTTOM_RIGHT_WALL) {
                        image.setBackgroundResource(R.drawable.bottom_right_fade_color);
                        bottom_right_color = (AnimationDrawable) image.getBackground();
                        bottom_right_color.start();
                    } else if (borderState[row][col] == RIGHT_WALL) {
                        image.setBackgroundResource(R.drawable.right_fade_color);
                        right_color = (AnimationDrawable) image.getBackground();
                        right_color.start();
                    } else if (borderState[row][col] == BOTTOM_WALL) {
                        image.setBackgroundResource(R.drawable.bottom_fade_color);
                        bottom_color = (AnimationDrawable) image.getBackground();
                        bottom_color.start();
                    } else {
                        image.setBackgroundResource(R.drawable.fade_trunc);
                        fade_trunc = (AnimationDrawable) image.getBackground();
                        fade_trunc.start();
                    }
                }


            }, SPEED * i);


            min = 32767;
            for (j = 0; j < 480; j++) { // for all vertices j
                if (min > distance[j] && !visitedDijkstras[j]) { // if j is not visited and there is a distance of j less than min
                    min = distance[j]; // new minimum is set
                    current = j; // new current node is set
                }
            } // new current node is found


            visitedDijkstras[current] = true;
            // this loop sees whether there is a new shorter path for all nodes passing through current
            for (k = 0; k < 480; k++) { //for all vertices k
                if (!visitedDijkstras[k] && (matrixMaze[current][k])) { // if k is not visited and here is a link from current to k
                    if ((distance[current] + 1) < distance[k]) { // (matrixMaze[current][k] ? 1 : 32767) and if the new possible distance through current is lesser
                        distance[k] = distance[current] + 1;//weightMatrix[current][k]; // set it to lesser distance
                        pathLink[k] = current; // path link of k is current
                    }
                }
            }


        }

    }

    void backTrack(final View view, int delay) {
        int j = 479;
        int count = 0;
        do { // back tracking each node through its pathLink
            count++;

            j = pathLink[j];

            Handler handler1 = new Handler();


            final int finalCount = count;

            final int finalJ = j;
            handler1.postDelayed(new Runnable() {

                int col = finalJ % 20;
                int row = finalJ / 20;

                // String temp_id;
                // int id;
                //  Resources res = getResources();

                @Override
                public void run() {
                    // temp_id = "i0" + "_" + finalCount;
                    //  id = res.getIdentifier(temp_id, "id", getContext().getPackageName());
                    ImageView image = view.findViewById(row * 100 + col);
                    //  Thread.sleep(150);
                    Log.d(TAG, "run: BLOCK State: " + row + "-" + col + " " + borderState[row][col]);
                    if (borderState[row][col] == BOTTOM_RIGHT_WALL) {
                        image.setBackgroundResource(R.drawable.reverse_botright);
                        bottom_right_color = (AnimationDrawable) image.getBackground();
                        bottom_right_color.start();
                    } else if (borderState[row][col] == RIGHT_WALL) {
                        image.setBackgroundResource(R.drawable.reverse_right);
                        right_color = (AnimationDrawable) image.getBackground();
                        right_color.start();
                    } else if (borderState[row][col] == BOTTOM_WALL) {
                        image.setBackgroundResource(R.drawable.reverse_bottom);
                        bottom_color = (AnimationDrawable) image.getBackground();
                        bottom_color.start();
                    } else {
                        image.setBackgroundResource(R.drawable.reverse_trunc);
                        fade_trunc = (AnimationDrawable) image.getBackground();
                        fade_trunc.start();
                    }
                }


            }, (SPEED) * (finalCount + delay));


        } while (j != 0); //j!=Source
    }
}
