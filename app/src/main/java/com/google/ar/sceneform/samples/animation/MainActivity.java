/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.animation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import ru.ctvt.cps.sdk.SDKManager;

/** Demonstrates playing animated FBX models. */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AnimationSample";
    //low, mid_sub, andy, baseball
    private static final int Mas_Model[] = {1,2,3,4,5};

    private ArFragment arFragment;
    // Model loader class to avoid leaking the activity context.
    private ModelLoader modelLoader;
    private ModelRenderable lowRenderable;
    private AnchorNode anchorNode;
    private SkeletonNode model;
    // Controls animation playback.
    private ModelAnimator animator = null;

    private ModelRenderable mid_subRenderable;

    public static Boolean Flag = false;
    public static Spinner spinner_t;
    String[] data_menu = {"Меню","Сменить вид","Анимация","Тест","Завершить тест"};
    private SeekBar seekBar_speed;
    private TextView mTextView_speed;
    public static TextView mTextView_q;
    private int zn_speed=0;

    /*anchor для теста*/
    private TransformableNode el1Node;
    private TransformableNode el2Node;
    private TransformableNode el3Node;
    private ModelRenderable anemometrRenderable;
    private ModelRenderable brakeRenderable;
    private ModelRenderable retRenderable;
    private AnchorNode anchorNode1;
    private AnchorNode anchorNode2;
    private AnchorNode anchorNode3;
    private HitResult hitResult_test1;
    private HitResult hitResult_test2;
    private HitResult hitResult_test3;
    /******************/
    private boolean Test_flag = false;
    private Anchor anchor;
    private Anchor anchor1;
    private Anchor anchor2;
    private Anchor anchor3;

    private float scale = 0.1f;
    private float speed=0.0f;


    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        modelLoader = new ModelLoader(this);

        modelLoader.loadModel(Mas_Model[0], R.raw.animation_low_v1);
        modelLoader.loadModel(Mas_Model[1], R.raw.animation_mid_sub_v1);
        modelLoader.loadModel(Mas_Model[2], R.raw.anemometr);
        modelLoader.loadModel(Mas_Model[3], R.raw.brake);
        modelLoader.loadModel(Mas_Model[4], R.raw.ret);



        // When a plane is tapped, the model is placed on an Anchor node anchored to the plane.
        arFragment.setOnTapArPlaneListener(this::onPlaneTap);

        //инициализация библиотеки
        SDKManager.getInstance().init(this);
        Proc_Seekbars();
        Proc_Texts();
        Proc_Spinner();
    }


    private void Proc_Timer(){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Moving();
                }
            }, 0, 30);
    }


    private void Moving(){
        if ((speed<0.1)&&(Test_flag)) {
            Vector3 localPosition = model.getLocalPosition();
            speed += 0.001;
            localPosition.set(0.03f*scale/0.5f+localPosition.x, 1.65f*scale/0.5f+speed+localPosition.y, -0.21f*scale/0.5f+localPosition.z);
            el1Node.setLocalPosition(localPosition);

            localPosition = model.getLocalPosition();
            localPosition.set(-0.01f*scale/0.5f+localPosition.x, 1.7f*scale/0.5f+speed+localPosition.y, 0.151f*scale/0.5f+localPosition.z);
            el2Node.setLocalPosition(localPosition);

            localPosition = model.getLocalPosition();
            localPosition.set(0.00f * scale / 0.5f+localPosition.x, 1.65f * scale / 0.5f + speed+localPosition.y, 0.079f * scale / 0.5f+localPosition.z);
            el3Node.setLocalPosition(localPosition);
        }
        if (!Test_flag)
            speed=0.0f;

    }


    private void onPlayAnimation() {
            try {
                AnimationData data;
                if (Flag) {
                        data = lowRenderable.getAnimationData("animation_low_v1");
                        animator = new ModelAnimator(data, lowRenderable);

                } else {
                    data = mid_subRenderable.getAnimationData("animation_mid_sub_v1");
                    animator = new ModelAnimator(data, mid_subRenderable);

                }
                animator.setRepeatCount(10);
                animator.start();
            }
            catch (Exception e){
                Toast.makeText(getBaseContext(), "анимация запущена", Toast.LENGTH_SHORT).show();
            }
    }

    private void onPlaneTap(HitResult hitResult, Plane unusedPlane, MotionEvent unusedMotionEvent) {

        if (lowRenderable == null || mid_subRenderable == null) {
            mTextView_q.setText("Перезапустите приложение");
            return;
        }
        mTextView_q.setText("");
        // Create the Anchor.
        anchor = hitResult.createAnchor();
        hitResult_test1 = hitResult;
        hitResult_test2 = hitResult;
        hitResult_test3 = hitResult;


        if (anchorNode == null) {
            anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            model = new SkeletonNode();

            Vector3 localPosition = new Vector3();
            localPosition.set(0.0f, 0.0f, 0.0f);
            model.setParent(anchorNode);
            model.setLocalPosition(localPosition);
            model.setLocalScale(new Vector3(scale, scale, scale));
            model.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1, 0f), 270));

            if (!Flag) {
                model.setRenderable(lowRenderable);
                Flag = true;
            }
            else{
                model.setRenderable(mid_subRenderable);
                Flag = false;
            }
        }
    }

    //загрузка моделей
    void setRenderable(int id, ModelRenderable renderable) {
        switch (id) {
            case 1:{this.lowRenderable = renderable; break;}
            case 2:{this.mid_subRenderable = renderable; break;}
            case 3:{this.anemometrRenderable = renderable; break;}
            case 4:{this.brakeRenderable = renderable; break;}
            case 5:{this.retRenderable = renderable; break;}
        }
    }

    void onException(int id, Throwable throwable) {
        Toast toast = Toast.makeText(this, "Unable to load renderable: " + id, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.e(TAG, "Unable to load andy renderable", throwable);
    }

    private void Proc_Spinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_menu);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_t = (Spinner) findViewById(R.id.spinner_test);
        spinner_t.setAdapter(adapter);
        spinner_t.setPrompt("Title");    // заголовок
        spinner_t.setSelection(0);
        spinner_t.setBackgroundColor(Color.GRAY);
        spinner_t.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (spinner_t.getSelectedItem().toString()) {
                    case "Сменить вид":{
                        animator = null;
                        if ((!Flag)&&(model!=null)) {
                            Flag = true;
                            model.setRenderable(lowRenderable);
                        }
                        else if ((Flag)&&(model!=null)) {
                            Flag = false;
                            model.setRenderable(mid_subRenderable);
                        }
                        break;}
                    case "Анимация":{
                        onPlayAnimation();//анимация
                        break;}
                    case "Тест":{
                        if (anchorNode!=null) {
                            animator = null;
                            Test_flag = true;
                            Flag = false;
                            model.setRenderable(mid_subRenderable);
                            del_scen();
                            create_scen_test();
                            question();
                        }
                        break;}
                    case "Завершить тест":{
                        if (Test_flag) {
                            Test_flag = false;
                            question();
                            del_scen();
                            if (anchorNode != null) {
                                Flag = true;
                                model.setRenderable(lowRenderable);
                            }
                            animator = null;
                            speed=0.0f;
                        }
                        break;}
                }
                spinner_t.setSelection(0);//выбираем меню
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    private void Proc_Seekbars() {
        //обработчик на бегунок изменения скорости вращения
        seekBar_speed = (SeekBar) findViewById(R.id.seekBar_speed);
        seekBar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextView_speed.setText(String.valueOf(progress+1));
                zn_speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new RetrieveFeedTask().execute(String.valueOf(112+(zn_speed)*100));
            }
        });
    }

    private void Proc_Texts() {
        //обработчик на вывод текста
        mTextView_speed = (TextView) findViewById(R.id.textview_speed);
        mTextView_speed.setText("");
        mTextView_q = (TextView) findViewById(R.id.textview_question);
    }

    private void del_scen() {
        if (anchorNode1 != null) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode1);
            //anchorNode1.getAnchor().detach();
            anchorNode1.setParent(null);
            anchorNode1 = null;
        }
        if (anchorNode2 != null) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode2);
            //anchorNode2.getAnchor().detach();
            anchorNode2.setParent(null);
            anchorNode2 = null;
        }
        if (anchorNode3 != null) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode3);
            //anchorNode3.getAnchor().detach();
            anchorNode3.setParent(null);
            anchorNode3 = null;
        }
    }

    private void create_scen_test() {
        anchor1 = hitResult_test1.createAnchor();
        anchor2 = hitResult_test2.createAnchor();
        anchor3 = hitResult_test3.createAnchor();

        anchorNode1 = new AnchorNode(anchor1);
        anchorNode1.setParent(anchorNode);
        el1Node = new TransformableNode(arFragment.getTransformationSystem());
        Vector3 localPosition = model.getLocalPosition();
        localPosition.set(localPosition.x+0.03f*scale/0.5f, localPosition.y+1.65f*scale/0.5f, localPosition.z + (-0.1f*scale/0.5f));
        el1Node.setParent(anchorNode1);
        el1Node.setLocalPosition(localPosition);
        el1Node.setRenderable(anemometrRenderable);
        el1Node.getTranslationController().setEnabled(false);
        el1Node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1, 0f), 270));


        localPosition = model.getLocalPosition();
        anchorNode2 = new AnchorNode(anchor2);
        anchorNode2.setParent(anchorNode);
        el2Node = new TransformableNode(arFragment.getTransformationSystem());
        localPosition.set(localPosition.x + -0.01f*scale/0.5f, localPosition.y + 1.7f*scale/0.5f, localPosition.z + 0.1f*scale/0.5f);
        el2Node.setParent(anchorNode2);
        el2Node.setLocalPosition(localPosition);
        el2Node.setRenderable(brakeRenderable);
        el2Node.getTranslationController().setEnabled(false);
        el2Node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1, 0f), 270));

        localPosition = model.getLocalPosition();
        anchorNode3 = new AnchorNode(anchor3);
        anchorNode3.setParent(anchorNode);
        el3Node = new TransformableNode(arFragment.getTransformationSystem());
        localPosition.set(0.00f*scale/0.5f + localPosition.x, 1.65f*scale/0.5f+localPosition.y, 0.05f*scale/0.5f+localPosition.z);
        el3Node.setParent(anchorNode3);
        el3Node.setLocalPosition(localPosition);
        el3Node.setRenderable(retRenderable);
        el3Node.getTranslationController().setEnabled(false);
        el3Node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1, 0f), 270));


        Proc_Timer();
        Listener();
    }

    private void question() {
        if (Test_flag) {
            mTextView_q.setBackgroundColor(Color.GRAY);
            mTextView_q.setTextColor(Color.BLACK);
            mTextView_q.setText("Что должно сработать при сильном ветре?");
        }
        else {
            mTextView_q.setBackgroundColor(Color.TRANSPARENT);
            mTextView_q.setText("");
        }
    }

    private void Listener() {
        el1Node.setOnTouchListener(
                (HitTestResult hittestResult, MotionEvent motionEvent) -> {
                    if (el1Node.onTouchEvent(hittestResult,motionEvent)){
                        mTextView_q.setText("Что должно сработать при сильном ветре?\nОтвет не верный.");
                    }
                    return true;
                });

        el2Node.setOnTouchListener(
                (HitTestResult hittestResult, MotionEvent motionEvent) -> {
                    if (el2Node.onTouchEvent(hittestResult,motionEvent)){
                        mTextView_q.setText("Что должно сработать при сильном ветре?\nОтвет верный.");
                    }
                    return true;
                });

        el3Node.setOnTouchListener(
                (HitTestResult hittestResult, MotionEvent motionEvent) -> {
                    if (el3Node.onTouchEvent(hittestResult,motionEvent)){
                        mTextView_q.setText("Что должно сработать при сильном ветре?\nОтвет не верный.");
                    }
                    return true;
                });
    }

}
