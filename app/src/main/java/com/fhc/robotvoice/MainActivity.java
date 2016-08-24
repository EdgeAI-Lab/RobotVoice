package com.fhc.robotvoice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Button start;

    //语音控件
    private SpeechUnderstander mSpeechUnderstander;  // 语义理解对象(语音到语义)
    private TextUnderstander mTextUnderstander;      // 语义理解对象(文本到语义)
    private SpeechRecognizer mIat;  				 // 语音听写对象
    private SpeechSynthesizer mTts;  				 // 语音合成对象
    private RecognizerDialog mIatDialog;            // 语音听写UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        initVoice();
        setRecognizerParam();
        setTtsParam();

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIatDialog.setParameter("asr_sch", "1");
                mIatDialog.setParameter("nlp_version", "2.0");

                // 显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();

            }
        });
    }

    private void initVoice() {

        // 初始化语义理解对象(语音到语义)
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(MainActivity.this, mSpeechUdrInitListener);

        // 初始化语义理解对象(文本到语义)
        mTextUnderstander =  TextUnderstander.createTextUnderstander(MainActivity.this, textUnderstanderListener);

        // 初始化语音合成对象
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, mTtsInitListener);

        // 初始化语音识别对象
        mIat = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);

        //
        mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);

    }


    /***start*************************************语音控件初始化回调**************************************************/

    /**
     * 初始化语义理解监听器(语音到语义)
     */
    private InitListener mSpeechUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
//            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败,错误码："+code);
            }
        }
    };


    /**
     * 初始化语义理解监听器(文本到语义)
     */
    private InitListener textUnderstanderListener = new InitListener() {

        @Override
        public void onInit(int code) {
//            Log.d(TAG, "textUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败,错误码："+code);
            }
        }
    };

    /**
     * 初始化语音识别监听器
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
//            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败，错误码：" + code);
            }
        }
    };



    /**
     * 初始化语音合成监听器
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            Toast.makeText(MainActivity.this,"InitListener init() code = " + code,Toast.LENGTH_SHORT).show();
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败,错误码："+code);
                Log.d(TAG, "初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    /***end*************************************语音控件初始化回调***************************************************/

    /***start***********************************语音参数设置********************************************************/
    /**
     * 语音识别参数设置
     *
     */
    public void setRecognizerParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mSpeechUnderstander.setParameter(SpeechConstant.PARAMS, null);
        mTextUnderstander.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mSpeechUnderstander.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTextUnderstander.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        mTextUnderstander.setParameter("nlp_version", "2.0");

        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mTextUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");


        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mTextUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");
        mTextUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");

        mIat.setParameter(SpeechConstant.VAD_ENABLE, "1");
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_ENABLE, "1");
        mTextUnderstander.setParameter(SpeechConstant.VAD_ENABLE, "1");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "6000");
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "6000");
        mTextUnderstander.setParameter(SpeechConstant.VAD_BOS, "6000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");
        mTextUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");
        mTextUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
        mTextUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mTextUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_DWA, "0");
        mTextUnderstander.setParameter(SpeechConstant.ASR_DWA, "0");
    }


    /**
     * 语音合成参数设置
     *
     */
    private void setTtsParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "jiajia");

        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    /***end***********************************语音参数设置********************************************************/


    /***start**********************************语音控件回调*******************************************************/

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

            String text = recognizerResult.getResultString();
            Log.d(TAG,text);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };


    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /***end**********************************语音控件回调*******************************************************/

}
