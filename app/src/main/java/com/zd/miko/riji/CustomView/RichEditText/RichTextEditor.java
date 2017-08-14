package com.zd.miko.riji.CustomView.RichEditText;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zd.miko.riji.Bean.ContentJson;
import com.zd.miko.riji.Bean.Element;
import com.zd.miko.riji.R;
import com.zd.miko.riji.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 这是一个富文本编辑器，给外部提供insertImage接口，添加的图片跟当前光标所在位置有关
 *
 * @author xmuSistone
 */
public class RichTextEditor extends InterceptLinearLayout {
    private static final int EDIT_PADDING = 10; // edittext常规padding是10dp
    private static final int EDIT_FIRST_PADDING_TOP = 10; // 第一个EditText的paddingTop值

    private int imageWidth = 200;
    private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnClickListener btnListener; // 图片右上角红叉按钮监听器
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    private EditText lastFocusEdit; // 最近被聚焦的EditText
    private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
    private int editNormalPadding = 0; //
    private int disappearingImageIndex = 0;
    private Context context;
    private FileUtils fileUtils;

    private int textColor = -12303292;

    private float textSize = 16;

    private String hintText;

    private EditText firstEdit;

    public LinearLayout getAllLayout() {
        return allLayout;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        firstEdit.setHint(hintText);
    }

    public void setHintColor(int color) {
        firstEdit.setHintTextColor(color);
    }

    public interface LayoutClickListener {
        void layoutClick();
    }

    private LayoutClickListener mLayoutClickListener;

    public void setLayoutClickListener(LayoutClickListener mLayoutClickListener) {
        this.mLayoutClickListener = mLayoutClickListener;
    }

    public RichTextEditor(Context context) {
        this(context, null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public interface OnImageClickListener {
        void onClick(String path, int type);
    }

    private OnImageClickListener clickListener;

    public void setOnImageClickListener(OnImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setIntercept(boolean b) {
        super.setIntercept(b);
    }

    private void init(AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RichTextEditor);
        hintText = array.getString(R.styleable.RichTextEditor_hintText);
        editNormalPadding = (int) array.getDimension(R.styleable.RichTextEditor_paddingLeftAndRight,
                Utils.dpToPx(10));

        array.recycle();
        fileUtils = new FileUtils(context);
        inflater = LayoutInflater.from(context);

        // 1. 初始化allLayout
        allLayout = this;
        allLayout.setOrientation(LinearLayout.VERTICAL);
        allLayout.setFocusable(true);
        allLayout.setFocusableInTouchMode(true);
        allLayout.setBackgroundColor(Color.WHITE);
        setupLayoutTransitions();


        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                }
                return false;
            }
        };

        // 3. 图片叉掉处理
        btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout parentView = (RelativeLayout) v.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusListener = (v, hasFocus) -> {
            if (hasFocus) {
                lastFocusEdit = (EditText) v;
            }
        };

        LayoutParams firstEditParam = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        editNormalPadding = dip2px(EDIT_PADDING);
        firstEdit = createEditText("", dip2px(EDIT_FIRST_PADDING_TOP));
        firstEdit.setHint(hintText);
        firstEdit.clearFocus();
        allLayout.addView(firstEdit, firstEditParam);

        lastFocusEdit = firstEdit;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mLayoutClickListener != null)
                    mLayoutClickListener.layoutClick();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = allLayout.indexOfChild(editTxt);
            View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RelativeLayout) {
                    // 光标EditText的上一个view对应的是图片
                    onImageCloseClick(preView);
                } else if (preView instanceof EditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView;
                    String str2 = preEdit.getText().toString();

                    // 合并文本view时，不需要transition动画
                    allLayout.setLayoutTransition(null);
                    allLayout.removeView(editTxt);
                    allLayout.setLayoutTransition(mTransitioner); // 恢复transition动画

                    // 文本合并
                    preEdit.setText(str2 + str1);
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
     */
    private void onImageCloseClick(View view) {
        if (!mTransitioner.isRunning()) {
            disappearingImageIndex = allLayout.indexOfChild(view);
            allLayout.removeView(view);
        }
    }

    /**
     * 生成文本输入框
     */

    private TextView createTextView(String text, int paddingTop) {
        TextView textView = (TextView) inflater.inflate(
                R.layout.rich_read_text, null);
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setScaleX(1.02f);
        textView.setPadding(editNormalPadding, paddingTop, editNormalPadding, 0);
        return textView;
    }

    private EditText createEditText(String hint, int paddingTop) {
        EditText editText = (EditText) inflater.inflate(
                R.layout.rich_text, null);
        editText.setScaleX(1.02f);
        editText.setTextSize(textSize);
        editText.setTextColor(textColor);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                float add = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    float mul = editText.getLineSpacingMultiplier();
                    editText.setLineSpacing(0f, 1f);
                    editText.setLineSpacing(Utils.dpToPx(8), mul);
                }
            }
        });

        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, 0);
        editText.setHint(hint);
        editText.setOnFocusChangeListener(focusListener);
        return editText;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.rich_image, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }

    private RelativeLayout createGifLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.rich_gif, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }

    private RelativeLayout createVideoLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.rich_video, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }




    public static final int IMAGE = 0;
    public static final int GIF = 1;
    public static final int VIDEO = 2;
    public static final int TEXT = -1;

    public void insertReadText(String text) {
        firstEdit.setVisibility(GONE);
        TextView textView = createTextView(text, 10);
        textView.setText(text);
        allLayout.addView(textView);
    }

    public void insertReadVideo(String videoPath) {

        RelativeLayout videoLayout = (RelativeLayout) LayoutInflater
                .from(this.getContext())
                .inflate(R.layout.rich_read_video, null);

        ImageView imageView = (ImageView) videoLayout.findViewById(R.id.edit_imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this).load(videoPath).into(imageView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);
        allLayout.addView(videoLayout);

        ImageButton playButton = (ImageButton) videoLayout.findViewById(R.id.id_bt_play);

        playButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(videoPath, VIDEO);
            }
        });
    }

    public void insertReadGif(String imagePath) {

        RelativeLayout imageLayout = (RelativeLayout) LayoutInflater
                .from(this.getContext())
                .inflate(R.layout.rich_read_gif, null);
        ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.edit_imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this).load(imagePath).into(imageView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);
        allLayout.addView(imageLayout);
        imageView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onClick(imagePath, GIF);
        });
    }

    public void insertReadImage(String imagePath) {

        RelativeLayout imageLayout = (RelativeLayout) LayoutInflater
                .from(this.getContext())
                .inflate(R.layout.rich_read_image, null);
        ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.edit_imageView);
        Glide.with(this).load(imagePath).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);
        allLayout.addView(imageLayout);
        imageView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onClick(imagePath, IMAGE);
        });
    }

    public void setRichTextColor(int color) {
        textColor = color;
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setTextColor(color);
            }
        }
    }

    /**
     * 单位sp
     */
    public void setRichTextSize(int spSize) {
        textSize = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize
                        , getResources().getDisplayMetrics());
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setTextSize(textSize);
            }
        }
    }

    /**
     * 插入一张图片
     */
    public void insertImage(String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);
        lastFocusEdit.setText(editStr1);
        String editStr2 = lastEditStr.substring(cursorIndex).trim();

        addEditTextAtIndex(lastEditIndex + 1, editStr2);
        addImageViewAtIndex(lastEditIndex + 1, imagePath);

        EditText ed = (EditText) allLayout.getChildAt(allLayout.getChildCount() - 1);

        ed.requestFocus();
        ed.setSelection(ed.getText().toString().length());
//        lastFocusEdit.requestFocus();
//        lastFocusEdit.setSelection(editStr1.length(), editStr1.length());
        hideKeyBoard();
    }

    public void insertGif(String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);
        lastFocusEdit.setText(editStr1);
        String editStr2 = lastEditStr.substring(cursorIndex).trim();
        addEditTextAtIndex(lastEditIndex + 1, editStr2);
        addGifAtIndex(lastEditIndex + 1, imagePath);
        EditText ed = (EditText) allLayout.getChildAt(allLayout.getChildCount() - 1);

        ed.requestFocus();
        ed.setSelection(ed.getText().toString().length());
        hideKeyBoard();
    }

    public void insertVideo(String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);
        lastFocusEdit.setText(editStr1);
        String editStr2 = lastEditStr.substring(cursorIndex).trim();
        addEditTextAtIndex(lastEditIndex + 1, editStr2);
        addVideoAtIndex(lastEditIndex + 1, imagePath);

        EditText ed = (EditText) allLayout.getChildAt(allLayout.getChildCount() - 1);
        ed.requestFocus();
        ed.setSelection(ed.getText().toString().length());
        hideKeyBoard();
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    public EditText addEditTextAtIndex(final int index, String editStr) {
        EditText editText2 = createEditText("", getResources()
                .getDimensionPixelSize(R.dimen.richtextedit_padding_top));
        editText2.setText(editStr);
        // 请注意此处，EditText添加、或删除不触动Transition动画
        allLayout.setLayoutTransition(null);
        allLayout.addView(editText2, index);
        allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
        return editText2;
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index,
                                    String imagePath) {
        final RelativeLayout imageLayout = createImageLayout();
        DataImageView imageView = (DataImageView) imageLayout
                .findViewById(R.id.edit_imageView);
        imageView.setType(IMAGE);
        Glide.with(this).load(imagePath).into(imageView);

        imageView.setAbsolutePath(imagePath);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);

        // onActivityResult无法触发动画，此处post处理
        allLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                allLayout.addView(imageLayout, index);
            }
        }, 200);

        imageView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onClick(imagePath, IMAGE);
        });

    }

    public void addVideoAtIndex(final int index,
                                String imagePath) {
        RelativeLayout imageLayout = createVideoLayout();
        DataImageView imageView = (DataImageView) imageLayout
                .findViewById(R.id.edit_imageView);
        imageView.setType(VIDEO);

        Glide.with(this).load(imagePath).into(imageView);

        imageView.setAbsolutePath(imagePath);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 调整imageView的高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);

        // onActivityResult无法触发动画，此处post处理
        allLayout.postDelayed(() -> allLayout.addView(imageLayout, index), 200);
        ImageButton playButton = (ImageButton) imageLayout.findViewById(R.id.id_bt_play);

        playButton.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onClick(imagePath, VIDEO);
        });
    }

    public void addGifAtIndex(final int index,
                              String imagePath) {
        final RelativeLayout imageLayout = createGifLayout();
        DataImageView imageView = (DataImageView) imageLayout
                .findViewById(R.id.edit_imageView);
        imageView.setType(GIF);
        Glide.with(this).load(imagePath).into(imageView);

        imageView.setAbsolutePath(imagePath);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, Utils.dpToPx(imageWidth));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(editNormalPadding, 6, editNormalPadding, 6);
        imageView.setLayoutParams(lp);

        // onActivityResult无法触发动画，此处post处理
        allLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                allLayout.addView(imageLayout, index);
            }
        }, 200);

        imageView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onClick(imagePath, GIF);
        });
    }


    /**
     * 根据view的宽度，动态缩放bitmap尺寸
     */
    public Bitmap getScaledBitmap(String filePath) {
        int width = getWidth();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int sampleSize = options.outWidth > width ? options.outWidth / width
                + 1 : 1;
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions() {
        mTransitioner = new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.addTransitionListener(new TransitionListener() {

            @Override
            public void startTransition(LayoutTransition transition,
                                        ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition,
                                      ViewGroup container, View view, int transitionType) {
                if (!transition.isRunning()
                        && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                    // transition动画结束，合并EditText
                    // mergeEditText();
                }
            }
        });
        mTransitioner.setDuration(300);
    }

    /**
     * 图片删除的时候，如果上下方都是EditText，则合并处理
     */
    @SuppressWarnings("unused")
    private void mergeEditText() {
        View preView = allLayout.getChildAt(disappearingImageIndex - 1);
        View nextView = allLayout.getChildAt(disappearingImageIndex);
        if (preView != null && preView instanceof EditText && null != nextView
                && nextView instanceof EditText) {
            EditText preEdit = (EditText) preView;
            EditText nextEdit = (EditText) nextView;
            String str1 = preEdit.getText().toString();
            String str2 = nextEdit.getText().toString();
            String mergeText = "";
            if (str2.length() > 0) {
                mergeText = str1 + "\n" + str2;
            } else {
                mergeText = str1;
            }

            allLayout.setLayoutTransition(null);
            allLayout.removeView(nextEdit);
            preEdit.setText(mergeText);
            preEdit.requestFocus();
            preEdit.setSelection(str1.length(), str1.length());
            allLayout.setLayoutTransition(mTransitioner);
        }
    }

    /**
     * dp和pixel转换
     *
     * @param dipValue dp值
     * @return 像素值
     */
    public int dip2px(float dipValue) {
        float m = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 对外提供的接口, 生成编辑数据上传
     */
    public List<EditData> buildEditData() {
        List<EditData> dataList = new ArrayList<EditData>();
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);
            EditData itemData = new EditData();
            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                itemData.inputStr = item.getText().toString();
            } else if (itemView instanceof RelativeLayout) {
                DataImageView item = (DataImageView) itemView
                        .findViewById(R.id.edit_imageView);
                itemData.imagePath = item.getAbsolutePath();
                itemData.bitmap = item.getBitmap();
            }
            dataList.add(itemData);
        }

        return dataList;
    }

    public ContentJson getRichContent() {
        List<Element> elements = new ArrayList<>();
        int num = allLayout.getChildCount();
        int imageIndex = 0, videoIndex = 0, gifIndex = 0, textIndex = 0;
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);

            Element e = new Element();

            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                e.setElementType(TEXT);
                e.setContent(item.getText().toString());
                e.setIndex(textIndex++);
            } else if (itemView instanceof RelativeLayout) {
                DataImageView item = (DataImageView) itemView
                        .findViewById(R.id.edit_imageView);
                e.setElementType(item.getType());
                e.setContent(item.getAbsolutePath());
                if (item.getType() == IMAGE) {
                    e.setIndex(imageIndex++);
                } else if (item.getType() == GIF) {
                    e.setIndex(gifIndex++);
                } else if (item.getType() == VIDEO) {
                    e.setIndex(videoIndex++);
                }
            }
            elements.add(e);
        }
        ContentJson contentJson = new ContentJson();
        contentJson.setElementList(elements);
        return contentJson;
    }

    public StringBuilder getRichEditData() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        StringBuilder editTextSB = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        List<String> imgUrls = new ArrayList<String>();
        char separator = 26;
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);
            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                editTextSB.append(item.getText().toString());
                sb.append(item.getText().toString());
            } else if (itemView instanceof RelativeLayout) {
                DataImageView item = (DataImageView) itemView
                        .findViewById(R.id.edit_imageView);
                imgUrls.add(item.getAbsolutePath());
                editTextSB.append(separator);
            }
        }
        data.put("text", editTextSB);
        data.put("imgUrls", imgUrls);
        return sb;
    }

    class EditData {
        String inputStr;
        String imagePath;
        Bitmap bitmap;
    }
}
