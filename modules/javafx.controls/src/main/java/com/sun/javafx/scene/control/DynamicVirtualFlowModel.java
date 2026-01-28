/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.javafx.scene.control;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link VirtualFlowModel} that supports dynamic sizes. This model fully uses the estimation API
 * and caches the item size results to improve the performance.
 *
 * @param <T>
 *         the conrecte type of the {@link IndexedCell}
 */
public class DynamicVirtualFlowModel<T extends IndexedCell> extends VirtualFlowModel<T> {

    /**
     * A list containing the cached version of the calculated size (height for vertical, width for horizontal)
     * for a (fictive or real) cell for each element of the backing data.
     * This list is used to calculate the estimatedSize.
     * The list is not expected to be complete, but it is always up to date.
     * When the size of the items in the backing list changes, this list is cleared.
     */
    private final List<Double> itemSizeCache;

    /**
     * Creates the DynamicVirtualFlowModel for the {@link VirtualFlow}.
     *
     * @param flow
     *         the {@link VirtualFlow}
     */
    public DynamicVirtualFlowModel(VirtualFlow<T> flow) {
        super(flow);

        itemSizeCache = new ArrayList<>();
    }

    @Override
    public void cellCountChanged() {
        setEstimatedSize(0);
        itemSizeCache.clear();
    }

    @Override
    public double computeBaseOffset(int toIndex) {
        if (flow.getCellCount() <= 0) {
            return 0;
        }

        double estSize = getEstimatedSize() / flow.getCellCount();
        double total = 0d;
        for (int index = 0; index < toIndex; index++) {
            double nextSize = getCellSizeOrEstimated(index, estSize);
            total += nextSize;
        }
        return total;
    }

    @Override
    public int computeCurrentCellIndex(int cellCount) {
        if (cellCount <= 0) {
            return 0;
        }

        double estSize = getEstimatedSize() / cellCount;
        double absoluteOffset = flow.getAbsoluteOffset();

        double total = 0;
        for (int index = 0; index < cellCount; index++) {
            double nextSize = getCellSizeOrEstimated(index, estSize);
            total += nextSize;
            if (total > absoluteOffset) {
                return index;
            }
        }
        return cellCount - 1;
    }

    @Override
    public double getCellHeight(T cell, double breadth) {
        return Utils.boundedSize(cell.prefHeight(breadth), cell.minHeight(breadth), cell.maxHeight(breadth));
    }

    @Override
    public double getCellLength(int index) {
        return flow.applyOnCell(this::getCellLength, index);
    }

    @Override
    public double getCellLength(T cell) {
        return flow.isVertical() ? cell.getLayoutBounds().getHeight() : cell.getLayoutBounds().getWidth();
    }

    @Override
    public double getCellWidth(T cell, double breadth) {
        return Utils.boundedSize(cell.prefWidth(breadth), cell.minWidth(breadth), cell.maxWidth(breadth));
    }

    @Override
    public double getLineSize() {
        // For the scrolling to be reasonably consistent,
        // we set the lineSize to the average size of all currently loaded lines.
        return flow.getAverageCellLength();
    }

    @Override
    public void recalculateEstimatedSize(int fromIndex, int improve) {
        int itemCount = flow.getCellCount();
        if (itemCount <= 0) {
            return;
        }

        if (fromIndex > 0) {
            ensureSizeCacheSize(fromIndex);
        }

        if (improve > 0) {
            addToCache(improve, itemCount);
        }

        int cacheCount = getSizeCacheCount();
        int count = 0;
        double total = 0d;
        for (int index = 0; (index < itemCount && index < cacheCount); index++) {
            Double il = getCachedSize(index);
            if (il != null) {
                total = total + il;
                count++;
            }
        }

        if (count == 0) {
            setEstimatedSize(0);
            return;
        }

        setEstimatedSize(total * itemCount / count);
    }

    private void addToCache(int improve, int itemCount) {
        int added = 0;
        while (itemCount > getSizeCacheCount() && added < improve) {
            int index = getSizeCacheCount();
            double size = getCellSizeImpl(index, true);
            setCachedSize(index, size);

            added++;
        }
    }

    @Override
    public void updateCellSize(T cell) {
        int cellIndex = cell.getIndex();

        if (getSizeCacheCount() <= cellIndex) {
            return;
        }

        double newSize = flow.isVertical() ? cell.getLayoutBounds().getHeight() : cell.getLayoutBounds().getWidth();
        setCachedSize(cellIndex, newSize);
    }

    @Override
    protected double computeViewportOffsetImpl(int cellCount) {
        double absoluteOffset = flow.getAbsoluteOffset();

        double estSize = getEstimatedSize() / cellCount;
        double total = 0d;
        for (int index = 0; index < cellCount; index++) {
            double nextSize = getCellSizeOrEstimated(index, estSize);
            if (total + nextSize > absoluteOffset) {
                return absoluteOffset - total;
            }
            total += nextSize;
        }
        return 0d;
    }

    private void ensureSizeCacheSize(int size) {
        while (getSizeCacheCount() < size) {
            itemSizeCache.add(null);
        }
    }

    private double getAvailableCellSize(int index) {
        return getCellSizeImpl(index, false);
    }

    private Double getCachedSize(int index) {
        return itemSizeCache.get(index);
    }

    private double getCellSizeImpl(int index, boolean createNew) {
        if (index < 0) {
            return -1;
        }

        if (getSizeCacheCount() > index) {
            Double cached = getCachedSize(index);
            if (cached != null) {
                return cached;
            }
        }
        if (!createNew) {
            return -1;
        }

        ensureSizeCacheSize(index + 1);

        return getCellLength(index);
    }

    private double getCellSizeOrEstimated(int index, double estimatedSize) {
        double nextSize = getAvailableCellSize(index);
        if (nextSize < 0) {
            nextSize = estimatedSize;
        }
        return nextSize;
    }

    private int getSizeCacheCount() {
        return itemSizeCache.size();
    }

    private void setCachedSize(int index, double size) {
        itemSizeCache.set(index, size);
    }
}
