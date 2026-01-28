package com.sun.javafx.scene.control;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

/**
 * Model for the {@link VirtualFlow} with the purpose to compute various requests
 * that the {@link VirtualFlow} will make to correctly calculate and map cell position, cell size, and viewport size.
 *
 * <p>The model is considered to be estimation based, so the {@link VirtualFlow} will make various requests to calculate
 * and retrieve the estimated size with {@link #recalculateEstimatedSize(int, int)} and {@link #getEstimatedSize()}.
 *
 * <p>Subclasses do not necessarily need to work estimation-based, and can therefore ignore those requests and
 * calculate the estimated size just once in {@link #cellCountChanged()}.
 *
 * @param <T>
 *         the conrecte type of the {@link IndexedCell}
 */
public abstract class VirtualFlowModel<T extends IndexedCell> {

    protected final VirtualFlow<T> flow;

    /**
     * An estimation of the total size (height for vertical, width for horizontal).
     */
    private double estimatedSize = 0;

    protected VirtualFlowModel(VirtualFlow<T> flow) {
        this.flow = flow;
    }

    /**
     * Called when the cell count has changed.
     */
    public abstract void cellCountChanged();

    /**
     * Computes the base offset to the given index.
     *
     * @param toIndex
     *         the index, to which we calculate the base offset
     * @return the base offset
     */
    public abstract double computeBaseOffset(int toIndex);

    /**
     * Compute the index of the first visible cell for the current cell count.
     */
    public final int computeCurrentCellIndex() {
        return computeCurrentCellIndex(flow.getCellCount());
    }

    /**
     * Compute the index of the first visible cell for the given cell count.
     */
    public abstract int computeCurrentCellIndex(int cellCount);

    /**
     * Given a position value between 0 and 1, compute and return the viewport
     * offset from the "current" cell associated with that position value.
     * That is, if the return value of this function is used as a translation
     * factor for a sheet that contained all the items, then the current
     * item would end up positioned correctly.
     * We calculate the total size until the absoluteOffset is reached.
     */
    public final double computeViewportOffset() {
        return computeViewportOffset(flow.getCellCount());
    }

    /**
     * Given a position value between 0 and 1, compute and return the viewport
     * offset from the "current" cell associated with that position value.
     * That is, if the return value of this function is used as a translation
     * factor for a sheet that contained all the items, then the current
     * item would end up positioned correctly.
     * We calculate the total size until the absoluteOffset is reached.
     */
    public final double computeViewportOffset(int cellCount) {
        double maxOff = getEstimatedSize() - flow.getViewportLength();
        double absoluteOffset = flow.getAbsoluteOffset();
        if (maxOff > 0 && absoluteOffset > maxOff) {
            return maxOff - absoluteOffset;
        }

        return computeViewportOffsetImpl(cellCount);
    }

    public abstract double getCellHeight(T cell, double breadth);

    public abstract double getCellLength(int index);

    public abstract double getCellLength(T cell);

    public abstract double getCellWidth(T cell, double breadth);

    /**
     * Returns the estimated size of the model, that is the total size of all cells.
     *
     * @return the estimated size
     * @implNote Some models might be able to provide a perfectly accurate estimation.
     */
    public final double getEstimatedSize() {
        return estimatedSize;
    }

    public abstract double getLineSize();

    public abstract void recalculateEstimatedSize(int fromIndex, int improve);

    public abstract void updateCellSize(T cell);

    protected abstract double computeViewportOffsetImpl(int cellCount);

    protected final void setEstimatedSize(double estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

}
