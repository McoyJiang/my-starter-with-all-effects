package com.android.Mylauncher2.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.Mylauncher.R;
import com.android.Mylauncher2.Workspace;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 页面辑编
 * 前当页面是不是可删除用view的id判断，当id > 0时弗成删除，否则是可以删除的，所以在传入view时，要根据是不是可删除设置好id
 * 用tag记标页面本来的页面索引
 * @author wusj
 * 
 */
public class HomeEditView extends ViewGroup implements OnTouchListener,
		OnLongClickListener {
	private static final String TAG = "HomeEditView";
	private Context mContext;
	private Workspace mWorkspace;
	private List<View> mChilds;
	// view从一点移到一另点时的平挪动画时长
	private static final int ANIMATION_DURATION = 250;

	// 小于即是4个时的度宽和高度
	private static final int SMALL_WIDTH = 300;
	private static final int SMALL_HEIGHT = 300;
	// 大于4个页面时，单个页面表现的度宽和高度
	private static final int BIG_WIDTH = 100;
	private static final int BIG_HEIGHT = 140;
	// 水平两页面之间的间隙
	private static final int PAGE_GAP_WIDTH = 5;
	// 竖直两页面之间的间隙
	private static final int PAGE_GAP_HEIGHT = 15;
	// 删除区域的高度
	private static final int DELETE_ZONE_HEIGHT = 40;

	int dragged = -1;
	// 前当是不是在挪动view
	private boolean movingView = false;

	private int dragLeft;
	private int dragTop;
	private TextView add;
	
	private boolean isDraggingDefaultPage;
	
	
	public interface OnClickPageListener {
		public void onClickPage(int index, List<PageInfo> pages);
	}
	
	private OnClickPageListener clickPage;
	public void setOnClickPageListener(OnClickPageListener listener) {
		this.clickPage = listener;
	}
	
	private OnClickListener mAddClickListener;
	public void setAddClickListener(OnClickListener addClickListener) {
		mAddClickListener = addClickListener;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.e(TAG, "onClick---v.id is " + v.getId());
			if (clickPage != null) {
				List<PageInfo> pages = getPreviewAllPages();
				clickPage.onClickPage(v.getId(), pages);
			}
		}
	};

	// 面前是view面后是view地点的置位
	private SparseArray<Integer> newPositions = new SparseArray<Integer>();

	private int initX;
	private int initY;
	private int lastTouchX;
	private int lastTouchY;

	private int lastTarget = -1;
	private Point startPoint;

	public HomeEditView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HomeEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		setOnTouchListener(this);
		setOnLongClickListener(this);

	}
	
	public void initWorkspace(Workspace workspace) {
		mWorkspace = workspace;
	}

	/**
	 * when customer click add button
	 * @param view
	 * @param i
	 */
	public void addOnePage(View view, int i) {
		addView(view, i);
		//view.setTag(i);
		newPositions.put(i, i);
	}
	
	/**
	 * create the previews according to all CellLayouts existed now
	 * @param views  all previews created from CellLayout
	 */
	public void addExistedPages(List<View> views) {
		removeAllViews();
		
		for (int i = 0; i < views.size(); i++) {
			View view = views.get(i);
			addView(view);
			view.setTag(i);
			newPositions.put(i, i);
		}
		
		//create ADD page
		if (views.size() <= 8) {
			addView(createAddView());
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			initX = (int) event.getX();
			initY = (int) event.getY();
			
			int target = getTargetAtCoor(initX, initY);
			if(target == mWorkspace.getDefaultHomePage()) {
				isDraggingDefaultPage = true;
			}

			lastTouchX = (int) event.getX();
			lastTouchY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			eventMove(event);
			break;
		case MotionEvent.ACTION_UP:
			touchUp(event);
			break;

		default:
			break;
		}

		if (aViewIsDragged()) {
			return true;
		}

		return false;
	}

	private void touchUp(MotionEvent event) {
		if (!aViewIsDragged()) {
			if (onClickListener != null) {
				View clickedView = getChildAt(getTargetAtCoor((int) event.getX(), (int) event.getY()));
				Log.e(TAG, "clickedView : " + clickedView);
                if(clickedView != null)
                    onClickListener.onClick(clickedView);
			}
		} else {
			Log.e(TAG, "touchUp---lastTarget is " + lastTarget);
			//hideDeleteView();
			manageChildrenReordering();
			movingView = false;
			dragged = -1;
			if(isDraggingDefaultPage){
				mWorkspace.setDefaultHomePage(lastTarget);
			}
			lastTarget = -1;
			cancelAnimations();
		}
	}

	private void cancelAnimations() {
		for (int i = 0; i < getChildCount(); i++) {
			if (i != dragged) {
				View child = getChildAt(i);
				child.clearAnimation();
			}
		}
	}

	private void manageChildrenReordering() {
		/*boolean draggedDeleted = touchUpInDeleteZoneDrop(lastTouchX, lastTouchY);
		Log.e(TAG, "the draggedDeleted is " + draggedDeleted);
		if (draggedDeleted) {
			View view = getChildAt(dragged);
			int id = view.getId();
			Log.e(TAG, "id : " + id);
		}

		if (draggedDeleted) {
			animateDeleteDragged();
			reorderChildrenWhenDraggedIsDeleted();
		} else {
			reorderChildren();
		}*/
		reorderChildren();
	}
	
	// 当有页面被删除时，重新排列view
	private void reorderChildrenWhenDraggedIsDeleted() {
		Integer newDraggedPosition = newPositions.get(dragged,dragged);

		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);

//		tellAdapterDraggedIsDeleted(newDraggedPosition);
		removeViewAt(newDraggedPosition);
		if (add != null && add.getParent() != this) {
			addView(createAddView(), getChildCount() - 1);
		}

		requestLayout();
	}
	
	// 删除时的缩小动画
	private void animateDeleteDragged() {
		ScaleAnimation scale = new ScaleAnimation(1.4f, 0f, 1.4f, 0f);//, biggestChildWidth / 2 , biggestChildHeight / 2);
		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		getChildAt(dragged).clearAnimation();
		getChildAt(dragged).startAnimation(scale);
	}

	private void reorderChildren() {
		Log.e(TAG, "reorderChildren()");
		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);
		requestLayout();
	}

	private void removeItemChildren(List<View> children) {
		for (View child : children) {
			removeView(child);
		}
	}

	private List<View> cleanUnorderedChildren() {
		List<View> children = saveChildren();
		removeItemChildren(children);
		return children;
	}

	private void addReorderedChildrenToParent(List<View> children) {
		List<View> reorderedViews = reeorderView(children);
		newPositions.clear();

		for (View view : reorderedViews) {
			if (view != null)
				addView(view);
		}
	}

	// 重新排列view
	private List<View> reeorderView(List<View> children) {
		Log.e(TAG, "reeorderView()");
		View[] views = new View[children.size()];

		for (int i = 0; i < children.size(); i++) {
			int position = newPositions.get(i, -1);
			Log.e(TAG, "the position is " + position);
			if (position != -1) {
				views[position] = children.get(i);
			} else {
				views[i] = children.get(i);
			}
		}

		return new ArrayList<View>(Arrays.asList(views));
	}

	// 将view存保返回
	private List<View> saveChildren() {
		List<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.clearAnimation();
			children.add(child);
		}
		return children;
	}

	private void eventMove(MotionEvent event) {
		if (movingView && aViewIsDragged()) {
			lastTouchX = (int) event.getX();
			lastTouchY = (int) event.getY();

			moveDraggedView(lastTouchX, lastTouchY);
			manageSwapPosition(lastTouchX, lastTouchY);
			//manageDeleteZoneHover(lastTouchX, lastTouchY);
		}
	}

	// 挪动时的置位换交管理
	private void manageSwapPosition(int x, int y) {
		int target = getTargetAtCoor(x, y);
		Log.e(TAG, "target : " + target);
		if (target != -1 && target != lastTarget) {
			animateGap(target);
			lastTarget = target;
		}
	}

	// 通过面后的置位(value)，失掉面前的view（key）
	private int currentViewAtPosition(int targetLocation) {
		int viewAtPosition = targetLocation;
		for (int i = 0; i < newPositions.size(); i++) {
			int value = newPositions.valueAt(i);
			if (value == targetLocation) {
				viewAtPosition = newPositions.keyAt(i);
				break;
			}
		}

		return viewAtPosition;
	}

	private void animateGap(int target) {
		Log.e(TAG, "animateGap()---the target is " + target);
		int viewAtPosition = currentViewAtPosition(target);
		if (getChildAt(viewAtPosition) == add) {
			return;
		}

		// 源始置位
		View origin = getChildAt(viewAtPosition);
		Log.e(TAG, "the origin.getid is " + origin.getId());
		int originLeft = origin.getLeft();
		int originTop = origin.getTop();
		if (viewAtPosition == dragged) {
			originLeft = dragLeft;
			originTop = dragTop;
		}

		// 前当置位
		View curr = getChildAt(target);
		Log.e(TAG, "the curr.getid is " + curr.getId());
		int currLeft = curr.getLeft();
		int currTop = curr.getTop();
		if (target == dragged) {
			currLeft = dragLeft;
			currTop = dragTop;
		}

		// 将要到达置位
		View tar = getChildAt(newPositions.get(dragged, dragged));
		Log.e(TAG, "the tar.getid is " + tar.getId());
		int tarLeft = tar.getLeft();
		int tarTop = tar.getTop();
		if (newPositions.get(dragged, dragged) == dragged) {
			tarLeft = dragLeft;
			tarTop = dragTop;
		}

		Point oldOffset = new Point();
		oldOffset.x = currLeft - originLeft;
		oldOffset.y = currTop - originTop;

		Point newOffset = new Point();
		newOffset.x = tarLeft - originLeft;
		newOffset.y = tarTop - originTop;

		animateMoveToNewPosition(getChildAt(viewAtPosition), oldOffset,
				newOffset);

		saveNewPosition(target, viewAtPosition);
	}

	private void saveNewPosition(int target, int viewInTarget) {
		newPositions.put(viewInTarget, newPositions.get(dragged, dragged));
		newPositions.put(dragged, target);

	}

	// 获得指定点上的view的索引
	private int getTargetAtCoor(int x, int y) {
		int ret = -1;
		// 减1明说：最后的deleteZone
		for (int i = 0; i < getChildCount() - 1; i++) {
			View child = getChildAt(i);
			if (child == getChildAt(dragged)) {
				// if (dragged != i)
				int count = getChildCount();
				if (count < 5) {
					if ((x > dragLeft && x < dragLeft + SMALL_WIDTH)
							&& (y > dragTop && y < dragTop + SMALL_HEIGHT)) {
						return i;
					}
				} else {
					if ((x > dragLeft && x < dragLeft + BIG_WIDTH)
							&& (y > dragTop && y < dragTop + BIG_HEIGHT)) {
						return i;
					}
				}

				continue;
			}

			if (isPointInsideView(x, y, child)) {
				return i;
			}
		}

		return ret;
	}

	// 挪动被拖曳的view
	private void moveDraggedView(int x, int y) {
		View childAt = getChildAt(dragged);
		int width = childAt.getMeasuredWidth();
		int height = childAt.getMeasuredHeight();

		int l = x - (1 * width / 2);
		int t = y - (1 * height / 2);

		childAt.layout(l, t, l + width, t + height);
	}

	// 调整view的巨细
	private void updateSize() {
		int count = getChildCount() - 1;
		if (count < 5) {
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				float wid = view.getWidth();
				float hei = view.getHeight();
				view.setScaleX(SMALL_WIDTH / wid);
				view.setScaleY(SMALL_HEIGHT / hei);
			}
		} else {
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				float wid = view.getWidth();
				float hei = view.getHeight();
				view.setScaleX(BIG_WIDTH / wid);
				view.setScaleY(BIG_HEIGHT / hei);
			}
		}
	}

	//mcoy 获取preview界面上所有的pages
	public List<PageInfo> getPreviewAllPages() {
		List<PageInfo> pages = new ArrayList<PageInfo>();
		for (int i = 0; i < getChildCount() - 1; i++) {
			View child = getChildAt(i);
			if (child == add) {
				continue;
			}
			
			PageInfo info = new PageInfo();
			info.originPage = (Integer) child.getTag();
			Log.e(TAG, "tag : " + info.originPage);
			info.currentPage = i;
			pages.add(info);
		}
		return pages;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		// updateSize();
		Log.e(TAG, "count is " + count);
		int left1 = getWidth() / 2 - (BIG_WIDTH * 3 + 2 * PAGE_GAP_WIDTH) / 2;
		int left2 = left1;
		int left3 = left1;

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			//mcoy after we clicking one page, we turn to the appropriat page according to the id
			child.setId(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}

			if (i < 3) {
				child.layout(left1, DELETE_ZONE_HEIGHT, left1 + BIG_WIDTH,
						BIG_HEIGHT + DELETE_ZONE_HEIGHT);
				left1 += BIG_WIDTH + PAGE_GAP_WIDTH;
			} else if (i >= 3 && i <= 5) {
				child.layout(left2, BIG_HEIGHT + PAGE_GAP_HEIGHT
						+ DELETE_ZONE_HEIGHT, left2 + BIG_WIDTH, BIG_HEIGHT * 2
						+ PAGE_GAP_HEIGHT + DELETE_ZONE_HEIGHT);
				left2 += BIG_WIDTH + PAGE_GAP_WIDTH;
			} else {
				child.layout(left3, BIG_HEIGHT * 2 + PAGE_GAP_HEIGHT * 2
						+ DELETE_ZONE_HEIGHT, left3 + BIG_WIDTH, BIG_HEIGHT * 2
						+ PAGE_GAP_HEIGHT * 2 + BIG_HEIGHT + DELETE_ZONE_HEIGHT);
				left3 += BIG_WIDTH + PAGE_GAP_WIDTH;
			}

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.e(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		
		if (count < 5) {
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				child.measure(MeasureSpec.makeMeasureSpec(SMALL_WIDTH,
						MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						SMALL_HEIGHT, MeasureSpec.EXACTLY));
			}
		} else {
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				child.measure(MeasureSpec.makeMeasureSpec(BIG_WIDTH,
						MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						BIG_HEIGHT, MeasureSpec.EXACTLY));
			}
		}

	}

	private float getPixelFromDip(int size) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
				r.getDisplayMetrics());
		return px;
	}

	@Override
	public boolean onLongClick(View v) {
		Log.e(TAG, "onLongClick()");
		if ((dragged = positionForView()) != -1) {
			if (getChildAt(dragged) == add) {
				// 长按的是添加按钮不造成移动事件
				return true;
			}
			
			startPoint = new Point();
			// getLeft（）和getTop（）取对相父控件的距离
			dragLeft = (int) getChildAt(dragged).getLeft();
			dragTop = (int) getChildAt(dragged).getTop();

			movingView = true;
			animateMoveAllItems();
			animateDragged();
			//popDeleteView();

			return true;
		}

		return false;
	}

	// 长按时，判断前当按下的是不是一个页面
	private int positionForView() {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (isPointInsideView(initX, initY, child)) {
				return i;
			}
		}

		return -1;
	}

	// 判断按下的点是不是在view上
	private boolean isPointInsideView(float x, float y, View view) {
		int viewX = view.getLeft();
		int viewY = view.getTop();

		if (pointIsInsideViewBounds(x, y, view, viewX, viewY)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pointIsInsideViewBounds(float x, float y, View view,
			int viewX, int viewY) {
		return (x > viewX && x < (viewX + view.getWidth()))
				&& (y > viewY && y < (viewY + view.getHeight()));
	}

	// 动启其它页面的跳动动画
	private void animateMoveAllItems() {
		Animation rotateAnimation = createFastRotateAnimation();

		for (int i = 0; i < getChildCount() - 1; i++) {
			View child = getChildAt(i);
			child.startAnimation(rotateAnimation);
		}
	}

	// 拖拽时其它页面的跳动动画
	private Animation createFastRotateAnimation() {
		Animation rotate = new RotateAnimation(-2.0f, 2.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		rotate.setRepeatMode(Animation.REVERSE);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setDuration(60);
		rotate.setInterpolator(new AccelerateDecelerateInterpolator());

		return rotate;
	}

	// 给被拖曳的item加放大动画
	private void animateDragged() {
		ScaleAnimation scale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f);// ,
																		// biggestChildWidth
																		// / 2 ,
																		// biggestChildHeight
																		// / 2);

		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		if (aViewIsDragged()) {
			getChildAt(dragged).clearAnimation();
			getChildAt(dragged).startAnimation(scale);
		}
	}

	// old动画起始点对相view的距离，new动画点终对相view的距离
	private TranslateAnimation createTranslateAnimation(Point oldOffset,
			Point newOffset) {
		TranslateAnimation translate = new TranslateAnimation(
				Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
				newOffset.x, Animation.ABSOLUTE, oldOffset.y,
				Animation.ABSOLUTE, newOffset.y);
		translate.setDuration(ANIMATION_DURATION);
		translate.setFillEnabled(true);
		translate.setFillAfter(true);
		translate.setInterpolator(new AccelerateDecelerateInterpolator());
		return translate;
	}

	private void animateMoveToNewPosition(View targetView, Point oldOffset,
			Point newOffset) {
		AnimationSet set = new AnimationSet(true);

		Animation rotate = createFastRotateAnimation();
		Animation translate = createTranslateAnimation(oldOffset, newOffset);

		set.addAnimation(rotate);
		set.addAnimation(translate);

		targetView.clearAnimation();
		targetView.startAnimation(set);

	}

	private boolean aViewIsDragged() {
		return dragged != -1;
	}

	// 建创表现在最后的添加view
	private TextView createAddView() {
		if (add != null) {
			return add;
		}
		
		add = new TextView(getContext());
		add.setBackgroundResource(R.drawable.preview_border);
		add.setGravity(Gravity.CENTER);
		add.setText("+");
		if(mAddClickListener != null) {
			add.setOnClickListener(mAddClickListener);
		}
		return add;
	}

}
