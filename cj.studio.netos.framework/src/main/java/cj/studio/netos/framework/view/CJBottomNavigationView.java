package cj.studio.netos.framework.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cj.studio.netos.framework.R;

//BadgeRadioButton:
//https://github.com/lwcye/BadgeRadioButton
public class CJBottomNavigationView extends RadioGroup {
    MenuBuilder menuBuilder;

    public CJBottomNavigationView(Context context) {
        super(context);
        menuBuilder = new MenuBuilder(context);
        initNavigation();
    }

    public CJBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CJBottomNavigationView);
        int menu = ta.getResourceId(R.styleable.CJBottomNavigationView_menu, 0);
        MenuInflater menuInflater = new SupportMenuInflater(context);
        menuBuilder = new MenuBuilder(context);
        if(menu>0) {
            menuInflater.inflate(menu, menuBuilder);
        }
        ta.recycle();
        initNavigation();

    }

    private void initNavigation() {
        setWillNotDraw(false);
        setBackgroundColor(getResources().getColor(R.color.navigation_backgroup));

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        removeAllViews();
        for (int i = 0; i < menuBuilder.size(); i++) {
            MenuItem item = menuBuilder.getItem(i);
            RadioButton button = new BadgeRadioButton(getContext());
            initButton(button, item);
            addView(button);
        }
    }

    private void initButton(RadioButton button, MenuItem item) {
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        button.setId(item.getItemId());
        button.setLayoutParams(layoutParams);
        button.setText(item.getTitle());
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        button.setButtonDrawable(android.R.color.transparent);//
        button.setBackground(null);
        button.setGravity(Gravity.CENTER);
        if (item.isChecked()) {
            button.setChecked(true);
        }
        if (!item.isVisible()) {
            button.setVisibility(INVISIBLE);
        }
        if (!item.isEnabled()) {
            button.setEnabled(false);
        }

        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.navigation_icon_selector);
        Drawable drawable = tintListDrawable(item.getIcon(), csl);
        if (drawable != null) {
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        }
        button.setTextColor(getResources().getColorStateList(R.color.navigation_text_selector));//设置选中/未选中的文字颜色
    }

    public void unselectedAll() {
        clearCheck();
    }

    public void inflateMenu(int menuid) {
        menuBuilder.clear();
        MenuInflater menuInflater = new SupportMenuInflater(getContext());
        menuBuilder = new MenuBuilder(getContext());
        menuInflater.inflate(menuid, menuBuilder);
        removeAllViews();
        for (int i = 0; i < menuBuilder.size(); i++) {
            MenuItem item = menuBuilder.getItem(i);
            RadioButton button = new BadgeRadioButton(getContext());
            initButton(button, item);
            addView(button);
        }
    }

    /**
     * Drawable 颜色转化类
     *
     * @param drawable 源Drawable
     * @param colors
     * @return 改变颜色后的Drawable
     */
    public static Drawable tintListDrawable(@NonNull Drawable drawable, ColorStateList colors) {
        if (drawable == null) return null;
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.navigation_top_line));
        paint.setStrokeWidth(1.0F);
        float x = getX();
        float y = 0;
        float sx = x + getWidth();
        canvas.drawLine(0, y, sx, y, paint);

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.menuBuilder.clear();
        this.menuBuilder = null;
    }



    public RadioButton getBottomNavigationItemView(int index) {
        MenuItem item = menuBuilder.getItem(index);
        for (int i = 0; i < getChildCount(); i++) {
            RadioButton view = (RadioButton) getChildAt(index);
            if (view.getId() == item.getItemId()) {
                return view;
            }
        }
        return null;
    }


    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        BottomNavigationViewOnCheckedChangeListener onCheckedChangeListener = new BottomNavigationViewOnCheckedChangeListener(listener);
        super.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void clearMenu() {
        this.menuBuilder.clear();
        removeAllViews();
    }


    public MenuBuilder getMenu() {
        return menuBuilder;
    }

    public void addItem(int group, int id, int categoryOrder, CharSequence title) {
        menuBuilder.add(group, id, categoryOrder, title);
        MenuItem item = menuBuilder.findItem(id);
        RadioButton button = new BadgeRadioButton(getContext());
        initButton(button, item);
        addView(button);
    }

    private class BottomNavigationViewOnCheckedChangeListener implements OnCheckedChangeListener {
        OnCheckedChangeListener listener;

        public BottomNavigationViewOnCheckedChangeListener(OnCheckedChangeListener listener) {
            this.listener = listener;
        }

        public OnCheckedChangeListener getListener() {
            return listener;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (listener == null) {
                return;
            }
            listener.onCheckedChanged(group, checkedId);
        }
    }


}
