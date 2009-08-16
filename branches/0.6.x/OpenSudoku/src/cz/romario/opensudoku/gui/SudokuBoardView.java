package cz.romario.opensudoku.gui;

import java.util.Collection;

import cz.romario.opensudoku.game.SudokuCell;
import cz.romario.opensudoku.game.SudokuCellCollection;
import cz.romario.opensudoku.game.SudokuGame;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 *  Sudoku board widget.
 *  
 * @author romario
 *
 */
public class SudokuBoardView extends View {

	public static final int DEFAULT_BOARD_SIZE = 100;
	
	private float mCellWidth;
	private float mCellHeight;
	
	private Paint mLinePaint;
	private Paint mNumberPaint;
	private Paint mNotePaint;
	private int mNumberLeft;
	private int mNumberTop;
	private float mNoteTop;
	private Paint mReadonlyPaint;
	private Paint mTouchedPaint;
	private Paint mSelectedPaint;
	
	private SudokuCell mTouchedCell;
	private SudokuCell mSelectedCell;
	private boolean mReadonly = false;
	
	private boolean mAutoHideTouchedCellHint = true;
	
	private SudokuGame mGame;
	private SudokuCellCollection mCells;
	
	private OnCellTappedListener mOnCellTappedListener;
	private OnCellSelectedListener mOnCellSelectedListener;
	
	public SudokuBoardView(Context context) {
		super(context);
		initWidget();
	}
	
	public SudokuBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initWidget();
	}
	
	public void setGame(SudokuGame game) {
		mGame = game;
		setCells(game.getCells());
	}

	// TODO: not used
	public void setCells(SudokuCellCollection cells) {
		mCells = cells;
		if (!mReadonly) {
			mSelectedCell = mCells.getCell(0, 0); // first cell will be selected by default
			onCellSelected(mSelectedCell);
		}
		invalidate();
	}
	
	public SudokuCellCollection getCells() {
		return mCells;
	}
	
	public SudokuCell getSelectedCell() {
		return mSelectedCell;
	}
	
	public void setReadOnly(boolean readonly) {
		mReadonly = readonly;
	}
	
	public boolean isReadOnly() {
		return mReadonly;
	}
	
	/**
	 * Registers callback which will be invoked when user taps the cell.
	 * 
	 * @param l
	 */
	public void setOnCellTappedListener(OnCellTappedListener l) {
		mOnCellTappedListener = l;
	}
	
	protected void onCellTapped(SudokuCell cell) {
		if (mOnCellTappedListener != null) {
			mOnCellTappedListener.onCellTapped(cell);
		}
	}
	
	/**
	 * Registers callback which will be invoked when cell is selected. Cell selection
	 * can change without user interaction.
	 * 
	 * @param l
	 */
	public void setOnCellSelectedListener(OnCellSelectedListener l) {
		mOnCellSelectedListener = l;
	}
	
	protected void onCellSelected(SudokuCell cell) {
		if (mOnCellSelectedListener != null) {
			mOnCellSelectedListener.onCellSelected(cell);
		}
	}
	
	
	
	private void initWidget() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		setBackgroundColor(Color.WHITE);
		
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.BLACK);
		
		mNumberPaint = new Paint();
		mNumberPaint.setColor(Color.BLACK);
		mNumberPaint.setAntiAlias(true);

		mNotePaint = new Paint();
		mNotePaint.setColor(Color.BLACK);
		mNotePaint.setAntiAlias(true);
		
		mReadonlyPaint = new Paint();
		mReadonlyPaint.setColor(Color.LTGRAY);

		mTouchedPaint = new Paint();
		mTouchedPaint.setColor(Color.rgb(50, 50, 255));
		//touchedPaint.setColor(Color.rgb(100, 255, 100));
		mTouchedPaint.setAlpha(100);
		
		mSelectedPaint = new Paint();
		mSelectedPaint.setColor(Color.YELLOW);
		mSelectedPaint.setAlpha(100);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        
//        Log.d(TAG, "widthMode=" + getMeasureSpecModeString(widthMode));
//        Log.d(TAG, "widthSize=" + widthSize);
//        Log.d(TAG, "heightMode=" + getMeasureSpecModeString(heightMode));
//        Log.d(TAG, "heightSize=" + heightSize);
        
        int width = -1, height = -1;
        if (widthMode == MeasureSpec.EXACTLY) {
        	width = widthSize;
        } else {
        	width = DEFAULT_BOARD_SIZE;
        	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
        		width = widthSize;
        	}
        }
        if (heightMode == MeasureSpec.EXACTLY) {
        	height = heightSize;
        } else {
        	height = DEFAULT_BOARD_SIZE;
        	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
        		height = heightSize;
        	}
        }
        
        if (widthMode != MeasureSpec.EXACTLY) {
        	width = height;
        }
        
        if (heightMode != MeasureSpec.EXACTLY) {
        	height = width;
        }
        
    	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
    		width = widthSize;
    	}
    	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
    		height = heightSize;
    	}
        
    	mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 9.0f;
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 9.0f;

        setMeasuredDimension(width, height);
        
        mNumberPaint.setTextSize(mCellHeight * 0.75f);
        mNotePaint.setTextSize(mCellHeight / 3.0f);
        // compute offsets in each cell to center the rendered number
        mNumberLeft = (int) ((mCellWidth - mNumberPaint.measureText("9")) / 2);
        mNumberTop = (int) ((mCellHeight - mNumberPaint.getTextSize()) / 2);
        
        // add some offset because in some resolutions notes are cut-off in the top
        mNoteTop = mCellHeight / 50.0f;

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// some notes:
		// Drawable has its own draw() method that takes your Canvas as an arguement
		
		// TODO: I don't get this, why do I need to substract padding only from one side?
		int width = getWidth() - getPaddingRight();
		int height = getHeight() - getPaddingBottom();
		
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		
		// draw cells
		int cellLeft, cellTop;
		if (mCells != null) {
			
			float numberAscent = mNumberPaint.ascent();
			float noteAscent = mNotePaint.ascent();
			float noteWidth = mCellWidth / 3f;
			for (int row=0; row<9; row++) {
				for (int col=0; col<9; col++) {
					SudokuCell cell = mCells.getCell(row, col);
					
					cellLeft = Math.round((col * mCellWidth) + paddingLeft);
					cellTop = Math.round((row * mCellHeight) + paddingTop);

					// draw read-only field background
					if (!cell.isEditable()) {
						canvas.drawRect(
								cellLeft, cellTop, 
								cellLeft + mCellWidth, cellTop + mCellHeight,
								mReadonlyPaint);
					}
					
					// draw cell Text
					int value = cell.getValue();
					if (value != 0) {
						mNumberPaint.setColor(cell.isInvalid() ? Color.RED : Color.BLACK);
						canvas.drawText(Integer.toString(value),
								cellLeft + mNumberLeft, 
								Math.round(cellTop) + mNumberTop - numberAscent, 
								mNumberPaint);
					} else {
						if (cell.hasNote()) {
							Collection<Integer> numbers = cell.getNoteNumbers();
							for (Integer number : numbers) {
								int n = number - 1;
								int c = n % 3;
								int r = n / 3;
								//canvas.drawText(Integer.toString(number), cellLeft + c*noteWidth + 2, cellTop + noteAscent + r*noteWidth - 1, mNotePaint);
								canvas.drawText(Integer.toString(number), cellLeft + c*noteWidth + 2, cellTop + mNoteTop - noteAscent + r*noteWidth - 1, mNotePaint);
							}
							
//							int[] numbers = cell.getNoteNumbers();
//							for (int number=1; number<numbers.length; number++) {
//								if (numbers[number] == 1) {
//									int n = number - 1;
//									int c = n % 3;
//									int r = n / 3;
//									canvas.drawText(Integer.toString(number), cellLeft + c*noteWidth + 2, cellTop - noteAscent + r*noteWidth - 1, mNotePaint);
//								}
//							}
						}
					}
					
					
						
				}
			}
			
			// highlight selected cell
			if (!mReadonly && mSelectedCell != null) {
				cellLeft = Math.round(mSelectedCell.getColumnIndex() * mCellWidth) + paddingLeft;
				cellTop = Math.round(mSelectedCell.getRowIndex() * mCellHeight) + paddingTop;
				canvas.drawRect(
						cellLeft, cellTop, 
						cellLeft + mCellWidth, cellTop + mCellHeight,
						mSelectedPaint);
			}
			
			// visually highlight cell under the finger (to cope with touch screen
			// imprecision)
			if (mTouchedCell != null) {
				cellLeft = Math.round(mTouchedCell.getColumnIndex() * mCellWidth) + paddingLeft;
				cellTop = Math.round(mTouchedCell.getRowIndex() * mCellHeight) + paddingTop;
				canvas.drawRect(
						cellLeft, paddingTop,
						cellLeft + mCellWidth, height,
						mTouchedPaint);
				canvas.drawRect(
						paddingLeft, cellTop,
						width, cellTop + mCellHeight,
						mTouchedPaint);
			}

		}
		
		// draw vertical lines
		for (int c=0; c <= 9; c++) {
			float x = (c * mCellWidth) + paddingLeft;
			if (c % 3 == 0) {
				canvas.drawRect(x-1, paddingTop, x+1, height, mLinePaint);
			} else {
				canvas.drawLine(x, paddingTop, x, height, mLinePaint);
			}
		}
		
		// draw horizontal lines
		for (int r=0; r <= 9; r++) {
			float y = r * mCellHeight + paddingTop;
			if (r % 3 == 0) {
				canvas.drawRect(paddingLeft, y-1, width, y+1, mLinePaint);
			} else {
				canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (!mReadonly) {
			int x = (int)event.getX();
			int y = (int)event.getY();
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mTouchedCell = getCellAtPoint(x, y);
				break;
			case MotionEvent.ACTION_UP:
				mSelectedCell = getCellAtPoint(x, y);
				
				if (mSelectedCell != null) {
					onCellTapped(mSelectedCell);
					onCellSelected(mSelectedCell);
				}
				
				if (mAutoHideTouchedCellHint) {
					mTouchedCell = null;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchedCell = null;
				break;
			}
			invalidate();
		}
		
		return !mReadonly;
	}
	
	// TODO: do I really need this?
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
    	// Actually, just let these come through as D-pad events.
    	return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!mReadonly) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:
					return moveCellSelection(0, -1);
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					return moveCellSelection(1, 0);
				case KeyEvent.KEYCODE_DPAD_DOWN:
					return moveCellSelection(0, 1);
				case KeyEvent.KEYCODE_DPAD_LEFT:
					return moveCellSelection(-1, 0);
				case KeyEvent.KEYCODE_0:
				case KeyEvent.KEYCODE_SPACE:
				case KeyEvent.KEYCODE_DEL:
					// clear value in selected cell
					// TODO: I'm not really sure that this is thread-safe
					if (mSelectedCell != null) {
						if (event.isShiftPressed() || event.isAltPressed()) {
							setCellNote(mSelectedCell, null);
							postInvalidate();
						} else {
							setCellValue(mSelectedCell, 0);
							moveCellSelectionRight();
						}
					}
					return true;
				case KeyEvent.KEYCODE_DPAD_CENTER:
					if (mSelectedCell != null) {
						onCellTapped(mSelectedCell);
					}
					return true;
			}
			
			if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
				int selNumber = keyCode - KeyEvent.KEYCODE_0;
				SudokuCell cell = mSelectedCell;
				
				if (event.isShiftPressed() || event.isAltPressed()) {
					// add or remove number to notes
					// TODO: ugly, I should just pass the number to command
					setCellNote(cell, SudokuCell.numberListToNoteString(cell.toggleNoteNumber(selNumber)));
					invalidate();
				} else {
					// enter number in cell
					setCellValue(cell, selNumber);
					moveCellSelectionRight();
				}
				return true;
			}
		}
	
		
		return false;
	}
	
	
	public void setCellValue(SudokuCell cell, int value) {
		if (cell.isEditable()) {
			if (mGame != null) {
				mGame.setCellValue(cell, value);
			} else {
				cell.setValue(value);
			}
		}
	}
	
	public void setCellNote(SudokuCell cell, String note) {
		if (cell.isEditable()) {
			if (mGame != null) {
				mGame.setCellNote(cell, note);
			} else {
				cell.setNote(note);
			}
		}
	}
	
	
	/**
	 * Moves selected cell by one cell to the right. If edge is reached, selection
	 * skips on beginning of another line. 
	 */
	public void moveCellSelectionRight() {
		if (!moveCellSelection(1, 0)) {
			int selRow = mSelectedCell.getRowIndex();
			selRow++;
			if (!moveCellSelectionTo(selRow, 0)) {
				moveCellSelectionTo(0, 0);
			}
		}
	}
	
	/**
	 * Moves selected by vx cells right and vy cells down. vx and vy can be negative. Returns true,
	 * if new cell is selected.
	 * 
	 * @param vx Horizontal offset, by which move selected cell.
	 * @param vy Vertical offset, by which move selected cell.
	 */
	private boolean moveCellSelection(int vx, int vy) {
		int newRow = 0;
		int newCol = 0;
		
		if (mSelectedCell != null) {
			newRow = mSelectedCell.getRowIndex() + vy;
			newCol = mSelectedCell.getColumnIndex() + vx;
		}
		
		return moveCellSelectionTo(newRow, newCol);
	}
	
	
	/**
	 * Moves selection to the cell given by row and column index.
	 * @param row Row index of cell which should be selected.
	 * @param col Columnd index of cell which should be selected.
	 * @return True, if cell was successfuly selected.
	 */
	private boolean moveCellSelectionTo(int row, int col) {
		if(col >= 0 && col < SudokuCellCollection.SUDOKU_SIZE 
				&& row >= 0 && row < SudokuCellCollection.SUDOKU_SIZE) {
			mSelectedCell = mCells.getCell(row, col);
			onCellSelected(mSelectedCell);
			
			postInvalidate();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get cell at given screen coordinates. Returns null if no cell is found.
	 * @param x
	 * @param y
	 * @return
	 */
	private SudokuCell getCellAtPoint(int x, int y) {
		// TODO: this is not nice, col/row vs x/y
		
		// take into account padding
		int lx = x - getPaddingLeft();
		int ly = y - getPaddingTop();
		
		int row = (int) (ly / mCellHeight);
		int col = (int) (lx / mCellWidth);
		
		if(col >= 0 && col < SudokuCellCollection.SUDOKU_SIZE 
				&& row >= 0 && row < SudokuCellCollection.SUDOKU_SIZE) {
			return mCells.getCell(row, col);
		} else {
			return null;
		}
	}
	
	/**
	 * Occurs when user tap the cell.
	 * 
	 * @author romario
	 *
	 */
	public interface OnCellTappedListener {
		void onCellTapped(SudokuCell cell);
	}
	
	/**
	 * Occurs when user selects the cell.
	 * 
	 * @author romario
	 *
	 */
	public interface OnCellSelectedListener {
		void onCellSelected(SudokuCell cell);
	}

	private String getMeasureSpecModeString(int mode) {
		String modeString = null;
		switch (mode) {
		case MeasureSpec.AT_MOST:
			modeString = "MeasureSpec.AT_MOST";
			break;
		case MeasureSpec.EXACTLY:
			modeString = "MeasureSpec.EXACTLY";
			break;
		case MeasureSpec.UNSPECIFIED:
			modeString = "MeasureSpec.UNSPECIFIED";
			break;
		}
		
		if (modeString == null)
			modeString = new Integer(mode).toString();
		
		return modeString;
	}

	public void setAutoHideTouchedCellHint(boolean autoHideTouchedCellHint) {
		mAutoHideTouchedCellHint = autoHideTouchedCellHint;
	}

	public boolean getAutoHideTouchedCellHint() {
		return mAutoHideTouchedCellHint;
	}
	
	public void hideTouchedCellHint() {
		mTouchedCell = null;
		invalidate();
	}
	


}