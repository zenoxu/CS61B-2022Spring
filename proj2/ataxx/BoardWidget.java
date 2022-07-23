/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import java.awt.event.MouseEvent;


import java.util.concurrent.ArrayBlockingQueue;

import static ataxx.PieceColor.*;
import static ataxx.Utils.*;

/** Widget for displaying an Ataxx board.
 *  @author Ziyi Xu
 */
class BoardWidget extends Pad  {

    /** Length of side of one square, in pixels. */
    static final int SQDIM = 50;
    /** Number of squares on a side. */
    static final int SIDE = Board.SIDE;
    /** Radius of circle representing a piece. */
    static final int PIECE_RADIUS = 15;
    /** Dimension of a block. */
    static final int BLOCK_WIDTH = 40;

    /** Color of red pieces. */
    private static final Color RED_COLOR = Color.RED;
    /** Color of blue pieces. */
    private static final Color BLUE_COLOR = Color.BLUE;
    /** Color of painted lines. */
    private static final Color LINE_COLOR = Color.BLACK;
    /** Color of blank squares. */
    private static final Color BLANK_COLOR = Color.WHITE;
    /** Color of selected squared. */
    private static final Color SELECTED_COLOR = new Color(150, 150, 150);
    /** Color of blocks. */
    private static final Color BLOCK_COLOR = Color.BLACK;

    /** Stroke for lines. */
    private static final BasicStroke LINE_STROKE = new BasicStroke(1.0f);
    /** Stroke for blocks. */
    private static final BasicStroke BLOCK_STROKE = new BasicStroke(5.0f);

    /** A new widget sending commands resulting from mouse clicks
     *  to COMMANDQUEUE. */
    BoardWidget(ArrayBlockingQueue<String> commandQueue) {
        _commandQueue = commandQueue;
        setMouseHandler("click", this::handleClick);
        _dim = SQDIM * SIDE;
        _blockMode = false;
        setPreferredSize(_dim, _dim);
        setMinimumSize(_dim, _dim);
    }

    /** Indicate that SQ (of the form CR) is selected, or that none is
     *  selected if SQ is null. */
    void selectSquare(String sq) {
        if (sq == null) {
            _selectedCol = _selectedRow = 0;
        } else {
            _selectedCol = sq.charAt(0);
            _selectedRow = sq.charAt(1);
        }
        repaint();
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BLANK_COLOR);
        g.fillRect(0, 0, _dim, _dim);
        g.setColor(LINE_COLOR);
        g.setStroke(LINE_STROKE);
        for (int i = 0; i <= SIDE; i++) {
            g.drawLine(i * SQDIM, 0, i * SQDIM, _dim);
            g.drawLine(0, i * SQDIM, _dim, i * SQDIM);
        }
        drawBoard(g);
        drawSelected(g);
    }

    /** Draw a block centered at (CX, CY) on G. */
    void drawBlock(Graphics2D g, int cx, int cy) {
        g.setColor(BLOCK_COLOR);
        g.setStroke(BLOCK_STROKE);
        int temp = BLOCK_WIDTH / 2;
        g.drawLine(cx - temp, cy - temp, cx + temp, cy + temp);
        g.drawLine(cx - temp, cy + temp, cx + temp, cy - temp);
        for (int i = -1; i <= 1; i++) {
            g.drawLine(cx - temp, cy - temp * i, cx + temp, cy - temp * i);
            g.drawLine(cx - temp * i, cy - temp, cx - temp * i, cy + temp);
        }
    }

    /** Draw a Color Piece centered at (CX, CY) on G.
     * Do nothing if Piece is not red or blue.
     * @param g The graph to be painted
     * @param cx X coordinate of Piece's center
     * @param cy Y coordinate of Piece's center
     * @param pieceColor The color of the Piece
     */
    void drawPiece(Graphics2D g, int cx, int cy, Color pieceColor) {
        if (pieceColor == Color.RED || pieceColor == Color.BLUE) {
            g.setColor(pieceColor);
            int x = cx - PIECE_RADIUS;
            int y = cy - PIECE_RADIUS;
            g.fillOval(x, y, 2 * PIECE_RADIUS, 2 * PIECE_RADIUS);
        }
    }

    void drawBoard(Graphics2D g) {
        for (char col0 = 'a'; col0 < 'a' + SIDE; col0 += 1) {
            for (char row0 = '1'; row0 < '1' + SIDE; row0 += 1) {
                if (_model.get(col0, row0) != EMPTY) {
                    int[] temp = convertToCenter(col0, row0);
                    int cx = temp[0];
                    int cy = temp[1];
                    if (_model.get(col0, row0) == BLOCKED) {
                        drawBlock(g, cx, cy);
                    } else if (_model.get(col0, row0) == RED) {
                        drawPiece(g, cx, cy, RED_COLOR);
                    } else if (_model.get(col0, row0) == BLUE) {
                        drawPiece(g, cx, cy, BLUE_COLOR);
                    }
                }
            }
        }
    }

    /** Turn the selected square into selected color.
     * Do nothing if we have not selected any square yet.
     * @param g The Graph to be painted
     */
    void drawSelected(Graphics2D g) {
        if (isSelected()) {
            g.setColor(SELECTED_COLOR);
            int[] temp = convertToCenter(_selectedCol, _selectedRow);
            int cx = temp[0];
            int cy = temp[1];
            g.fillRect(cx - SQDIM / 2, cy - SQDIM / 2,
                    SQDIM, SQDIM);
            if (_model.get(_selectedCol, _selectedRow) == RED) {
                g.setColor(RED_COLOR);
                drawPiece(g, cx, cy, RED_COLOR);
            } else if (_model.get(_selectedCol, _selectedRow) == BLUE) {
                g.setColor(BLUE_COLOR);
                drawPiece(g, cx, cy, BLUE_COLOR);
            } else if (_model.get(_selectedCol, _selectedRow) == BLOCKED) {
                g.setColor(BLOCK_COLOR);
                g.setStroke(BLOCK_STROKE);
                drawBlock(g, cx, cy);
            }
        }
    }

    /** Clear selected block, if any, and turn off block mode. */
    void reset() {
        _selectedRow = _selectedCol = 0;
        setBlockMode(false);
    }

    /** Set block mode on iff ON. */
    void setBlockMode(boolean on) {
        _blockMode = on;
    }

    /** Convert the col and row to the center of the square in pixel.
     * @param col The Col in the board.
     * @param row The Row in the board.
     * @return A List contain coordinate of X and Y
     */
    int[] convertToCenter(char col, char row) {
        int[] result = new int[2];
        int mouseCol, mouseRow;
        mouseCol = (col - 'a') * SQDIM + SQDIM / 2;
        mouseRow = (SQDIM * SIDE - (row - '1') * SQDIM) - SQDIM / 2;
        result[0] = mouseCol;
        result[1] = mouseRow;
        return result;
    }

    /** Return true if we have selected a square, otherwise return false. */
    boolean isSelected() {
        return !(_selectedRow == 0 && _selectedCol == 0);
    }

    /** Issue move command indicated by mouse-click event WHERE. */
    private void handleClick(String unused, MouseEvent where) {
        int x = where.getX(), y = where.getY();
        char mouseCol, mouseRow;
        if (where.getButton() == MouseEvent.BUTTON1) {
            mouseCol = (char) (x / SQDIM + 'a');
            mouseRow = (char) ((SQDIM * SIDE - y) / SQDIM + '1');
            if (mouseCol >= 'a' && mouseCol <= 'g'
                && mouseRow >= '1' && mouseRow <= '7') {
                if (_blockMode) {
                    _commandQueue.offer(String.format("block %s%s",
                            mouseCol, mouseRow));
                } else {
                    if (isSelected()) {
                        if (_model.get(_selectedCol, _selectedRow) == RED
                                || _model.get(_selectedCol,
                                _selectedRow) == BLUE) {
                            if (_model.legalMove(_selectedCol, _selectedRow,
                                    mouseCol, mouseRow)) {
                                _commandQueue.offer(String.format("%s%s-%s%s",
                                        _selectedCol, _selectedRow,
                                        mouseCol, mouseRow));
                            }
                        }
                        reset();
                    } else {
                        _selectedCol = mouseCol;
                        _selectedRow = mouseRow;
                    }
                }
            }
        }
        repaint();
    }

    public synchronized void update(Board board) {
        _model = new Board(board);
        repaint();
    }

    /** Dimension of current drawing surface in pixels. */
    private int _dim;

    /** Model being displayed. */
    private static Board _model;

    /** Coordinates of currently selected square, or '\0' if no selection. */
    private char _selectedCol, _selectedRow;

    /** True iff in block mode. */
    private boolean _blockMode;

    /** Destination for commands derived from mouse clicks. */
    private ArrayBlockingQueue<String> _commandQueue;
}
